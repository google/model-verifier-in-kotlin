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

package com.google.mvik.lib

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class SetsTest {

  @Test
  fun subsets_size2_expectCorrectSubsets() {
    val input = setOf(1, 2)
    val expectation = setOf(setOf(1), setOf(2), setOf(1, 2))

    assertThat(input.subsets()).isEqualTo(expectation)
  }

  @Test
  fun subsets_size3_expectCorrectSubsets() {
    val input = setOf(1, 2, 3)
    val expectation =
      setOf(setOf(1), setOf(2), setOf(3), setOf(1, 2), setOf(1, 3), setOf(2, 3), setOf(1, 2, 3))

    assertThat(input.subsets()).isEqualTo(expectation)
  }

  @Test
  fun subsets_size4_expectCorrectSubsets() {
    val input = setOf(1, 2, 3, 4)
    val expectation =
      setOf(
        setOf(1),
        setOf(2),
        setOf(3),
        setOf(4),
        setOf(1, 2),
        setOf(1, 3),
        setOf(1, 4),
        setOf(2, 3),
        setOf(2, 4),
        setOf(3, 4),
        setOf(1, 2, 3),
        setOf(1, 2, 4),
        setOf(1, 3, 4),
        setOf(2, 3, 4),
        setOf(1, 2, 3, 4)
      )

    assertThat(input.subsets()).isEqualTo(expectation)
  }
}
