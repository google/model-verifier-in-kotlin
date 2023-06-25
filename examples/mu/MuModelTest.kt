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

import com.google.mvik.testing.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MuModelTest {

  @Test
  fun succeeds() {
    assertThat(MuModel()).succeeds()
  }

  @Test
  fun reachesMiu() {
    assertThat(MuModel()).reaches(MuState("MIU"))
  }

  @Test
  fun reachesMiuiuFromMiu() {
    assertThat(MuModel()).startingFrom(MuState("MIU")).reaches(MuState("MIUIU"))
  }

  @Test
  fun reachesMuuuFromMuiiiu() {
    assertThat(MuModel()).startingFrom(MuState("MUIIIU")).reaches(MuState("MUUU"))
  }

  @Test
  fun reachesMuFromMuuu() {
    assertThat(MuModel()).startingFrom(MuState("MUUU")).reaches(MuState("MU"))
  }

  @Test
  fun failsFromMuuu() {
    assertThat(MuModel()).startingFrom(MuState("MUUU")).fails()
  }
}
