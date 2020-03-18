import org.jetbrains.intellij.tasks.*
import org.gradle.internal.os.OperatingSystem
import java.io.File
import de.undercouch.gradle.tasks.download.Download as DownloadTask
import java.nio.file.Files


plugins {
    id("org.jetbrains.intellij") version "0.4.16"
    java
    kotlin("jvm") version "1.3.70"
    id("de.undercouch.download") version "4.0.4"
}

group = "com.firsttimeinforever.intellij.pdf.viewer"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
    maven("https://www.jetbrains.com/intellij-repository/snapshots")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testCompile("junit", "junit", "4.12")
}

intellij {
    version = "IC-LATEST-EAP-SNAPSHOT"
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

tasks.getByName<PatchPluginXmlTask>("patchPluginXml") {
    changeNotes("""
      Add change notes here.<br>
      <em>most HTML tags may be used</em>""")
}

tasks.register<DownloadTask>("downloadJcefJbr") {
    val urlBase = "https://bintray.com/jetbrains/intellij-jbr/download_file?file_path="
    val prefix = "jbr_jcef-11_0_6"
    val suffix = "x64-b765.15.tar.gz"
    val os = when(OperatingSystem.current()) {
        OperatingSystem.MAC_OS -> "osx"
        OperatingSystem.LINUX -> "linux"
        OperatingSystem.WINDOWS -> "windows"
        else -> throw IllegalArgumentException()
    }
    val targetName = "$prefix-$os-$suffix"
    src("$urlBase$targetName")
    dest(File(buildDir, targetName))
    overwrite(false)
}

// Copy task will not preserve symlinks
tasks.register<Copy>("unzipJcefJbr") {
    dependsOn("downloadJcefJbr")
    // Will be unpacked into build/jbr
    val target = tasks.getByName<DownloadTask>("downloadJcefJbr").dest
    from(tarTree(resources.gzip(target)))
    into(buildDir)
    // Fix broken symlinks
    doLast {
        val target = File(buildDir, "jbr/Contents/Frameworks/Chromium Embedded Framework.framework")
        Files.deleteIfExists(File(target, "Chromium Embedded Framework").toPath())
        Files.deleteIfExists(File(target, "Libraries").toPath())
        Files.deleteIfExists(File(target, "Resources").toPath())
        Files.deleteIfExists(File(target, "Versions/Current").toPath())
        Files.createSymbolicLink(File(target, "Chromium Embedded Framework").toPath(),
            File(target, "Versions/A/Chromium Embedded Framework").toPath())
        Files.createSymbolicLink(File(target, "Libraries").toPath(), File(target, "Versions/A/Libraries").toPath())
        Files.createSymbolicLink(File(target, "Resources").toPath(), File(target, "Versions/A/Resources").toPath())
        Files.createSymbolicLink(File(target, "Versions/Current").toPath(), File(target, "Versions/A").toPath())
    }
}

tasks.register("provideJcefJbr") {
    dependsOn("unzipJcefJbr")
}

tasks.register<RunIdeTask>("runIdeWithJcefJbr") {
    dependsOn("provideJcefJbr")
    executable(File(buildDir, "jbr/Contents/Home/bin/java").absoluteFile)
}

tasks.withType<RunIdeTask>() {
    systemProperties["ide.browser.jcef.enabled"] = true
    jvmArgs("--add-exports", "java.base/jdk.internal.vm=ALL-UNNAMED")
}
