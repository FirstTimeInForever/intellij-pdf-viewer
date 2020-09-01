package com.firsttimeinforever.intellij.pdf.viewer

import com.intellij.DynamicBundle
import org.jetbrains.annotations.PropertyKey

class PDFViewerBundle private constructor(): DynamicBundle(BUNDLE) {
    companion object {
        val INSTANCE = PDFViewerBundle()
        const val BUNDLE = "messages.PDFViewerBundle"

        fun message(
            @PropertyKey(resourceBundle = BUNDLE) key: String,
            vararg params: Object
        ): String =
            INSTANCE.getMessage(key, params)
    }
}
