const webpack = require("webpack"); // eslint-disable-line no-unused-vars
const path = require("path");
const CopyWebpackPlugin = require('copy-webpack-plugin');

module.exports = {
    context: path.join(__dirname, "src", "main", "node"),
    entry: {
        main: "./main.js"
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
        path: path.join(__dirname, "build", "resources", "main", "web-view", "build"),
        filename: "[name].bundle.js"
    },
    plugins: [
        new CopyWebpackPlugin([
            {from: 'index.html', to: 'index.html'},
            {from: path.join(__dirname, 'node_modules/pdfjs-dist/cmaps'), to: 'cmaps'}
        ])
    ],
    devServer: {
        contentBase: path.join(__dirname, 'build'),
        compress: true,
        port: 9000
    }
};
