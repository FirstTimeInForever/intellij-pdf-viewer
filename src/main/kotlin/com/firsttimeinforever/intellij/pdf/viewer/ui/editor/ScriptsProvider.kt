package com.firsttimeinforever.intellij.pdf.viewer.ui.editor

import com.intellij.openapi.diagnostic.DefaultLogger
import com.intellij.openapi.diagnostic.Logger


class ScriptsProvider {
    companion object {
        private val logger = Logger.getInstance(ScriptsProvider::class.java)

        fun load(path: String): String {
            val stream = this::class.java.getResourceAsStream(path)
            if (stream == null) {
                logger.error("Could not load file: $path")
            }
            return stream.use {
                it.bufferedReader().use {
                    it.readText()
                }
            }
        }
    }
}
