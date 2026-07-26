package com.firsttimeinforever.intellij.pdf.viewer.frontend.jcef

import org.cef.CefSettings

data class JcefConsoleMessage(
  val level: CefSettings.LogSeverity,
  val message: String,
  val source: String,
  val line: Int
)
