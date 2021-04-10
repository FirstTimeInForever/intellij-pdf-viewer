package com.firsttimeinforever.intellij.pdf.viewer.application

import com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.ViewerAdapter
import com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.types.PdfViewerApplication

@ExperimentalJsExport
@JsExport
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
