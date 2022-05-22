@file:JsModule("pdfjs-dist/lib/display/api")
package pdfjs.lib.display

import pdfjs.lib.AnnotationStorage
import pdfjs.lib.PdfPageViewport
import kotlin.js.Promise

/**
 * The loading task controls the operations required to load a PDF document
 * (such as network requests) and provides a way to listen for completion,
 * after which individual pages can be rendered.
 */
@JsName("PDFDocumentLoadingTask")
external class PdfDocumentLoadingTask {
  /**
   * Promise for document loading task completion.
   * @type {Promise<PDFDocumentProxy>}
   */
  val promise: Promise<PdfDocumentProxy>

  /**
   * Whether the loading task is destroyed or not.
   */
  val destroyed: Boolean

  /**
   * Abort all network requests and destroy the worker.
   * @returns {Promise<void>} A promise that is resolved when destruction is
   *   completed.
   */
  fun destroy(): Promise<Unit>

  // /**
  //  * Unique identifier for the document loading task.
  //  * @type {string}
  //  */
  // val docId: String

  /**
   * Callback to request a password if a wrong or no password was provided.
   * The callback receives two parameters: a function that should be called
   * with the new password, and a reason (see {@link PasswordResponses}).
   * @type {function}
   */
  var onPassword: ((String) -> Unit, dynamic) -> Unit

  /**
   * Callback to be able to monitor the loading progress of the PDF file
   * (necessary to implement e.g. a loading bar).
   * The callback receives an {@link OnProgressParameters} argument.
   * @type {function}
   */
  var onProgress: (dynamic) -> Unit

  /**
   * Callback for when an unsupported feature is used in the PDF document.
   * The callback receives an {@link UNSUPPORTED_FEATURES} argument.
   * @type {function}
   */
  var onUnsupportedFeature: (dynamic) -> Unit
}

// /**
//  * {
//  *     /**
//  * - Used stream types in the
//  * document (an item is set to true if specific stream ID was used in the
//  * document).
// */
// streamTypes: {
// [x: string]: boolean;
// };
// /**
//  * - Used font types in the
//  * document (an item is set to true if specific font ID was used in the
//  * document).
// */
// fontTypes: {
// [x: string]: boolean;
// };
//  * }
//  * @typedef {Object} PDFDocumentStats
//  * @property {Object<string, boolean>} streamTypes - Used stream types in the
//  *   document (an item is set to true if specific stream ID was used in the
//  *   document).
//  * @property {Object<string, boolean>} fontTypes - Used font types in the
//  *   document (an item is set to true if specific font ID was used in the
//  *   document).
//  */
// external class PdfDocumentStats

external interface PdfTextItem {
  val width: Float
  val height: Float
  val fontName: String
  val transform: Array<Float>

  @JsName("str")
  val stringContent: String

  @JsName("dir")
  val direction: String

  @JsName("hasEOL")
  val hasEol: Boolean
}

external interface PdfTextItems {
  val items: Array<PdfTextItem>
  val styles: dynamic
}

@JsName("PDFPageProxy")
external class PdfPageProxy {
  fun getOperatorList(): Promise<Any>

  @JsName("commonObjs")
  val commonObjects: dynamic

  @JsName("objs")
  val objects: dynamic

  fun getViewport(options: GetViewportOptions): PdfPageViewport

  fun getAnnotations(): Promise<dynamic>

  fun getTextContent(): Promise<PdfTextItems>

  fun render(context: CanvasPageRenderContext): PageCanvasRenderTask
}

// external class RefProxy

