package com.firsttimeinforever.intellij.pdf.viewer.application.utility

import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget
import org.w3c.dom.events.KeyboardEvent

object CommonBrowserUtilities {
  fun EventTarget.addEventListener(type: String, singleTime: Boolean = false, listener: (Event) -> Unit) {
    addEventListener(type, listener, singleTime)
  }

  fun EventTarget.addEventListener(type: String, singleTime: Boolean = false, listener: (KeyboardEvent) -> Unit) {
    addEventListener(type, { listener(it as KeyboardEvent) }, singleTime)
  }
}
