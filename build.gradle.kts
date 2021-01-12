import org.jetbrains.intellij.tasks.*
import com.moowork.gradle.node.npm.*
import org.jetbrains.changelog.closure

plugins {
    id("java")
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.intellij") version "0.6.5"
    id("org.jetbrains.changelog") version "0.6.2"
    id("com.github.node-gradle.node") version "2.2.3"
}

val kotlinVersion: String by project
val webviewSourceDirectory = file("${projectDir}/src/main/web-view")

repositories {
    mavenCentral()
    maven("https://www.jetbrains.com/intellij-repository/snapshots")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
    implementation("io.sentry:sentry:1.7.30") {
        // IntelliJ already bundles it and will report a classloader problem if this isn't excluded
        exclude("org.slf4j")
    }
}

intellij {
    version = "IC-2020.2.3"
}

tasks {
    compileJava {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
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
        sinceBuild("202")
        untilBuild("204")
        changeNotes(closure {
            changelog.getLatest().withHeader(false).toHTML()
        })
    }
    node {
        download = true
        version = "13.2.0"
        nodeModulesDir = webviewSourceDirectory
    }
}

tasks.register("ensureNodeModulesInstalled") {
    dependsOn("nodeSetup")
    dependsOn("npmSetup")
    if (!file(File(webviewSourceDirectory, "node_modules")).exists()) {
        dependsOn("npm_ci")
    }
    else {
        println("Skipping npm_ci step")
    }
}

fun cacheWebviewBuildTask(task: NpmTask) {
    with(task.inputs) {
        files(
            File(webviewSourceDirectory, "package.json"),
            File(webviewSourceDirectory, "package-lock.json")
        ).withPathSensitivity(PathSensitivity.RELATIVE)
        dir(File(webviewSourceDirectory, "src")).withPathSensitivity(PathSensitivity.RELATIVE)
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

tasks.withType<RunIdeTask>() {
    systemProperties["ide.browser.jcef.enabled"] = true
    systemProperties["pdf.viewer.debug"] = true
    jvmArgs("--add-exports", "java.base/jdk.internal.vm=ALL-UNNAMED", "-Xmx4096m", "-Xms128m")
}
