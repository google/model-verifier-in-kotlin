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

package com.github.zmatti.mvik.examples.counter.sync

import com.github.zmatti.mvik.framework.Init
import com.github.zmatti.mvik.framework.Model
import com.github.zmatti.mvik.framework.Safety
import com.github.zmatti.mvik.framework.Step
import com.github.zmatti.mvik.lang.collecting
import com.github.zmatti.mvik.lib.emptyTable

object CounterConstants {
  val FOLDERS = setOf(1, 2, 3)
  val ITEMS = setOf(11, 12, 13)
  val ITEM_VALUE = 111
}

class CounterModel : Model<CounterState>() {
  
  @Init
  fun startEmpty() : CounterState {
    return CounterState(emptyTable(), emptyTable())
  }

  @Step
  fun insertRow(s: CounterState) : Set<CounterState> = collecting {
    for (folder in CounterConstants.FOLDERS) {
      for (item in CounterConstants.ITEMS) {
        val newKey = ItemsKey(folder, item)
        if (!s.items.keys.contains(newKey)) {
          emit(CounterState(
            s.items.insert(newKey, CounterConstants.ITEM_VALUE),
            s.counts.upsert(newKey.folder, (s.counts.at(newKey.folder) ?: 0) + 1)))
        }
      }
    }
  }

  @Step
  fun deleteRow(s: CounterState) : Set<CounterState> = collecting {
    for (folder in CounterConstants.FOLDERS) {
      for (item in CounterConstants.ITEMS) {
        val newKey = ItemsKey(folder, item)
        if (s.items.keys.contains(newKey)) {
          emit(CounterState(
            s.items.delete(newKey),
            s.counts.upsert(newKey.folder, s.counts.at(newKey.folder)!! - 1)))
        }
      }
    }
  }

  @Safety
  fun countsMatch(s: CounterState) : Boolean {
    for (folder in CounterConstants.FOLDERS) {
      val numItemsInFolder = s.items.countIf({k, _ -> k.folder == folder})
      val countInCountsTable = s.counts.at(folder) ?: 0
      if (numItemsInFolder != countInCountsTable) {
        return false
      }
    }
    return true
  }
}