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
class DatabaseTableTest {
  companion object Constants {
    val KEY1 = 1
    val KEY2 = 2
    val KEY3 = 3
    val VALUE1 = 11
    val VALUE2 = 22
    val VALUE3 = 33
  }

  @Test
  fun emptyTable() {
    val table = emptyTable<Int, Int>()

    assertThat(table.empty)
  }

  @Test
  fun insert_expectCorrectSize() {
    val table = emptyTable<Int,Int>().insert(KEY1, VALUE1)

    assertThat(table.size).isEqualTo(1)
  }

  @Test
  fun insert_expectCorrectKeys() {
    val table = emptyTable<Int,Int>().insert(KEY1, VALUE1)

    assertThat(table.keys).isEqualTo(setOf(1))
  }

  @Test
  fun insert_expectCorrectValues() {
    val table = emptyTable<Int,Int>().insert(KEY1, VALUE1)

    assertThat(table.values).isEqualTo(setOf(VALUE1))
  }

  @Test
  fun insertTwoRows_expectCorrectSize() {
    val table = emptyTable<Int,Int>().insert(KEY1, VALUE1).insert(KEY2, VALUE2)

    assertThat(table.size).isEqualTo(2)
  }

  @Test
  fun insertTwoRows_expectCorrectKeys() {
    val table = emptyTable<Int,Int>().insert(KEY1, VALUE1).insert(KEY2, VALUE2)

    assertThat(table.keys).isEqualTo(setOf(KEY1, KEY2))
  }

  @Test
  fun insertSameRowTwice_expectCorrectSize() {
    val table = emptyTable<Int,Int>().insert(KEY1, VALUE1).insert(KEY1, VALUE1)

    assertThat(table.size).isEqualTo(1)
  }

  @Test
  fun insertSameRowWithNewValue_expectPreviousValueRetained() {
    val table = emptyTable<Int,Int>().insert(KEY1, VALUE1).insert(KEY1, VALUE2)

    assertThat(table.at(1)).isEqualTo(VALUE1)
  }

  @Test
  fun deleteFromEmpty_expectCorrectSize() {
    val table = emptyTable<Int,Int>().delete(KEY1)

    assertThat(table.size).isEqualTo(0)
  }

  @Test
  fun insertThenDelete_expectCorrectSize() {
    val table = emptyTable<Int,Int>().insert(KEY1,  VALUE1).delete(KEY1)

    assertThat(table.size).isEqualTo(0)
  }

  @Test
  fun insertThenDelete_expectEmpty() {
    val table = emptyTable<Int,Int>().insert(KEY1, VALUE1).delete(KEY1)

    assertThat(table.empty).isEqualTo(true)
  }

  @Test
  fun insertTwoThenDeleteOne_expectCorrectSize() {
    val table = emptyTable<Int,Int>().insert(KEY1, VALUE1).insert(KEY2, VALUE2).delete(KEY1)

    assertThat(table.size).isEqualTo(1)
  }

  @Test
  fun insertTwoThenDeleteOne_expectCorrectKeys() {
    val table = emptyTable<Int,Int>().insert(KEY1, VALUE1).insert(KEY2, VALUE2).delete(KEY1)

    assertThat(table.keys).isEqualTo(setOf(KEY2))
  }

  @Test
  fun upsert_expectCorrectSize() {
    val table = emptyTable<Int,Int>().upsert(KEY1, VALUE1)

    assertThat(table.size).isEqualTo(1)
  }

  @Test
  fun upsert_expectCorrectKeys() {
    val table = emptyTable<Int,Int>().upsert(KEY1, VALUE1)

    assertThat(table.keys).isEqualTo(setOf(KEY1))
  }

  @Test
  fun upsertToUpdate_expectNewValue() {
    val table = emptyTable<Int,Int>().upsert(KEY1, VALUE1).upsert(KEY1, VALUE2)

    assertThat(table.at(KEY1)).isEqualTo(VALUE2)
  }

