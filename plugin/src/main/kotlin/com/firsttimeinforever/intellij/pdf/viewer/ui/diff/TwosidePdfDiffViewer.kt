package com.firsttimeinforever.intellij.pdf.viewer.ui.diff

import com.intellij.diff.DiffContext
import com.intellij.diff.requests.ContentDiffRequest
import com.intellij.diff.requests.DiffRequest
import com.intellij.diff.tools.holders.PdfEditorHolder
import com.intellij.diff.tools.util.side.TwosideDiffViewer
import com.intellij.openapi.progress.ProgressIndicator

class TwosidePdfDiffViewer(
  context: DiffContext,
  request: DiffRequest
) : TwosideDiffViewer<PdfEditorHolder>(context, request as ContentDiffRequest, PdfEditorHolder.Factory.INSTANCE) {
  override fun performRediff(indicator: ProgressIndicator): Runnable {
    return Runnable {
      // No-op for PDF diff viewer, as we don't support re-diffing
    }
  }
}
