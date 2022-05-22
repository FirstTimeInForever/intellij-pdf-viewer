package application.components.layers

import application.ReactUtilities.withReference
import application.components.PageViewportProperties
import application.components.PageViewportProperties.Companion.obtainViewport
import application.useStateWithEffect
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import org.w3c.dom.HTMLElement
import pdfjs.lib.AnnotationLayer
import pdfjs.lib.AnnotationLayerRenderOptions
import pdfjs.lib.PdfPageViewport
import pdfjs.lib.display.GetViewportOptions
import pdfjs.lib.display.PdfPageProxy
import pdfjs.web.PdfLinkService
import react.*
import react.dom.html.ReactHTML.div

external interface AnnotationLayerProps: Props {
  var page: PdfPageProxy
  var linkService: PdfLinkService
  var viewportProperties: PageViewportProperties
}

@OptIn(DelicateCoroutinesApi::class)
val annotationLayer = FC<AnnotationLayerProps> { props ->
  val annotationsState by GlobalScope.useStateWithEffect(props.viewportProperties, props.linkService) {
    props.page.getAnnotations().await()
  }
  val annotations = annotationsState ?: return@FC
  annotationRenderer {
    this.page = props.page
    this.annotations = annotations
    this.linkService = props.linkService
    this.viewport = props.page.obtainViewport(props.viewportProperties)
  }
}

private external interface AnnotationRendererProps: Props {
  var page: PdfPageProxy
  var annotations: dynamic
  var linkService: PdfLinkService
  var viewport: PdfPageViewport
}

private val annotationRenderer = FC<AnnotationRendererProps> { props ->
  val elementReference = createRef<HTMLElement>()
  useEffect {
    val element = elementReference.current ?: return@useEffect
    val options = AnnotationLayerRenderOptions(
      annotations = props.annotations,
      element = element,
      linkService = props.linkService,
      page = props.page,
      viewport = props.viewport
    )
    element.innerHTML = ""
    AnnotationLayer.render(options)
  }
  div {
    // classNames("pdf-viewer-annotation-layer", "annotationLayer")
    withReference(elementReference)
  }
}
