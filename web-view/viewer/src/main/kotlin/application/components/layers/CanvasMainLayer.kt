package application.components.layers

import application.ReactUtilities.withReference
import application.components.PageViewportProperties
import application.components.PageViewportProperties.Companion.obtainViewport
import csstype.ClassName
import csstype.None
import csstype.px
import emotion.react.css
import kotlinx.browser.window
import org.w3c.dom.HTMLCanvasElement
import pdfjs.lib.AnnotationMode
import pdfjs.lib.display.CanvasPageRenderContext
import pdfjs.lib.display.GetViewportOptions
import pdfjs.lib.display.PdfPageProxy
import react.*
import react.dom.html.ReactHTML.canvas
import react.dom.html.ReactHTML.div
import kotlin.math.floor

external interface CanvasMainLayerProps: Props {
  var page: PdfPageProxy
  var viewportProperties: PageViewportProperties
}

private fun obtainPixelRatio(): Double {
  return window.devicePixelRatio
}

val canvasMainLayer = FC<CanvasMainLayerProps> { props ->
  val canvasReference = useRef<HTMLCanvasElement>(null)
  useEffect {
    val canvasElement = canvasReference.current ?: return@useEffect
    val viewport = props.page.obtainViewport(props.viewportProperties)
    val pixelRatio = obtainPixelRatio()
    val renderViewportProperties = props.viewportProperties.copy(
      scale = props.viewportProperties.scale * pixelRatio.toFloat()
    )
    val renderViewport = props.page.obtainViewport(renderViewportProperties)
    canvasElement.apply {
      width = renderViewport.width.toInt()
      height = renderViewport.height.toInt()
      style.width = floor(viewport.width).px.toString()
      style.height = floor(viewport.height).px.toString()
    }
    val options = CanvasPageRenderContext(
      annotationMode = AnnotationMode.ENABLE,
      canvasContext = canvasElement.getContext("2d")!!,
      renderViewport
    )
    props.page.render(options).promise.then {
      console.log("Page rendered")
    }
  }
  div {
    canvas {
      withReference(canvasReference)
      css {
        userSelect = None.none
      }
    }
  }
}
