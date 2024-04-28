package com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.types

import kotlin.js.Promise

external class PdfDocument {
  val numPages: Int
  fun getOutline(): Promise<Array<InternalOutline>?>
}
