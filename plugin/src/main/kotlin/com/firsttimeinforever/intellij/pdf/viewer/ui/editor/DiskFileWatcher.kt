package com.firsttimeinforever.intellij.pdf.viewer.ui.editor

import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.logger
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runInterruptible
import java.io.IOException
import java.nio.file.ClosedWatchServiceException
import java.nio.file.Path
import java.nio.file.ProviderMismatchException
import java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import java.nio.file.StandardWatchEventKinds.ENTRY_DELETE
import java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY
import java.nio.file.WatchService
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.milliseconds

/**
 * Intended for when the user uses an external program which updates the pdf, see e.g. #150.
 * Since the VFS will not refresh without trigger, we cannot use the IntelliJ apis to implement a watcher.
 * Therefore, we work around them manually.
 *
 * Watches the parent directory of [filePath] via a JDK NIO [WatchService] and invokes [onChange]
 * shortly after the watched filename is created or modified on disk. A short debounce coalesces
 * bursts of events that non-atomic writers produce while a file is still being written.
 *
 * On macOS the JDK falls back to a polling implementation (~10s), so the existing focus-based
 * refresh in [PdfFileEditor] may be preferred by users (but requires switching focus away from the IDe.).
 */
class DiskFileWatcher(filePath: Path, private val onChange: () -> Unit) : Disposable {
  private val fileName: Path? = filePath.fileName
  private val parent: Path? = filePath.parent
  private val scope = CoroutineScope(
    SupervisorJob() + Dispatchers.IO + CoroutineName("pdf-viewer-disk-watcher-${filePath.fileName}")
  )
  private val watchService: WatchService?
  private val debounceLock = Any()
  private var debounceJob: Job? = null
  private val closed = AtomicBoolean(false)

  init {
    watchService = openWatchService()
    if (watchService != null) {
      scope.launch { runWatchLoop(watchService) }
    }
  }

  private fun openWatchService(): WatchService? {
    if (parent == null || fileName == null) {
      logger.debug("Disk watcher unavailable: no parent directory for path")
      return null
    }
    var service: WatchService? = null
    return try {
      service = parent.fileSystem.newWatchService()
      parent.register(service, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE)
      service
    } catch (e: IOException) {
      logger.debug("Disk watcher unavailable for $parent", e)
      service?.runCatching { close() }
      null
    } catch (e: UnsupportedOperationException) {
      logger.debug("Disk watcher unavailable for $parent", e)
      service?.runCatching { close() }
      null
    } catch (e: ProviderMismatchException) {
      logger.debug("Disk watcher unavailable for $parent", e)
      service?.runCatching { close() }
      null
    } catch (e: SecurityException) {
      logger.debug("Disk watcher unavailable for $parent", e)
      service?.runCatching { close() }
      null
    }
  }

  private suspend fun runWatchLoop(service: WatchService) {
    try {
      while (scope.isActive) {
        val key = runInterruptible { service.take() }
        try {
          for (event in key.pollEvents()) {
            val context = event.context() as? Path ?: continue
            if (context.fileName != fileName) continue
            val kind = event.kind()
            if (kind == ENTRY_CREATE || kind == ENTRY_MODIFY) {
              scheduleDebounced()
            }
          }
        } catch (e: CancellationException) {
          throw e
        } catch (e: Throwable) {
          logger.debug("Ignoring error while handling watch events", e)
        }
        if (!key.reset()) {
          logger.debug("Watch key for $parent is no longer valid; stopping watcher")
          return
        }
      }
    } catch (_: ClosedWatchServiceException) {
      // Normal shutdown via dispose().
    } catch (e: CancellationException) {
      throw e
    } catch (e: Throwable) {
      logger.debug("Disk watcher loop crashed", e)
    }
  }

  private fun scheduleDebounced() {
    synchronized(debounceLock) {
      debounceJob?.cancel()
      debounceJob = scope.launch {
        delay(DEBOUNCE_MS.milliseconds)
        onChange()
      }
    }
  }

  override fun dispose() {
    if (!closed.compareAndSet(false, true)) return
    watchService?.runCatching { close() }
    scope.cancel()
  }

  companion object {
    private const val DEBOUNCE_MS = 200L
    private val logger = logger<DiskFileWatcher>()
  }
}
