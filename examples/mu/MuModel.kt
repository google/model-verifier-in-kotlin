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

package com.google.mvik.examples.mu

import com.google.mvik.framework.Boundary
import com.google.mvik.framework.Init
import com.google.mvik.framework.Model
import com.google.mvik.framework.Safety
import com.google.mvik.framework.Step
import com.google.mvik.lang.collecting

data class MuState(val d: String) {

  val last: Char
    get() = d.last()

  val length: Int
    get() = d.length

  fun substring(index: Int): String {
    return d.substring(index)
  }

  fun substring(index1: Int, index2: Int): String {
    return d.substring(index1, index2)
  }

  fun append(s: String): MuState {
    return MuState(d + s)
  }
}

/**
 * Hofstadter Mu puzzle (https://en.wikipedia.org/wiki/MU_puzzle).
 *
 * Start with string "MI". Follow four rules:
 * 1) For string ending in I, apppend U.
 * 2) Double the string after the M.
 * 3) Replace "III" with "U"
 * 4) Remove "UU"
 *
 * The puzzle says "MU" can't be reached.
 */
class MuModel : Model<MuState>() {

  @Init
  fun mi(): MuState {
    return MuState("MI")
  }

  @Step
  fun appendU(s: MuState): MuState? {
    if (s.last == 'I') {
      return s.append("U")
    }
    return null
  }

  @Step
  fun doubleAfterM(s: MuState): MuState {
    val afterM = s.substring(1)
    return s.append(afterM)
  }

  @Step
  fun squashTripleI(s: MuState): Set<MuState> = collecting {
    for (i in 0..s.length - 3) {
      if (s.substring(i, i + 3) == "III") {
        emit(MuState(s.substring(0, i) + "U" + s.substring(i + 3, s.length)))
      }
    }
  }

  @Step
  fun squashDoubleU(s: MuState): Set<MuState> = collecting {
    for (i in 0..s.length - 2) {
      if (s.substring(i, i + 2) == "UU") {
        emit(MuState(s.substring(0, i) + s.substring(i + 2, s.length)))
      }
    }
  }

  @Boundary
  fun limitedLength(s: MuState): Boolean {
    return s.length < 8
  }

  @Safety
  fun expectedLetters(s: MuState): Boolean {
    if (s.d[0] != 'M') return false
    for (i in 1..s.length - 1) {
      if (s.d[i] != 'I' && s.d[i] != 'U') return false
    }
    return true
  }

  @Safety
  fun noMu(s: MuState): Boolean {
    return s != MuState("MU")
  }
}
