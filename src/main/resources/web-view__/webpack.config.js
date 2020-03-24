const webpack = require("webpack"); // eslint-disable-line no-unused-vars
const path = require("path");
const CopyWebpackPlugin = require('copy-webpack-plugin');

module.exports = {
    context: __dirname,
    entry: {
        main: "./src/main.js"
    },
    module: {
        rules: [{
            test: /\.css$/i,
            use: ['style-loader', 'css-loader']
        }, {
            test: /\.(jpe?g|png|gif|svg)$/i,
            loader: "file-loader"
        }]
    },
    mode: "none",
    output: {
        path: path.join(__dirname, "build"),
        filename: "[name].bundle.js"
    },
    plugins: [
        new CopyWebpackPlugin([
            {from: 'src/index.html', to: 'index.html'},
            {from: 'node_modules/pdfjs-dist/cmaps', to: 'cmaps'}
        ])
    ],
    devServer: {
        contentBase: path.join(__dirname, 'build'),
        compress: true,
        port: 9000
    }
};
