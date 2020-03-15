package com.firsttimeinforever.intellij.pdf.viewer.ui.editor

import com.intellij.openapi.vfs.VirtualFile
import java.util.*

class PdfFileLoader {
    companion object {
        fun load(file: VirtualFile): String {
            val bytes = file.inputStream.use {
                it.readAllBytes()
            }
            return Base64.getEncoder().encodeToString(bytes)
        }
    }
}
