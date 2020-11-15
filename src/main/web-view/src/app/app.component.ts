import {Component, ViewChild} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute} from "@angular/router";
import {PdfJsViewerComponent} from "ng2-pdfjs-viewer";
import {MessageReceiverService, SubscriptableEvents} from "./message-receiver.service";
import {MessageSenderService, TriggerableEvents} from "./message-sender.service";
import {PresentationModeController} from "./PresentationModeController";
import {SidebarController, SidebarViewMode} from "./SidebarController";
import {style} from "@angular/animations";

// @ts-ignore
const iframeCssOverrides = require("./iframe-overrides.less").default;

// const viewerFolder = '64fa8636-e686-4c63-9956-132d9471ce77/assets/pdfjs'

enum SpreadState {
    none,
    odd,
    even
}

interface ThemeColors {
    background: string;
    foreground: string;
    icons: string;
    documentColorInvertIntensity: number;
}

@Component({
    selector: 'app-root',
    template: `<ng2-pdfjs-viewer #viewer viewerId="__uniqueViewerId" (onPageChange)="pageChanged($event)" 
                                 [page]="actualPage" [download]=false [openFile]=false 
                                 viewerFolder='64fa8636-e686-4c63-9956-132d9471ce77/assets/pdfjs' 
                                 [viewBookmark]=false pagemode='none'>
    </ng2-pdfjs-viewer>`,
    styles: []
})
export class AppComponent {
    @ViewChild('viewer')
    private viewer: PdfJsViewerComponent;

    private presentationModeController: PresentationModeController = null;
    private sidebarController: SidebarController = null;

    actualPage: number;

    pageChanged(pageNumber: number) {
        this.messageSenderService.triggerEvent(TriggerableEvents.PAGE_CHANGED, {pageNumber});
    }

    private pagesCount = 0;

    private sendFocusTransferNotification() {
        if (!this.messageSenderService) {
            return;
        }
        this.messageSenderService.triggerEvent(TriggerableEvents.FRAME_FOCUSED, {})
    }

    private hideToolbar() {
        const appConfig = this.viewer.PDFViewerApplication.appConfig;
        appConfig.toolbar.container.parentElement.parentElement.style.display = "none";
        appConfig.viewerContainer.parentElement.style['top'] = 0;
        appConfig.sidebar.outerContainer.querySelector("#sidebarContainer").style['top'] = 0;
    }

    private showToolbar() {
        const appConfig = this.viewer.PDFViewerApplication.appConfig;
        appConfig.toolbar.container.parentElement.parentElement.style.display = "block";
        appConfig.viewerContainer.parentElement.style['top'] = "32px";
        appConfig.sidebar.outerContainer.querySelector("#sidebarContainer").style['top'] = "32px";
    }

    private isToolbarActive() {
        const appConfig = this.viewer.PDFViewerApplication.appConfig;
        return appConfig.toolbar.container.parentElement.parentElement.style.display == "block";
    }

    private toggleToolbar() {
        if (this.isToolbarActive()) {
            this.hideToolbar();
        }
        else {
            this.showToolbar();
        }
    }

    private setThemeColors(colors: ThemeColors) {
        const appConfig = this.viewer.PDFViewerApplication.appConfig;
        appConfig.appContainer.style.background = colors.background;
        console.log(colors);
        console.log(this.viewer);
        this.attachStylesheet(this.generateStylesheet(colors));
    }

    // https://github.com/allefeld/atom-pdfjs-viewer/issues/4#issuecomment-622942606
    private generateStylesheet(colors: ThemeColors) {
        // language=CSS
        return `
        .outlineItemToggler.outlineItemsHidden::after {
            background-color: ${colors.icons};
        }
        .outlineItemToggler::after {
            background-color: ${colors.icons};
        }
        .outlineItem > a {
            color: ${colors.foreground};
        }
        #toolbarSidebar {
            background-color: ${colors.background};
        }
        .page, .thumbnailImage {
            filter: invert(${colors.documentColorInvertIntensity}%);
        }`;
    }

