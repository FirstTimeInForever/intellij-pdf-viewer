import application.*
import kotlinx.browser.window
import kotlinx.coroutines.*
import org.w3c.dom.Element
import pdfjs.PdfCore
import react.*
import react.dom.client.createRoot

fun obtainRootElement(): Element {
  val element = window.document.getElementById("root")
  return requireNotNull(element)
}

suspend fun runApplication() {
  val url = "https://raw.githubusercontent.com/mozilla/pdf.js/ba2edeae/web/compressed.tracemonkey-pldi-09.pdf"
  // val url = "./zsh_a4.pdf"
  val document = PdfCore.obtainDocument(url).promise.await()
  val root = createRoot(obtainRootElement())
  val container = application.create {
    this.document = document
  }
  root.render(container)
}

suspend fun main() {
  WindowUtils.invokeOnContentLoadedIfNeeded {
    PdfCore.performBaseSetup()
    runApplication()
  }
}
