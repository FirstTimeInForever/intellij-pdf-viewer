package com.firsttimeinforever.intellij.pdf.viewer.ui.editor

import java.io.File
import java.io.FileNotFoundException

object ResourceLoader {
    fun load(file: File): ByteArray {
        val targetPath = ensureCorrectFormedPath(file)
        val stream = this::class.java.getResourceAsStream(targetPath)
            ?: throw FileNotFoundException("Could not load resource file: $targetPath")
        return stream.readAllBytes()
    }

    // Fix incorrect pathing on windows, duh
    private fun ensureCorrectFormedPath(file: File): String {
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            return file.toString().replace('\\', '/')
        }
        return file.toString()
    }
}
