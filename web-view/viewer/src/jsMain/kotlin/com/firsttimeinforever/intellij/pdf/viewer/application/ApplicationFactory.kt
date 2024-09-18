package com.firsttimeinforever.intellij.pdf.viewer.application

import com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.ViewerAdapter
import com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.types.PdfViewerApplication
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.serialization.ExperimentalSerializationApi
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
    return Promise { resolve, _ ->
      document.addEventListener("webviewerloaded", {
        window.asDynamic().PDFViewerApplication.initializedPromise.then {
          window.asDynamic().PDFViewerApplication.eventBus.on("documentloaded") {
            console.log("pdf document loaded")
            resolve(Unit)
          }
        }
        console.log("pdf web viewer loaded")
      })
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
    }
  }

}

@ExperimentalSerializationApi
fun main() {
  ApplicationFactory.init()
}
