package com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.types

external class PdfHistory {
  fun pushCurrentPosition()

  fun forward()

  fun back()

  val _position: dynamic
}
