package com.firsttimeinforever.intellij.pdf.viewer.ui.editor

class ScriptsProvider {
    companion object {
        val INDEX = load("/scripts/index.html")

        private fun load(path: String): String {
            val stream = this::class.java.getResourceAsStream(path)
            return stream.use {
                it.bufferedReader().use {
                    it.readText()
                }
            }
        }
    }
}
