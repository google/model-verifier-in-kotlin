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

package com.github.zmatti.mvik.examples.eightqueens

import com.github.zmatti.mvik.framework.Init
import com.github.zmatti.mvik.framework.Model
import com.github.zmatti.mvik.framework.Safety
import com.github.zmatti.mvik.framework.Step
import com.github.zmatti.mvik.lang.collecting

/*
 * The state is effectively a list, where j at location i means there's a queen at row i, col j.
 */
data class EightQueensState(val locations: List<Int>) {
  fun place(newCol: Int): EightQueensState {
    return EightQueensState(locations + newCol)
  }
}

object EightQueensConstants {
  const val BOARD_SIZE = 8
}

/** Can you place 8 queens on a chess board so that none attack each other? */
class EightQueensModel : Model<EightQueensState>() {

  @Init
  fun noQueensPlaced(): EightQueensState {
    return EightQueensState(listOf())
  }

  @Step
  fun placeQueen(s: EightQueensState): Set<EightQueensState> = collecting {
    val numPrevious = s.locations.size
    for (i in 0..EightQueensConstants.BOARD_SIZE - 1) {
      val tentativeNewState: EightQueensState = s.place(i)
      if (!(0..numPrevious - 1).any({ attacks(tentativeNewState, numPrevious, it) })) {
        emit(tentativeNewState)
      }
    }
  }

  @Safety
  fun noSolution(s: EightQueensState): Boolean {
    return !isSolution(s)
  }

  private fun isSolution(s: EightQueensState): Boolean {
    if (s.locations.size != EightQueensConstants.BOARD_SIZE) return false
    for (i in 0..EightQueensConstants.BOARD_SIZE - 1) {
      for (j in i + 1..EightQueensConstants.BOARD_SIZE - 1) {
        if (attacks(s, i, j)) return false
      }
    }
    return true
  }

  private fun attacks(s: EightQueensState, i: Int, j: Int): Boolean {
    return s.locations[i] == s.locations[j] ||
      s.locations[i] - s.locations[j] == i - j ||
      s.locations[i] - s.locations[j] == j - i
  }
}
