
import com.jetbrains.plugin.structure.base.utils.readText
import org.jetbrains.changelog.closure
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.tasks.PatchPluginXmlTask
import org.jetbrains.intellij.tasks.RunIdeTask
import java.nio.file.Paths

fun fromProperties(key: String) = project.findProperty(key).toString()

plugins {
  id("java")
  kotlin("jvm")
  kotlin("plugin.serialization")
  id("org.jetbrains.intellij") version "0.7.3"
  id("org.jetbrains.changelog") version "1.1.2"
  id("com.github.ben-manes.versions") version "0.36.0"
}

group = fromProperties("group")
version = fromProperties("version")

val kotlinVersion: String by project
val kotlinxSerializationJsonVersion: String by project

val webView: Configuration by configurations.creating
val webViewSourceDirectory = file("$projectDir/src/main/web-view")

repositories {
  mavenCentral()
  maven("https://www.jetbrains.com/intellij-repository/snapshots")
  // maven("http://maven.geotoolkit.org/")
}

dependencies {
  // implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
  // implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationJsonVersion")
  implementation("io.sentry:sentry:1.7.30") {
    // Included in IJ
    exclude("org.slf4j")
    exclude("com.fasterxml.jackson.core", "jackson-core")
  }
  // kotlinx.serialization also should be present in the platform
  implementation(project(":mpi")) {
    exclude("org.jetbrains.kotlinx", "kotlinx-serialization-json")
  }
  implementation(project(":model")) {
    exclude("org.jetbrains.kotlinx", "kotlinx-serialization-json")
  }
  webView(project(":web-view:bootstrap"))
}

intellij {
  version = fromProperties("platformVersion")
  sameSinceUntilBuild = true
  updateSinceUntilBuild = false
  pluginName = fromProperties("pluginName")

  setPlugins("nl.rubensten.texifyidea:0.7.5")
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
    path = Paths.get(projectDir.path, "..", "CHANGELOG.md").toString()
    header = closure { project.version }
    itemPrefix = "-"
    keepUnreleasedSection = true
    unreleasedTerm = "Unreleased"
  }
  withType<PatchPluginXmlTask> {
    sinceBuild(fromProperties("pluginSinceVersion"))
    untilBuild(fromProperties("pluginUntilVersion"))
    changeNotes(closure {
      changelog.getLatest().withHeader(true).toHTML()
    })
    setPluginDescription(extractPluginDescription())
  }
  runPluginVerifier {
    ideVersions(fromProperties("pluginVerifierIdeVersions"))
  }
  // https://youtrack.jetbrains.com/issue/KTIJ-782
  buildSearchableOptions {
    enabled = false
  }
}

@Throws(GradleException::class)
fun extractPluginDescription(): String {
  // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
  val lines = Paths.get(projectDir.path, "..", "README.md").readText().lines()
  val start = "<!-- Plugin description -->"
  val end = "<!-- Plugin description end -->"
  if (!lines.containsAll(listOf(start, end))) {
    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
  }
  val descriptionLines = lines.subList(lines.indexOf(start) + 1, lines.indexOf(end))
  val descriptionText = descriptionLines.joinToString("\n")
  return markdownToHTML(descriptionText)
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
