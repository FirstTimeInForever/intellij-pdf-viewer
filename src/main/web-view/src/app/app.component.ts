import {Component, ViewChild} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute} from "@angular/router";
import {PdfJsViewerComponent} from "ng2-pdfjs-viewer";
import {MessageReceiverService} from "./message-receiver.service";
import {MessageSenderService} from "./message-sender";

// const viewerFolder = '64fa8636-e686-4c63-9956-132d9471ce77/assets/pdfjs'

@Component({
    selector: 'app-root',
    template: `<ng2-pdfjs-viewer #viewer viewerId="__uniqueViewerId" (onPageChange)="pageChanged($event)" 
                                 [page]="actualPage" [download]=false [fullScreen]=false [openFile]=false 
                                 viewerFolder='64fa8636-e686-4c63-9956-132d9471ce77/assets/pdfjs' 
                                 [viewBookmark]=false pagemode='none'>
    </ng2-pdfjs-viewer>`,
    styles: []
})
export class AppComponent {
    @ViewChild('viewer')
    private viewer: PdfJsViewerComponent;

    actualPage: number;

    pageChanged(pageNumber: number) {
        this.messageSenderService.triggerEvent("pageChanged", {pageNumber});
    }

    constructor(private http: HttpClient, private route: ActivatedRoute,
        private messageReceiverService: MessageReceiverService,
        private messageSenderService: MessageSenderService) {
        const subscription = this.route.queryParams.subscribe(params => {
            const targetUrl = params['path'];
            if (!targetUrl) {
                return;
            }
            subscription.unsubscribe();
            console.log(targetUrl);
            let request = this.http.get(targetUrl, {
                responseType: 'blob'
            });
            request.subscribe((res: Blob) => {
                this.viewer.pdfSrc = res;
                this.viewer.refresh();
            });
        });
        this.messageReceiverService.subscribe("pageSet", (data: any) => {
            this.actualPage = data.pageNumber;
        });
    }
}
