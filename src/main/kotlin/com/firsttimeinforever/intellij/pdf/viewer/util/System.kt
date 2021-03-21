package com.firsttimeinforever.intellij.pdf.viewer.util

import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

fun runCommand(vararg parts: String, directory: File? = null): String? = try {
    val proc = if (directory != null) {
        ProcessBuilder(*parts).directory(directory)
    } else {
        ProcessBuilder(*parts)
    }
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()

    if (proc.waitFor(3, TimeUnit.SECONDS)) {
        proc.inputStream.bufferedReader().readText()
    }
    else {
        proc.destroy()
        proc.waitFor()
        null
    }
} catch (e: IOException) {
    e.printStackTrace()
    null
}

fun String.runCommand(directory: File? = null) =
    runCommand(*(this.split("\\s".toRegex())).toTypedArray(), directory = directory)