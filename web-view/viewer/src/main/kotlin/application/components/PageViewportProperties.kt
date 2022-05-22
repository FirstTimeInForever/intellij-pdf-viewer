package application.components

import pdfjs.lib.PdfPageViewport
import pdfjs.lib.display.GetViewportOptions
import pdfjs.lib.display.PdfPageProxy

data class PageViewportProperties(
  val scale: Float,
  val rotation: Float
) {
  fun obtainViewport(page: PdfPageProxy): PdfPageViewport {
    return page.getViewport(GetViewportOptions(
      scale = scale,
      rotation = rotation
    ))
  }

  companion object {
    fun PdfPageProxy.obtainViewport(properties: PageViewportProperties): PdfPageViewport {
      return properties.obtainViewport(this)
    }
  }
}
