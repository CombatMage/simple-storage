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
@Config(manifest= Config.NONE)
class SimpleListStorageTest {

	private lateinit var unit: SimpleListStorage<Int>

	@Before
	fun setUp() {
		this.unit = SimpleListStorage(
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
		assertTrue(this.unit.get().blockingIterable().first().isEmpty())
	}

	@Test
	fun saveAndGet() {
		// arrange
		val data = listOf(1,2,3,4)
		// action
		this.unit.save(data).blockingSubscribe()
		// verify
		assertTrue(data.containsAll(this.unit.get().blockingIterable().first()))
	}

	@Test
	fun clear() {
		// arrange
		val data = listOf(1,2,3,4)
		this.unit.save(data).blockingSubscribe()
		assertTrue(data.containsAll(this.unit.get().blockingIterable().first()))
		// action
		this.unit.clear()
		// verify
		assertTrue(this.unit.get().blockingIterable().first().isEmpty())
	}

	@Test
	fun saveNonPrimitive() {
		// arrange
		val testStorage = SimpleListStorage(RuntimeEnvironment.application, TestUser::class.java)
		val data = listOf(TestUser("user_1", 30), TestUser("user_2", 32))
		// action
		testStorage.save(data).blockingSubscribe()
		val retrievedData = testStorage.get().blockingIterable().first()
		// verify
		data.forEachIndexed { i, testUser ->
			assertEquals(testUser, retrievedData[i])
		}
		assertEquals(data.size, retrievedData.size)
	}

	private data class TestUser(val name: String, val age: Int)
}
