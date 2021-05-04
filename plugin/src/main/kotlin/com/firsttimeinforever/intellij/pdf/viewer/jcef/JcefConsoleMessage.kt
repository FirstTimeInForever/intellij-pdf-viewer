package com.firsttimeinforever.intellij.pdf.viewer.jcef

import org.cef.CefSettings

data class JcefConsoleMessage(
  val level: CefSettings.LogSeverity,
  val message: String,
  val source: String,
  val line: Int
)
