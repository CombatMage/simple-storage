package org.neidhardt.simplestorage

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.annotation.CheckResult
import android.text.TextUtils
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * SimpleListStorage is a helper to store list of data of type [T] in [PreferenceManager].
 * Internally the data is converted to json and joined to a single string and stored.
 * It supports nested data structures.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class SimpleListStorage<T>(context: Context, private val classOfT: Class<T>) {

	private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
	private val converter = Gson()

	val storageKey = "SimpleStorage_List_${classOfT.name}"

	fun saveItemSync(item: T) {
		val data = ArrayList<T>().apply {
			addAll(getSync())
			add(item)
		}
		saveSync(data)
	}

	fun saveSync(data: List<T>) {
		val listOfJson = data.map { item -> converter.toJson(item, classOfT) }
		sharedPreferences.putListString(storageKey, listOfJson)
	}

	fun getSync(): List<T> {
		val listOfJson = sharedPreferences.getListString(storageKey)
		return listOfJson.map { json -> converter.fromJson(json, classOfT) }
	}

	@CheckResult
	fun save(data: List<T>): Observable<List<T>> {
		return Observable.fromCallable {
			data.also { saveSync(it) }
		}.subscribeOn(Schedulers.computation())
	}

	@CheckResult
	fun get(): Observable<List<T>> {
		return Observable.create<List<T>> { subscriber ->
			subscriber.onNext(getSync())
			subscriber.onComplete()
		}
	}

	fun clear() {
		sharedPreferences.edit()
				.remove(storageKey)
				.apply()
	}
}

private fun SharedPreferences.putListString(key: String, stringList: List<String>) {
	val myStringList = stringList.toTypedArray()
	edit().putString(key, TextUtils.join("‚‗‚", myStringList)).apply()
}

private fun SharedPreferences.getListString(key: String): ArrayList<String> {
	return ArrayList(listOf(*TextUtils.split(getString(key, ""), "‚‗‚")))
}