    // FIXME: Save reference to generated stylesheet to prevent creating of new stylesheet for each request
    private attachStylesheet(stylesheet: string) {
        const targetDocument = this.viewer.PDFViewerApplication.appConfig.appContainer.ownerDocument;
        const head = targetDocument.head;
        const element = targetDocument.createElement("style");
        element.textContent = stylesheet;
        head.append(element);
        return element;
    }

    private collectDocumentInfo() {
        const info = {};
        Object.assign(info, this.viewer.PDFViewerApplication.pdfDocumentProperties.fieldData);
        info["fileName"] = this.fileName;
        return info;
    }

    private delayedThemeColors: ThemeColors;
    private fileName: string;

    private static parseFileName(url: string) {
        const split = decodeURIComponent(url).split("/");
        return split[split.length - 1];
    }

    private spreadState = SpreadState.none;

    private spreadNonePages() {
        if (this.spreadState == SpreadState.none) {
            return;
        }
        this.viewer.PDFViewerApplication.appConfig.secondaryToolbar.spreadNoneButton.click();
        this.spreadState = SpreadState.none;
    }

    private spreadOddPages() {
        if (this.spreadState == SpreadState.odd) {
            return;
        }
        this.viewer.PDFViewerApplication.appConfig.secondaryToolbar.spreadOddButton.click();
        this.spreadState = SpreadState.odd;
    }

    private spreadEvenPages() {
        if (this.spreadState == SpreadState.even) {
            return;
        }
        this.viewer.PDFViewerApplication.appConfig.secondaryToolbar.spreadEvenButton.click();
        this.spreadState = SpreadState.even;
    }

    private rotateClockwise() {
        this.viewer.PDFViewerApplication.appConfig.secondaryToolbar.pageRotateCwButton.click();
    }

    private rotateCounterclockwise() {
        this.viewer.PDFViewerApplication.appConfig.secondaryToolbar.pageRotateCcwButton.click();
    }

    private currentScrollDirectionHorizontal = false;

    private toggleScrollDirection() {
        const toolbar = this.viewer.PDFViewerApplication.appConfig.secondaryToolbar;
        if (this.currentScrollDirectionHorizontal) {
            toolbar.scrollVerticalButton.click();
            this.currentScrollDirectionHorizontal = false;
        }
        else {
            toolbar.scrollHorizontalButton.click();
            this.currentScrollDirectionHorizontal = true;
        }
    }

    private togglePresentationMode() {
        if (!this.presentationModeController) {
            console.warn("presentationModeController was null at the time of enter request.");
            return;
        }
        if (!this.presentationModeController.isFullscreen()) {
            this.presentationModeController.enter();
        }
        else {
            this.presentationModeController.exit();
        }
    }

    private createPresentationModeController() {
        this.presentationModeController = new PresentationModeController(this.viewer.PDFViewerApplication);
        this.presentationModeController.addEnterEventHandler(() => {
            this.messageSenderService.triggerEvent(TriggerableEvents.PRESENTATION_MODE_ENTER, {});
        });
        this.presentationModeController.addEnterReadyEventHandler(() => {
            this.messageSenderService.triggerEvent(TriggerableEvents.PRESENTATION_MODE_ENTER_READY, {});
        });
        this.presentationModeController.addExitEventHandler(() => {
            this.messageSenderService.triggerEvent(TriggerableEvents.PRESENTATION_MODE_EXIT, {});
        });
    }

    private ignoredClickTargets = [];

    private buildIgnoreClickTargetsList() {
        const config = this.viewer.PDFViewerApplication.appConfig;
        this.ignoredClickTargets = [
            config.findBar.findPreviousButton,
            config.findBar.findNextButton,
            config.toolbar.previous,
            config.toolbar.next,
            config.toolbar.print,
            config.toolbar.zoomOut,
            config.toolbar.zoomIn,
            this.viewer.PDFViewerApplication.pdfSidebar.toggleButton,
            this.viewer.PDFViewerApplication.pdfSidebar.thumbnailButton,
            this.viewer.PDFViewerApplication.pdfSidebar.outlineButton,
            this.viewer.PDFViewerApplication.pdfSidebar.attachmentsButton,
            config.secondaryToolbar.scrollHorizontalButton,
            config.secondaryToolbar.scrollVerticalButton,
            config.secondaryToolbar.pageRotateCcwButton,
            config.secondaryToolbar.pageRotateCwButton,
            config.secondaryToolbar.spreadEvenButton,
            config.secondaryToolbar.spreadNoneButton,
            config.secondaryToolbar.spreadOddButton,
        ];
    }

