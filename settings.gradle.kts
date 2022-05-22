rootProject.name = "intellij-pdf-viewer"

include(":web-view")
include(":web-view:viewer")
include(":web-view:pdfjs")
include(":plugin")
include(":mpi")
include(":model")

val kotlinVersion = "1.6.20"
val kotlinxSerializationJsonVersion = "1.3.2"
val kotlinxCoroutinesVersion = "1.6.1"
val kotlinWrappersVersion = "0.0.1-pre.330-kotlin-1.6.20"

fun kotlinWrappers(name: String): String {
  return "org.jetbrains.kotlin-wrappers:kotlin-$name"
}

fun kotlinWrappers(name: String, version: String): String {
  return "org.jetbrains.kotlin-wrappers:kotlin-$name:$version"
}

fun kotlinx(name: String, version: String): String {
  return "org.jetbrains.kotlinx:kotlinx-$name:$version"
}

dependencyResolutionManagement {
  repositories {
    mavenCentral()
    maven("https://www.jetbrains.com/intellij-repository/snapshots")
  }
  versionCatalogs {
    create("libs") {
      library("kotlinReflect", "org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
      library("kotlinxCoroutinesCore", kotlinx("coroutines-core", kotlinxCoroutinesVersion))
      library("kotlinxSerialization", kotlinx("serialization-json", kotlinxSerializationJsonVersion))
      library("kotlinWrappersBom", kotlinWrappers("wrappers-bom", kotlinWrappersVersion))
    }
  }
}

pluginManagement {
  plugins {
    val kotlinVersion: String by settings
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    kotlin("multiplatform") version kotlinVersion
    kotlin("js") version kotlinVersion
    id("com.github.ben-manes.versions") version "0.41.0"
  }
}
