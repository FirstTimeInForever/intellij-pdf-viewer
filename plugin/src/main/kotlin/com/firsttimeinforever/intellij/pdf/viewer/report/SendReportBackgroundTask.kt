package com.firsttimeinforever.intellij.pdf.viewer.report

import com.firsttimeinforever.intellij.pdf.viewer.PdfViewerBundle
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.SubmittedReportInfo
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.util.Consumer
import io.sentry.SentryClient
import io.sentry.connection.EventSendCallback
import io.sentry.event.Event
import io.sentry.event.EventBuilder

internal class SendReportBackgroundTask(
  private val sentryClient: SentryClient,
  project: Project?,
  private val event: EventBuilder,
  private val consumer: Consumer<in SubmittedReportInfo>
) : Task.Backgroundable(project, PdfViewerBundle.message("pdf.viewer.error.report.sending")) {
  override fun run(indicator: ProgressIndicator) {
    sentryClient.sendEvent(event)
    sentryClient.addEventSendCallback(object : EventSendCallback {
      override fun onSuccess(event: Event?) {
        ApplicationManager.getApplication().invokeLater {
          val group = NotificationGroupManager.getInstance().getNotificationGroup("Error Report")
          group.createNotification(
            PdfViewerBundle.message("pdf.viewer.error.report.notifications.submit.success"),
            NotificationType.INFORMATION
          ).notify(project)
          consumer.consume(SubmittedReportInfo(SubmittedReportInfo.SubmissionStatus.NEW_ISSUE))
        }
      }

      override fun onFailure(event: Event?, exception: Exception?) {
        ApplicationManager.getApplication().invokeLater {
          val group = NotificationGroupManager.getInstance().getNotificationGroup("Error Report")
          group.createNotification(
            PdfViewerBundle.message("pdf.viewer.error.report.notifications.submit.failed"),
            NotificationType.ERROR
          ).notify(project)
          thisLogger().error(exception)
          consumer.consume(SubmittedReportInfo(SubmittedReportInfo.SubmissionStatus.FAILED))
        }
      }
    })
  }
}
