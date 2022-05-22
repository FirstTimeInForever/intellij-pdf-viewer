@file:OptIn(DelicateCoroutinesApi::class)

package application.components.layers

import application.ReactUtilities.withReference
import application.components.PageViewportProperties
import application.components.PageViewportProperties.Companion.obtainViewport
import application.useEffect
import csstype.NamedColor
import emotion.react.css
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.dom.clear
import org.w3c.dom.HTMLElement
import org.w3c.dom.svg.SVGElement
import pdfjs.lib.PdfPageViewport
import pdfjs.lib.SvgGraphics
import pdfjs.lib.display.PdfPageProxy
import react.*
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.span

external interface SvgMainLayerProps: Props {
  var page: PdfPageProxy
  var viewportProperties: PageViewportProperties
}

val svgMainLayer = FC<SvgMainLayerProps> { props ->
  val page = props.page
  var svg by useState<SVGElement?>(null)
  val containerReference = useRef<HTMLElement>(null)
  GlobalScope.useEffect(props.page, props.viewportProperties) {
    val container = containerReference.current ?: return@useEffect
    val operators = page.getOperatorList().await()
    val graphics = SvgGraphics(page.commonObjects, page.objects)
    val viewport = props.page.obtainViewport(props.viewportProperties)
    val renderedSvg = graphics.getSvg(operators, viewport).await()
    svg = renderedSvg
    container.apply {
      clear()
      appendChild(renderedSvg)
    }
  }
  when (svg) {
    null -> svgNotReady.create()
    else -> svgContainer(containerReference)
  }
}

private fun ChildrenBuilder.svgContainer(reference: RefObject<HTMLElement>) {
  div {
    withReference(reference)
    css {
      backgroundColor = NamedColor.white
    }
  }
}

private val svgNotReady = FC<Props> {
  div {
    span { +"Svg for this page is not ready yet" }
  }
}
