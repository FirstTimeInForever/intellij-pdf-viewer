import org.jetbrains.changelog.Changelog
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
  id("org.jetbrains.intellij") version "1.17.3"
  id("org.jetbrains.changelog") version "2.2.0"
  id("com.github.ben-manes.versions") version "0.51.0"
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
  version.set(fromProperties("platformVersion"))
  sameSinceUntilBuild.set(true)
  updateSinceUntilBuild.set(false)
  pluginName.set(fromProperties("pluginName"))
  plugins.set(listOf("nl.rubensten.texifyidea:${fromProperties("texifyVersion")}"))
}

tasks {
  compileKotlin {
    kotlinOptions {
      jvmTarget = JavaVersion.VERSION_17.toString()
      freeCompilerArgs += listOf("-Xopt-in=kotlin.RequiresOptIn", "-Xjvm-default=all")
    }
  }
  jar {
    exclude("com/jetbrains/**")
  }
  instrumentedJar {
    exclude("com/jetbrains/**")
  }
  changelog {
    version = "${rootProject.version}"
    path = Paths.get(projectDir.path, "..", "CHANGELOG.md").toString()
    header = project.version.toString()
    itemPrefix = "-"
    keepUnreleasedSection = true
    unreleasedTerm = "Unreleased"
  }
  withType<PatchPluginXmlTask> {
    sinceBuild.set(fromProperties("pluginSinceVersion"))
    untilBuild.set(fromProperties("pluginUntilVersion"))
    changeNotes.set(changelog.renderItem(changelog.getLatest().withHeader(true), Changelog.OutputType.HTML))
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
  from(webView)
  exclude("tmp/**")
  into(project.layout.buildDirectory.dir("resources/main/web-view"))
}

tasks.getByName("processResources") {
  // dependsOn(copyWebViewBuildResults)
  inputs.dir(copyWebViewBuildResults.map { it.outputs.files.singleFile })
}

tasks.withType<RunIdeTask> {
  // Some warning asked for this to be set explicitly
  systemProperties["idea.log.path"] = file("build/idea-sandbox/system/log").absolutePath
  jbrVariant.set("jcef")
  systemProperties["ide.browser.jcef.enabled"] = true
  systemProperties["pdf.viewer.debug"] = true
  jvmArgs("--add-exports", "java.base/jdk.internal.vm=ALL-UNNAMED", "-Xmx4096m", "-Xms128m")
}
