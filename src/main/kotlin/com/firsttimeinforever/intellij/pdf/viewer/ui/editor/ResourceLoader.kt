package com.firsttimeinforever.intellij.pdf.viewer.ui.editor

import java.io.File
import java.io.FileNotFoundException

object ResourceLoader {
    private val shouldFixPaths =
        System.getProperty("os.name").toLowerCase().contains("windows")

    fun load(file: File): ByteArray {
        val targetPath = ensureCorrectFormedPath(file)
        return this::class.java.getResourceAsStream(targetPath).use {
            if (it == null) {
                throw FileNotFoundException("Could not load resource file: $targetPath")
            }
            it.readBytes()
        }
    }

    // Fix incorrect pathing on windows, duh
    private fun ensureCorrectFormedPath(file: File): String {
        if (shouldFixPaths) {
            return file.toString().replace('\\', '/')
        }
        return file.toString()
    }
}
