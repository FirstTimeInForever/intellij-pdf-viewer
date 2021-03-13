package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events.objects

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events.ColorSerializer
import kotlinx.serialization.Serializable
import java.awt.Color

@Serializable
class SetThemeColorsDataObject(
    @Serializable(with = ColorSerializer::class)
    val background: Color,
    @Serializable(with = ColorSerializer::class)
    val foreground: Color,
    @Serializable(with = ColorSerializer::class)
    val icons: Color,
    val documentColorInvertIntensity: Int
)
