package com.firsttimeinforever.intellij.pdf.viewer.util

import com.intellij.openapi.vfs.VirtualFile

fun VirtualFile.isSynctexAvailable(): Boolean = parent.children.any { file ->
    file.name.takeWhile { it != '.' } == name.takeWhile { it != '.' }
}

fun parseSynctexEditOutput(output: String): Pair<String, Int>? {
    val inputRegex = "Input:(?<file>[^\\n]+)".toRegex()
    val lineRegex = "Line:(?<line>\\d+)".toRegex()

    val filePath = inputRegex.find(output)?.groups?.get("file")?.value ?: return null
    val line = lineRegex.find(output)?.groups?.get("line")?.value?.toInt() ?: return null
    return filePath to line
}