    private focusEventHandler = (event) => {
        console.log(event.target);
        if (this.ignoredClickTargets.includes(event.target)) {
            return;
        }
        this.sendFocusTransferNotification();
    };

    constructor(private http: HttpClient, private route: ActivatedRoute,
        private messageReceiverService: MessageReceiverService,
        private messageSenderService: MessageSenderService) {
        window.addEventListener("click", this.focusEventHandler);
        const subscription = this.route.queryParams.subscribe(params => {
            const targetUrl = params['path'];
            this.fileName = AppComponent.parseFileName(targetUrl);
            if (!targetUrl) {
                return;
            }
            console.log(this.fileName);
            subscription.unsubscribe();
            console.log(targetUrl);
            let request = this.http.get(targetUrl, {
                responseType: 'blob'
            });
            request.subscribe((res: Blob) => {
                this.setupErrorCatcher();
                this.viewer.pdfSrc = res;
                this.viewer.refresh();
                this.viewer.onDocumentLoad.subscribe(() => {
                    this.onDocumentLoad();
                });
            });
        });
        this.registerEventSubscriptions();
    }

    private setupErrorCatcher() {
        const iframe = this.viewer.iframe;
        iframe.nativeElement.addEventListener("load", () => {
            iframe.nativeElement.contentWindow.addEventListener("unhandledrejection", (event) => {
                try {
                    if (event.reason.message && event.reason.message.includes("while loading the PDF")) {
                        this.messageSenderService.triggerEvent(TriggerableEvents.DOCUMENT_LOAD_ERROR, {event});
                    }
                    else {
                        this.messageSenderService.triggerEvent(TriggerableEvents.UNHANDLED_ERROR, {});
                    }
                }
                catch (error) {
                    console.error(error);
                }
            });
        });
    }

    private onDocumentLoad() {
        this.attachStylesheet(iframeCssOverrides);
        this.setThemeColors(this.delayedThemeColors);
        this.hideToolbar();
        this.viewer.PDFViewerApplication.unbindWindowEvents();
        window["debugApplication"] = this.viewer.PDFViewerApplication;
        this.sidebarController = new SidebarController(this.viewer);
        this.sidebarController.fixSidebar();
        this.sidebarController.availableViewsInfoChanged.subscribe(info => {
            console.log(`Available views info changed: ${JSON.stringify(info)}`);
            this.messageSenderService.triggerEvent(TriggerableEvents.SIDEBAR_AVAILABLE_VIEWS_CHANGED, info);
        });
        this.sidebarController.viewStateChanged.subscribe(state => {
            console.log(`Sending view state: ${JSON.stringify(state)}`);
            this.messageSenderService.triggerEvent(TriggerableEvents.SIDEBAR_VIEW_STATE_CHANGED, state);
        });
        // Send initial state
        this.messageSenderService.triggerEvent(TriggerableEvents.SIDEBAR_VIEW_STATE_CHANGED,
            this.sidebarController.getCurrentState());
        this.messageSenderService.triggerEvent(TriggerableEvents.SIDEBAR_AVAILABLE_VIEWS_CHANGED,
            this.sidebarController.getAvailableViewsInfo());
        this.createPresentationModeController();
        const targetDocument = this.viewer.PDFViewerApplication.pdfPresentationMode.container.ownerDocument;
        this.buildIgnoreClickTargetsList();
        targetDocument.addEventListener("click", this.focusEventHandler);
        this.ensureDocumentPropertiesReady();
        this.pagesCount = this.viewer.PDFViewerApplication.pdfDocument.numPages;
        if (this.pagesCount) {
            console.log(`Sending pages count: ${this.pagesCount}`);
            this.messageSenderService.triggerEvent(TriggerableEvents.PAGES_COUNT, {
                count: this.pagesCount
            });
        }
    }

    private ensureDocumentPropertiesReady() {
        this.viewer.PDFViewerApplication.pdfDocumentProperties.open();
        this.viewer.PDFViewerApplication.pdfDocumentProperties.close();
    }

