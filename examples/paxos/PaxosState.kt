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

package com.google.mvik.examples.paxos

/** The state corresponding to a single acceptor (participant). */
data class AcceptorState(
  val maxBallot: Int? = null,
  val maxVBallot: Int? = null,
  val maxValue: Int? = null
) {
  fun withNewMaxPromise(newMaxBallot: Int): AcceptorState {
    return AcceptorState(newMaxBallot, maxVBallot, maxValue)
  }

  fun hasPromised(): Boolean = maxBallot != null

  fun hasAccepted(): Boolean = maxBallot != null && maxVBallot != null && maxValue != null
}

/** There are four kinds of messages in Paxos, corresponding to stages 1a, 2b, 2a, 2b. */
sealed class Message {
  data class Prepare(val ballot: Int) : Message() {}
  data class Promise(
    val ballot: Int,
    val acceptor: Int,
    val maxBallot: Int?,
    val maxVBallot: Int?
  ) : Message() {}
  data class Accept(val ballot: Int, val value: Int) : Message() {}
  data class Accepted(val ballot: Int, val value: Int, val acceptor: Int) : Message() {}
}

/** Overall state of the systems is all messages ever sent, plus the state of each participant. */
data class PaxosState(val acceptors: Map<Int, AcceptorState>, val msgs: Set<Message>) {
  fun send(m: Message): PaxosState {
    return PaxosState(acceptors, msgs + m)
  }

  fun updateAcceptor(acceptorIndex: Int, newState: AcceptorState): PaxosState {
    return updateAcceptor(acceptorIndex, { _ -> newState })
  }

  fun accept(acceptorIndex: Int, ballot: Int): PaxosState {
    return updateAcceptor(acceptorIndex, { a -> a.withNewMaxPromise(ballot) })
  }

  private fun updateAcceptor(
    acceptorIndex: Int,
    newStateFun: (AcceptorState) -> AcceptorState
  ): PaxosState {
    var newAcceptors = mutableMapOf<Int, AcceptorState>()
    acceptors.forEach { entry ->
      if (entry.key == acceptorIndex) {
        newAcceptors.put(entry.key, newStateFun(entry.value))
      } else {
        newAcceptors.put(entry.key, entry.value)
      }
    }
    return PaxosState(newAcceptors.toMap(), msgs)
  }

  fun containsMessageMatching(pred: (Message) -> Boolean): Boolean = msgs.any { pred(it) }
}
