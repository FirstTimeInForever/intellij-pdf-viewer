package com.firsttimeinforever.intellij.pdf.viewer.jcef

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.view.PdfJcefPreviewController
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.util.registry.Registry
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.ui.jcef.JBCefJSQuery
import org.cef.CefSettings
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefDisplayHandlerAdapter
import org.cef.handler.CefLoadHandler
import org.cef.handler.CefLoadHandlerAdapter
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

internal object JcefUtils {
  @Suppress("NOTHING_TO_INLINE")
  inline fun JBCefBrowser.addLoadHandler(handler: CefLoadHandler): CefLoadHandler {
    jbCefClient.addLoadHandler(handler, cefBrowser)
    return handler
  }

  fun JBCefBrowser.addLoadEndHandler(handler: (CefBrowser) -> Unit): CefLoadHandler {
    return addLoadHandler(object : CefLoadHandlerAdapter() {
      override fun onLoadEnd(browser: CefBrowser, frame: CefFrame, httpStatusCode: Int) {
        handler(browser)
      }
    })
  }

  private class LatchedLoadEndHandler(private val latch: CountDownLatch): CefLoadHandlerAdapter() {
    override fun onLoadError(
      browser: CefBrowser?,
      frame: CefFrame?,
      errorCode: CefLoadHandler.ErrorCode?,
      errorText: String?,
      failedUrl: String?
    ) {
      logger.warn("Failed to load page.\n\tUrl: $failedUrl\n\tError text: $errorText")
      latch.countDown()
    }

    override fun onLoadEnd(browser: CefBrowser?, frame: CefFrame?, httpStatusCode: Int) {
      latch.countDown()
    }
  }

  fun JBCefBrowser.invokeAndWaitForLoadEnd(block: () -> Unit) {
    ApplicationManager.getApplication().invokeAndWait {
      val latch = CountDownLatch(1)
      val handler = LatchedLoadEndHandler(latch)
      block()
      try {
        latch.countDown()
        latch.await(loadLatchTimeout, TimeUnit.MILLISECONDS)
      } catch (exception: Throwable) {
        logger.error(exception)
      } finally {
        jbCefClient.removeLoadHandler(handler, cefBrowser)
      }
    }
  }

  @Suppress("NOTHING_TO_INLINE")
  inline fun CefBrowser.executeJavaScript(code: String) {
    executeJavaScript(code, null, 0)
  }

  @Suppress("NOTHING_TO_INLINE")
  inline fun JBCefBrowser.executeJavaScript(code: String, source: String? = null, line: Int = 0) {
    cefBrowser.executeJavaScript(code, source, line)
  }

  fun JBCefBrowser.addConsoleMessageListener(listener: (JcefConsoleMessage) -> Unit) {
    jbCefClient.addDisplayHandler(object: CefDisplayHandlerAdapter() {
      override fun onConsoleMessage(
        browser: CefBrowser?,
        level: CefSettings.LogSeverity?,
        message: String?,
        source: String?,
        line: Int
      ): Boolean {
        if (level == null || message == null || source == null) {
          logger.warn("Some of required message values were null!")
          logger.warn("level: $level source: $source:$line\n\tmessage: $message")
        } else {
          listener(JcefConsoleMessage(level, message, source, line))
        }
        return super.onConsoleMessage(browser, level, message, source, line)
      }
    }, cefBrowser)
  }

  private fun formatMessage(message: JcefConsoleMessage): String {
    return message.run { "[$level][$source:$line]:\n$message" }
  }

  fun createDefaultConsoleMessageListener(logger: Logger): (JcefConsoleMessage) -> Unit {
    return {
      val formattedMessage = formatMessage(it)
      when (it.level) {
        CefSettings.LogSeverity.LOGSEVERITY_ERROR, CefSettings.LogSeverity.LOGSEVERITY_FATAL -> logger.error(formattedMessage)
        CefSettings.LogSeverity.LOGSEVERITY_INFO -> logger.info(formattedMessage)
        CefSettings.LogSeverity.LOGSEVERITY_WARNING -> logger.warn(formattedMessage)
        CefSettings.LogSeverity.LOGSEVERITY_VERBOSE -> logger.debug(formattedMessage)
        else -> logger.info(formattedMessage)
      }
    }
  }

  fun createDebugConsoleMessageListener(logger: Logger): (JcefConsoleMessage) -> Unit {
    return {
      val formattedMessage = formatMessage(it)
      when (it.level) {
        CefSettings.LogSeverity.LOGSEVERITY_ERROR, CefSettings.LogSeverity.LOGSEVERITY_FATAL -> logger.error(formattedMessage)
        CefSettings.LogSeverity.LOGSEVERITY_INFO -> logger.info(formattedMessage)
        CefSettings.LogSeverity.LOGSEVERITY_WARNING -> logger.warn(formattedMessage)
        else -> logger.info(formattedMessage)
      }
    }
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

  private val loadLatchTimeout: Long
    get() = Registry.intValue("pdf.viewer.jcefLatchTimeout", 10000).toLong()

  private val logger = thisLogger()
}