@JsName("PDFDocumentProxy")
external class PdfDocumentProxy(pdfInfo: dynamic, transport: dynamic) {
  /**
   *  Storage for annotation data in forms.
   */
  val annotationStorage: AnnotationStorage

  /**
   * Total number of pages in the PDF file.
   */
  @JsName("numPages")
  val pagesCount: Int

  /**
   * @type {Array<string, string|null>} A (not guaranteed to be) unique ID to
   *   identify the PDF document.
   *   NOTE: The first element will always be defined for all PDF documents,
   *   whereas the second element is only defined for *modified* PDF documents.
   */
  val fingerprints: Array<String?>

  /**
   * @type {PDFDocumentStats | null} The current statistics about document
   *   structures, or `null` when no statistics exists.
   */
  val stats: Any?

  /**
   * @param {number} pageNumber - The page number to get. The first page is 1.
   * @returns {Promise<PDFPageProxy>} A promise that is resolved with
   *   a {@link PDFPageProxy} object.
   */
  fun getPage(pageNumber: Int): Promise<PdfPageProxy>

  /**
   * @param {RefProxy} ref - The page reference.
   * @returns {Promise<number>} A promise that is resolved with the page index,
   *   starting from zero, that is associated with the reference.
   */
  fun getPageIndex(ref: dynamic): Promise<Int>

  /**
   * @returns {Promise<Object<string, Array<any>>>} A promise that is resolved
   *   with a mapping from named destinations to references.
   *
   * This can be slow for large documents. Use `getDestination` instead.
   */
  fun getDestinations(): Promise<dynamic>

  /**
   * @param {string} id - The named destination to get.
   * @returns {Promise<Array<any> | null>} A promise that is resolved with all
   *   information of the given named destination, or `null` when the named
   *   destination is not present in the PDF file.
   */
  fun getDestination(id: String): Promise<Array<Any>?>

  /**
   * @returns {Promise<Array<string> | null>} A promise that is resolved with
   *   an {Array} containing the page labels that correspond to the page
   *   indexes, or `null` when no page labels are present in the PDF file.
   */
  fun getPageLabels(): Promise<Array<String>?>

  /**
   * @returns {Promise<string>} A promise that is resolved with a {string}
   *   containing the page layout name.
   */
  fun getPageLayout(): Promise<String>

  /**
   * @returns {Promise<string>} A promise that is resolved with a {string}
   *   containing the page mode name.
   */
  fun getPageMode(): Promise<String>

  /**
   * @returns {Promise<Object | null>} A promise that is resolved with an
   *   {Object} containing the viewer preferences, or `null` when no viewer
   *   preferences are present in the PDF file.
   */
  fun getViewerPreferences(): Promise<Any?>

  /**
   * @returns {Promise<any>} A promise that is resolved with a lookup table
   *   for mapping named attachments to their content.
   */
  fun getAttachments(): Promise<Any>

  /**
   * @typedef {Object} OutlineNode
   * @property {string} title
   * @property {boolean} bold
   * @property {boolean} italic
   * @property {Uint8ClampedArray} color - The color in RGB format to use for
   *   display purposes.
   * @property {string | Array<any> | null} dest
   * @property {string | null} url
   * @property {string | undefined} unsafeUrl
   * @property {boolean | undefined} newWindow
   * @property {number | undefined} count
   * @property {Array<OutlineNode>} items
   */
  /**
   * @returns {Promise<Array<OutlineNode>>} A promise that is resolved with an
   *   {Array} that is a tree outline (if it has one) of the PDF file.
   */
  fun getOutline(): Promise<dynamic>

  // /**
  //  * @returns {Promise<{ info: Object, metadata: Metadata }>} A promise that is
  //  *   resolved with an {Object} that has `info` and `metadata` properties.
  //  *   `info` is an {Object} filled with anything available in the information
  //  *   dictionary and similarly `metadata` is a {Metadata} object with
  //  *   information from the metadata section of the PDF.
  //  */
  // getMetadata(): Promise<{
  //   info: Object;
  //   metadata: Metadata;
  // }>;

  /**
   * @returns {Promise<{ length: number }>} A promise that is resolved when the
   *   document's data is loaded. It is resolved with an {Object} that contains
   *   the `length` property that indicates size of the PDF data in bytes.
   */
  fun getDownloadInfo(): Promise<dynamic>

  // /**
  //  * Cleans up resources allocated by the document on both the main and worker
  //  * threads.
  //  *
  //  * NOTE: Do not, under any circumstances, call this method when rendering is
  //  * currently ongoing since that may lead to rendering errors.
  //  *
  //  * @param {boolean} [keepLoadedFonts] - Let fonts remain attached to the DOM.
  //  *   NOTE: This will increase persistent memory usage, hence don't use this
  //  *   option unless absolutely necessary. The default value is `false`.
  //  * @returns {Promise} A promise that is resolved when clean-up has finished.
  //  */
  // cleanup(keepLoadedFonts?: boolean | undefined): Promise<any>;

  /**
   * Destroys the current document instance and terminates the worker.
   */
  fun destroy(): Promise<Unit>

  // /**
  //  * @type {DocumentInitParameters} A subset of the current
  //  *   {DocumentInitParameters}, which are needed in the viewer.
  //  */
  // get loadingParams(): DocumentInitParameters;

  /**
   * The loadingTask for the current document.
   */
  val loadingTask: PdfDocumentLoadingTask
}
