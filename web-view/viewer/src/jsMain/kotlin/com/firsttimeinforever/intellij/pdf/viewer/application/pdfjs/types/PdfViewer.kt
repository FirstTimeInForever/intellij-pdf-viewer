package com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.types

external class PdfViewer {
  @Suppress("PropertyName")
  val _location: PdfViewerLocation
  @Suppress("PropertyName")
  val _pages: Array<InternalPageObject>
  var spreadMode: Int
  val scrollMode: Int
  val pagesRotation: Int
  val pagesCount: Int
  var currentPageNumber: Int
  val currentScale: Int
  var currentScaleValue: String
}
