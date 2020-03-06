package com.firsttimeinforever.intellij.pdf.viewer.lang

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

class PdfFileType private constructor(): LanguageFileType(PdfLanguage.INSTANCE) {
    companion object {
        private const val LANGUAGE_NAME = "PDF"
        private const val LANGUAGE_EXTENSION = "pdf"
        val INSTANCE = PdfFileType()
    }

    override fun getIcon(): Icon? {
        // TODO: Add valid icon path
        return null
    }

    override fun getName(): String {
        return LANGUAGE_NAME
    }

    override fun getDefaultExtension(): String {
        return LANGUAGE_EXTENSION
    }

    override fun getDescription(): String {
        return LANGUAGE_NAME
    }
}
