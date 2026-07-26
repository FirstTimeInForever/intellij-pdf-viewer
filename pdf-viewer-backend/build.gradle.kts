import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType

plugins {
  kotlin("jvm")
  id("org.jetbrains.intellij.platform.module")
  id("rpc")
  kotlin("plugin.serialization")
}

repositories {
  mavenCentral()
  intellijPlatform {
    defaultRepositories()
    maven("https://www.jetbrains.com/intellij-repository/snapshots")
  }
}

dependencies {
  intellijPlatform {
    intellijIdea(project.findProperty("platformVersion").toString())
    bundledModule("intellij.platform.rpc")
    bundledModule("intellij.platform.rpc.backend")
    // Backend might need texify-idea if we move TexPdfViewer there
    plugin("nl.rubensten.texifyidea:${project.findProperty("texifyVersion")}")
  }
  api(project(":pdf-viewer-common"))
  implementation(project(":model"))
}

intellijPlatform {
  pluginConfiguration {
    id = "com.firsttimeinforever.intellij.pdf.viewer.backend"
  }
}
