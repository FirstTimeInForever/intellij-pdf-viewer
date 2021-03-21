const path = require('path');
const CopyPlugin = require("copy-webpack-plugin");

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
        extensions: ['.tsx', '.ts', '.js']
    },
    output: {
        filename: 'main.js',
        path: path.resolve(__dirname, 'build')
    },
    plugins: [
        new CopyPlugin({
            patterns: [
                { from: "./node_modules/pdfjs-dist/cmaps", to: "./cmaps" },
                { from: "./node_modules/pdf.js/web/images", to: "./images" },
                { from: "./patched-assets/locale", to: "./locale" },
                { from: "./node_modules/pdfjs-dist/build/pdf.worker.js", to: "./" },
                { from: "./node_modules/pdfjs-dist/web/pdf_viewer.css", to: "./" },
                { from: "./assets", to: "./assets" },
                { from: "./index.html", to: "./" },
                { from: "./src/viewer.css", to: "./" },
                { from: "./src/fixes.css", to: "./" },
                { from: "./src/sidebar-triangles.css", to: "./" }
            ]
        })
    ]
};
