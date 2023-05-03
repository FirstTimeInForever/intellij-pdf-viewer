package com.firsttimeinforever.intellij.pdf.viewer.report

import com.firsttimeinforever.intellij.pdf.viewer.PdfViewerBundle
import com.intellij.AbstractBundle
import com.intellij.diagnostic.IdeaReportingEvent
import com.intellij.ide.DataManager
import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.idea.IdeaLogger
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationNamesInfo
import com.intellij.openapi.application.ex.ApplicationInfoEx
import com.intellij.openapi.diagnostic.ErrorReportSubmitter
import com.intellij.openapi.diagnostic.IdeaLoggingEvent
import com.intellij.openapi.diagnostic.SubmittedReportInfo
import com.intellij.openapi.util.SystemInfo
import com.intellij.util.Consumer
import io.sentry.DefaultSentryClientFactory
import io.sentry.SentryClient
import io.sentry.dsn.Dsn
import io.sentry.event.Event
import io.sentry.event.EventBuilder
import io.sentry.event.interfaces.ExceptionInterface
import io.sentry.event.interfaces.SentryException
import java.awt.Component
import java.util.*

internal class PdfErrorReportSubmitter : ErrorReportSubmitter() {
  override fun getReportActionText(): String = PdfViewerBundle.message("pdf.viewer.error.report.action.text")

  override fun submit(
    events: Array<out IdeaLoggingEvent>,
    additionalInfo: String?,
    parentComponent: Component,
    consumer: Consumer<in SubmittedReportInfo>
  ): Boolean {
    val context = DataManager.getInstance().getDataContext(parentComponent)
    val event = createEvent(events)
      .withMessage(additionalInfo ?: "No additional info were provided")
      .also { attachExtraInfo(it) }
    val project = CommonDataKeys.PROJECT.getData(context)
    SendReportBackgroundTask(sentryClient, project, event, consumer).queue()
    return true
  }

  private fun createEvent(events: Array<out IdeaLoggingEvent>): EventBuilder {
    val errors = events
      .filterIsInstance<IdeaReportingEvent>()
      .mapTo(ArrayDeque(events.size)) {
        val throwable = it.data.throwable
        SentryException(throwable, throwable.stackTrace)
      }
    return EventBuilder()
      .withLevel(Event.Level.ERROR)
      .withSentryInterface(ExceptionInterface(errors))
  }

  private fun attachExtraInfo(event: EventBuilder) {
    with(event) {
      (pluginDescriptor as? IdeaPluginDescriptor)?.let { withRelease(it.version) }
      withExtra("last_action", IdeaLogger.ourLastActionId)
      withTag("OS Name", SystemInfo.OS_NAME)
      withTag("Java Version", SystemInfo.JAVA_VERSION)
      ApplicationNamesInfo.getInstance().let {
        withTag("App Name", it.productName)
        withTag("App Full Name", it.fullProductName)
      }
      ApplicationInfoEx.getInstanceEx()?.let {
        withTag("App Version name", it.versionName)
        withTag("Is EAP", it.isEAP.toString())
        withTag("App Build", it.build.asString())
        withTag("App Version", it.fullVersion)
      }
    }
  }

  companion object {
    private val sentryClient: SentryClient by lazy {
      val factory = object : DefaultSentryClientFactory() {
        override fun getInAppFrames(dsn: Dsn): Collection<String> =
          listOf("com.firsttimeinforever.intellij.pdf.viewer")
      }
      val dsn = AbstractBundle.message(ResourceBundle.getBundle("sentry"), "dsn")
      factory.createClient(dsn)
    }
  }
}
