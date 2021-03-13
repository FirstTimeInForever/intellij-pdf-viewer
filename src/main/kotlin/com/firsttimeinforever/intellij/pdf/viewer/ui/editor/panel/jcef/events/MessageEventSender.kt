package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.ui.jcef.JBCefBrowser
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MessageEventSender(private val browser: JBCefBrowser, val jsonSerializer: Json) {
    companion object {
        private val logger = thisLogger()

        private const val TRIGGER_FUNCTION = "triggerMessageEvent"
    }

    fun trigger(event: TriggerableEventType, data: String = "{}") {
        logger.debug("Triggering event: $event")
        browser.cefBrowser.executeJavaScript("$TRIGGER_FUNCTION('${event.displayName}', $data)", null, 0)
    }

    fun triggerWith(event: TriggerableEventType, data: String) {
        logger.debug("Triggering event: $event with payload: $data")
        trigger(event, data)
    }

    inline fun <reified DataType : Any> triggerWith(event: TriggerableEventType, data: DataType) {
        triggerWith(event, jsonSerializer.encodeToString(data))
    }
}
