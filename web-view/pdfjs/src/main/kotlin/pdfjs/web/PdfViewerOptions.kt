package pdfjs.web

import org.w3c.dom.Element

@OptIn(ExperimentalJsExport::class)
@JsExport
data class PdfViewerOptions(
  val container: Element,
  val eventBus: Any,
  val linkService: Any,
  val renderingQueue: PdfRenderingQueue,
  val renderer: String = "svg",
  val textLayerMode: Int = 0
)
