package com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.types


external class PdfViewerApplication {
  val pdfSidebar: PdfViewerSidebar
  val pdfAttachmentViewer: PdfAttachmentViewer
  val pdfDocument: PdfDocument
  val pdfOutlineViewer: PdfOutlineViewer
  val pdfHistory: PdfHistory
  val pdfLinkService: PdfLinkService
  val pdfViewer: PdfViewer
  val eventBus: PdfEventBus
  val findController: PdfFindController

  val initializedPromise: dynamic

  val pdfDocumentProperties: dynamic

  val baseUrl: String

  fun requestPresentationMode()
}
