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
    AppOptions.set("cMapUrl", "cmaps/");
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
