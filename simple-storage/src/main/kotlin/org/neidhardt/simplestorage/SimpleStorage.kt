package org.neidhardt.simplestorage

import android.content.Context
import android.preference.PreferenceManager
import android.support.annotation.CheckResult
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

/**
 * SimpleStorage is a helper to store data of type [T] in [PreferenceManager].
 * Internally the data is converted to json and stored. It supports nested data structures.
 */
@Suppress("MemberVisibilityCanPrivate")
open class SimpleStorage<T>(context: Context, private val classOfT: Class<T>) {

	private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
	private val converter = Gson()

	private var cachedData: T? = null

	val storageKey = "SimpleStorage_${classOfT.name}"

	/**
	 * saveSync stores data of type [T] in storage.
	 * The returned Observable's scheduler is [Schedulers.computation()].
	 *
	 * @param data - object of [T] to be stored
	 * @return [Observable], containing the data just saved
	 */
	@CheckResult
	fun save(data: T): Observable<T> {
		return Observable.fromCallable {
			saveSync(data)
			data
		}.subscribeOn(Schedulers.computation())
	}

	/**
	 * getSync returns an stored data of type [T] as [Optional].
	 * If no data was stored before, [Optional.empty] is true.
	 * The returned Observable's scheduler is default.
	 *
	 * @return [Observable] containing the data
	 */
	@CheckResult
	fun get(): Observable<Optional<T>> {
		cachedData?.let { cachedData ->
			return Observable.just(Optional.of(cachedData))
		}

		return Observable.create<Optional<T>> { subscriber ->
			val data = getSync()
			if (data == null) {
				subscriber.onNext(Optional.empty())
			} else {
				subscriber.onNext(Optional.of(data))
			}
			subscriber.onComplete()
		}
	}

	/**
	 * clear deletes all stored data.
	 */
	fun clear() {
		cachedData = null
		sharedPreferences.edit()
				.remove(storageKey)
				.apply()
	}

	/**
	 * saveSync stores data of type [T] in storage.
	 *
	 * @param data - object of [T] to be stored
	 */
	fun saveSync(data: T) {
		cachedData = data
		val json = converter.toJson(data)
		sharedPreferences.edit()
				.putString(storageKey, json)
				.apply()
	}

	/**
	 * getSync returns stored data of type [T] or null if no data was stored before.
	 */
	fun getSync(): T? {
		val storedJson = sharedPreferences.getString(storageKey, null)
		return converter.fromJson(storedJson, classOfT)
	}
}