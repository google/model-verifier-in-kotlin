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

package com.google.mvik.examples.readerswriters

import com.google.mvik.framework.Init
import com.google.mvik.framework.Model
import com.google.mvik.framework.Safety
import com.google.mvik.framework.Step
import com.google.mvik.lang.collecting
import com.google.mvik.lib.head
import com.google.mvik.lib.isSubsetOf
import com.google.mvik.lib.tail

object ReadersWritersConstants {
  val ACTORS = setOf(1, 2, 3, 4)
}

enum class Action {
  READ,
  WRITE
}

data class ActorAndAction(val actor: Int, val action: Action) {}

data class ReadersWritersState(
  val readers: Set<Int>,
  val writers: Set<Int>,
  val waiting: List<ActorAndAction>
) {}

/** From https://en.wikipedia.org/wiki/Readers–writers_problem */
class ReadersWritersModel : Model<ReadersWritersState>() {

  @Init
  fun startEmpty(): ReadersWritersState {
    return ReadersWritersState(setOf(), setOf(), listOf())
  }

  @Step
  fun tryRead(s: ReadersWritersState): Set<ReadersWritersState> = collecting {
    for (actor in ReadersWritersConstants.ACTORS subtract actorsWaiting(s, Action.READ)) {
      emit(
        ReadersWritersState(s.readers, s.writers, s.waiting + ActorAndAction(actor, Action.READ))
      )
    }
  }

  @Step
  fun tryWrite(s: ReadersWritersState): Set<ReadersWritersState> = collecting {
    for (actor in ReadersWritersConstants.ACTORS subtract actorsWaiting(s, Action.WRITE)) {
      emit(
        ReadersWritersState(s.readers, s.writers, s.waiting + ActorAndAction(actor, Action.WRITE))
      )
    }
  }

  @Step
  fun readOrWrite(s: ReadersWritersState): ReadersWritersState? {
    if (s.waiting.isEmpty()) return null
    if (!s.writers.isEmpty()) return null

    val actorAndAction = s.waiting.head
    val actor = actorAndAction.actor
    val action = actorAndAction.action

    if (action == Action.READ) {
      return read(s, actor)
    } else {
      return write(s, actor)
    }
  }

  @Step
  fun stopA(s: ReadersWritersState): Set<ReadersWritersState> = collecting {
    for (actor in s.readers union s.writers) {
      emit(stopActivity(s, actor))
    }
  }

  @Safety
  fun typeOk(s: ReadersWritersState): Boolean {
    return s.writers isSubsetOf ReadersWritersConstants.ACTORS &&
      s.readers isSubsetOf ReadersWritersConstants.ACTORS
  }

  @Safety
  fun eitherReadersOrWriters(s: ReadersWritersState): Boolean {
    return s.readers.isEmpty() || s.writers.isEmpty()
  }

  @Safety
  fun atMostOneWriter(s: ReadersWritersState): Boolean {
    return s.writers.size <= 1
  }

  private fun actorsWaiting(s: ReadersWritersState, a: Action): Set<Int> = collecting {
    for (actorAndAction in s.waiting) {
      if (actorAndAction.action == a) {
        emit(actorAndAction.actor)
      }
    }
  }

  private fun read(s: ReadersWritersState, actor: Int): ReadersWritersState {
    return ReadersWritersState(s.readers + setOf(actor), s.writers, s.waiting.tail)
  }

  private fun write(s: ReadersWritersState, actor: Int): ReadersWritersState? {
    if (!s.readers.isEmpty()) return null
    return ReadersWritersState(s.readers, s.writers union setOf(actor), s.waiting.tail)
  }

  private fun stopActivity(s: ReadersWritersState, actor: Int): ReadersWritersState {
    if (actor in s.readers) {
      return ReadersWritersState(s.readers subtract setOf(actor), s.writers, s.waiting)
    } else {
      return ReadersWritersState(s.readers, s.writers subtract setOf(actor), s.waiting)
    }
  }
}
