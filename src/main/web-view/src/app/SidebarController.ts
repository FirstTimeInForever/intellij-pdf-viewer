// See pdfjs pagemode
import {Subject} from "rxjs";

export enum SidebarViewMode {
    THUMBNAILS = "thumbs",
    BOOKMARKS = "bookmarks",
    ATTACHMENTS = "attachments"
    // Actually, there is one more - none.
    // We treat this by the additional field in SidebarViewState.
}

export interface SidebarViewState {
    mode: SidebarViewMode;
    hidden: boolean;
}

export interface AvailableViewsInfo {
    thumbnails: boolean;
    bookmarks: boolean;
    attachments: boolean;
}

export interface SidebarViewModeChangeMessage {
    mode: SidebarViewMode;
}

/*
 * Precondition: Sidebar is hidden
 */
export class SidebarController {
    private viewer: any = null;
    private sidebar: any = null;
    private availableViewsInfo: AvailableViewsInfo;
    readonly availableViewsInfoChanged = new Subject<AvailableViewsInfo>();
    private currentState: SidebarViewState = {
        mode: SidebarViewMode.THUMBNAILS,
        hidden: true
    };
    readonly viewStateChanged = new Subject<SidebarViewState>();

    constructor(viewer: any) {
        this.viewer = viewer;
        this.sidebar = this.viewer.PDFViewerApplication.pdfSidebar;
        this.availableViewsInfo = {
            attachments: this.viewer.PDFViewerApplication.pdfAttachmentViewer.attachments != null,
            // Thumbnails should always be available
            thumbnails: true,
            bookmarks: this.viewer.PDFViewerApplication.pdfOutlineViewer.outline != null
        };
        // Ensure we aren't missing bookmarks in current document
        this.viewer.PDFViewerApplication.pdfDocument.getOutline().then(outline => {
            if (!outline) {
                return;
            }
            this.availableViewsInfo.bookmarks = true;
            this.availableViewsInfoChanged.next(this.availableViewsInfo);
        });
        // There is no point for notifying subscribers as there are none of them yet
        // this.availableViewsInfoChanged.next(this.availableViewsInfo);
        console.log(this.availableViewsInfo);
        // Could this check actually fail?
        // console.assert(this.availableViewsInfo.thumbnails)
        // Click thumbnails button (it should be always available)
        // to set initial state
        this.sidebar.thumbnailButton.click();
    }

    fixSidebar() {
        const appConfig = this.viewer.PDFViewerApplication.appConfig;
        const sidebarContainer = appConfig.sidebar.outerContainer.querySelector("#sidebarContainer");
        // Hide weird dark top border
        sidebarContainer.style["border-top"] = "none";
        // Hide sidebar view buttons
        sidebarContainer.querySelector("#toolbarSidebar").style.display = "none";
        // Move sidebar content 32px up due to removal of view buttons
        sidebarContainer.querySelector("#sidebarContent").style.top = "0px";
    }

    setMode(mode: SidebarViewMode, ignoreListeners = false) {
        if (mode == SidebarViewMode.THUMBNAILS) {
            if (!this.availableViewsInfo.thumbnails) {
                throw Error("Could not set unavailable sidebar view!");
            }
            this.sidebar.thumbnailButton.click();
        }
        else if (mode == SidebarViewMode.BOOKMARKS) {
            if (!this.availableViewsInfo.bookmarks) {
                throw Error("Could not set unavailable sidebar view!");
            }
            this.sidebar.outlineButton.click();
        }
        else {
            if (!this.availableViewsInfo.attachments) {
                throw Error("Could not set unavailable sidebar view!");
            }
            this.sidebar.attachmentsButton.click();
        }
        this.currentState.mode = mode;
        if (!ignoreListeners) {
            this.notifyViewStateChangeSubscribers();
        }
    }

    setState(state: SidebarViewState) {
        if (state.hidden) {
            this.hide();
        }
        this.setMode(this.currentState.mode, true);
        this.currentState = state;
        this.notifyViewStateChangeSubscribers();
    }

    getCurrentState(): SidebarViewState {
        return this.currentState;
    }

    hide(): boolean {
        if (!this.currentState.hidden) {
            this.sidebar.toggleButton.click();
            this.currentState.hidden = true;
            this.notifyViewStateChangeSubscribers();
            return true;
        }
        return false;
    }

    show(): boolean {
        if (this.currentState.hidden) {
            this.sidebar.toggleButton.click();
            this.currentState.hidden = false;
            this.notifyViewStateChangeSubscribers();
            return true;
        }
        return false;
    }

    toggle() {
        if (this.currentState.hidden) {
            this.show();
        }
        else {
            this.hide();
        }
    }

    getAvailableViewsInfo(): AvailableViewsInfo {
        return this.availableViewsInfo;
    }

    private notifyViewStateChangeSubscribers() {
        this.viewStateChanged.next(this.currentState);
    }
}
