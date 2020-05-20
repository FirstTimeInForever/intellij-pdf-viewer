import org.jetbrains.intellij.tasks.*
import com.moowork.gradle.node.npm.*

plugins {
    id("org.jetbrains.intellij") version "0.4.18"
    kotlin("jvm") version "1.3.70"
    kotlin("plugin.serialization") version "1.3.70"
    java
    id("com.github.node-gradle.node") version "2.2.3"
}

group = "com.firsttimeinforever.intellij.pdf.viewer"
version = "0.0.4"

repositories {
    mavenCentral()
    maven("https://www.jetbrains.com/intellij-repository/snapshots")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testCompile("junit", "junit", "4.12")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")
}

intellij {
    // Build against next EAP version
    version = "LATEST-EAP-SNAPSHOT"
}
configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

val webviewSourceDirectory = file("${projectDir}/src/main/web-view")

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
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
    jvmArgs("--add-exports", "java.base/jdk.internal.vm=ALL-UNNAMED")
    jbrVersion("jbr_jcef-11_0_6b840.3")
}