  @Test
  fun update_expectNewValue() {
    val table = emptyTable<Int,Int>().insert(KEY1, VALUE1).update(KEY1, VALUE2)

    assertThat(table.at(KEY1)).isEqualTo(VALUE2)
  }

  @Test
  fun updateUsingLambdas_expectNewValue() {
    val table = emptyTable<Int,Int>().insert(KEY1, VALUE1).update({it == KEY1},{VALUE2})

    assertThat(table.at(KEY1)).isEqualTo(VALUE2)
  }

  @Test
  fun containsKey_expectPositiveResult() {
    val table = emptyTable<Int,Int>().insert(KEY1, VALUE1)

    assertThat(table.containsKey(KEY1)).isTrue()
  }

  @Test
  fun containsKey_expectNegativeResult() {
    val table = emptyTable<Int,Int>().insert(KEY1, VALUE1)

    assertThat(table.containsKey(KEY2)).isFalse()
  }

  @Test
  fun countIf() {
    val table = emptyTable<Int,Int>().insert(KEY1, VALUE1).insert(KEY2, VALUE2)

    assertThat(table.countIf({k, _ -> k == KEY1})).isEqualTo(1)
  }

  @Test
  fun forEachRow() {
    val table = emptyTable<Int,Int>().insert(KEY1, VALUE1).insert(KEY2, VALUE2)
    var captor = mutableMapOf<Int,Int>()

    table.forEachRow({k, v -> captor.put(k,v)})

    assertThat(captor).isEqualTo(mapOf(KEY1 to VALUE1, KEY2 to VALUE2))
  }

  @Test
  fun select_keyAndValueMatch_expectFound() {
    val table = emptyTable<Int,Int>().insert(KEY1, VALUE1).insert(KEY2, VALUE2)

    assertThat(table.select({it == KEY1}, {it == VALUE1})).isEqualTo(listOf(KEY1))
  }

  @Test
  fun select_noMatch_expectNotFound() {
    val table = emptyTable<Int,Int>().insert(KEY1, VALUE1).insert(KEY2, VALUE2)


    assertThat(table.select({it == KEY1}, {it == VALUE2})).isEmpty()
  }

  @Test
  fun select_keyMatches_expectFound() {
    val table = emptyTable<Int,Int>().insert(KEY1, VALUE1).insert(KEY2, VALUE2)

    assertThat(table.select({it == KEY1})).isEqualTo(listOf(KEY1))
  }

  @Test
  fun select_keyDoesntMatch_expectFound() {
    val table = emptyTable<Int,Int>().insert(KEY1, VALUE1).insert(KEY2, VALUE2)

    assertThat(table.select({it == VALUE2})).isEmpty()
  }

  @Test
  fun tableOf1_expectCorrectSize() {
    val table = tableOf<Int,Int>(KEY1, VALUE1)

    assertThat(table.size).isEqualTo(1)
  }

  @Test
  fun tableOf1_expectCorrectKeys() {
    val table = tableOf<Int,Int>(KEY1, VALUE1)

    assertThat(table.keys).isEqualTo(setOf(KEY1))
  }

  @Test
  fun tableOf2_expectCorrectSize() {
    val table = tableOf<Int,Int>(KEY1, VALUE1, KEY2, VALUE2)

    assertThat(table.size).isEqualTo(2)
  }

  @Test
  fun tableOf2_expectCorrectKeys() {
    val table = tableOf<Int,Int>(KEY1, VALUE1, KEY2, VALUE2)

    assertThat(table.keys).isEqualTo(setOf(KEY1, KEY2))
  }

  @Test
  fun tableOf3_expectCorrectSize() {
    val table = tableOf<Int,Int>(KEY1, VALUE1, KEY2, VALUE2, KEY3, VALUE3)

    assertThat(table.size).isEqualTo(3)
  }

  @Test
  fun tableOf3_expectCorrectKeys() {
    val table = tableOf<Int,Int>(KEY1, VALUE1, KEY2, VALUE2, KEY3, VALUE3)

    assertThat(table.keys).isEqualTo(setOf(KEY1, KEY2, KEY3))
  }
}