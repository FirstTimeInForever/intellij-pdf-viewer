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
import io.sentry.Sentry
import io.sentry.SentryEvent
import io.sentry.protocol.SentryId

internal class SendReportBackgroundTask(
  project: Project?,
  private val events: List<SentryEvent>,
  private val consumer: Consumer<in SubmittedReportInfo>
) : Task.Backgroundable(project, PdfViewerBundle.message("pdf.viewer.error.report.sending")) {
  override fun run(indicator: ProgressIndicator) {
    for (event in events) {
      val id: SentryId = Sentry.captureEvent(event)
      if (id != SentryId.EMPTY_ID) {
        ApplicationManager.getApplication().invokeLater {
          val group = NotificationGroupManager.getInstance().getNotificationGroup("Error Report")
          group.createNotification(
            PdfViewerBundle.message("pdf.viewer.error.report.notifications.submit.success"),
            NotificationType.INFORMATION
          ).notify(project)
          consumer.consume(SubmittedReportInfo(SubmittedReportInfo.SubmissionStatus.NEW_ISSUE))
        }
      } else {
        ApplicationManager.getApplication().invokeLater {
          val group = NotificationGroupManager.getInstance().getNotificationGroup("Error Report")
          group.createNotification(
            PdfViewerBundle.message("pdf.viewer.error.report.notifications.submit.failed"),
            NotificationType.ERROR
          ).notify(project)
          thisLogger().error(event.toString())
          consumer.consume(SubmittedReportInfo(SubmittedReportInfo.SubmissionStatus.FAILED))
        }
      }
    }
  }
}
