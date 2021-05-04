package com.firsttimeinforever.intellij.pdf.viewer.utility

import com.firsttimeinforever.intellij.pdf.viewer.PdfViewerBundle
import java.io.FileNotFoundException
import java.nio.charset.Charset
import java.nio.file.Path
import java.nio.file.Paths

internal object PdfResourceLoader {
  private val shouldFixPaths = System.getProperty("os.name").toLowerCase().contains("windows")

  // FIXME: Determine resource charset
  inline fun <reified T> load(path: Path): ByteArray {
    return T::class.java.getResourceAsStream(path.toString()).use {
      if (it == null) {
        throw FileNotFoundException("Could not load resource file: $path")
      }
      it.readBytes()
    }
  }

  fun loadFromRoot(path: Path): ByteArray {
    return load<PdfViewerBundle>(path)
  }

  inline fun <reified T> load(first: String, vararg rest: String): ByteArray {
    return load<T>(Paths.get(first, *rest))
  }

  inline fun <reified T> loadString(first: String, vararg rest: String): String {
    return load<T>(first, *rest).toString(Charset.defaultCharset())
  }
}
