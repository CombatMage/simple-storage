# simple-storage

A simple lib to store data in shared preferences app storage. 

**Gradle**

compile 'org.neidhardt:simple-storage:1.0.2'

**Usage**
```kotlin
val storage = SimpleStorage(context, User::class.java)
val data = User("user", listOf("item_1", "item_2"))

storage.save(data).subscribe {
    // DONE
}

storage.get().subscribe { result ->
    // DONE
}



val storage = SimpleListStorage(context, User::class.java)

val data = listOf(User("user_1", 30), User("user_2", 32))
testStorage.saveAsync(data).subscribe {
    // DONE
}

testStorage.getAsync().subscribe {
    // DONE
}
```