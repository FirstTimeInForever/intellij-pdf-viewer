@file:OptIn(ExperimentalJsExport::class)
package pdfjs.lib

import org.w3c.dom.HTMLElement
import pdfjs.lib.display.PdfPageProxy
import pdfjs.lib.display.PdfTextItem
import pdfjs.web.PdfLinkService

@JsExport
data class AnnotationLayerRenderOptions(
  val annotations: dynamic,
  @JsName("div")
  val element: HTMLElement,
  val linkService: PdfLinkService,
  val page: PdfPageProxy,
  val viewport: PdfPageViewport,
  val imageResourcesPath: String = "./",
  val renderForms: Boolean = false
)

val PdfTextItem.fontHeight
  get() = transform[0]

val PdfTextItem.fontWidth
  get() = transform[1]

val PdfTextItem.offsetX
  get() = transform[2]

val PdfTextItem.offsetY
  get() = transform[3]

val PdfTextItem.x
  get() = transform[4]

val PdfTextItem.y
  get() = transform[5]

val PdfPageViewport.xmin
  get() = viewBox[0]

val PdfPageViewport.xmax
  get() = viewBox[2]

val PdfPageViewport.ymin
  get() = viewBox[1]

val PdfPageViewport.ymax
  get() = viewBox[3]
