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

package com.github.zmatti.mvik.lang

/** This file contains structure of the model definition DSL. */
class Lang {}

fun <State : Any> collecting(init: StateReceiver<State>.() -> Unit): Set<State> {
  val receiver = StateReceiver<State>()
  receiver.init()
  return receiver.stateSet.toSet()
}

class StateReceiver<State : Any>(var stateSet: MutableSet<State> = mutableSetOf<State>()) {
  fun emit(s: State) {
    stateSet.add(s)
  }

  fun emit(ss: Set<State>) {
    for (s in ss) stateSet.add(s)
  }
}

fun <State : Any> assuming(cond: () -> Boolean): ConditionLhs<State> {
  return ConditionLhs<State>(cond())
}

class ConditionLhs<State : Any>(val cond: Boolean) {
  infix fun collecting(init: StateReceiver<State>.() -> Unit): Set<State> {
    val receiver = StateReceiver<State>()
    if (cond) receiver.init()
    return receiver.stateSet.toSet()
  }
}
