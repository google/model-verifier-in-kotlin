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

package com.github.zmatti.mvik.lib

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class DatabaseTableTest {
  @Test
  fun emptyTable() {
    val table = emptyTable<Int, Int>()

    assertThat(table.empty)
  }

  @Test
  fun insert_expectCorrectSize() {
    val table = emptyTable<Int,Int>().insert(1,11)

    assertThat(table.size).isEqualTo(1)
  }

  @Test
  fun insert_expectCorrectKeys() {
    val table = emptyTable<Int,Int>().insert(1,11)

    assertThat(table.keys).isEqualTo(setOf(1))
  }

  @Test
  fun insertTwoRows_expectCorrectSize() {
    val table = emptyTable<Int,Int>().insert(1,11).insert(2,22)

    assertThat(table.size).isEqualTo(2)
  }

  @Test
  fun insertTwoRows_expectCorrectKeys() {
    val table = emptyTable<Int,Int>().insert(1,11).insert(2,22)

    assertThat(table.keys).isEqualTo(setOf(1,2))
  }

  @Test
  fun insertSameRowTwice_expectCorrectSize() {
    val table = emptyTable<Int,Int>().insert(1,11).insert(1,11)

    assertThat(table.size).isEqualTo(1)
  }

  @Test
  fun insertSameRowWithNewValue_expectPreviousValueRetained() {
    val table = emptyTable<Int,Int>().insert(1,11).insert(1,12)

    assertThat(table.at(1)).isEqualTo(11)
  }

  @Test
  fun deleteFromEmpty_expectCorrectSize() {
    val table = emptyTable<Int,Int>().delete(1)

    assertThat(table.size).isEqualTo(0)
  }

  @Test
  fun insertThenDelete_expectCorrectSize() {
    val table = emptyTable<Int,Int>().insert(1,11).delete(1)

    assertThat(table.size).isEqualTo(0)
  }

  @Test
  fun insertThenDelete_expectEmpty() {
    val table = emptyTable<Int,Int>().insert(1,11).delete(1)

    assertThat(table.empty).isEqualTo(true)
  }

  @Test
  fun insertTwoThenDeleteOne_expectCorrectSize() {
    val table = emptyTable<Int,Int>().insert(1,11).insert(2,22).delete(1)

    assertThat(table.size).isEqualTo(1)
  }

  @Test
  fun insertTwoThenDeleteOne_expectCorrectKeys() {
    val table = emptyTable<Int,Int>().insert(1,11).insert(2,22).delete(1)

    assertThat(table.keys).isEqualTo(setOf(2))
  }

  @Test
  fun upsert_expectCorrectSize() {
    val table = emptyTable<Int,Int>().upsert(1,11)

    assertThat(table.size).isEqualTo(1)
  }

  @Test
  fun upsert_expectCorrectKeys() {
    val table = emptyTable<Int,Int>().upsert(1,11)

    assertThat(table.keys).isEqualTo(setOf(1))
  }

  @Test
  fun upsertToUpdate_expectNewValue() {
    val table = emptyTable<Int,Int>().upsert(1,11).upsert(1,12)

    assertThat(table.at(1)).isEqualTo(12)
  }

  @Test
  fun containsKey_expectPositiveResult() {
    val table = emptyTable<Int,Int>().insert(1,11)

    assertThat(table.containsKey(1)).isTrue()
  }

  @Test
  fun containsKey_expectNegativeResult() {
    val table = emptyTable<Int,Int>().insert(1,11)

    assertThat(table.containsKey(2)).isFalse()
  }

  @Test
  fun countIf() {
    val table = emptyTable<Int,Int>().insert(1,11).insert(2,12)

    assertThat(table.countIf({k, _ -> k == 1})).isEqualTo(1)
  }
}