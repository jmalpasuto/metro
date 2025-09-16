// Copyright (C) 2025 Zac Sweers
// SPDX-License-Identifier: Apache-2.0
package dev.zacsweers.metro.test.integration

import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.asContribution
import dev.zacsweers.metro.createGraph
import kotlin.test.Test
import kotlin.test.assertEquals

class ContributesGraphExtensionTest {
  @Test
  fun simple() {
    val exampleGraph = createGraph<SimpleScenario.ExampleGraph>()
    val loggedInGraph =
      exampleGraph.asContribution<SimpleScenario.LoggedInGraph.Factory>().createLoggedInGraph()
    val int = loggedInGraph.int
    assertEquals(int, 0)
  }

  @Test
  fun nested() {
    val exampleGraph = createGraph<SimpleScenario.ExampleGraph>()
    val loggedInGraph =
      exampleGraph.asContribution<SimpleScenario.LoggedInGraph.Factory>().createLoggedInGraph()
    val featureGraph =
      loggedInGraph.asContribution<SimpleScenario.FeatureGraph.Factory>().createFeatureGraph()
  }

  object SimpleScenario {
    abstract class SimpleApp

    abstract class SimpleFeatureScope
    abstract class SimpleLoggedInScope

    @GraphExtension(SimpleFeatureScope::class)
    interface FeatureGraph {

      @ContributesTo(SimpleLoggedInScope::class)
      @GraphExtension.Factory
      interface Factory {
        fun createFeatureGraph(): FeatureGraph
      }
    }

    @GraphExtension(SimpleLoggedInScope::class)
    interface LoggedInGraph {
      val int: Int

      @ContributesTo(SimpleApp::class)
      @GraphExtension.Factory
      interface Factory {
        fun createLoggedInGraph(): LoggedInGraph
      }
    }

    @DependencyGraph(scope = SimpleApp::class)
    interface ExampleGraph {
      @Provides fun provideInt(): Int = 0
    }
  }
}
