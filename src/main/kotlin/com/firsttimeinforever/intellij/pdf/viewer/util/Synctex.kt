package com.firsttimeinforever.intellij.pdf.viewer.util

import com.intellij.openapi.vfs.VirtualFile

/**
 * Checks if there is a SyncTeX file in the same folder as [this] file, with the same base name (until the first period).
 *
 * Call this function on a pdf file to check if it has an accompanying SyncTeX file in the same folder.
 */
fun VirtualFile.isSynctexFileAvailable(): Boolean = parent.children
    .filter { it.name.contains("synctex") }
    .any { file ->
        file.name.takeWhile { it != '.' } == name.takeWhile { it != '.' }
    }

fun isSynctexInstalled(): Boolean =
    "synctex version".runCommand()?.contains("This is SyncTeX command line utility") == true
