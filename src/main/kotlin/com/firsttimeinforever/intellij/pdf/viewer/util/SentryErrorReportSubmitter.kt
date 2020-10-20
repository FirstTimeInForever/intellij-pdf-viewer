package com.firsttimeinforever.intellij.pdf.viewer.util

import com.firsttimeinforever.intellij.pdf.viewer.PdfViewerBundle
import com.intellij.AbstractBundle
import com.intellij.diagnostic.IdeaReportingEvent
import com.intellij.diagnostic.ReportMessages
import com.intellij.ide.DataManager
import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.idea.IdeaLogger
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ApplicationNamesInfo
import com.intellij.openapi.application.ex.ApplicationInfoEx
import com.intellij.openapi.diagnostic.ErrorReportSubmitter
import com.intellij.openapi.diagnostic.IdeaLoggingEvent
import com.intellij.openapi.diagnostic.SubmittedReportInfo
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.SystemInfo
import com.intellij.util.Consumer
import io.sentry.DefaultSentryClientFactory
import io.sentry.SentryClient
import io.sentry.connection.EventSendCallback
import io.sentry.dsn.Dsn
import io.sentry.event.Event
import io.sentry.event.EventBuilder
import io.sentry.event.interfaces.ExceptionInterface
import io.sentry.event.interfaces.SentryException
import java.awt.Component
import java.lang.Exception
import java.util.*

class SentryErrorReportSubmitter: ErrorReportSubmitter() {
    override fun getReportActionText(): String = PdfViewerBundle.message("pdf.viewer.error.report.action.text")

    private class SendReportBackgroundTask(
        project: Project?,
        private val event: EventBuilder,
        private val consumer: Consumer<in SubmittedReportInfo>
    ): Task.Backgroundable(
        project,
        PdfViewerBundle.message("pdf.viewer.error.report.sending")
    ) {
        override fun run(indicator: ProgressIndicator) {
            sentryClient.sendEvent(event)
            sentryClient.addEventSendCallback(object: EventSendCallback {
                override fun onSuccess(event: Event?) {
                    ApplicationManager.getApplication().invokeLater {
                        ReportMessages.GROUP.createNotification(
                            PdfViewerBundle.message("pdf.viewer.error.report.notifications.submit.success"),
                            NotificationType.INFORMATION
                        ).notify(project)
                        consumer.consume(SubmittedReportInfo(SubmittedReportInfo.SubmissionStatus.NEW_ISSUE))
                    }
                }

                override fun onFailure(event: Event?, exception: Exception?) {
                    ApplicationManager.getApplication().invokeLater {
                        ReportMessages.GROUP.createNotification(
                            PdfViewerBundle.message("pdf.viewer.error.report.notifications.submit.failed"),
                            NotificationType.ERROR
                        ).notify(project)
                        logger<SentryErrorReportSubmitter>().error(exception)
                        consumer.consume(SubmittedReportInfo(SubmittedReportInfo.SubmissionStatus.FAILED))
                    }
                }
            })
        }
    }

    override fun submit(
        events: Array<out IdeaLoggingEvent>,
        additionalInfo: String?,
        parentComponent: Component,
        consumer: Consumer<in SubmittedReportInfo>
    ): Boolean {
        val context = DataManager.getInstance().getDataContext(parentComponent)
        SendReportBackgroundTask(
            CommonDataKeys.PROJECT.getData(context),
            createEvent(events).withMessage(additionalInfo).also {
                attachExtraInfo(it)
            },
            consumer
        ).queue()
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
        with (event) {
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
            val factory = object: DefaultSentryClientFactory() {
                override fun getInAppFrames(dsn: Dsn): Collection<String> =
                    listOf("com.firsttimeinforever.intellij.pdf.viewer")
            }
            val dsn = AbstractBundle.message(ResourceBundle.getBundle("sentry"), "dsn")
            println(dsn)
            factory.createClient(dsn)
        }
    }
}
