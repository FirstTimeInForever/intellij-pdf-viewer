plugins {
  kotlin("js")
  kotlin("plugin.serialization")
}

val kotlinxCoroutinesVersion: String by project
val kotlinxSerializationJsonVersion: String by project
val kotlinWrappersVersion: String by project

fun kotlinWrappers(name: String): String {
  return "org.jetbrains.kotlin-wrappers:kotlin-$name"
}

val viewerApplicationBundle: Configuration by configurations.creating {
  isCanBeConsumed = true
  isCanBeResolved = false
}

dependencies {
  implementation(project(":web-view:pdfjs"))
  implementation(kotlin("stdlib-js"))
  implementation(enforcedPlatform(libs.kotlinWrappersBom))
  implementation(libs.kotlinxCoroutinesCore)
  implementation(libs.kotlinxSerialization)

  // implementation(kotlinWrappers("styled-next"))
  // implementation("org.jetbrains.kotlin-wrappers:kotlin-redux:4.1.2-pre.328-kotlin-1.6.20")
  // implementation("org.jetbrains.kotlin-wrappers:kotlin-react-redux:7.2.6-pre.328-kotlin-1.6.20")
  // implementation(kotlinWrappers("css"))
  implementation(kotlinWrappers("react"))
  implementation(kotlinWrappers("react-dom"))
  implementation(kotlinWrappers("react-css"))
  implementation(npm("react", "18.0.0"))
  implementation(npm("react-dom", "18.0.0"))

  implementation(devNpm("copy-webpack-plugin", "^7.0.0"))
  implementation(devNpm("sass-loader", "^12.6.0"))
  implementation(devNpm("style-loader", "^3.3.1"))
  implementation(devNpm("sass", "^1.49.10"))
}

kotlin {
  js(IR) {
    useCommonJs()
    browser {
      webpackTask {
        sourceMaps = true
        report = true
      }
      commonWebpackConfig {
        cssSupport.enabled = true
        cssSupport.mode = "import"
      }
      binaries.executable()
    }
    compilations.all {
      kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf("-opt-in=kotlin.RequiresOptIn")
      }
    }
  }
}

artifacts {
  val distributionTask = tasks.findByName("browserDistribution")!!
  // TODO: Find a better way to select viewer.js file
  val targetFile = File(distributionTask.outputs.files.singleFile, "viewer.js")
  add(viewerApplicationBundle.name, targetFile) {
    builtBy(distributionTask)
  }
}
