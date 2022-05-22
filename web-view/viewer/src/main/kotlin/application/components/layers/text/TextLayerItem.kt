package application.components.layers.text

import application.ReactUtilities.withReference
import application.components.PageViewportProperties
import application.components.PageViewportProperties.Companion.obtainViewport
import csstype.*
import csstype.px
import emotion.react.css
import org.w3c.dom.HTMLElement
import pdfjs.lib.*
import pdfjs.lib.display.PdfPageProxy
import pdfjs.lib.display.PdfTextItem
import react.*
import react.dom.html.ReactHTML.span
import kotlin.Float

external interface TextLayerItemProps: Props {
  var page: PdfPageProxy
  var textItem: PdfTextItem
  var width: Float
  var height: Float
  var viewportProperties: PageViewportProperties
}

val textLayerItem = FC<TextLayerItemProps> { props ->
  val elementReference = useRef<HTMLElement>(null)
  useEffect {
    val element = elementReference.current ?: return@useEffect
    transformElement(element, props)
  }
  span {
    withReference(elementReference)
    css {
      height = 1.em
      fontFamily = string("sans-serif")
      position = Position.absolute
      transformOrigin = "left bottom".asDynamic()
      whiteSpace = WhiteSpace.pre
      pointerEvents = PointerEvents.all
    }
    +props.textItem.asDynamic().str.unsafeCast<String>()
  }
}

private fun transformElement(element: HTMLElement, props: TextLayerItemProps) {
  val viewport = props.page.obtainViewport(props.viewportProperties)
  val scale = props.viewportProperties.scale
  val targetWidth = props.width * scale
  val actualWidth = element.getBoundingClientRect().width
  val transform = "scaleX(${targetWidth / actualWidth})"
  // const ascent = fontData ? fontData.ascent : 0;
  // if (ascent) {
  //   transform += ` translateY(${(1 - ascent) * 100}%)`;
  // }
  // val top = props.item.run { x + offsetX + viewport.ymin }
  val top = props.textItem.run { viewport.ymax - (y + offsetY) }
  val left = props.textItem.run { x - viewport.xmin }
  element.style.apply {
    // this.transform = transform
    fontSize = (props.textItem.fontHeight * scale).px.toString()
    this.top = (top * scale).px.toString()
    this.left = (left * scale).px.toString()
  }
}
