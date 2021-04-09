package com.firsttimeinforever.intellij.pdf.viewer.mpi

import java.net.URLDecoder
import java.net.URLEncoder

actual object MessageEncoder {
    actual fun encode(data: String): String {
        return URLEncoder.encode(data, Charsets.UTF_8).replace("+", "%20")
    }

    actual fun decode(data: String): String {
        return URLDecoder.decode(data, Charsets.UTF_8)
    }
}
