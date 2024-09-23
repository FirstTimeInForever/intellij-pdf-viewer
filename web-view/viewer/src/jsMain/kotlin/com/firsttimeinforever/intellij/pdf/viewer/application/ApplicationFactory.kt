package com.firsttimeinforever.intellij.pdf.viewer.application

import com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.ViewerAdapter
import com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.types.PdfViewerApplication
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.serialization.ExperimentalSerializationApi
import org.w3c.dom.COMPLETE
import org.w3c.dom.CustomEvent
import org.w3c.dom.DocumentReadyState
import org.w3c.dom.INTERACTIVE
import org.w3c.dom.events.Event
import kotlin.js.Promise

/**
 * This is basically an adapter for easy calls from plain Javascript.
 */
@ExperimentalSerializationApi
object ApplicationFactory {

  private fun createApplication(viewerApp: PdfViewerApplication): Application {
    return Application(ViewerAdapter(viewerApp))
  }

  private fun startApplication(application: Application) {
    application.run()
  }

  private fun waitForDocument(): Promise<Unit> {
    return Promise { resolve, reject ->
      val listener = { event: Event? ->
        val viewerApp = window.asDynamic().PDFViewerApplication
        if (viewerApp == undefined) {
          reject(Exception("no window.PDFViewerApplication, did pdf.js viewer load?"))
        } else if (event == null || event.type == "webviewerloaded") {
          console.log("PDF Viewer is ready")
          viewerApp.initializedPromise.then { viewerApp.eventBus.on("documentloaded") { resolve(Unit) } }
        }
      }
      if (document.readyState == DocumentReadyState.INTERACTIVE || document.readyState == DocumentReadyState.COMPLETE) {
        listener(null)
      } else {
        document.addEventListener("webviewerloaded", listener)
        document.addEventListener("DOMContentLoaded", listener, true)
      }
    }
  }

  private fun waitForIde(): Promise<Unit> {
    return Promise { resolve, _ ->
      window.addEventListener("IdeReady", {
        console.log("Ide is ready")
        resolve(Unit)
      }, true)
    }
  }

  private fun bootstrap():Promise<Array<out Unit>> {
    return Promise.all(arrayOf(waitForIde(), waitForDocument()))
  }

  fun init() {
    bootstrap().then {
      console.log("Starting application")
      val application = createApplication(window.asDynamic().PDFViewerApplication)
      startApplication(application)
    }.catch { error ->
      console.error("${error.message}")
    }
  }

}

@ExperimentalSerializationApi
fun main() {
  ApplicationFactory.init()
}
