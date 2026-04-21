import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
  kotlin("jvm") apply false
  kotlin("multiplatform") apply false
  kotlin("plugin.serialization") apply false
}

// Pin JVM target to 21 across all subprojects. Otherwise Gradle defaults to the
// JDK running the build (e.g. JDK 24 on a recent macOS toolchain), producing
// class files that the IDE's bundled JBR 21 cannot load. That manifests as
// `UnsupportedClassVersionError` at plugin load time and makes every extension
// point (fileType, fileEditorProvider, etc.) silently fail to register.
subprojects {
  tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_21)
    }
  }
  tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
  }
}
