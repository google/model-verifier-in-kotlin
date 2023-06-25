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

package com.google.mvik.examples.diehard

import com.google.mvik.framework.Init
import com.google.mvik.framework.Model
import com.google.mvik.framework.Safety
import com.google.mvik.framework.Step

data class DieHardState(val jug5: Int, val jug3: Int) {}

/** Can you measure four gallons with 3-gallon jug, 5-gallon jug, and a faucet? */
class DieHardModel : Model<DieHardState>() {

  @Init
  fun startBothJugsEmpty(): DieHardState {
    return DieHardState(0, 0)
  }

  @Step
  fun fill3(s: DieHardState): DieHardState {
    return DieHardState(s.jug5, 3)
  }

  @Step
  fun fill5(s: DieHardState): DieHardState {
    return DieHardState(3, s.jug3)
  }

  @Step
  fun empty3(s: DieHardState): DieHardState {
    return DieHardState(s.jug5, 0)
  }

  @Step
  fun empty5(s: DieHardState): DieHardState {
    return DieHardState(0, s.jug3)
  }

  @Step
  fun pour3to5(s: DieHardState): DieHardState {
    if (s.jug5 + s.jug3 <= 5) {
      return DieHardState(s.jug5 + s.jug3, 0)
    } else {
      val amount = 5 - s.jug5
      return DieHardState(5, s.jug3 - amount)
    }
  }

  @Step
  fun pour5to3(s: DieHardState): DieHardState {
    if (s.jug5 + s.jug3 <= 3) {
      return DieHardState(0, s.jug5 + s.jug3)
    } else {
      val amount = 3 - s.jug3
      return DieHardState(s.jug5 - amount, 3)
    }
  }

  @Safety
  fun jug5DoesntContain4Gallos(s: DieHardState): Boolean {
    return s.jug5 != 4
  }

  @Safety
  fun jug3WithinSize(s: DieHardState): Boolean {
    return s.jug3 <= 3
  }
}
