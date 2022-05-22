plugins {
  kotlin("js")
  kotlin("plugin.serialization")
}

dependencies {
  implementation(kotlin("stdlib-js"))
  implementation(libs.kotlinxCoroutinesCore)
  implementation(npm("pdfjs-dist", "2.13.216"))
  implementation(npm("webl10n", "^1.0.0"))
}

kotlin {
  js(IR) {
    useCommonJs()
    browser()
    binaries.executable()
    compilations.all {
      kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf("-opt-in=kotlin.RequiresOptIn")
      }
    }
  }
}
