import org.jetbrains.kotlin.utils.addToStdlib.safeAs

plugins {
  kotlin("js")
  kotlin("plugin.serialization")
}

val kotlinxSerializationJsonVersion: String by project

repositories {
  mavenCentral()
}

dependencies {
  implementation(kotlin("stdlib-js"))
  implementation(project(":mpi"))
  implementation(project(":model"))
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationJsonVersion")
}

kotlin {
  js(IR) {
    browser {
      webpackTask {
        cssSupport {
          enabled.set(true)
        }
        sourceMaps = true
      }

      runTask {
        cssSupport {
          enabled.set(true)
        }
      }

      testTask {
        useKarma {
          useChromeHeadless()
          webpackConfig.cssSupport {
            enabled.set(true)
          }
        }
      }
    }
    binaries.executable()
  }
}

artifacts {
  val distributionTask = tasks.findByName("browserDistribution")!!
  // TODO: Find a better way to select viewer.js file
  val targetFile = File(distributionTask.outputs.files.singleFile, "viewer.js")
  add(configurations.viewerApplicationBundle.name, targetFile) {
    builtBy(distributionTask)
  }
}
