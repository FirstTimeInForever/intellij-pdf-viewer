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
        console.log(`Triggereing event: ${eventName}`);
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
