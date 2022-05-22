plugins {
  kotlin("multiplatform")
  kotlin("plugin.serialization")
}

group = "org.example"
version = "1.0-SNAPSHOT"

kotlin {
  jvm()
  js(IR) {
    browser {
      binaries.executable()
    }
  }
  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(kotlin("stdlib-common"))
        implementation(libs.kotlinxSerialization)
      }
    }
  }
}
