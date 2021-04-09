package com.firsttimeinforever.intellij.pdf.viewer.mpi

expect object MessageEncoder {
    fun encode(data: String): String

    fun decode(data: String): String
}
