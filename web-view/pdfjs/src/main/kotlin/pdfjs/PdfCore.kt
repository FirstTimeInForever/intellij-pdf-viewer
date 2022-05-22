package pdfjs

import pdfjs.lib.GlobalWorkerOptions
import pdfjs.lib.display.PdfDocumentLoadingTask
import pdfjs.lib.getDocument
import kotlin.js.json

object PdfCore {
  fun setupWorker() {
    GlobalWorkerOptions.workerSource = "pdf.worker.min.js"
  }

  fun performBaseSetup() {
    setupWorker()
  }

  fun obtainDocument(url: String): PdfDocumentLoadingTask {
    val options = json(
      "url" to url,
      "cMapUrl" to "./cmaps/",
      "cMapPacked" to true
    )
    return getDocument(options)
  }
}
