package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef

import java.awt.Color

internal fun transformColor(color: Color): String {
    val colors = listOf(color.red, color.blue, color.green, color.alpha)
    val colorString = colors.joinToString("", transform = Integer::toHexString)
    return "#${colorString}"
}
