import {Component, ViewChild} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute} from "@angular/router";

@Component({
    selector: 'app-root',
    template: `<ng2-pdfjs-viewer #viewer [download]=false [fullScreen]=false [openFile]=false [viewBookmark]=false pagemode='none'></ng2-pdfjs-viewer>`,
    styles: []
})
export class AppComponent {
    @ViewChild('viewer')
    private viewer;

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
