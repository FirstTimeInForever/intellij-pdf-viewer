import {Injectable, NgZone} from '@angular/core';


interface SubscriptionsList {
    [key: string]: Function[];
}

export enum TriggerableEvents {
    PAGE_CHANGED = "pageChanged",
    DOCUMENT_INFO = "documentInfo",
    PRESENTATION_MODE_ENTER_READY = "presentationModeEnterReady",
    PRESENTATION_MODE_ENTER = "presentationModeEnter",
    PRESENTATION_MODE_EXIT = "presentationModeExit",
    FRAME_FOCUSED = "frameFocused",
    PAGES_COUNT = "pagesCount",
    DOCUMENT_LOAD_ERROR = "documentLoadError",
    UNHANDLED_ERROR = "unhandledError",
    SIDEBAR_VIEW_STATE_CHANGED = "sidebarViewStateChanged",
    SIDEBAR_AVAILABLE_VIEWS_CHANGED = "sidebarAvailableViewsChanged",
    SYNC_EDITOR = "syncEditor",
    ASK_FORWARD_SEARCH_DATA = "askForwardSearchData"
}

@Injectable({
    providedIn: 'root',
})
export class MessageSenderService {
    private subscriptions: SubscriptionsList = {};

    constructor(private zone: NgZone) {
        window['subscribeToMessageEvent'] = (eventName: string, callback: Function) => {
            if (!this.subscriptions[eventName]) {
                this.subscriptions[eventName] = [callback];
            }
            else {
                this.subscriptions[eventName].push(callback);
            }
        }
    }

    triggerEvent(eventName: string, data: any) {
        if (!this.subscriptions[eventName]) {
            return;
        }
        console.log(`Triggereing event: ${eventName} with: ${JSON.stringify(data)}`);
        console.log(data);
        this.subscriptions[eventName].forEach(callback => {
            try {
                callback.apply(null, [data]);
            }
            catch(error) {
                console.warn(`Could not trigger event: ${eventName} with callback: ${callback}`);
                console.warn(error);
            }
        });
    }
}
