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

package com.google.mvik.lib

data class SnapshottingDatabaseTable <K : Any, V : Any> (val data: Map<K,Map<out Int,V?>>) {
  fun insert(k: K, v: V, t: Int) : SnapshottingDatabaseTable<K,V> {
    return internalInsert(k, v, t)
  }

  fun delete(k: K, t: Int) : SnapshottingDatabaseTable<K,V> {
    return internalInsert(k, null, t)
  }

  val empty : Boolean = keys().isEmpty()

  fun empty(atTime: Int?) = keys(atTime).isEmpty()

  fun keys() = keys(null)

  fun keys(atTime: Int?) : Set<K> {
    var keySet = mutableSetOf<K>()
    for (entry in data.entries.iterator()) {
      val latestValue = latest(entry.value, atTime)
      if (latestValue != null) {
        keySet.add(entry.key)
      }
    }
    return keySet.toSet()
  }

  fun at(key: K) = at(key, null)

  fun at(key : K, atTime: Int?) : V? {
    val valueSeq = this.data.get(key)
    return if (valueSeq == null) null else latest(valueSeq, atTime)
  }

  private fun internalInsert(k: K, v: V?, t: Int) : SnapshottingDatabaseTable<K,V> {
    var newData = mutableMapOf<K,Map<out Int,V?>>()
    if (k in data.keys) {
      for (entry in data.entries.iterator()) {
        if (entry.key == k) {
          var newValue = mutableMapOf<Int, V?>()
          newValue.putAll(entry.value)
          newValue.put(t,v)
          newData.put(k, newValue)
        } else {
          newData.put(entry.key, entry.value)
        }
      }
    } else {
      newData.putAll(data)
      newData.put(k, mapOf(t to v))
    }
    return SnapshottingDatabaseTable(newData.toMap())
  }

  private fun latest(valueSeq : Map<out Int, V?>, by : Int?) : V? {
    val filteredValueSeq = valueSeq.entries.filter {by == null || it.key <= by} .sortedBy { it.key }
    return if (filteredValueSeq.isEmpty()) null else filteredValueSeq.takeLast(1)[0].value
  }
}

fun <K : Any, V : Any> emptySnapshottingTable() : SnapshottingDatabaseTable<K,V> { return SnapshottingDatabaseTable<K,V>(mapOf()) }