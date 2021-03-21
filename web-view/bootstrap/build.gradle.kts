import com.moowork.gradle.node.npm.NpmTask
import java.nio.file.Paths

plugins {
    id("com.github.node-gradle.node") version "2.2.3"
}

repositories {
    gradlePluginPortal()
}

val default by configurations.creating

dependencies {
    default(project(":web-view:viewer", configurations.viewerApplicationBundle.name))
}

tasks {
    node {
        download = true
        version = project.properties["nodeVersion"].toString()
        nodeModulesDir = projectDir
    }
}

val copyApplicationBundle by tasks.registering(Copy::class) {
    from(default)
    into(File(projectDir, "application"))
}

val ensureNodeModulesInstalled by tasks.registering {
    dependsOn("nodeSetup")
    dependsOn("npmSetup")
    inputs.file(File(projectDir, "package.json")).withPathSensitivity(PathSensitivity.RELATIVE)
    inputs.file(File(projectDir, "package-lock.json")).withPathSensitivity(PathSensitivity.RELATIVE)
    if (!file(Paths.get(projectDir.toString(), "node_modules")).exists()) {
        dependsOn("npm_ci")
    }
}

val collectOuterSources by tasks.registering {
    val files = listOf(
        "package.json",
        "package-lock.json",
        "postinstall.js",
        "webpack.config.js",
        "webpack.config.js",
        "missing-props.properties",
        "index.html"
    )
    for (file in files) {
        inputs.file(File(projectDir, file)).withPathSensitivity(PathSensitivity.RELATIVE)
    }
    inputs.dir("src").withPathSensitivity(PathSensitivity.RELATIVE)
    outputs.files(inputs.files)
}

val buildWebView by tasks.registering(NpmTask::class) {
    dependsOn(ensureNodeModulesInstalled)
    inputs.files(
        collectOuterSources.map { it.outputs },
        copyApplicationBundle.map { it.outputs }
    )
    setArgs(listOf("run", "build"))
    outputs.dir(File(projectDir, "build"))
    // outputs.cacheIf { true }
}

artifacts {
    add(default.name, buildWebView)
}
