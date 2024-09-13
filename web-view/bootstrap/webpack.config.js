const path = require('path');
const CopyPlugin = require("copy-webpack-plugin");
const webpack = require("webpack");

module.exports = {
  entry: './src/main.ts',
  module: {
    rules: [
      {
        test: /\.tsx?$/,
        use: 'ts-loader',
        exclude: /node_modules/
      }
    ]
  },
  resolve: {
    alias: {
      "pdfjs-lib": path.join(__dirname, "./node_modules/pdfjs-dist/build/pdf.mjs"),
      "fluent-bundle": path.join(__dirname, "./node_modules/@fluent/bundle/esm/index.js"),
      "fluent-dom": path.join(__dirname, "./node_modules/@fluent/dom/esm/index.js"),
      "web-alt_text_manager": path.join(__dirname, "./node_modules/pdf.js/web/alt_text_manager.js"),
      "web-annotation_editor_params": path.join(__dirname, "./node_modules/pdf.js/web/annotation_editor_params.js"),
      "web-download_manager": path.join(__dirname, "./node_modules/pdf.js/web/download_manager.js"),
      "web-external_services": path.join(__dirname, "./node_modules/pdf.js/web/genericcom.js"),
      "web-new_alt_text_manager": path.join(__dirname, "./node_modules/pdf.js/web/new_alt_text_manager.js"),
      "web-null_l10n": path.join(__dirname, "./node_modules/pdf.js/web/genericl10n.js"),
      "web-pdf_attachment_viewer": path.join(__dirname, "./node_modules/pdf.js/web/pdf_attachment_viewer.js"),
      "web-pdf_cursor_tools": path.join(__dirname, "./node_modules/pdf.js/web/pdf_cursor_tools.js"),
      "web-pdf_document_properties": path.join(__dirname, "./node_modules/pdf.js/web/pdf_document_properties.js"),
      "web-pdf_find_bar": path.join(__dirname, "./node_modules/pdf.js/web/pdf_find_bar.js"),
      "web-pdf_layer_viewer": path.join(__dirname, "./node_modules/pdf.js/web/pdf_layer_viewer.js"),
      "web-pdf_outline_viewer": path.join(__dirname, "./node_modules/pdf.js/web/pdf_outline_viewer.js"),
      "web-pdf_presentation_mode": path.join(__dirname, "./node_modules/pdf.js/web/pdf_presentation_mode.js"),
      "web-pdf_sidebar": path.join(__dirname, "./node_modules/pdf.js/web/pdf_sidebar.js"),
      "web-pdf_thumbnail_viewer": path.join(__dirname, "./node_modules/pdf.js/web/pdf_thumbnail_viewer.js"),
      "web-preferences": path.join(__dirname, "./node_modules/pdf.js/web/genericcom.js"),
      "web-print_service": path.join(__dirname, "./node_modules/pdf.js/web/pdf_print_service.js"),
      "web-secondary_toolbar": path.join(__dirname, "./node_modules/pdf.js/web/secondary_toolbar.js"),
      "web-toolbar": path.join(__dirname, "./node_modules/pdf.js/web/toolbar.js"),
    },
    extensions: ['.tsx', '.ts', '.js']
  },
  output: {
    filename: 'main.js',
    path: path.resolve(__dirname, 'build')
  },
  plugins: [
    new webpack.DefinePlugin({
      'typeof PDFJSDev': JSON.stringify('object'),
      PDFJSDev: `({
        test: function (code) {
          if (code === "PRODUCTION") {
            return true;
          } else if (code === "GENERIC") {
            return true;
          } else if (code === "!PRODUCTION || GENERIC"
            || code === "!PRODUCTION || GENERIC || CHROME"
            || code === "GENERIC || CHROME"
            || code === "CHROME || GENERIC"
            || code === "MOZCENTRAL || GENERIC"
            || code === "!PRODUCTION || (GENERIC && !LIB)"
            || code === "GENERIC && !TESTING") {
            return true;
          } else if (code === "GENERIC && !SKIP_BABEL") {
            return false;
          } else if (code === "LIB"
            || code === "LIB && TESTING") {
            return false;
          } else if (code === "CHROME"
            || code === "MOZCENTRAL"
            || code === "MOZCENTRAL || CHROME"
            || code === "GECKOVIEW") {
            return false;
          } else if (code === "TESTING"
            || code === "!PRODUCTION || TESTING"
            || code === "TESTING || MOZCENTRAL"
            || code === "TESTING || LIB") {
            return false;
          } else if (code === "COMPONENTS") {
            return false;
          }
          throw new Error("PDFJSDev-test-" + code);
        },
        eval: function (code) {
          if (code === "BUNDLE_VERSION") {
            return "4.6.82";
          } else if (code === "DEFAULT_PREFERENCES") {
            return window.PDFViewerDefaultPreferences??{};
          } else if (code === "DEFAULT_FTL") {
            return "";
          }
          throw new Error("PDFJSDev-eval-" + code);
        },
        json: function (code) {
          throw new Error("PDFJSDev-json-" + code);
        },
      })`
    }),
    new CopyPlugin({
      patterns: [
        {from: "./node_modules/pdfjs-dist/cmaps", to: "./cmaps"},
        {from: "./node_modules/pdf.js/web/images", to: "./images"},
        {from: "./node_modules/pdf.js/l10n/en-US/viewer.ftl", to: "./locale/en-US/"},
        {from: "./node_modules/pdfjs-dist/build/pdf.worker.mjs", to: "./"},
        {from: "./node_modules/pdfjs-dist/web/pdf_viewer.css", to: "./"},
        {from: "./assets", to: "./assets"},
        {from: "./index.html", to: "./"},
        {from: "./locale.json", to: "./locale/"},
        {from: "./src/viewer.css", to: "./"},
        {from: "./src/fixes.css", to: "./"},
        {from: "./src/sidebar-triangles.css", to: "./"}
      ]
    })
  ]
};
