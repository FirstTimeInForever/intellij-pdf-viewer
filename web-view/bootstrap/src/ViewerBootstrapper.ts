const {AppOptions} = require("pdfjs-dist/lib/web/app_options");
const {PDFViewerApplication} = require("pdfjs-dist/lib/web/app");
import {GenericExternalServices} from "./support/GenericExternalServices"
import {getViewerConfiguration} from "./support/ViewerConfiguration";
import "pdfjs-dist/lib/web/pdf_print_service";

export class ViewerBootstrapper {
  static defineViewer(): any {
    PDFViewerApplication.externalServices = GenericExternalServices;
    Object.defineProperty(window, "PDFViewerApplication", {
      get: () => PDFViewerApplication
    });
    AppOptions.set("workerSrc", "pdf.worker.js");
    Object.defineProperty(window, "PDFViewerApplicationOptions", {
      get: () => AppOptions
    });
    return PDFViewerApplication;
  }

  static load(fileUrl: String | null = null): Promise<void> {
    return new Promise(resolve => {
      AppOptions.set("defaultUrl", fileUrl);
      const config = getViewerConfiguration();
      config.eventBus.on("documentloaded", () => resolve());
      // PDFViewerApplication.initialize(config).then()
      PDFViewerApplication.run(config);
    });
  }
}

// function webViewerInitialized() {
//     const appConfig = PDFViewerApplication.appConfig;
//     const queryString = document.location.search.substring(1);
//     const params = parseQueryString(queryString);
//     let file = "file" in params ? params.file : AppOptions.get("defaultUrl");
//     validateFileURL(file);
//
//     if (typeof PDFJSDev === "undefined" || PDFJSDev.test("GENERIC")) {
//         const fileInput = document.createElement("input");
//         fileInput.id = appConfig.openFileInputName;
//         fileInput.className = "fileInput";
//         fileInput.setAttribute("type", "file");
//         fileInput.oncontextmenu = noContextMenuHandler;
//         document.body.appendChild(fileInput);
//
//         if (
//           !window.File ||
//           !window.FileReader ||
//           !window.FileList ||
//           !window.Blob
//         ) {
//             appConfig.toolbar.openFile.setAttribute("hidden", "true");
//             appConfig.secondaryToolbar.openFileButton.setAttribute("hidden", "true");
//         } else {
//             fileInput.value = null;
//         }
//
//         fileInput.addEventListener("change", function (evt) {
//             const files = evt.target.files;
//             if (!files || files.length === 0) {
//                 return;
//             }
//             PDFViewerApplication.eventBus.dispatch("fileinputchange", {
//                 source: this,
//                 fileInput: evt.target,
//             });
//         });
//
//         // Enable dragging-and-dropping a new PDF file onto the viewerContainer.
//         appConfig.mainContainer.addEventListener("dragover", function (evt) {
//             evt.preventDefault();
//
//             evt.dataTransfer.dropEffect = "move";
//         });
//         appConfig.mainContainer.addEventListener("drop", function (evt) {
//             evt.preventDefault();
//
//             const files = evt.dataTransfer.files;
//             if (!files || files.length === 0) {
//                 return;
//             }
//             PDFViewerApplication.eventBus.dispatch("fileinputchange", {
//                 source: this,
//                 fileInput: evt.dataTransfer,
//             });
//         });
//     } else {
//         appConfig.toolbar.openFile.setAttribute("hidden", "true");
//         appConfig.secondaryToolbar.openFileButton.setAttribute("hidden", "true");
//     }
//
//     appConfig.mainContainer.addEventListener(
//       "transitionend",
//       function (evt) {
//           if (evt.target === /* mainContainer */ this) {
//               PDFViewerApplication.eventBus.dispatch("resize", { source: this });
//           }
//       },
//       true
//     );
//
//     try {
//         webViewerOpenFileViaURL(file);
//     } catch (reason) {
//         PDFViewerApplication.l10n
//           .get("loading_error", null, "An error occurred while loading the PDF.")
//           .then(msg => {
//               PDFViewerApplication.error(msg, reason);
//           });
//     }
// }

// let webViewerOpenFileViaURL;
// if (typeof PDFJSDev === "undefined" || PDFJSDev.test("GENERIC")) {
//     webViewerOpenFileViaURL = function (file) {
//         if (file && file.lastIndexOf("file:", 0) === 0) {
//             // file:-scheme. Load the contents in the main thread because QtWebKit
//             // cannot load file:-URLs in a Web Worker. file:-URLs are usually loaded
//             // very quickly, so there is no need to set up progress event listeners.
//             PDFViewerApplication.setTitleUsingUrl(file);
//             const xhr = new XMLHttpRequest();
//             xhr.onload = function () {
//                 PDFViewerApplication.open(new Uint8Array(xhr.response));
//             };
//             xhr.open("GET", file);
//             xhr.responseType = "arraybuffer";
//             xhr.send();
//             return;
//         }
//
//         if (file) {
//             PDFViewerApplication.open(file);
//         }
//     };
// } else if (PDFJSDev.test("MOZCENTRAL || CHROME")) {
//     webViewerOpenFileViaURL = function (file) {
//         PDFViewerApplication.setTitleUsingUrl(file);
//         PDFViewerApplication.initPassiveLoading();
//     };
// } else {
//     webViewerOpenFileViaURL = function (file) {
//         if (file) {
//             throw new Error("Not implemented: webViewerOpenFileViaURL");
//         }
//     };
// }
