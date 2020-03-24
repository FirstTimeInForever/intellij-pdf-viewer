import org.jetbrains.intellij.tasks.*
import com.moowork.gradle.node.task.*
import com.moowork.gradle.node.npm.*

plugins {
    id("org.jetbrains.intellij") version "0.4.17"
    kotlin("jvm") version "1.3.70"
    id("com.github.node-gradle.node") version "2.2.3"
    java
    idea
}

group = "com.firsttimeinforever.intellij.pdf.viewer"
version = "1.0-SNAPSHOT"

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
    }
}

idea {
    module {
        excludeDirs.add(file("node_modules"))
    }
}

tasks.getByName<PatchPluginXmlTask>("patchPluginXml") {
    changeNotes("""
      Add change notes here.<br>
      <em>most HTML tags may be used</em>""")
}

tasks.register<NpmTask>("webViewBuild") {
    dependsOn("npm_ci")
    setArgs(listOf("run", "build"))
}

tasks.getByName("processResources") {
    dependsOn("webViewBuild")
}

tasks.withType<RunIdeTask>() {
    systemProperties["ide.browser.jcef.enabled"] = true
    jvmArgs("--add-exports", "java.base/jdk.internal.vm=ALL-UNNAMED")
//    executable(file("$buildDir/jbr/Contents/Home/bin/java"))
//    jbrVersion("jbr_jcef-11_0_6b765.15")
}
