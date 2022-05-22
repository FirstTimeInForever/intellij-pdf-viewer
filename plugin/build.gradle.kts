import org.jetbrains.changelog.closure
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.tasks.PatchPluginXmlTask
import org.jetbrains.intellij.tasks.RunIdeTask
import java.nio.file.Files
import java.nio.file.Paths

fun fromProperties(key: String) = project.findProperty(key).toString()

plugins {
  id("java")
  kotlin("jvm")
  kotlin("plugin.serialization")
  id("org.jetbrains.intellij") version "1.3.1"
  id("org.jetbrains.changelog") version "1.1.2"
}

group = fromProperties("group")
version = fromProperties("version")

repositories {
  mavenCentral()
  maven("https://www.jetbrains.com/intellij-repository/snapshots")
}

@Suppress("NOTHING_TO_INLINE")
inline fun DependencyHandler.project(path: String, configuration: Configuration): Dependency {
  return project(mapOf(
    "path" to path,
    "configuration" to configuration.name
  ))
}

val viewerApplicationBundle: Configuration by configurations.creating {
  isCanBeConsumed = false
  isCanBeResolved = true
}
val webViewSourceDirectory = file("$projectDir/src/main/web-view")

dependencies {
  implementation(libs.kotlinxCoroutinesCore)
  implementation(libs.kotlinxSerialization)
  implementation(libs.kotlinReflect)
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
  viewerApplicationBundle(project(":web-view:viewer", viewerApplicationBundle))
}

intellij {
  version.set(fromProperties("platformVersion"))
  sameSinceUntilBuild.set(true)
  updateSinceUntilBuild.set(false)
  pluginName.set(fromProperties("pluginName"))
  plugins.set(listOf("nl.rubensten.texifyidea:${fromProperties("texifyVersion")}"))
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
    sinceBuild.set(fromProperties("pluginSinceVersion"))
    untilBuild.set(fromProperties("pluginUntilVersion"))
    changeNotes.set(changelog.getLatest().withHeader(true).toHTML())
    pluginDescription.set(extractPluginDescription())
  }
  runPluginVerifier {
    ideVersions.set(fromProperties("pluginVerifierIdeVersions").split(", "))
  }
//  // https://youtrack.jetbrains.com/issue/KTIJ-782
//  buildSearchableOptions {
//    enabled = false
//  }
}

@Throws(GradleException::class)
fun extractPluginDescription(): String {
  // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
  val lines = Files.readAllLines(Paths.get(projectDir.path, "..", "README.md"))
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
  from(viewerApplicationBundle)
  into(Paths.get(buildDir.toString(), "resources", "main", "web-view"))
}

tasks.getByName("processResources") {
  dependsOn(copyWebViewBuildResults)
  inputs.dir(copyWebViewBuildResults.map { it.outputs.files.singleFile })
}

tasks.withType<RunIdeTask> {
  systemProperties["ide.browser.jcef.enabled"] = true
  systemProperties["pdf.viewer.debug"] = true
  jvmArgs("--add-exports", "java.base/jdk.internal.vm=ALL-UNNAMED", "-Xmx4096m", "-Xms128m")
}
