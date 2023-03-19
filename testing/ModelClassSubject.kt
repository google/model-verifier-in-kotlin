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

package com.github.zmatti.mvik.testing

import com.google.common.truth.Truth
import com.github.zmatti.mvik.framework.Model
import com.github.zmatti.mvik.framework.Result

fun <State : Any> assertThat(modelClass: Model<State>): ModelClassSubject<State> {
  return ModelClassSubject(modelClass)
}

class ModelClassSubject<State : Any>(val model: Model<State>, val initState: State? = null) {
  fun startingFrom(initState: State): ModelClassSubject<State> {
    return ModelClassSubject(model, initState)
  }

  fun succeeds() {
    val result = solve()
    if (!result.success) throw AssertionError("failed in $result.endState")
  }

  fun fails() {
    Truth.assertThat(solve().success).isFalse()
  }

  fun failsWith(target: State) {
    val result = solve()
    Truth.assertThat(result.success).isFalse()
    Truth.assertThat(result.endState).isEqualTo(target)
  }

  fun reaches(target: State) {
    val safetyFailingAtTarget = Model.SafetySpec({ s: State -> s != target })
    Truth.assertThat(solve(listOf(safetyFailingAtTarget)).success).isFalse()
  }

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
