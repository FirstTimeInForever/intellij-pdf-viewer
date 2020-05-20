package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel

import com.intellij.openapi.Disposable
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.ui.jcef.JBCefJSQuery
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefLoadHandlerAdapter

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

    fun addHandler(eventName: String, handler: (String) -> JBCefJSQuery.Response?) {
        subscriptions[eventName]!!.addHandler(handler)
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
