import org.jetbrains.changelog.closure
import org.jetbrains.intellij.tasks.PatchPluginXmlTask
import org.jetbrains.intellij.tasks.RunIdeTask
import java.nio.file.Paths

plugins {
  id("java")
  kotlin("jvm")
  kotlin("plugin.serialization")
  id("org.jetbrains.intellij") version "0.6.5"
  id("org.jetbrains.changelog") version "0.6.2"
  id("com.github.ben-manes.versions") version "0.36.0"
}

val kotlinVersion: String by project
val pluginSinceVersion: String by project
val pluginUntilVersion: String by project
val webViewSourceDirectory = file("$projectDir/src/main/web-view")

repositories {
  mavenCentral()
  maven("https://www.jetbrains.com/intellij-repository/snapshots")
}

val webView: Configuration by configurations.creating

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
  implementation("io.sentry:sentry:1.7.30") {
    // IntelliJ already bundles it and will report a classloader problem if this isn't excluded
    exclude("org.slf4j")
  }
  implementation(project(":mpi"))
  webView(project(":web-view:bootstrap"))
}

intellij {
  version = "IC-2020.3"
}

tasks {
  compileKotlin {
    kotlinOptions {
      jvmTarget = JavaVersion.VERSION_11.toString()
      @Suppress("SuspiciousCollectionReassignment")
      freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }
  }
  changelog {
    version = "${rootProject.version}"
    path = "${projectDir}/../CHANGELOG.md"
    header = closure { project.version }
    itemPrefix = "-"
    keepUnreleasedSection = true
    unreleasedTerm = "Unreleased"
  }
  withType<PatchPluginXmlTask>() {
    sinceBuild(pluginSinceVersion)
    untilBuild(pluginUntilVersion)
    changeNotes(closure {
      changelog.getLatest().withHeader(false).toHTML()
    })
  }
  runPluginVerifier {
    ideVersions(project.properties["pluginVerifierIdeVersions"].toString())
  }
  // https://youtrack.jetbrains.com/issue/KTIJ-782
  buildSearchableOptions {
    enabled = false
  }
}

val copyWebViewBuildResults by tasks.registering(Copy::class) {
  from(webView)
  into(Paths.get(buildDir.toString(), "resources", "main", "web-view"))
}

tasks.getByName("processResources") {
  // dependsOn(copyWebViewBuildResults)
  inputs.dir(copyWebViewBuildResults.map { it.outputs.files.singleFile })
}

tasks.withType<RunIdeTask> {
  systemProperties["ide.browser.jcef.enabled"] = true
  systemProperties["pdf.viewer.debug"] = true
  jvmArgs("--add-exports", "java.base/jdk.internal.vm=ALL-UNNAMED", "-Xmx4096m", "-Xms128m")
}