    private registerEventSubscriptions() {
        this.messageReceiverService.subscribe(SubscriptableEvents.SET_PAGE, (data: any) => {
            this.actualPage = data.pageNumber;
        });
        this.messageReceiverService.subscribe(SubscriptableEvents.TOGGLE_SIDEBAR, () => {
            this.sidebarController.toggle();
        });
        this.messageReceiverService.subscribe(SubscriptableEvents.SET_SIDEBAR_VIEW_MODE, data => {
            console.log(`Setting view mode: ${data.mode}`);
            if (!this.sidebarController) {
                return;
            }
            this.sidebarController.setMode(data.mode, true);
        });
        this.messageReceiverService.subscribe(SubscriptableEvents.INCREASE_SCALE, () => {
            this.viewer.PDFViewerApplication.appConfig.toolbar.zoomIn.click();
            // this.viewer.PDFViewerApplication.zoomIn(2);
        });
        this.messageReceiverService.subscribe(SubscriptableEvents.DECREASE_SCALE, () => {
            this.viewer.PDFViewerApplication.appConfig.toolbar.zoomOut.click();
            // this.viewer.PDFViewerApplication.zoomOut(2);
        });
        this.messageReceiverService.subscribe(SubscriptableEvents.PRINT_DOCUMENT, () => {
            this.viewer.PDFViewerApplication.appConfig.toolbar.print.click();
        });
        this.messageReceiverService.subscribe(SubscriptableEvents.GOTO_NEXT_PAGE, () => {
            this.viewer.PDFViewerApplication.appConfig.toolbar.next.click();
        });
        this.messageReceiverService.subscribe(SubscriptableEvents.GOTO_PREVIOUS_PAGE, () => {
            this.viewer.PDFViewerApplication.appConfig.toolbar.previous.click();
        });
        this.messageReceiverService.subscribe(SubscriptableEvents.FIND_NEXT, (data: any) => {
            this.viewer.PDFViewerApplication.appConfig.findBar.findField.value = data.searchTarget;
            this.viewer.PDFViewerApplication.appConfig.findBar.findNextButton.click();
        });
        this.messageReceiverService.subscribe(SubscriptableEvents.FIND_PREVIOUS, (data: any) => {
            this.viewer.PDFViewerApplication.appConfig.findBar.findField.value = data.searchTarget;
            this.viewer.PDFViewerApplication.appConfig.findBar.findPreviousButton.click();
        });
        this.subscribeTo(SubscriptableEvents.TOGGLE_PDFJS_TOOLBAR, this.toggleToolbar);
        this.messageReceiverService.subscribe(SubscriptableEvents.SET_THEME_COLORS, (data: ThemeColors) => {
            this.delayedThemeColors = data;
            try {
                this.setThemeColors(this.delayedThemeColors);
            }
            catch (error) {
                console.warn("Could not set theme color!");
            }
        });
        this.messageReceiverService.subscribe(SubscriptableEvents.GET_DOCUMENT_INFO, () => {
            this.messageSenderService.triggerEvent(TriggerableEvents.DOCUMENT_INFO, this.collectDocumentInfo());
        });
        this.subscribeTo(SubscriptableEvents.TOGGLE_SCROLL_DIRECTION, this.toggleScrollDirection);
        this.subscribeTo(SubscriptableEvents.ROTATE_CLOCKWISE, this.rotateClockwise);
        this.subscribeTo(SubscriptableEvents.ROTATE_COUNTERCLOCKWISE, this.rotateCounterclockwise);
        this.subscribeTo(SubscriptableEvents.SPREAD_NONE, this.spreadNonePages);
        this.subscribeTo(SubscriptableEvents.SPREAD_ODD_PAGES, this.spreadOddPages);
        this.subscribeTo(SubscriptableEvents.SPREAD_EVEN_PAGES, this.spreadEvenPages);
        this.subscribeTo(SubscriptableEvents.TOGGLE_PRESENTATION_MODE, this.togglePresentationMode);
    }

    private subscribeTo(event: string, callback) {
        this.messageReceiverService.subscribe(event, (data: any) => {
            callback.apply(this, data);
        });
    }
}
