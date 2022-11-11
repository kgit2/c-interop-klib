# cmake-add-klib

This tutorial will show you how to call kotlin native library through c more efficiently. I will tell you step by step, from the simplest and cruder approach to a more elegant approach that can be applied to various scenarios.

First let's create a directory to organize our kotlin projects and c projects

```bash
mkdir c-interop-klib
cd c-interop-klib
```

2. Look, it's simple. Next let's create a gradle project to initialize a kotlin native library, please note that I'm skilled enough, but you can choose IntelliJ idea to help you do it all.

```bash
# pwd=c-interop-klib
mkdir libnative
cd libnative
# before this, you must have gradle executable in your path, such as mac: brew install gradle
gradle init
# choose 1: basic
1
# choose 2: kotlin
2
```

```
Starting a Gradle Daemon (subsequent builds will be faster)

Select type of project to generate:
  1: basic
  2: application
  3: library
  4: Gradle plugin
Enter selection (default: basic) [1..4] 1

Select build script DSL:
  1: Groovy
  2: Kotlin
Enter selection (default: Groovy) [1..2] 2

Generate build using new APIs and behavior (some features may change in the next minor release)? (default: no) [yes, no]
Project name (default: libnative):

> Task :init
Get more help with your project: Learn more about Gradle by exploring our samples at https://docs.gradle.org/7.4.2/samples

BUILD SUCCESSFUL in 1m 7s
2 actionable tasks: 2 executed
```

We can use IntelliJ Idea open it.

3. Replace build.gradle.kts

```kotlin
plugins {
    kotlin("multiplatform") version "1.7.20"
}

group = "com.libnative"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> {
            when (System.getProperty("os.arch")) {
                "aarch64" -> macosArm64("native")
                else -> macosX64("native")
            }
        }
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        binaries {
            executable {
                entryPoint = "main"
            }
            sharedLib {
                baseName = "native"
            }
        }
    }

    sourceSets {
        val nativeMain by getting
        val nativeTest by getting
    }
}
```

4. Create dir for sourceSets

```bash
#pwd=c-interop-klib/libnative
mkdir -p src/commonMain/kotlin
touch src/commandMain/kotlin/Main.kt
touch src/commandMain/kotlin/User.kt
touch src/commandMain/kotlin/Name.kt
```

5. Fill User & Name

```kotlin
// User.kt
data class User(
    val name: Name,
    val age: Int,
) {
    override fun toString(): String {
        return "name=$name, age=$age"
    }
}

// Name.kt
data class Name(
    val first: String,
    val last: String,
) {
    override fun toString(): String {
        return "$first $last"
    }
}
```

```kotlin
// Main.kt
fun main() {
    println(User(Name("Foo", "Bar"), 42))
}
```

6. Run it

```bash
#pwd=c-interop-klib/libnative
./gradlew runDebugExecutableNative
# you'll got
# name=Foo Bar, age=42
```

At this point, you're halfway there, but next we need to compile libnative to a dylib/so, and have a c program that will call it.

```bash
#pwd=c-interop-klib/libnative
./gradlew linkDebugSharedNative
cd ..
#pwd=c-interop-klib
mkdir app
cd app
touch CMakeLists.txt
touch main.c
```

7. Fill CMakeLists.txt

```
cmake_minimum_required(VERSION 3.23)
project(app C)

set(CMAKE_C_STANDARD 99)

add_executable(app main.c)

target_link_libraries(app libnative)

add_library(libnative SHARED IMPORTED)

set_target_properties(libnative PROPERTIES IMPORTED_LOCATION ${CMAKE_CURRENT_SOURCE_DIR}/../libnative/build/bin/native/debugShared/libnative.dylib)

include_directories(${CMAKE_CURRENT_SOURCE_DIR}/../libnative/build/bin/native/debugShared)
```

8. Fill main.c

```c
#include <stdio.h>
#include "libnative_api.h"

int main() {
    libnative_ExportedSymbols *lib = libnative_symbols();
    libnative_kref_Name name = lib->kotlin.root.Name.Name("Foo", "Bar");
    libnative_kref_User user = lib->kotlin.root.User.User(name, 42);
    printf("%s\n", lib->kotlin.root.User.toString(user));
    return 0;
}
```

9. Build & Run

```bash
#pwd=c-interop-klib/app
mkdir cmake-build-debug
cd cmake-build-debug
cmake ..
cmake --build . --target app
./app
# you'll got
# name=Foo Bar, age=42
```

Looks great, everything works as expected, but we try to get the properties in User and we have to use getters to get them one by one.

10. Replace main.c

```diff
#include <stdio.h>
#include "libnative_api.h"

int main() {
    libnative_ExportedSymbols *lib = libnative_symbols();
    libnative_kref_Name name = lib->kotlin.root.Name.Name("Foo", "Bar");
    libnative_kref_User user = lib->kotlin.root.User.User(name, 42);
    - printf("%s\n", lib->kotlin.root.User.toString(user));
    + const char* first = lib->kotlin.root.Name.get_first(name);
    + const char* last = lib->kotlin.root.Name.get_last(name);
    + int age = lib->kotlin.root.User.get_age(user);
    + printf("%s %s %d\n", first, last, age);
    return 0;
}
```

```bash
#pwd=c-interop-klib/app
mkdir cmake-build-debug
cd cmake-build-debug
cmake ..
cmake --build . --target app
./app
# you'll got
# Foo Bar 42
```

Next I want to implement User and Name in a more elegant way.

[Kotlin/Native as a dynamic library — tutorial](https://medium.com/@bppleman/kotlin-native-as-a-dynamic-library-tutorial-c1b844a401bc)
