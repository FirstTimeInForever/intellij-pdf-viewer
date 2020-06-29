package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef

import java.awt.Color

internal fun transformColorHex(color: Color): String {
    val colorString = listOf(
        color.red,
        color.blue,
        color.green,
        color.alpha
    ).joinToString("") {
        val value = Integer.toHexString(it)
        when (value.length) {
            0 -> "00"
            1 -> "0$value"
            else -> value
        }
    }
    return "#$colorString"
}

internal fun transformColorRgba(color: Color): String =
    color.run { "rgba($red, $green, $blue, $alpha)" }
