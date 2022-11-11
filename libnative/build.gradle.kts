plugins {
    kotlin("multiplatform") version "1.7.20"
}

group = "com.demo"
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
        compilations.getByName("main") {
            cinterops {
                val libapi by creating {
                    defFile("src/nativeInterop/cinterop/libapi.def")
                }
            }
        }
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

tasks {
    val generateDef by creating {
        doLast {
            val libapi = File("${projectDir}/../libapi/cmake-build-debug")
            val defFile = File("${projectDir}/src/nativeInterop/cinterop/libapi.def")
            val content = """
                |headers = api.h
                |staticLibraries = libapi.a
                |compilerOpts = -I${File(libapi, "include").normalize().absolutePath}
                |libraryPaths = ${File(libapi, "lib").normalize().absolutePath}
            """.trimMargin()
            defFile.writeText(content)
        }
    }

    val cinteropLibapiNative by getting {
        dependsOn(generateDef)
    }
}
