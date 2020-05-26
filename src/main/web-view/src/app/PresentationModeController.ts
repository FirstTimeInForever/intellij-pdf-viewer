export class PresentationModeController {
    private viewer: any = null;
    private targetDocument: Document = null;
    private htmlRoot: HTMLElement = null;

    private enterEventHandlers = [];
    private enterReadyEventHandlers = [];
    private exitEventHandlers = [];

    private clickEventListener = () => {
        this.viewer.pdfPresentationMode.request();
        this.htmlRoot.removeEventListener("click", this.clickEventListener);
        this.htmlRoot.addEventListener("keydown", this.escapeEventListener)
        for (const handler of this.enterEventHandlers) {
            handler();
        }
    };

    private escapeEventListener = (event) => {
        if (event.key === "Escape") {
            this.htmlRoot.removeEventListener("keydown", this.escapeEventListener)
            this.exit();
        }
    };

    constructor(viewer: any) {
        this.viewer = viewer;
        this.targetDocument = viewer.pdfPresentationMode.container.ownerDocument;
        this.htmlRoot = this.targetDocument.getElementsByTagName("html")[0];
    }

    isFullscreen(): boolean {
        return this.viewer.pdfPresentationMode.isFullscreen;
    }

    enter(): boolean {
        console.info("Presentation mode enter call");
        if (this.viewer.pdfPresentationMode.isFullscreen) {
            return false;
        }
        this.htmlRoot.addEventListener("click", this.clickEventListener);
        for (const handler of this.enterReadyEventHandlers) {
            handler();
        }
        console.info("Presentation mode enter call - seems like success");
        return true;
    }

    exit() {
        console.info("Presentation mode exit call");
        if (!this.viewer.pdfPresentationMode.isFullscreen) {
            return;
        }
        //ignored promise
        this.targetDocument.exitFullscreen();
        for (const handler of this.exitEventHandlers) {
            handler();
        }
        console.info("Presentation mode exit call - seems like success");
    }

    addEnterEventHandler(handler) {
        this.enterEventHandlers.push(handler);
    }

    addEnterReadyEventHandler(handler) {
        this.enterReadyEventHandlers.push(handler);
    }

    addExitEventHandler(handler) {
        this.exitEventHandlers.push(handler);
    }
}
