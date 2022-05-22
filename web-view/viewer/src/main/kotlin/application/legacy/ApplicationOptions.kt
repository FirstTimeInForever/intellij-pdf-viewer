package application.legacy

import org.w3c.dom.Element
import pdfjs.lib.display.PdfDocumentProxy

data class ApplicationOptions(
  val document: PdfDocumentProxy,
  val viewerElement: Element,
  // val thumbnailViewerElement: Element
)
