package com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.types

external class PdfHistory {
  fun pushCurrentPosition()

  fun forward()

  fun back()

  @Suppress("PropertyName")
  val _position: dynamic
}
