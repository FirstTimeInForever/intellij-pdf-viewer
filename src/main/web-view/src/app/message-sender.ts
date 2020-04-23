import {Injectable, NgZone} from '@angular/core';


interface SubscriptionsList {
    [key: string]: Function[];
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
        console.log("Triggereing event");
        this.subscriptions[eventName].forEach(callback => {
            callback.apply(null, [data]);
        });
    }
}
