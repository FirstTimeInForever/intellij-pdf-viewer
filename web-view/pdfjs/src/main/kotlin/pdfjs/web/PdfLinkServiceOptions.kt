package pdfjs.web

import pdfjs.web.event.utils.PdfEventBus

@OptIn(ExperimentalJsExport::class)
@JsExport
data class PdfLinkServiceOptions(val eventBus: PdfEventBus)
