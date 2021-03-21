package com.firsttimeinforever.intellij.pdf.viewer.utility

import java.io.File
import java.io.FileNotFoundException
import java.nio.charset.Charset
import java.nio.file.Path
import java.nio.file.Paths

internal object PdfResourceLoader {
    private val shouldFixPaths = System.getProperty("os.name").toLowerCase().contains("windows")

    // FIXME: Determine resource charset
    fun load(file: File): ByteArray {
        val targetPath = ensureCorrectFormedPath(file)
        return this::class.java.getResourceAsStream(targetPath).use {
            if (it == null) {
                throw FileNotFoundException("Could not load resource file: $targetPath")
            }
            it.readBytes()
        }
    }

    fun load(path: Path): ByteArray {
        return load(path.toFile())
    }

    fun load(first: String, vararg rest: String): ByteArray {
        return load(Paths.get(first, *rest))
    }

    fun loadString(first: String, vararg rest: String): String {
        return load(first, *rest).toString(Charset.defaultCharset())
    }

    // FIXME: Use correct resolve methods
    // Fix incorrect pathing on windows, duh
    private fun ensureCorrectFormedPath(file: File): String {
        val path = file.toString()
        return when {
            shouldFixPaths -> path.replace('\\', '/')
            else -> path
        }
    }
}
