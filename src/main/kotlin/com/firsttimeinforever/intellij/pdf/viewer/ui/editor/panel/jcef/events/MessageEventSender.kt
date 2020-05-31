package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events

import com.intellij.openapi.diagnostic.logger
import com.intellij.ui.jcef.JBCefBrowser
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json

class MessageEventSender(private val browser: JBCefBrowser, private val jsonSerializer: Json) {
    companion object {
        private const val TRIGGER_FUNCTION = "triggerMessageEvent"
    }

    private val logger = logger<MessageEventSender>()

    fun trigger(event: TriggerableEventType, data: String = "{}") {
        logger.debug("Triggering event: $event")
        browser.cefBrowser.executeJavaScript("$TRIGGER_FUNCTION('${event.displayName}', $data)", null, 0)
    }

    fun <DataType> triggerWith(event: TriggerableEventType, data: DataType, strategy: SerializationStrategy<DataType>) {
        val targetData = jsonSerializer.toJson(strategy, data).toString()
        logger.debug("Triggering event: $event with payload: $targetData")
        trigger(event, targetData)
    }
}
