@file:JsModule("pdfjs-dist/lib/web/pdf_thumbnail_viewer")
package pdfjs.web

import pdfjs.lib.display.PdfDocumentProxy

@JsName("PDFThumbnailViewer")
external class PdfThumbnailViewer(options: PdfThumbnailViewerOptions) {
  fun setDocument(document: PdfDocumentProxy)

  fun setPageLabels(labels: Array<String>)

  fun getThumbnail(pageNumber: Int): dynamic
}
