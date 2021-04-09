package com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.types

external class PdfViewer {
    val _location: PdfViewerLocation
    val spreadMode: Int
    val scrollMode: Int
    val pagesRotation: Int
    val pagesCount: Int
    var currentPageNumber: Int
    val currentScale: Int
    val currentScaleValue: String
}
