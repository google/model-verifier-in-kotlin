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

package com.google.mvik.framework

import java.util.LinkedList
import java.util.Queue
import kotlin.reflect.KCallable
import kotlin.reflect.KClass

/**
 * Base class for models.
 * 
 * A model first needs a definition of state as a Kotlin data class. The states are global,
 * but composition can be used to divide states into states of subsystems when required. The
 * data class corresponding to the state of the model is the type parameter "State".
 * 
 * To implement a model, extend this class, and provide the building block of the model
 * as annotated methods. Use @Init for defining the mandatory initial state. Use @Step to
 * define state changes. There can be as many or as few steps as you'd like, but useful
 * models always contain at least one. Use @Safety to define satefy properties of the model.
 * These are boolean expressions on states ie. the invariants that should hold regardless
 * of which state transitions defined in the steps are executed in which. You can define
 * as many safety properties as you'd like. Models that can be meaningfully verified have
 * at least one safety property (since otherwise there's nothing to verify).
 * 
 * The state space can also be limited in size using @Boundary. Boundaries aren't parts of
 * the model as such, but a mechanism to keep the state-space size and therefore the 
 * time-to-verify in check.
 * 
 * A model is verified by calling solve(). It can return a success, in which case an 
 * exhaustive search of the state space starting from the initial state using the defined
 * steps didn't find a state that breaks any safety property. It can also return a failure,
 * in which case a state breaking a safety property was found. The results contains in that
 * case data indicating the sequence of steps leading to the failure. See comments on the
 * Result class for more details.
 * 
 * Models can be unit tested using ModelClassSubject.
 */
abstract class Model<State : Any> {
  fun solve(): Result<State> {
    return solve(init(), safeties())
  }

  fun solveFrom(initStates: List<State>): Result<State> {
    return solve(initStates, safeties())
  }

  fun solveWithSafeties(safetyProperties: List<SafetySpec<State>>): Result<State> {
    return solve(init(), safetyProperties)
  }

  fun solve(initStates: List<State>, safetyProperties: List<SafetySpec<State>>): Result<State> {
    var visited = mutableSetOf<State>()
    var toVisit: Queue<StateAndChain<State>> = LinkedList<StateAndChain<State>>()
    for (state in initStates) toVisit.add(StateAndChain(state, listOf()))
    visited.addAll(initStates)

    while (!toVisit.isEmpty()) {
      val currentStateAndChain = toVisit.poll()
      val currentState = currentStateAndChain.state

      if (safetyViolated(currentState, safetyProperties)) {
        return failure(Result.Stats(visited.size), currentState, currentStateAndChain.chain)
      }

      for (step in steps()) {
        for (nextState in step.generator(currentState)) {
          if (withinBoundaries(nextState) && !visited.contains(nextState)) {
            toVisit.add(
              StateAndChain(
                nextState,
                merge(currentStateAndChain.chain, currentStateAndChain.state, step.methodName)
              )
            )
            visited.add(nextState)
          }
        }
      }
    }

    return success(Result.Stats(visited.size))
  }

  private data class StepSpec<State>(val generator: (State) -> Set<State>, val methodName: String)

  data class SafetySpec<State>(val property: (State) -> Boolean)

  private data class StateAndChain<State>(val state: State, val chain: List<Result.ChainElement<State>>)

  private data class BoundarySpec<State>(val property: (State) -> Boolean)

  private fun steps(): List<StepSpec<State>> {
    var steps = mutableListOf<StepSpec<State>>()
    for (callable in this::class.members) {
      if (!callable.isAnnotatedAs(Step::class)) continue
      steps.add(StepSpec({ s: State -> produceStates(callable, s) }, callable.name))
    }

    return steps.toList()
  }

  private fun boundaries(): List<BoundarySpec<State>> {
    var boundaries = mutableListOf<BoundarySpec<State>>()
    for (callable in this::class.members) {
      if (!callable.isAnnotatedAs(Boundary::class)) continue

      @Suppress("UNCHECKED_CAST") val boundaryFun = callable as KCallable<Boolean>
      boundaries.add(BoundarySpec({ s: State -> boundaryFun.call(this, s) }))
    }
    return boundaries.toList()
  }

  private fun init(): List<State> {
    var initStates = mutableListOf<State>()
    for (callable in this::class.members) {
      if (!callable.isAnnotatedAs(Init::class)) continue
      @Suppress("UNCHECKED_CAST") val initFun = callable as KCallable<State>
      initStates.add(initFun.call(this))
    }
    return initStates.toList()
  }

  private fun produceStates(callable: KCallable<*>, s: State): Set<State> {
    val r = callable.call(this, s)
    if (r is Set<*>) {
      @Suppress("UNCHECKED_CAST") return r as Set<State>
    } else if (r == null) {
      return setOf()
    } else {
      @Suppress("UNCHECKED_CAST") return setOf(r as State)
    }
  }

  private fun KCallable<*>.isAnnotatedAs(clazz: KClass<*>): Boolean {
    for (annotation in this.annotations) {
      if (annotation.annotationClass == clazz) return true
    }
    return false
  }

  private fun createState(stateFun: KCallable<State>, prevState: State): State {
    return stateFun.call(this, prevState)
  }

  private fun merge(
    chain: List<Result.ChainElement<State>>,
    state: State,
    methodName: String
  ): List<Result.ChainElement<State>> {
    return buildList {
      addAll(chain)
      add(Result.ChainElement(state, methodName))
    }
  }

  private fun withinBoundaries(state: State): Boolean {
    for (boundary in boundaries()) {
      if (!boundary.property(state)) return false
    }
    return true
  }

  private fun safetyViolated(state: State, safetyProperties: List<SafetySpec<State>>): Boolean {
    for (property in safetyProperties) {
      if (!property.property(state)) return true
    }
    return false
  }

  private fun safeties(): List<SafetySpec<State>> {
    var safeties = mutableListOf<SafetySpec<State>>()
    for (callable in this::class.members) {
      if (!callable.isAnnotatedAs(Safety::class)) continue

      @Suppress("UNCHECKED_CAST") val safetyFun = callable as KCallable<Boolean>
      safeties.add(SafetySpec({ s: State -> safetyFun.call(this, s) }))
    }
    return safeties.toList()
  }
}
