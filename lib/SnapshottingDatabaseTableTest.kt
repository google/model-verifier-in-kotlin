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
class SnapshottingDatabaseTableTest {
  @Test
  fun emptyTable_expectEmpty() {
    val table = emptySnapshottingTable<Int, Int>()

    assertThat(table.empty).isTrue()
  }

  @Test
  fun emptyTable_atSnapshot_expectEmpty() {
    val table = emptySnapshottingTable<Int, Int>()

    assertThat(table.empty(1)).isTrue()
  }

  @Test
  fun emptyTable_expectNoKeys() {
    val table = emptySnapshottingTable<Int, Int>()

    assertThat(table.keys()).isEmpty()
  }

  @Test
  fun emptyTable_atSnapshot_expectNoKeys() {
    val table = emptySnapshottingTable<Int, Int>()

    assertThat(table.keys(1)).isEmpty()
  }

  @Test
  fun insertOneRow_expectCorrectKeys() {
    val table = emptySnapshottingTable<Int, Int>().insert(10, 100, 1)

    assertThat(table.keys()).isEqualTo(setOf(10))
  }

  @Test
  fun insertOneRow_atSnapshotJustBefore_expectNoKeys() {
    val table = emptySnapshottingTable<Int, Int>().insert(10, 100, 1)

    assertThat(table.keys(0)).isEmpty()
  }

  @Test
  fun insertOneRow_atExactSnapshot_expectNoKeys() {
    val table = emptySnapshottingTable<Int, Int>().insert(10, 100, 1)

    assertThat(table.keys(1)).isEqualTo(setOf(10))
  }

  @Test
  fun insertOneRow_atLaterSnapshot_expectNoKeys() {
    val table = emptySnapshottingTable<Int, Int>().insert(10, 100, 1)

    assertThat(table.keys(1)).isEqualTo(setOf(10))
  }

  @Test
  fun insertOneRow_expectCorrectValue() {
    val table = emptySnapshottingTable<Int, Int>().insert(10, 100, 1)

    assertThat(table.at(10)).isEqualTo(100)
  }

  @Test
  fun insertOneRow_atSnapshotJustBefore_expectCorrectValue() {
    val table = emptySnapshottingTable<Int, Int>().insert(10, 100, 1)

    assertThat(table.at(10, 0)).isNull()
  }

  @Test
  fun insertOneRow_atExactSnapshot_expectCorrectValue() {
    val table = emptySnapshottingTable<Int, Int>().insert(10, 100, 1)

    assertThat(table.at(10, 1)).isEqualTo(100)
  }

  @Test
  fun insertOneRow_atLaterSnapshot_expectCorrectValue() {
    val table = emptySnapshottingTable<Int, Int>().insert(10, 100, 1)

    assertThat(table.at(10, 2)).isEqualTo(100)
  }

  @Test
  fun insertOneRowThenUpdate_expectCorrectValue() {
    val table = emptySnapshottingTable<Int, Int>().insert(10, 100, 1).insert(10, 101, 3)

    assertThat(table.at(10)).isEqualTo(101)
  }

  @Test
  fun insertOneRowThenUpdate_atSnapshotBeforeUpdate_expectFirstValue() {
    val table = emptySnapshottingTable<Int, Int>().insert(10, 100, 1).insert(10, 101, 3)

    assertThat(table.at(10, 2)).isEqualTo(100)
  }

  @Test
  fun insertOneRowThenDelete_expectNoKeys() {
    val table = emptySnapshottingTable<Int, Int>().insert(10, 100, 1).delete(10, 3)

    assertThat(table.keys()).isEmpty()
  }

  @Test
  fun insertOneRowThenDelete_atSnapshotBeforeDelete_expectPreviousKeys() {
    val table = emptySnapshottingTable<Int, Int>().insert(10, 100, 1).delete(10, 3)

    assertThat(table.keys(2)).isEqualTo(setOf(10))
  }

  @Test
  fun insertOneRowThenDelete_expectNoValue() {
    val table = emptySnapshottingTable<Int, Int>().insert(10, 100, 1).delete(10, 3)

    assertThat(table.at(10)).isNull()
  }

  @Test
  fun insertOneRowThenDelete_atSnapshotBeforeDelete_expectPreviousValue() {
    val table = emptySnapshottingTable<Int, Int>().insert(10, 100, 1).delete(10, 3)

    assertThat(table.at(10,2)).isEqualTo(100)
  }

  @Test
  fun insertTwoRows_expectCorrectKeys() {
    val table = emptySnapshottingTable<Int, Int>().insert(10, 100, 1).insert(20, 200, 1)

    assertThat(table.keys()).isEqualTo(setOf(10, 20))
  }

  @Test
  fun insertTwoRowsThenDeleteOne_expectCorrectKeys() {
    val table = emptySnapshottingTable<Int, Int>().insert(10, 100, 1).insert(20, 200, 1).delete(10, 2)

    assertThat(table.keys()).isEqualTo(setOf(20))
  }

  @Test
  fun insertTwoRowsThenDeleteOne_expectCorrectValueForRemainingRow() {
    val table = emptySnapshottingTable<Int, Int>().insert(10, 100, 1).insert(20, 200, 1).delete(10, 2)

    assertThat(table.at(20)).isEqualTo(200)
  }

  @Test
  fun insertTwoRowsThenDeleteOne_expectNoValueForDeletedRow() {
    val table = emptySnapshottingTable<Int, Int>().insert(10, 100, 1).insert(20, 200, 1).delete(10, 2)

    assertThat(table.at(10)).isNull()
  }

  @Test
  fun insertDeleteInsert_expectCorrectKeys() {
    val table = emptySnapshottingTable<Int, Int>().insert(10, 100, 1).delete(10, 2).insert(10, 103, 3)

    assertThat(table.keys()).isEqualTo(setOf(10))
  }

  @Test
  fun insertDeleteInsert_expectCorrectValue() {
    val table = emptySnapshottingTable<Int, Int>().insert(10, 100, 1).delete(10, 2).insert(10, 103, 3)

    assertThat(table.at(10)).isEqualTo(103)
  }
}