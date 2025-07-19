import de.undercouch.gradle.tasks.download.Download

plugins {
  id("de.undercouch.download") version "5.6.0"
}

repositories {
  gradlePluginPortal()
}

val default by configurations.creating

dependencies {
  default(project(":web-view:viewer", configurations.viewerApplicationBundle.name))
}

val pdfjsVersion = project.findProperty("pdfjsVersion").toString()

val downloadZipFile by tasks.registering(Download::class) {
  val destFile = layout.projectDirectory.asFile.resolve("dist/pdfjs-$pdfjsVersion-dist.zip")
  src("https://github.com/mozilla/pdf.js/releases/download/v$pdfjsVersion/pdfjs-$pdfjsVersion-dist.zip")
  dest(destFile)
  tempAndMove(true)
  onlyIf { !destFile.exists() || destFile.length() == 0L }
}

val downloadAndUnzipFile by tasks.registering(Copy::class) {
  dependsOn(downloadZipFile)
  from(zipTree(downloadZipFile.get().dest)) {
    exclude("*/*.pdf")
    exclude("*/*.js.map")
    exclude("*/*.mjs.map")
  }
  into(project.layout.buildDirectory)
  doLast {
    val viewerHtml = layout.buildDirectory.get().asFile.resolve("web/viewer.html")
    val modifiedContent = viewerHtml.readText()
      .replace("(<link rel=\"stylesheet\" href=\"viewer.css\">)".toRegex(), "$1<link rel=\"stylesheet\" href=\"../fixes.css\">")
      .replace("(<script src=\"viewer.m?js\"[^<>]*></script>)".toRegex(), "$1<script src=\"../viewer.js\"></script>")
    viewerHtml.writeText(modifiedContent)
    val tmpdir = file(layout.buildDirectory.get().asFile.resolve("tmp"))
    if (tmpdir.exists()) {
        tmpdir.deleteRecursively()
    }
  }
}

val buildWebView by tasks.registering(Copy::class) {
  dependsOn(downloadAndUnzipFile)
  from("src") {
    include("*.css")
  }
  into(project.layout.buildDirectory)
}

artifacts {
  add(default.name, buildWebView)
}
