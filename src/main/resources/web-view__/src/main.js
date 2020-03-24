import './index.css';
import 'pdfjs-dist/web/pdf_viewer.css';
const pdfjsLib = require('pdfjs-dist/webpack');
const pdfjsViewer = require('pdfjs-dist/web/pdf_viewer');


const eventBus = new pdfjsViewer.EventBus();

// (Optionally) enable hyperlinks within PDF files.
const pdfLinkService = new pdfjsViewer.PDFLinkService({
    eventBus: eventBus,
});

// (Optionally) enable find controller.
const pdfFindController = new pdfjsViewer.PDFFindController({
    eventBus: eventBus,
    linkService: pdfLinkService,
});

console.log(document.getElementById('viewerContainer'));

const pdfViewer = new pdfjsViewer.PDFViewer({
    container: document.getElementById('viewerContainer'),
    eventBus: eventBus,
    linkService: pdfLinkService,
    findController: pdfFindController,
});
pdfLinkService.setViewer(pdfViewer);

eventBus.on('pagesinit', () => {
    pdfViewer.currentScaleValue = 'page-width';
    // if (SEARCH_FOR) {
    //     pdfFindController.executeCommand("find", {query: SEARCH_FOR});
    // }
});

function loadPdfDocument(url) {
    // pdfjsLib.disableFontFace = true;
    const loadingTask = pdfjsLib.getDocument({
        url,
        cMapUrl: 'cmaps',
        cMapPacked: false
    });
    loadingTask.promise.then((pdfDocument) => {
        pdfViewer.setDocument(pdfDocument);
        pdfLinkService.setDocument(pdfDocument, null);
    });
}

window.loadPdfDocument = loadPdfDocument;
