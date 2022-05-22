@file:JsModule("pdfjs-dist/lib/web/pdf_link_service")
package pdfjs.web

import pdfjs.lib.display.PdfDocumentProxy

@JsName("PDFLinkService")
external class PdfLinkService(options: PdfLinkServiceOptions) {
  fun setViewer(viewer: PdfViewer)

  fun setDocument(document: PdfDocumentProxy, other: Any?)
}
