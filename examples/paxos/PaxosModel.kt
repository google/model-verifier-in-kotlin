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

import com.github.zmatti.mvik.framework.Init
import com.github.zmatti.mvik.framework.Model
import com.github.zmatti.mvik.framework.Safety
import com.github.zmatti.mvik.framework.Step
import com.github.zmatti.mvik.lang.assuming
import com.github.zmatti.mvik.lang.collecting
import com.github.zmatti.mvik.lib.subsets

object PaxosConstants {
  val BALLOTS = setOf(1, 2, 3)
  val ACCEPTORS = setOf(0, 1, 2)
  val VALUES = setOf(101, 280)
}

/** An attempt at specifying a very basic version of the Paxos algorithm. */
class PaxosModel : Model<PaxosState>() {

  @Init
  fun startEmpty(): PaxosState {
    return PaxosState(PaxosConstants.ACCEPTORS.associate { Pair(it, AcceptorState()) }, setOf())
  }

  @Step
  fun prepare(s: PaxosState): Set<PaxosState> = collecting {
    for (ballot in PaxosConstants.BALLOTS) {
      emit(prepare(s, ballot))
    }
  }

  private fun prepare(s: PaxosState, ballot: Int): PaxosState {
    return s.send(Message.Prepare(ballot))
  }

  @Step
  fun promise(s: PaxosState): Set<PaxosState> = collecting {
    for (acceptor in PaxosConstants.ACCEPTORS) {
      emit(promise(s, acceptor))
    }
  }

  private fun promise(s: PaxosState, acceptor: Int): Set<PaxosState> = collecting {
    for (msg in s.msgs) {
      if (msg is Message.Prepare) {
        val maxBallot = s.acceptors[acceptor]!!.maxBallot
        if (maxBallot == null || msg.ballot > maxBallot) {
          emit(
            s.accept(acceptor, msg.ballot).send(Message.Promise(msg.ballot, acceptor, null, null))
          )
        }
      }
    }
  }

  @Step
  fun accept(s: PaxosState): Set<PaxosState> = collecting {
    for (ballot in PaxosConstants.BALLOTS) {
      for (value in PaxosConstants.VALUES) {
        emit(accept(s, ballot, value))
      }
    }
  }

  private fun accept(s: PaxosState, ballot: Int, value: Int): Set<PaxosState>

  // If an accept message for this ballot hasn't been sent
  =
    assuming<PaxosState> { !s.msgs.any { it is Message.Accept && it.ballot == ballot } } collecting
      {
        for (quorum in quorums()) {
          // All promise messages for this ballot from this quorum
          val promises =
            s.msgs
              .filter({
                it is Message.Promise && quorum.contains(it.acceptor) && it.ballot == ballot
              })
              .map({ it as Message.Promise })
          // Subset of the promise messages that communicate a previously accepted ballot number
          val promisesWithMaxBallots = promises.filter { it.maxBallot != null }

          // If each acceptor in Quorum has sent a promise message
          if (quorum.all({ acceptor -> promises.any { it.acceptor == acceptor } })) {
            // Proceed in either of two conditions:
            // (a) No acceptor in quorum communicated a previously accepted ballot, or
            // (b) At least one acceptor in quorum communicated a previously accepted ballot with
            //     value equal to that in the current ballot, and that that acceptor has the largest
            //     previously accepted ballot.
            val conditionA = promisesWithMaxBallots.isEmpty()
            val conditionB =
              promisesWithMaxBallots.any({ lhs ->
                lhs.maxVBallot == value &&
                  promisesWithMaxBallots.all({ rhs ->
                    rhs.maxBallot != null && lhs.maxBallot != null && rhs.maxBallot <= lhs.maxBallot
                  })
              })

            if (conditionA || conditionB) {
              emit(s.send(Message.Accept(ballot, value)))
            }
          }
        }
      }

  private fun quorums(): List<List<Int>> =
    PaxosConstants.ACCEPTORS.subsets().filter({ it.size >= 2 }).map({ it.toList() })

  @Step
  fun accepted(s: PaxosState): Set<PaxosState> = collecting {
    for (acceptor in PaxosConstants.ACCEPTORS) {
      emit(accepted(s, acceptor))
    }
  }

  private fun accepted(s: PaxosState, acceptor: Int): Set<PaxosState> = collecting {
    for (msg in s.msgs) {
      if (msg is Message.Accept) {
        val acceptorMaxBallot = s.acceptors[acceptor]!!.maxBallot
        if (acceptorMaxBallot == null || msg.ballot >= acceptorMaxBallot) {
          emit(
            s.updateAcceptor(acceptor, AcceptorState(msg.ballot, msg.ballot, msg.value))
              .send(Message.Accepted(msg.ballot, msg.value, acceptor))
          )
        }
      }
    }
  }

  data class Vote(val ballot: Int, val value: Int) {}

  /**
   * Votes are recorded in the ACCEPTED messages.
   *
   * This method extracts them to an array so that we can easily use "votes" in the invariants.
   */
  private fun votes(s: PaxosState): List<List<Vote>> {
    var votes = mutableListOf<List<Vote>>()
    for (acceptorIndex in PaxosConstants.ACCEPTORS) {
      votes.add(
        s.msgs
          .filter({ it is Message.Accepted && it.acceptor == acceptorIndex })
          .map({ it as Message.Accepted })
          .map({ Vote(it.ballot, it.value) })
      )
    }
    return votes.toList()
  }

  @Safety
  fun acceptorStateConverged(s: PaxosState): Boolean {
    val votes = votes(s)
    for (acceptorIndex in PaxosConstants.ACCEPTORS) {
      val acceptor = s.acceptors[acceptorIndex]!!
      if (acceptor.maxVBallot == null) {
        if (acceptor.maxValue != null) return false
      } else {
        if (acceptor.maxValue == null) return false
        if (!(Vote(acceptor.maxVBallot, acceptor.maxValue) in votes[acceptorIndex])) return false
      }
    }
    return true
  }
}
