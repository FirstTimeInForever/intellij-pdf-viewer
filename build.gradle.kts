import org.jetbrains.intellij.tasks.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.moowork.gradle.node.npm.*
import org.jetbrains.changelog.closure

plugins {
    id("java")
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.intellij") version "0.6.5"
    id("org.jetbrains.changelog") version "0.6.2"
    id("com.github.node-gradle.node") version "2.2.3"
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

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
    implementation("io.sentry:sentry:1.7.30") {
        // IntelliJ already bundles it and will report a classloader problem if this isn't excluded
        exclude("org.slf4j")
    }
}

intellij {
    version = "IC-2020.3"
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
    changelog {
        version = "${project.version}"
        path = "${project.projectDir}/CHANGELOG.md"
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
    node {
        download = true
        version = project.properties["nodeVersion"].toString()
        nodeModulesDir = webViewSourceDirectory
    }
    runPluginVerifier {
        ideVersions(project.properties["pluginVerifierIdeVersions"].toString())
    }
    // https://youtrack.jetbrains.com/issue/KTIJ-782
    buildSearchableOptions {
        enabled = false
    }
}

tasks.register("ensureNodeModulesInstalled") {
    dependsOn("nodeSetup")
    dependsOn("npmSetup")
    if (!file(File(webViewSourceDirectory, "node_modules")).exists()) {
        dependsOn("npm_ci")
    }
}

fun cacheWebviewBuildTask(task: NpmTask) {
    with(task.inputs) {
        files(
            File(webViewSourceDirectory, "package.json"),
            File(webViewSourceDirectory, "package-lock.json")
        ).withPathSensitivity(PathSensitivity.RELATIVE)
        dir(File(webViewSourceDirectory, "src")).withPathSensitivity(PathSensitivity.RELATIVE)
    }
    with(task.outputs) {
        dir("${projectDir}/build/resources/main/web-view")
        cacheIf { true }
    }
}

tasks.register<NpmTask>("webViewBuildDev") {
    cacheWebviewBuildTask(this)
    dependsOn("ensureNodeModulesInstalled")
    setArgs(listOf("run", "buildDev"))
}

tasks.register<NpmTask>("webViewBuild") {
    cacheWebviewBuildTask(this)
    dependsOn("ensureNodeModulesInstalled")
    setArgs(listOf("run", "build"))
}

tasks.getByName("processResources") {
    dependsOn("webViewBuild")
}

tasks.withType<RunIdeTask> {
    systemProperties["ide.browser.jcef.enabled"] = true
    systemProperties["pdf.viewer.debug"] = true
    jvmArgs("--add-exports", "java.base/jdk.internal.vm=ALL-UNNAMED", "-Xmx4096m", "-Xms128m")
}
