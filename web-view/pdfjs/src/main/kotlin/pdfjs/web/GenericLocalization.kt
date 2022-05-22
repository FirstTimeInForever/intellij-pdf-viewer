// @file:JsModule("pdfjs-dist/lib/web/genericl10n")
package pdfjs.web

import kotlin.js.Promise

// @JsName("GenericL10n")
@OptIn(ExperimentalJsExport::class)
@JsExport
class GenericLocalization(private val language: String) {
  fun getLanguage(): Promise<String> {
    return Promise.resolve(language)
  }

  fun getDirection(): Promise<dynamic> {
    return Promise.resolve("ltr")
  }

  fun get(key: String, arguments: dynamic, fallback: dynamic): Promise<String> {
    return Promise.resolve("template string")
  }

  fun translate(element: dynamic): Promise<dynamic> {
    return Promise.resolve("template string##")
  }
}
