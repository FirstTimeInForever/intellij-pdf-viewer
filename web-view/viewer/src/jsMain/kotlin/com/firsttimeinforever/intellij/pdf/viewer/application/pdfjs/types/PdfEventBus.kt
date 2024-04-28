package com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.types

external class PdfEventBus {
  fun on(event: String, handler: (dynamic) -> Unit)

  fun dispatch(event: String, payload: dynamic)
}
