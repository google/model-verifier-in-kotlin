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

package com.github.zmatti.mvik.lib

infix fun <T> Set<T>.isSubsetOf(other: Set<T>): Boolean {
  return (this subtract other).isEmpty()
}

fun <T> Set<T>.subsets(): Set<Set<T>> {
  if (this.size == 0) {
    return setOf()
  }

  if (this.size == 1) {
    return setOf(this)
  }

  val item = pick(this)
  val rest = this subtract item
  var result = mutableSetOf<Set<T>>()
  for (p in rest.subsets()) {
    result.add(p)
    result.add(p union item)
  }
  result.add(item)
  return result.toSet()
}

private fun <T> pick(s: Set<T>): Set<T> {
  return setOf(s.elementAt(0))
}

/**  */
class Sets {}
