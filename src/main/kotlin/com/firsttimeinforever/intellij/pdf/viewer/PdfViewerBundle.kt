package com.firsttimeinforever.intellij.pdf.viewer

import com.intellij.DynamicBundle
import org.jetbrains.annotations.PropertyKey

class PdfViewerBundle private constructor(): DynamicBundle(BUNDLE) {
    companion object {
        val INSTANCE = PdfViewerBundle()
        const val BUNDLE = "messages.PdfViewerBundle"

        fun message(
            @PropertyKey(resourceBundle = BUNDLE) key: String,
            vararg params: Any
        ): String =
            INSTANCE.getMessage(key, params)
    }
}
