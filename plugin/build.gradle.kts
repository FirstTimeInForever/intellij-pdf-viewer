import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import java.nio.file.Files
import java.nio.file.Paths
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.tasks.VerifyPluginTask

fun fromProperties(key: String) = project.findProperty(key).toString()

plugins {
  id("java")
  kotlin("jvm")
  kotlin("plugin.serialization")
  id("org.jetbrains.intellij.platform") version "2.6.0"
  id("org.jetbrains.changelog") version "2.2.1"
  id("com.github.ben-manes.versions") version "0.52.0"
  // Plugin which can update Gradle dependencies, use the help/useLatestVersions task.
  id("se.patrikerdes.use-latest-versions") version "0.2.18"
  id("io.sentry.jvm.gradle") version "5.8.0"
}

group = fromProperties("group")
version = fromProperties("version")

val kotlinVersion: String by project
val kotlinxSerializationJsonVersion: String by project

val webView: Configuration by configurations.creating
val webViewSourceDirectory = file("$projectDir/src/main/web-view")

repositories {
  mavenCentral()
  // maven("http://maven.geotoolkit.org/")

  intellijPlatform {
    defaultRepositories()
    maven("https://www.jetbrains.com/intellij-repository/snapshots")
  }
}

dependencies {
  intellijPlatform {
    zipSigner()
    pluginVerifier()
    testFramework(TestFrameworkType.Platform)

    intellijIdeaCommunity(fromProperties("platformVersion"))

    plugin("nl.rubensten.texifyidea:${fromProperties("texifyVersion")}")
  }

  implementation("io.sentry:sentry:8.17.0") {
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

tasks {
  compileKotlin {
    compilerOptions {
      freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn", "-Xjvm-default=all")
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

//  // https://youtrack.jetbrains.com/issue/KTIJ-782
//  buildSearchableOptions {
//    enabled = false
//  }
}

intellijPlatform {
  pluginConfiguration {
    name = fromProperties("pluginName")
    description = extractPluginDescription()
    // Get the latest available change notes from the changelog file
    changeNotes = (
      provider {
        with(changelog) {
          renderItem(changelog.getLatest().withHeader(true), Changelog.OutputType.HTML)
        }
      }
      )
  }

  // Set name of archive https://github.com/JetBrains/intellij-platform-gradle-plugin/issues/1731#issuecomment-2372046338
  projectName = "intellij-pdf-viewer"

  pluginVerification {
    freeArgs = listOf("-mute", "TemplateWordInPluginId", "-mute", "TemplateWordInPluginName")
    ignoredProblemsFile = file("plugin-verifier-ignored-problems.txt")
//    failureLevel = VerifyPluginTask.FailureLevel.ALL

    ides {
      recommended()
    }
  }

  publishing {
    token.set(properties["intellijPublishToken"].toString())

    // Specify channel based on version
    channels.set(listOf(fromProperties("version").split('-').getOrElse(1) { "stable" }.split('.').first()))
  }
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

tasks.runIde {
  // Some warning asked for this to be set explicitly
  systemProperties["idea.log.path"] = file("build/idea-sandbox/system/log").absolutePath
  systemProperties["ide.browser.jcef.enabled"] = true
  systemProperties["pdf.viewer.debug"] = true
  jvmArgs("--add-exports", "java.base/jdk.internal.vm=ALL-UNNAMED", "-Xmx4096m", "-Xms128m")
}

