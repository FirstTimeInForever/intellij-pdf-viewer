package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events

import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.ui.jcef.JBCefJSQuery

class MessageEventSubscriptionsManager private constructor(private val browser: JBCefBrowser) {
    val subscriptions = mutableMapOf<String, JBCefJSQuery>()

    companion object {
        fun fromList(browser: JBCefBrowser, events: List<String>): MessageEventSubscriptionsManager {
            val manager = MessageEventSubscriptionsManager(browser)
            events.forEach {
                manager.subscriptions.put(it, JBCefJSQuery.create(browser)!!)
            }
            return manager
        }
    }

    fun addHandlerWithResponse(eventName: String, handler: (String) -> JBCefJSQuery.Response?) {
        check(subscriptions.contains(eventName))
        subscriptions[eventName]!!.addHandler(handler)
    }

    fun addHandler(eventName: String, handler: (String) -> Unit) {
        addHandlerWithResponse(eventName) {
            handler(it)
            null
        }
    }

    fun injectSubscriptions() {
        subscriptions.forEach {
            subscribeToEvent(it.key, it.value)
        }
    }

    private fun subscribeToEvent(eventName: String, query: JBCefJSQuery) {
        browser.cefBrowser.executeJavaScript("""
            subscribeToMessageEvent('$eventName', (data) => {
                ${query.inject("JSON.stringify(data)")}
            })
        """.trimIndent(), null, 0)
    }
}
