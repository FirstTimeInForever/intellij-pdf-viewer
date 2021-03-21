package com.firsttimeinforever.intellij.pdf.viewer.jcef

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.ui.jcef.JBCefJSQuery
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefLoadHandler
import org.cef.handler.CefLoadHandlerAdapter

internal object JcefUtils {
    @Suppress("NOTHING_TO_INLINE")
    inline fun JBCefBrowser.addLoadHandler(handler: CefLoadHandler) {
        jbCefClient.addLoadHandler(handler, cefBrowser)
    }

    fun JBCefBrowser.addLoadEndHandler(handler: (CefBrowser) -> Unit) {
        addLoadHandler(object: CefLoadHandlerAdapter() {
            override fun onLoadEnd(browser: CefBrowser, frame: CefFrame, httpStatusCode: Int) {
                handler(browser)
            }
        })
    }

    @Suppress("NOTHING_TO_INLINE")
    inline fun CefBrowser.executeJavaScript(code: String) {
        executeJavaScript(code, null, 0)
    }

    @Suppress("NOTHING_TO_INLINE")
    inline fun JBCefBrowser.executeJavaScript(code: String, source: String? = null, line: Int = 0) {
        cefBrowser.executeJavaScript(code, source, line)
    }

    fun JBCefJSQuery.addUnitHandler(handler: (String) -> Unit) {
        addHandler {
            when (it) {
                null -> logger.warn("Query handler called with null string!")
                else -> handler(it)
            }
            null
        }
    }

    private val logger = thisLogger()
}
