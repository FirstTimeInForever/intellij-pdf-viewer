import org.jetbrains.intellij.tasks.*
import com.moowork.gradle.node.npm.*

plugins {
    id("org.jetbrains.intellij") version "0.4.18"
    kotlin("jvm") version "1.3.70"
    java
    id("com.github.node-gradle.node") version "2.2.3"
}

group = "com.firsttimeinforever.intellij.pdf.viewer"
version = "0.0.2"

repositories {
    mavenCentral()
    maven("https://www.jetbrains.com/intellij-repository/snapshots")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testCompile("junit", "junit", "4.12")
}

intellij {
    // Build against next EAP version
    version = "LATEST-EAP-SNAPSHOT"
}
configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
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
        nodeModulesDir = file("${projectDir}/src/main/web-view")
    }
}

tasks.register<NpmTask>("webViewBuild") {
    if (!file("${projectDir}/src/main/web-view/node_modules").exists()) {
        dependsOn("npm_ci")
    }
    else {
        println("Skipping npm_ci step")
    }
    setArgs(listOf("run", "build"))
}

tasks.getByName("buildPlugin") {
    dependsOn("webViewBuild")
}

tasks.withType<RunIdeTask>() {
    systemProperties["ide.browser.jcef.enabled"] = true
    jvmArgs("--add-exports", "java.base/jdk.internal.vm=ALL-UNNAMED")
    jbrVersion("jbr_jcef-11_0_6b765.25")
}
