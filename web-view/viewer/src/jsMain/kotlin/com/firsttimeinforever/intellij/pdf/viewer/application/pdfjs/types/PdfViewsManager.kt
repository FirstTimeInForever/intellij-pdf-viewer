package com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.types

/**
 * Manages the sidebar, see https://github.com/mozilla/pdf.js/blob/master/web/views_manager.js
 */
external class PdfViewsManager {
  fun switchView(mode: Number, forceOpen: Boolean = definedExternally)
  fun open()
  fun close()
  fun toggle(visibility: Boolean = definedExternally)
  val visibleView: Number
}
