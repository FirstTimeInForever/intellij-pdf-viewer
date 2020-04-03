import org.jetbrains.intellij.tasks.*

plugins {
    id("org.jetbrains.intellij") version "0.4.18"
    kotlin("jvm") version "1.3.70"
    java
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
}

tasks.withType<RunIdeTask>() {
    systemProperties["ide.browser.jcef.enabled"] = true
    jvmArgs("--add-exports", "java.base/jdk.internal.vm=ALL-UNNAMED")
//    jbrVersion("jbr_jcef-11_0_6b765.15")
}
