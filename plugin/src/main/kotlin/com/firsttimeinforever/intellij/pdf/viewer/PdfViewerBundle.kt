package com.firsttimeinforever.intellij.pdf.viewer

import com.intellij.AbstractBundle
import org.jetbrains.annotations.PropertyKey
import java.util.*

object PdfViewerBundle {
    private const val BUNDLE_NAME = "messages.PdfViewerBundle"

    private val bundle by lazy { ResourceBundle.getBundle(BUNDLE_NAME) }

    fun message(
        @PropertyKey(resourceBundle = BUNDLE_NAME) key: String,
        vararg params: Any
    ): String {
        return AbstractBundle.message(bundle, key, params)
    }
}
