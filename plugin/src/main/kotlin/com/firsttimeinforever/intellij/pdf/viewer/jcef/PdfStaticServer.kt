package com.firsttimeinforever.intellij.pdf.viewer.jcef

import com.firsttimeinforever.intellij.pdf.viewer.settings.PdfViewerSettings
import com.firsttimeinforever.intellij.pdf.viewer.utility.PdfResourceLoader
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.jcef.JBCefScrollbarsHelper
import com.intellij.util.Url
import com.intellij.util.Urls
import com.intellij.util.io.URLUtil
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.HttpUtil
import io.netty.handler.codec.http.QueryStringDecoder
import io.netty.handler.stream.ChunkedStream
import org.jetbrains.ide.BuiltInServerManager
import org.jetbrains.ide.HttpRequestHandler
import org.jetbrains.io.FileResponses
import org.jetbrains.io.addKeepAliveIfNeeded
import org.jetbrains.io.flushChunkedResponse
import org.jetbrains.io.response
import org.jetbrains.io.send
import java.nio.file.Paths
import kotlin.random.Random

internal class PdfStaticServer : HttpRequestHandler() {
  private val serverUrl = "http://localhost:${BuiltInServerManager.getInstance().port}/$uuid"

  private val vfsMap = mutableMapOf<String, VirtualFile>()

  init {
    logger.debug("Starting static server with url: $serverUrl")
  }

  override fun process(
    urlDecoder: QueryStringDecoder,
    request: FullHttpRequest,
    context: ChannelHandlerContext
  ): Boolean {
    logger.debug("Incoming request:\n\tpath: ${urlDecoder.path()}\n\tparameters: ${urlDecoder.parameters()}")
    // Check if current request is actually ours
    if (!urlDecoder.path().contains(uuid)) {
      logger.debug("Current url is not ours. Passing it to the next handler.")
      return false
    }
    val requestPath = urlDecoder.path().removePrefix("/$uuid")
    logger.debug(requestPath)
    if (isExternalFilePath(requestPath)) {
      sendExternalFile(requestPath.removePrefix("/get-file/"), context, request)
    } else {
      sendInternalFile(requestPath, context, request)
    }
    return true
  }

  private fun isExternalFilePath(path: String): Boolean {
    return path.startsWith("/get-file/")
  }

  private fun sendExternalFile(url: String, context: ChannelHandlerContext, request: FullHttpRequest) {
    val file: VirtualFile? = vfsMap[url]
    val channel = context.channel()
    if (file == null) {
      logger.debug("Cannot find pdf file $url")
      HttpResponseStatus.NOT_FOUND.send(channel, request)
    } else if (file.isInLocalFileSystem) {
      val path = Paths.get(file.path)
      logger.debug("Sending external file, path: $path")
      FileResponses.sendFile(request, channel, path)
    } else {
      logger.debug("Sending external file, url: $url")
      // see org.jetbrains.builtInWebServer.StaticFileHandler
      val response = FileResponses.prepareSend(request, channel, file.timeStamp, file.name) ?: return
      val isKeepAlive = response.addKeepAliveIfNeeded(request)
      // remove accept-ranges as we don't support
      response.headers().remove(HttpHeaderNames.ACCEPT_RANGES)
      if (request.method() != HttpMethod.HEAD) {
        HttpUtil.setContentLength(response, file.length)
      }
      channel.write(response)
      if (request.method() != HttpMethod.HEAD) {
        channel.write(ChunkedStream(file.inputStream))
      }
      flushChunkedResponse(channel, isKeepAlive)
    }
  }

  private fun makeInternalPath(path: String): String {
    val actualPath = when {
      path.first() == '/' -> path.drop(1)
      else -> path
    }
    return "/$baseDirectory/$actualPath"
  }

  private fun sendInternalFile(path: String, context: ChannelHandlerContext, request: FullHttpRequest) {
    val targetFile = makeInternalPath(path)
    val url = PdfStaticServer::class.java.getResource(targetFile)
    if (url == null) {
      logger.debug("Cannot find internal file $targetFile")
      HttpResponseStatus.NOT_FOUND.send(context.channel(), request)
      return
    }
    val contentType = FileResponses.getContentType(targetFile)
    logger.debug("Sending internal file: $targetFile with contentType: $contentType")
    var bytes = PdfResourceLoader.loadFromRoot(targetFile)
    if (targetFile == "/web-view/fixes.css") {
      bytes += JBCefScrollbarsHelper.buildScrollbarsStyle().toByteArray(Charsets.UTF_8)
    }
    val resultBuffer = Unpooled.wrappedBuffer(bytes)
    val response = response(contentType, resultBuffer)
    response.send(context.channel(), request)
  }

  fun getPreviewUrl(file: VirtualFile, withReloadSalt: Boolean = false): String {
    val salt = if (withReloadSalt) Random.nextInt() else 0
    vfsMap[file.url] = file
    val url = parseEncodedPath("$serverUrl/web/viewer.html")
    val server = BuiltInServerManager.getInstance()
    return server.addAuthToken(url)
      // `file` would be read via `urlDecoder.path()`, which calls `decodeComponent`
      .addParameters(mapOf("__reloadSalt" to "$salt", "file" to "/$uuid/get-file/${URLUtil.encodeURIComponent(file.url)}"))
      .toExternalForm()
  }

  fun disposePreviewUrl(file: VirtualFile) {
    vfsMap.remove(file.url)
  }

  private fun parseEncodedPath(target: String): Url {
    return Urls.parseEncoded(target) ?: error("Could not parse encoded path for \"$target\"")
  }

  companion object {
    private val logger = logger<PdfStaticServer>()

    val instance by lazy { checkNotNull(EP_NAME.findExtension(PdfStaticServer::class.java)) }

    private const val baseDirectory = "web-view"
    private const val uuid = "64fa8636-e686-4c63-9956-132d9471ce77"
  }
}
