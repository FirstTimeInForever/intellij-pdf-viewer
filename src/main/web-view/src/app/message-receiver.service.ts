import {Injectable, NgZone} from '@angular/core';


interface SubscriptionsList {
    [key: string]: Function[];
}

export enum SubscriptableEvents {
    SET_SCALE = "setScale",
    GOTO_NEXT_PAGE = "nextPage",
    GOTO_PREVIOUS_PAGE = "previousPage",
    TOGGLE_PDFJS_TOOLBAR = "toggleToolbar",
    GET_DOCUMENT_INFO = "getDocumentInfo",
    PRINT_DOCUMENT = "printDocument",
    TOGGLE_SCROLL_DIRECTION = "toggleScrollDirection",
    SPREAD_NONE = "spreadNonePages",
    SPREAD_EVEN_PAGES = "spreadEvenPages",
    SPREAD_ODD_PAGES = "spreadOddPages",
    ROTATE_CLOCKWISE = "rotateClockwise",
    ROTATE_COUNTERCLOCKWISE = "rotateCounterclockwise",
    TOGGLE_PRESENTATION_MODE = "togglePresentationMode",
    FIND_NEXT = "findNext",
    FIND_PREVIOUS = "findPrevious",
    SET_THEME_COLORS = "setThemeColors",
    SET_PAGE = "pageSet",
    TOGGLE_SIDEBAR = "toggleSidebar",
    SET_SIDEBAR_VIEW_MODE = "setSidebarViewState"
}

@Injectable({
    providedIn: 'root',
})
export class MessageReceiverService {
    private subscriptions: SubscriptionsList = {};

    constructor(private zone: NgZone) {
        window['triggerMessageEvent'] = (eventName, args) => {
            if (!this.subscriptions[eventName]) {
                return;
            }
            zone.run(() => {
                this.fireEvent(eventName, args);
            });
        };
    }

    subscribe(eventName: string, callback: Function) {
        if (!this.subscriptions[eventName]) {
            this.subscriptions[eventName] = [callback];
        }
        else {
            if (this.subscriptions[eventName].includes(callback)) {
                console.warn(`${callback} already registered for ${eventName}`);
                return;
            }
            this.subscriptions[eventName].push(callback);
        }
    }

    private fireEvent(eventName: string, args: any) {
        if (!this.subscriptions[eventName]) {
            return;
        }
        this.subscriptions[eventName].forEach((fn) => {
            fn.apply(null, [args]);
        });
    }
}
