package com.firsttimeinforever.intellij.pdf.viewer.lang

import com.firsttimeinforever.intellij.pdf.viewer.icons.PdfViewerIcons
import com.intellij.openapi.fileTypes.LanguageFileType

object PdfFileType: LanguageFileType(PdfLanguage) {
    override fun getIcon() = PdfViewerIcons.PDF_FILE

    override fun getName() = "PDF"

    override fun getDefaultExtension() = "pdf"

    //TODO: Add proper description
    override fun getDescription() = "PDF"
}
