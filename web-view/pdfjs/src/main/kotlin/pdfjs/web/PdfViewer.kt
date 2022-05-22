@file:JsModule("pdfjs-dist/lib/web/pdf_viewer")
package pdfjs.web

import pdfjs.lib.display.PdfDocumentProxy
import kotlin.js.Promise

@JsName("PDFViewer")
external class PdfViewer(options: PdfViewerOptions) {
  fun setDocument(document: PdfDocumentProxy)

  fun getPageView(pageNumber: Int): dynamic

  val firstPagePromise: Promise<dynamic>
}
