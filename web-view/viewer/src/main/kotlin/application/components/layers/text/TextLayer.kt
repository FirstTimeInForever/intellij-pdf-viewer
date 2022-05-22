package application.components.layers.text

import application.ReactUtilities.withReference
import application.components.PageViewportProperties
import application.components.PageViewportProperties.Companion.obtainViewport
import application.useStateWithEffect
import csstype.*
import emotion.react.css
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import org.w3c.dom.HTMLElement
import pdfjs.lib.display.PdfPageProxy
import react.*
import react.dom.html.ReactHTML.div
import kotlin.js.Promise


external interface TextLayerProps: Props {
  var page: PdfPageProxy
  var viewportProperties: PageViewportProperties
}

private fun PdfPageProxy.obtainFontData(fontName: String): Promise<Any> {
  return Promise { resolve, _ -> commonObjects.get(fontName, resolve) as Any }
}

@OptIn(DelicateCoroutinesApi::class)
val textLayer = FC<TextLayerProps> { props ->
  val textItemsState by GlobalScope.useStateWithEffect(props.page) {
    props.page.getTextContent().await()
  }
  val viewport = props.page.obtainViewport(props.viewportProperties)
  val elementReference = useRef<HTMLElement>(null)
  useEffect {
    val element = elementReference.current ?: return@useEffect
    element.style.apply {
      transform = "translate(-50%, -50%) rotate(${props.viewportProperties.rotation}deg)"
      width = viewport.width.px.toString()
      height = viewport.height.px.toString()
    }
  }
  div {
    withReference(elementReference)
    css {
      position = Position.absolute
      top = 50.pct
      left = 50.pct
      color = NamedColor.transparent
    }
    val textItems = textItemsState ?: return@div
    for (item in textItems.items) {
      textLayerItem {
        this.page = props.page
        this.textItem = item
        this.width = viewport.width
        this.height = viewport.height
        this.viewportProperties = props.viewportProperties
      }
    }
  }
}
