package com.firsttimeinforever.intellij.pdf.viewer.util

import com.intellij.openapi.vfs.VirtualFile

/**
 * Checks if there is a SyncTeX file in the same folder as [this] file, with the same base name (until the first period).
 * When there is no such SyncTeX file, all SyncTeX features should be disabled.
 *
 * Call this function on a pdf file to check if it has an accompanying SyncTeX file in the same folder.
 */
fun VirtualFile.isSynctexFileAvailable(): Boolean = parent.children
    .filter { it.name.contains("synctex") }
    .any { file ->
        file.name.takeWhile { it != '.' } == name.takeWhile { it != '.' }
    }

/**
 * Check if the SyncTeX command line utility is installed by trying to execute a SyncTeX command.
 */
fun isSynctexInstalled(): Boolean =
    "synctex version".runCommand()?.contains("This is SyncTeX command line utility") == true
