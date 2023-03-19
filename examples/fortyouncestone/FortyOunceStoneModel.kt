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

package com.github.zmatti.mvik.examples.fortyouncestone

import com.github.zmatti.mvik.framework.Init
import com.github.zmatti.mvik.framework.Model
import com.github.zmatti.mvik.framework.Safety
import com.github.zmatti.mvik.framework.Step
import com.github.zmatti.mvik.lang.assuming
import com.github.zmatti.mvik.lang.collecting
import com.github.zmatti.mvik.lib.subsets

data class FortyOunceStoneState(val pieces: Set<Int>) {}

/**
 * The problem is whether a 40-ounce stone can be split into integer-ounce parts so that all
 * integer-ounce weights up to 40 ounces can be measured using the stones and a scale.
 */
class FortyOunceStoneModel : Model<FortyOunceStoneState>() {

  @Init
  fun onePieceOf40(): FortyOunceStoneState {
    return FortyOunceStoneState(setOf(40))
  }

  @Step
  fun split(s: FortyOunceStoneState): Set<FortyOunceStoneState> =
    assuming<FortyOunceStoneState> { s.pieces.size < 4 } collecting
      {
        for (piece in s.pieces) {
          for (left in 1..piece - 1) {
            val right = piece - left
            emit(FortyOunceStoneState(s.pieces subtract setOf(piece) union setOf(left, right)))
          }
        }
      }

  @Safety
  fun noSolution(s: FortyOunceStoneState): Boolean {
    return !isSolution(s)
  }

  private fun isSolution(s: FortyOunceStoneState): Boolean {
    for (i in 1..40) {
      if (!isRepresentation(s.pieces, i)) return false
    }
    return true
  }

  private fun isRepresentation(pieces: Set<Int>, target: Int): Boolean {
    for (ss in pieces.subsets()) {
      val left = ss.sum()
      if (left == target) return true

      for (sss in (pieces subtract ss).subsets()) {
        val right = sss.sum()
        if (left - right == target || right - left == target) return true
      }
    }
    return false
  }
}
