@file:JsModule("pdfjs-dist/lib/web/pdf_rendering_queue")
package pdfjs.web

@JsName("PDFRenderingQueue")
external class PdfRenderingQueue {
  fun setViewer(viewer: PdfViewer)

  fun setThumbnailViewer(thumbnailViewer: PdfThumbnailViewer)

  fun isHighestPriority(view: dynamic): Boolean

  fun hasViewer(): Boolean
}
