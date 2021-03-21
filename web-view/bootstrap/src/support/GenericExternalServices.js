import { DefaultExternalServices } from "pdfjs-dist/lib/web/app";
import { DownloadManager } from "pdfjs-dist/lib/web/download_manager";
import { GenericLocalization } from "./GenericLocalization";
import { GenericPreferences } from "./GenericPreferences";

export class GenericExternalServices extends DefaultExternalServices {
    static createDownloadManager(options) {
        return new DownloadManager(options);
    }

    static createPreferences() {
        return new GenericPreferences();
    }

    static createL10n({locale = "en-US"}) {
        return new GenericLocalization(locale);
    }
}
