package com.firsttimeinforever.intellij.pdf.viewer.common.lang

import com.firsttimeinforever.intellij.pdf.viewer.common.icons.PdfViewerIcons
import com.intellij.openapi.fileTypes.UserBinaryFileType

object PdfFileType : UserBinaryFileType() {
  override fun getIcon() = PdfViewerIcons.PDF_FILE

  override fun getName() = "PDF"

  override fun getDefaultExtension() = "pdf"

  //TODO: Add proper description
  override fun getDescription() = "PDF"
}
