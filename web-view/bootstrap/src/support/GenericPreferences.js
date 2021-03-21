import { BasePreferences } from "pdfjs-dist/lib/web/preferences";

export class GenericPreferences extends BasePreferences {
    async _writeToStorage(prefObj) {
        window.localStorage.setItem("pdfjs.preferences", JSON.stringify(prefObj));
    }

    async _readFromStorage() {
        return JSON.parse(window.localStorage.getItem("pdfjs.preferences"));
    }
}
