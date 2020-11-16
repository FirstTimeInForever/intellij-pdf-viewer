import org.jetbrains.intellij.tasks.*
import com.moowork.gradle.node.npm.*
import org.jetbrains.changelog.closure

plugins {
    id("java")
    id("org.jetbrains.intellij") version "0.5.0"
    id("org.jetbrains.changelog") version "0.3.2"
    kotlin("jvm") version "1.4.10"
    kotlin("plugin.serialization") version "1.4.10"
    id("com.github.node-gradle.node") version "2.2.3"
}

group = "com.firsttimeinforever.intellij.pdf.viewer"
version = "0.8.1"

repositories {
    mavenCentral()
    maven("https://www.jetbrains.com/intellij-repository/snapshots")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.10")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.0")
    implementation("io.sentry:sentry:1.7.30") {
        // IntelliJ already bundles it and will report a classloader
        // problem if this isn't excluded
        exclude("org.slf4j")
    }
}

intellij {
//    version = "LATEST-EAP-SNAPSHOT"
    version = "IC-2020.2.3"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

val webviewSourceDirectory = file("${projectDir}/src/main/web-view")

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    changelog {
        version = "${project.version}"
        path = "${project.projectDir}/CHANGELOG.md"
        headerFormat = "{0}"
        headerArguments = listOf("${project.version}")
        itemPrefix = "-"
        keepUnreleasedSection = true
        unreleasedTerm = "Unreleased"
    }
    withType<PatchPluginXmlTask>() {
        sinceBuild("202")
        untilBuild("204")
        changeNotes(closure { changelog.getLatest().withHeader(false).toHTML() })
    }
    node {
        download = true
        version = "13.2.0"
        nodeModulesDir = webviewSourceDirectory
    }
}

tasks.register("ensureNodeModulesInstalled"){
    dependsOn("nodeSetup")
    dependsOn("npmSetup")
    if (!file("${projectDir}/src/main/web-view/node_modules").exists()) {
        dependsOn("npm_ci")
    }
    else {
        println("Skipping npm_ci step")
    }
}

fun cacheWebviewBuildTask(task: NpmTask) {
    task.run {
        inputs.file(File(webviewSourceDirectory, "package.json")).withPathSensitivity(PathSensitivity.RELATIVE)
        inputs.dir(File(webviewSourceDirectory, "src")).withPathSensitivity(PathSensitivity.RELATIVE)
        inputs.file(File(webviewSourceDirectory, "package-lock.json")).withPathSensitivity(PathSensitivity.RELATIVE)
        outputs.dir("${projectDir}/build/resources/main/web-view")
        outputs.cacheIf { true }
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
