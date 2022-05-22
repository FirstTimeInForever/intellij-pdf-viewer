package application

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.COMPLETE
import org.w3c.dom.DocumentReadyState
import org.w3c.dom.INTERACTIVE
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

object WindowUtils {
  inline fun <R> wrapAsResult(block: () -> R): Result<R> {
    return try {
      Result.success(block())
    } catch (exception: Throwable) {
      Result.failure(exception)
    }
  }

  suspend fun <R> waitForWindowEvent(event: String, block: suspend () -> R): R {
    return suspendCoroutine { continuation ->
      window.addEventListener(event, { block.startCoroutine(continuation) }, true)
    }
  }

  suspend fun <R> invokeOnContentLoadedIfNeeded(block: suspend () -> R): R {
    return when (document.readyState) {
      DocumentReadyState.COMPLETE, DocumentReadyState.INTERACTIVE -> block.invoke()
      else -> waitForWindowEvent("DOMContentLoaded", block)
    }
  }
}
