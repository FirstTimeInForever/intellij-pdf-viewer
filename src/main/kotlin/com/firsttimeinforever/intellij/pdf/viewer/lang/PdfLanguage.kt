package com.firsttimeinforever.intellij.pdf.viewer.lang

import com.intellij.lang.Language

// TODO: Research proper mime type for pdf files
class PdfLanguage private constructor(): Language("PDF", "application/pdf") {
    companion object {
        val INSTANCE = PdfLanguage()
    }
}
