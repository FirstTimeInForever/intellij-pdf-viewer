package com.firsttimeinforever.intellij.pdf.viewer.utility

import com.firsttimeinforever.intellij.pdf.viewer.PdfViewerBundle
import com.intellij.openapi.diagnostic.logger
import java.io.FileNotFoundException
import java.nio.charset.Charset

internal object PdfResourceLoader {
  private val logger = logger<PdfResourceLoader>()

  // FIXME: Determine resource charset
  inline fun <reified T> load(path: String): ByteArray {
    logger.info("Loading internal resource: $path")
    return T::class.java.getResourceAsStream(path).use {
      if (it == null) {
        throw FileNotFoundException("Could not load resource file: $path")
      }
      it.readBytes()
    }
  }

  fun loadFromRoot(path: String): ByteArray {
    return load<PdfViewerBundle>(path)
  }

  inline fun <reified T> loadString(path: String): String {
    return load<T>(path).toString(Charset.defaultCharset())
  }
}
