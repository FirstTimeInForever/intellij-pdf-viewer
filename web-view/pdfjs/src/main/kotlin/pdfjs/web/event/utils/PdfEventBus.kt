@file:JsModule("pdfjs-dist/lib/web/event_utils")
package pdfjs.web.event.utils

@JsName("EventBus")
external class PdfEventBus {
  fun on(event: String, handler: (dynamic) -> Unit)

  fun dispatch(event: String, payload: dynamic)
}
