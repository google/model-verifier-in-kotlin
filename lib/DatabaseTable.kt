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

  val size = data.size

  val empty = data.size == 0

  val keys = data.keys

  fun containsKey(key : K) : Boolean = this.keys.contains(key)

  fun at(key : K) : V? = this.data.get(key)
}

fun <K : Any, V : Any> emptyTable() : DatabaseTable<K,V> { return DatabaseTable<K,V>(mapOf()) }

fun <K : Any, V : Any> tableOf(k: K, v: V) : DatabaseTable<K,V> { return DatabaseTable<K,V>(mapOf(k to v)) }