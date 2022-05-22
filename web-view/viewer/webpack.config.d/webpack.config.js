const CopyPlugin = require("copy-webpack-plugin");

config.module.rules.push({
  test: /\.s[ac]ss$/i,
  use: [
    // Creates `style` nodes from JS strings
    "style-loader",
    // Translates CSS into CommonJS
    "css-loader",
    // Compiles Sass to CSS
    "sass-loader",
  ]
});

config.plugins.push(new CopyPlugin({
  patterns: [
    // {from: "./node_modules/pdfjs-dist/cmaps", to: "./cmaps"},
    // {from: "./node_modules/pdf.js/web/images", to: "./images"},
    // {from: "./patched-assets/locale", to: "./locale"},
    {from: "../../node_modules/pdfjs-dist/build/pdf.worker.min.js", to: "./"},
    // {from: "./node_modules/pdfjs-dist/web/pdf_viewer.css", to: "./"},
    // {from: "./assets", to: "./assets"},
    // {from: "./index.html", to: "./"},
    // {from: "./src/viewer.css", to: "./"},
    // {from: "./src/fixes.css", to: "./"},
    // {from: "./src/sidebar-triangles.css", to: "./"}
  ]
}));
