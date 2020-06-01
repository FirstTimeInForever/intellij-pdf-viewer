import {Injectable, NgZone} from '@angular/core';


interface SubscriptionsList {
    [key: string]: Function[];
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
