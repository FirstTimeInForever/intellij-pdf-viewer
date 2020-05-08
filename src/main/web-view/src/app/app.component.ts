import {Component, ViewChild} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute} from "@angular/router";
import {PdfJsViewerComponent} from "ng2-pdfjs-viewer";
import {MessageReceiverService} from "./message-receiver.service";
import {MessageSenderService} from "./message-sender";

// const viewerFolder = '64fa8636-e686-4c63-9956-132d9471ce77/assets/pdfjs'

// [viewerFolder]='64fa8636-e686-4c63-9956-132d9471ce77/assets/pdfjs'

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

    private hideToolbar() {
        this.viewer.PDFViewerApplication.appConfig.toolbar.container.parentElement.parentElement.style.display = "none";
        this.viewer.PDFViewerApplication.appConfig.viewerContainer.parentElement.style['top'] = 0;
        this.viewer.PDFViewerApplication.appConfig.sidebar.outerContainer.querySelector("#sidebarContainer").style['top'] = 0;
    }

    private showToolbar() {
        this.viewer.PDFViewerApplication.appConfig.toolbar.container.parentElement.parentElement.style.display = "block";
        this.viewer.PDFViewerApplication.appConfig.viewerContainer.parentElement.style['top'] = "32px";
        this.viewer.PDFViewerApplication.appConfig.sidebar.outerContainer.querySelector("#sidebarContainer").style['top'] = "32px";
    }

    private toggleToolbar() {
        if (this.viewer.PDFViewerApplication.appConfig.toolbar.container.parentElement.parentElement.style.display == "block") {
            this.hideToolbar();
        }
        else {
            this.showToolbar();
        }
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
                this.viewer.onDocumentLoad.subscribe(() => {
                    this.hideToolbar();
                });
            });
        });
        this.messageReceiverService.subscribe("pageSet", (data: any) => {
            this.actualPage = data.pageNumber;
        });
        this.messageReceiverService.subscribe("toggleSidebar", (data: any) => {
            this.viewer.PDFViewerApplication.pdfSidebar.toggleButton.click();
            console.log(this.viewer.PDFViewerApplication);
        });
        this.messageReceiverService.subscribe("increaseScale", (data: any) => {
            this.viewer.PDFViewerApplication.appConfig.toolbar.zoomIn.click();
            // this.viewer.PDFViewerApplication.zoomIn(2);
        });
        this.messageReceiverService.subscribe("decreaseScale", (data: any) => {
            this.viewer.PDFViewerApplication.appConfig.toolbar.zoomOut.click();
            // this.viewer.PDFViewerApplication.zoomOut(2);
        });
        this.messageReceiverService.subscribe("printDocument", (data: any) => {
            this.viewer.PDFViewerApplication.appConfig.toolbar.print.click();
        });
        this.messageReceiverService.subscribe("nextPage", (data: any) => {
            this.viewer.PDFViewerApplication.appConfig.toolbar.next.click();
        });
        this.messageReceiverService.subscribe("previousPage", (data: any) => {
            this.viewer.PDFViewerApplication.appConfig.toolbar.previous.click();
        });
        this.messageReceiverService.subscribe("findNext", (data: any) => {
            this.viewer.PDFViewerApplication.appConfig.findBar.findField.value = data.searchTarget;
            this.viewer.PDFViewerApplication.appConfig.findBar.findNextButton.click();
        });
        this.messageReceiverService.subscribe("findPrevious", (data: any) => {
            this.viewer.PDFViewerApplication.appConfig.findBar.findField.value = data.searchTarget;
            this.viewer.PDFViewerApplication.appConfig.findBar.findPreviousButton.click();
        });
        this.messageReceiverService.subscribe("toggleToolbar", (data: any) => {
            this.toggleToolbar();
        });
    }
}
