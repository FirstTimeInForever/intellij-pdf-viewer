package com.firsttimeinforever.intellij.pdf.viewer.ui.diff

import com.firsttimeinforever.intellij.pdf.viewer.lang.PdfFileType
import com.intellij.diff.DiffContext
import com.intellij.diff.contents.FileContent
import com.intellij.diff.requests.ContentDiffRequest
import com.intellij.diff.requests.DiffRequest
import com.intellij.diff.tools.binary.BinaryDiffTool

class PdfTwoSideDiffTool : BinaryDiffTool() {
  override fun canShow(context: DiffContext, request: DiffRequest): Boolean {
    if (request !is ContentDiffRequest) return false
    if (request.contents.size != 2) return false
    val left = request.contents[0] as? FileContent ?: return false
    val right = request.contents[1] as? FileContent ?: return false

    return left.file.fileType == PdfFileType && right.file.fileType == PdfFileType
  }

  override fun getName(): String = "PDF two-side diff"

  override fun createComponent(context: DiffContext, request: DiffRequest): TwosidePdfDiffViewer {
    return TwosidePdfDiffViewer(context, request)
  }
}
