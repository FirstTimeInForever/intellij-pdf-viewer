package com.firsttimeinforever.intellij.pdf.viewer.report

import com.firsttimeinforever.intellij.pdf.viewer.PdfViewerBundle
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
import io.sentry.Sentry
import io.sentry.SentryEvent
import io.sentry.SentryLevel
import io.sentry.protocol.Message
import java.awt.Component

internal class PdfErrorReportSubmitter : ErrorReportSubmitter() {
  override fun getReportActionText(): String = PdfViewerBundle.message("pdf.viewer.error.report.action.text")

  override fun submit(
    events: Array<out IdeaLoggingEvent>,
    additionalInfo: String?,
    parentComponent: Component,
    consumer: Consumer<in SubmittedReportInfo>
  ): Boolean {
    Sentry.init { options ->
      // JetBrains: AbstractBundle.message(ResourceBundle.getBundle("sentry"), "dsn")
      options.dsn = "https://7b17f8f7fcbc6f452a36eae2a1227db1@o4508981186461696.ingest.de.sentry.io/4508981189869648" // PHPirates' Sentry project
    }

    val context = DataManager.getInstance().getDataContext(parentComponent)
    val sentryEvents = createEvents(events, additionalInfo)
    val project = CommonDataKeys.PROJECT.getData(context)
    SendReportBackgroundTask(project, sentryEvents, consumer).queue()
    return true
  }

  private fun createEvents(events: Array<out IdeaLoggingEvent>, additionalInfo: String?): List<SentryEvent> {
    return events
      .map { ideaEvent ->
        SentryEvent().apply {
          this.message = Message().apply { this.message = additionalInfo ?: ideaEvent.throwableText }
          this.level = SentryLevel.ERROR
          this.throwable = ideaEvent.throwable

          (pluginDescriptor as? IdeaPluginDescriptor)?.let { release = it.version }
          extras = mapOf("last_action" to IdeaLogger.ourLastActionId)
          val applicationNamesInfo = ApplicationNamesInfo.getInstance()
          val instanceEx = ApplicationInfoEx.getInstanceEx()
          tags = mapOf(
            "OS Name" to SystemInfo.OS_NAME,
            "Java Version" to SystemInfo.JAVA_VERSION,
            "App Name" to applicationNamesInfo.productName,
            "App Full Name" to applicationNamesInfo.fullProductName,
            "App Version name" to instanceEx.versionName,
            "Is EAP" to instanceEx.isEAP.toString(),
            "App Build" to instanceEx.build.asString(),
            "App Version" to instanceEx.fullVersion,
          )
        }
      }
  }
}
