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

package com.google.mvik.examples.counter.sync

import com.google.mvik.lib.tableOf
import com.google.mvik.testing.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CounterModelTest {

  @Test 
  fun succeeds() {
    assertThat(CounterModel()).succeeds()
  }

  @Test
  fun reachesInsertedItems() {
    assertThat(CounterModel()).reaches({it.items.containsKey(ItemsKey(1,11))})
  }

  @Test
  fun reachesNonZeroCount() {
    assertThat(CounterModel()).reaches({it.counts.at(1) == 1})
  }

  @Test
  fun reachesIncrementedCount() {
    assertThat(CounterModel()).reaches({it.counts.at(1) == 2})
  }

  @Test
  fun reachesEmptyFromNonEmpty() {
    val init = CounterState(tableOf(ItemsKey(1,11), 123), tableOf(1, 1))
    assertThat(CounterModel()).startingFrom(init).reaches({it.items.empty})
  }

  @Test
  fun reachesZeroCountFromNonEmpty() {
    val init = CounterState(tableOf(ItemsKey(1,11), 123), tableOf(1, 1))
    assertThat(CounterModel()).startingFrom(init).reaches({it.counts.at(1) == 0})
  }
}