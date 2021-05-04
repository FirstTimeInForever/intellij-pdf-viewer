package com.firsttimeinforever.intellij.pdf.viewer.mpi.model

import java.awt.Color

object ViewThemeUtils {
  fun ViewTheme.Companion.create(background: Color, foreground: Color, icons: Color, colorInvertIntensity: Int): ViewTheme {
    return ViewTheme(
      background.toWebHexString(),
      foreground.toWebHexString(),
      icons.toWebHexString(),
      colorInvertIntensity
    )
  }

  /**
   * Based on the implementation of [com.intellij.ui.ColorUtil.toHex]
   */
  @Suppress("MemberVisibilityCanBePrivate")
  fun Color.toWebHexString(includeHash: Boolean = true): String {
    val resultRed = Integer.toHexString(red)
    val resultGreen = Integer.toHexString(green)
    val resultBlue = Integer.toHexString(blue)
    val resultAlpha = Integer.toHexString(alpha)
    return buildString {
      if (includeHash) {
        append("#")
      }
      append(ensureCorrectColorValuePrefix(resultRed))
      append(resultRed)
      append(ensureCorrectColorValuePrefix(resultGreen))
      append(resultGreen)
      append(ensureCorrectColorValuePrefix(resultBlue))
      append(resultBlue)
      append(ensureCorrectColorValuePrefix(resultAlpha))
      append(resultAlpha)
    }
  }

  @Suppress("NOTHING_TO_INLINE")
  private inline fun ensureCorrectColorValuePrefix(string: String): String {
    return when {
      string.length < 2 -> "0"
      else -> ""
    }
  }

  @Suppress("unused")
  fun Color.toWebRgbaString(): String {
    return "rgba($red, $green, $blue, $alpha)"
  }
}
