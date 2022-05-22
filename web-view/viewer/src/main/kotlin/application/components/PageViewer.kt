package application.components

import pdfjs.lib.display.PdfDocumentProxy
import pdfjs.lib.display.pageNumbers
import pdfjs.web.PdfLinkService
import react.*

external interface PageViewerProps: Props {
  var document: PdfDocumentProxy
  var linkService: PdfLinkService
  var viewportProperties: PageViewportProperties
}

val pageViewer = FC<PageViewerProps> { props ->
  val document = props.document
  for (pageNumber in document.pageNumbers) {
    val page = document.getPage(pageNumber)
    pageView {
      this.document = document
      this.page = page
      this.linkService = props.linkService
      rendererType = PageRendererType.CANVAS
      this.viewportProperties = props.viewportProperties
    }
  }
}
