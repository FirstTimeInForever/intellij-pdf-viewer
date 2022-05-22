package pdfjs.web

import org.w3c.dom.Element
import pdfjs.web.event.utils.PdfEventBus

@OptIn(ExperimentalJsExport::class)
@JsExport
data class PdfThumbnailViewerOptions(
  val container: Element,
  val eventBus: PdfEventBus,
  val linkService: PdfLinkService,
  val renderingQueue: PdfRenderingQueue,
  @JsName("l10n")
  val localization: GenericLocalization
)
