@file:JsModule("pdfjs-dist")
package pdfjs.lib

import org.w3c.dom.svg.SVGElement
import pdfjs.lib.display.PdfDocumentLoadingTask
import kotlin.js.Promise

internal external object GlobalWorkerOptions {
  @JsName("workerSrc")
  var workerSource: String
}

internal external fun getDocument(options: Any): PdfDocumentLoadingTask

@JsName("SVGGraphics")
external class SvgGraphics(
  pageCommonObjects: dynamic,
  pageObjects: dynamic,
  forceDataSchema: Boolean = definedExternally
) {
  @JsName("getSVG")
  fun getSvg(operatorList: dynamic, viewport: dynamic): Promise<SVGElement>
}

external class AnnotationLayer {
  companion object {
    fun render(options: AnnotationLayerRenderOptions)
  }
}

external object AnnotationMode {
  val DISABLE: Int
  val ENABLE: Int
  val ENABLE_FORMS: Int
  val ENABLE_STORAGE: Int
}
