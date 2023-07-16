/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.mvik.testing

import com.google.common.truth.Truth
import com.google.mvik.framework.Model
import com.google.mvik.framework.Result

/**
 * Assertions for model classes.
 * 
 * The basic usage is: assertThat(MyModel()).succeeds() or assertThat(MyModel()).fails().
 * This solves the model and asserts that the result is respectively either a success or
 * a failure.
 * 
 * By default, the initial state from the model definition is used. It's sometimes useful
 * to use a different initial state, which can be provides with
 * 
 * assertThat(MyModel()).startingFrom(initialState).succeeds().
 *
 * More precise flavors of test can be written using failsWith() or reaches(). See the
 * docs on ModelClassSubject for more details.
 */
fun <State : Any> assertThat(modelClass: Model<State>): ModelClassSubject<State> {
  return ModelClassSubject(modelClass)
}

class ModelClassSubject<State : Any>(val model: Model<State>, val initState: State? = null) {
  fun startingFrom(initState: State): ModelClassSubject<State> {
    return ModelClassSubject(model, initState)
  }

  /**
   * Asserts that the model succeeds.
   */
  fun succeeds() {
    val result = solve()
    if (!result.success) throw AssertionError("failed in $result.endState")
  }

  /**
   * Asserts that the model fails.
   */
  fun fails() {
    Truth.assertThat(solve().success).isFalse()
  }

  /**
   * Asserts that the model fails ending up in the provided state.
   * 
   * This is only useful in narrow scenarios. The order in which the state space
   * is searched is undefined and can be non-deterministic. Models that have multiple
   * failing states aren't guaranteed to hit a specific one.
   */
  fun failsWith(target: State) {
    val result = solve()
    Truth.assertThat(result.success).isFalse()
    Truth.assertThat(result.endState).isEqualTo(target)
  }

  /**
   * Asserts that the model reaches the specified state.
   * 
   * This primarily intended for unit testing that steps produce the intended outcome.
   */
  fun reaches(target: State) {
    val safetyFailingAtTarget = Model.SafetySpec({ s: State -> s != target })
    Truth.assertThat(solve(listOf(safetyFailingAtTarget)).success).isFalse()
  }

  /**
   * Asserts that the model reaches any state satisfying the predicate.
   * 
   * This primarily intended for unit testing that steps produce the intended outcome.
   * 
   * This overload can be used to avoid specifying the full target state in cases
   * the states are large and fully writing them out is cumbersome.
   */
  fun reaches(pred: (State) -> Boolean) {
    val safetyFailingAtTarget = Model.SafetySpec({ s: State -> !pred(s) })
    Truth.assertThat(solve(listOf(safetyFailingAtTarget)).success).isFalse()
  }

  private fun solve(): Result<State> {
    if (initState != null) {
      return model.solveFrom(listOf(initState))
    } else {
      return model.solve()
    }
  }

  private fun solve(safetyProperties: List<Model.SafetySpec<State>>): Result<State> {
    if (initState != null) {
      return model.solve(listOf(initState), safetyProperties)
    } else {
      return model.solveWithSafeties(safetyProperties)
    }
  }
}
