@file:OptIn(ExperimentalJsExport::class)
package pdfjs.lib.display

import org.w3c.dom.RenderingContext
import pdfjs.lib.PdfPageViewport
import kotlin.js.Promise

@JsExport
data class GetViewportOptions(
  val scale: Float,
  val rotation: Float = 0f,
  val offsetX: Int = 0,
  val offsetY: Int = 0,
  val dontFlip: Boolean = false
)

val PdfDocumentProxy.pageNumbers
  get() = 1..pagesCount

@JsExport
data class CanvasPageRenderContext(
  val annotationMode: Int,
  val canvasContext: RenderingContext,
  val viewport: PdfPageViewport
)

external interface PageCanvasRenderTask {
  val promise: Promise<Unit>
}
