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

data class DatabaseTable <K : Any, V : Any> (val data: Map<K,V>) {
  fun insert(newKey: K, newValue: V) : DatabaseTable<K,V> {
  	if (data.contains(newKey)) {
  	  return this
  	}
  	var newData = mutableMapOf<K,V>()
  	newData.putAll(data)
  	newData.put(newKey, newValue)
  	return DatabaseTable(newData.toMap())
  }

  fun upsert(key: K, value: V) : DatabaseTable<K,V> {
  	var newData = mutableMapOf<K,V>()
  	newData.putAll(data)
  	newData.put(key, value)
  	return DatabaseTable(newData.toMap())
  }

  fun update(key: K, value: V) : DatabaseTable<K,V> {
    if (!data.contains(key)) {
      return this
    }
    return upsert(key, value)
  }

  fun update(keyPred: (K) -> Boolean, valueUpdater: (V) -> V) : DatabaseTable<K,V> {
    var newData = mutableMapOf<K,V>()
    for (entry in data.entries.iterator()) {
      if (keyPred(entry.key)) {
        newData.put(entry.key, valueUpdater(entry.value))
      } else {
        newData.put(entry.key, entry.value)
      }
    }
    return DatabaseTable(newData.toMap())
  }

  fun delete(key: K) : DatabaseTable<K,V> {
  	if (!data.contains(key)) {
  	  return this
  	}
  	var newData = mutableMapOf<K,V>()
  	newData.putAll(data)
  	newData.remove(key)
  	return DatabaseTable(newData.toMap())
  }

  fun countIf(pred: (K,V) -> Boolean) : Int {
  	var count = 0
  	for (entry in data.entries.iterator()) {
  		if (pred(entry.key, entry.value)) {
  			count = count + 1
  		}
  	}
  	return count
  }

  fun forEachRow(consumer: (K,V) -> Unit) : Unit {
    for (entry in data.entries.iterator()) {
      consumer(entry.key, entry.value)
    }
  }

  fun select(keyPred: (K) -> Boolean) : List<K> = select(keyPred, {true})

  fun select(keyPred: (K) -> Boolean, valuePred: (V) -> Boolean) : List<K> {
    return data.keys.filter{keyPred(it)}.filter{valuePred(data.get(it)!!)}
  }

  val size = data.size

  val empty = data.size == 0

  val keys = data.keys

  fun containsKey(key : K) : Boolean = this.keys.contains(key)

  fun at(key : K) : V? = this.data.get(key)
}

fun <K : Any, V : Any> emptyTable() : DatabaseTable<K,V> { return DatabaseTable<K,V>(mapOf()) }

fun <K : Any, V : Any> tableOf(k: K, v: V) : DatabaseTable<K,V> { return DatabaseTable<K,V>(mapOf(k to v)) }

fun <K : Any, V : Any> tableOf(k1: K, v1: V, k2: K, v2: V) : DatabaseTable<K,V> { return DatabaseTable<K,V>(mapOf(k1 to v1, k2 to v2)) }

fun <K : Any, V : Any> tableOf(k1: K, v1: V, k2: K, v2: V, k3: K, v3: V) : DatabaseTable<K,V> { return DatabaseTable<K,V>(mapOf(k1 to v1, k2 to v2, k3 to v3)) }
