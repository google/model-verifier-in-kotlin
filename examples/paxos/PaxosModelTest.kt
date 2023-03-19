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

package com.github.zmatti.mvik.examples.paxos

import com.github.zmatti.mvik.testing.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PaxosModelTest {
  @Test
  fun succeeds() {
    assertThat(PaxosModel()).succeeds()
  }

  @Test
  fun reachesPrepareMessage() {
    assertThat(PaxosModel()).reaches({ it.msgs.any { msg -> msg is Message.Prepare } })
  }

  @Test
  fun reachesPromiseMessage() {
    assertThat(PaxosModel()).reaches({ it.msgs.any { msg -> msg is Message.Promise } })
  }

  @Test
  fun reachesAcceptMessage() {
    assertThat(PaxosModel()).reaches({ it.msgs.any { msg -> msg is Message.Accept } })
  }

  @Test
  fun reachesAcceptedMessage() {
    assertThat(PaxosModel()).reaches({ it.msgs.any { msg -> msg is Message.Accepted } })
  }

  @Test
  fun reachesPromisedAcceptors() {
    assertThat(PaxosModel()).reaches({ it.acceptors.all { entry -> entry.value.hasPromised() } })
  }

  @Test
  fun reachesAcceptedAcceptors() {
    assertThat(PaxosModel()).reaches({ it.acceptors.all { entry -> entry.value.hasAccepted() } })
  }

  @Test
  fun reachesConvergence() {
    assertThat(PaxosModel())
      .reaches({ it.acceptors.all { entry -> entry.value == AcceptorState(1, 1, 101) } })
  }
}
