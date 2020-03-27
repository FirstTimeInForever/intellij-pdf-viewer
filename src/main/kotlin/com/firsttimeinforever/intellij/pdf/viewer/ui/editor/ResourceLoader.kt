package com.firsttimeinforever.intellij.pdf.viewer.ui.editor

import java.io.File
import java.io.FileNotFoundException


class ResourceLoader {
    companion object {
        fun load(file: File): ByteArray {
            val stream = this::class.java.getResourceAsStream(file.toString())
                ?: throw FileNotFoundException("Could not load resource file: $file")
            return stream.readAllBytes()
        }
    }
}
