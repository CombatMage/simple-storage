package org.neidhardt.simplestorage

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config


/**
* Created by eric.neidhardt (eric.neidhardt@gmail.com)
* on 28.11.2016.
*/
@RunWith(RobolectricTestRunner::class)
@Config(manifest=Config.NONE)
class SimpleStorageTest {

	private lateinit var unit: SimpleStorage<Int>

	@Before
	fun setUp() {
		this.unit = SimpleStorage(
				RuntimeEnvironment.application.applicationContext,
				Int::class.java
		)
		this.unit.clear()
	}

	@Test
	fun precondition() {
		// action
		assertNotNull(this.unit.storageKey)
		// verify
		assertNull(this.readFirstItem())
	}

	@Test
	fun saveAndGet() {
		// action
		this.unit.save(42).blockingSubscribe()
		// verify
		assertEquals(42, this.readFirstItem())
	}

	@Test
	fun saveAndGetSync() {
		// action
		this.unit.saveSync(42)
		// verify
		assertEquals(42, this.unit.getSync())
	}

	@Test
	fun clear() {
		// arrange
		this.unit.save(42).blockingSubscribe()
		assertEquals(42, this.readFirstItem())
		// action
		this.unit.clear()
		// verify
		assertNull(this.readFirstItem())
	}

	@Test
	fun saveNonPrimitive() {
		// arrange
		val testStorage = SimpleStorage(RuntimeEnvironment.application, TestUser::class.java)
		val testData = TestUser("user", listOf("item_1", "item_2"))
		// action
		testStorage.save(testData).blockingSubscribe()
		val result = testStorage.get().blockingIterable().first().item
		// verify
		assertEquals(testData, result)
	}

	private fun readFirstItem(): Int? {
		val entry = this.unit.get().blockingIterable().first()
		if (entry.isEmpty)
			return null
		return entry.item
	}

	private data class TestUser(val name: String, val inventory: List<String>)
}

