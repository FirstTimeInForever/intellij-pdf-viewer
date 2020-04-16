import {Component, ViewChild} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute} from "@angular/router";
import {PdfJsViewerComponent} from "ng2-pdfjs-viewer";

// const viewerFolder = '64fa8636-e686-4c63-9956-132d9471ce77/assets/pdfjs'

@Component({
    selector: 'app-root',
    template: `<ng2-pdfjs-viewer #viewer viewerFolder='64fa8636-e686-4c63-9956-132d9471ce77/assets/pdfjs' [download]=false [fullScreen]=false [openFile]=false [viewBookmark]=false pagemode='none'></ng2-pdfjs-viewer>`,
    styles: []
})
export class AppComponent {
    @ViewChild('viewer')
    private viewer: PdfJsViewerComponent;

    constructor(private http: HttpClient, private route: ActivatedRoute) {
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
    }
}
