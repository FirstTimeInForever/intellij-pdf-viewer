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
      "pdfjs-lib": path.join(__dirname, "./node_modules/pdfjs-dist/build/pdf.js"),
      "pdfjs/core/worker.js": path.join(__dirname, "./node_modules/pdfjs-dist/build/pdf.worker.js"),
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
            || code === "!PRODUCTION || (GENERIC && !LIB)") {
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
          } else if (code === "TESTING" || code === "!PRODUCTION || TESTING") {
            return false;
          } else if (code === "COMPONENTS") {
            return false;
          }
          throw new Error("PDFJSDev-test-" + code);
        },
        eval: function (code) {
          if (code === "BUNDLE_VERSION") {
            return "2.16.105";
          } else if (code === "DEFAULT_PREFERENCES") {
            return window.PDFViewerApplicationOptions.getAll(0x80);
          }
          throw new Error("PDFJSDev-eval-" + code);
        },
        json: function (code) {
          if (code === "$ROOT/build/default_preferences.json") {
            return window.PDFViewerApplicationOptions.getAll(0x80);
          }
          throw new Error("PDFJSDev-json-" + code);
        },
      })`
    }),
    new CopyPlugin({
      patterns: [
        {from: "./node_modules/pdfjs-dist/cmaps", to: "./cmaps"},
        {from: "./node_modules/pdf.js/web/images", to: "./images"},
        {from: "./node_modules/pdf.js/l10n/en-US/viewer.properties", to: "./locale/en-US/"},
        {from: "./node_modules/pdfjs-dist/build/pdf.worker.js", to: "./"},
        {from: "./node_modules/pdfjs-dist/web/pdf_viewer.css", to: "./"},
        {from: "./assets", to: "./assets"},
        {from: "./index.html", to: "./"},
        {from: "./src/viewer.css", to: "./"},
        {from: "./src/fixes.css", to: "./"},
        {from: "./src/sidebar-triangles.css", to: "./"}
      ]
    })
  ]
};
