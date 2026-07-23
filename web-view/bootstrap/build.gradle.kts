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
  val viewerCssRegex = "(<link rel=\"stylesheet\" href=\"viewer.css\" />)".toRegex()
  val viewerScriptRegex = "(<script src=\"viewer.m?js\"[^<>]*></script>)".toRegex()
  from(zipTree(downloadZipFile.get().dest)) {
    exclude("*/*.pdf")
    exclude("*/*.js.map")
    exclude("*/*.mjs.map")
    filesMatching("**/viewer.html") {
      // Patch viewer.html while copying to keep the task declarative/config-cache friendly.
      filter { line: String ->
        line
          .replace(viewerCssRegex, "$1<link rel=\"stylesheet\" href=\"../fixes.css\" />")
          .replace(viewerScriptRegex, "$1<script src=\"../viewer.js\"></script>")
      }
    }
  }
  into(project.layout.buildDirectory)
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
