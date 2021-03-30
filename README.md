# simple-storage

A simple lib to store data in shared preferences app storage. 

## Gradle

```gradle
repositories {
    mavenCentral()
}

dependencies {
    implementation 'com.github.ericneid:simple-storage:1.3.1'
}
```

## Usage

```kotlin
val storage = SimpleStorage(context, User::class.java)
val data = User("user", listOf("item_1", "item_2"))

storage.saveSync(data).subscribe {
    // DONE
}

storage.getSync().subscribe { result ->
    // DONE
}



val storage = SimpleListStorage(context, User::class.java)

val data = listOf(User("user_1", 30), User("user_2", 32))
testStorage.save(data).subscribe {
    // DONE
}

testStorage.get().subscribe {
    // DONE
}
```
