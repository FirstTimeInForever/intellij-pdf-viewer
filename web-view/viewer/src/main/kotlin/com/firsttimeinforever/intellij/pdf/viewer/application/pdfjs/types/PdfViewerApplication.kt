package com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.types


external class PdfViewerApplication {
  val pdfSidebar: PdfViewerSidebar
  val pdfAttachmentViewer: PdfAttachmentViewer
  val pdfDocument: PdfDocument
  val pdfOutlineViewer: PdfOutlineViewer
  val pdfHistory: dynamic

  val pdfViewer: PdfViewer

  val eventBus: PdfEventBus

  val initializedPromise: dynamic
}
