rootProject.name = "intellij-pdf-viewer"

include(":web-view")
include(":web-view:viewer")
include(":web-view:bootstrap")
include(":plugin")
include(":mpi")
include(":model")
include(":pdf-viewer-common")
include(":pdf-viewer-backend")
include(":pdf-viewer-frontend")


pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://www.jetbrains.com/intellij-repository/releases")
    maven("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies/")
  }
  plugins {
    val kotlinVersion: String by settings
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    kotlin("multiplatform") version kotlinVersion
    id("org.jetbrains.intellij.platform") version "2.16.0"
    id("org.jetbrains.intellij.platform.settings") version "2.16.0"
    id("rpc") version "2.3.20-RC2-0.1"
  }
}

plugins {
  id("org.jetbrains.intellij.platform.settings")
}

// https://github.com/JetBrains/intellij-platform-gradle-plugin/issues/1750
dependencyResolutionManagement {
  repositories {
    mavenCentral()
    maven("https://www.jetbrains.com/intellij-repository/releases")
  }
}
