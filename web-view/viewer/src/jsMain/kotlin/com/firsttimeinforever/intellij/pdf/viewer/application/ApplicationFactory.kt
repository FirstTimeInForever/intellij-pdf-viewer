package com.firsttimeinforever.intellij.pdf.viewer.application

import com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.ViewerAdapter
import com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.types.PdfViewerApplication
import kotlinx.serialization.ExperimentalSerializationApi

/**
 * This is basically an adapter for easy calls from plain Javascript.
 */
@ExperimentalJsExport
@JsExport
@ExperimentalSerializationApi
object ApplicationFactory {
  @Suppress("UNUSED")
  fun createApplication(viewerApp: PdfViewerApplication): dynamic {
    return Application(ViewerAdapter(viewerApp))
  }

  @Suppress("UNUSED", "NON_EXPORTABLE_TYPE")
  fun startApplication(application: Application) {
    application.run()
  }
}
