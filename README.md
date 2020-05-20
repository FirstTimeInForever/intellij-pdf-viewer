# IntelliJ PDF Viewer Plugin

IntelliJ PDF Viewer plugin adds support for viewing PDF files in IntelliJ-based IDEs. This plugin uses recently integrated CEF (Chromium Embedded Framework) browser to render PDF documents with [PDF.js](https://mozilla.github.io/pdf.js/) library.

Since CEF browser is still an experimental feature, there is a high chance that it is not shipped by default with your IDE. To be able to use CEF functionality you need to switch to version of JBR that supports it. See [this issue](https://youtrack.jetbrains.com/issue/IDEA-231833#focus=streamItem-27-3993099.0-0) for more details. You can learn how to switch IDE runtime [here](https://www.jetbrains.com/help/idea/switching-boot-jdk.html).

## How it looks

![Plugin screenshot](images/plugin-screenshot.png)

## Development

To build plugin use `buildPlugin` gradle task. This will produce ready to use `zip` archive with plugin contents.

To run/debug IDE with this plugin `runIde` task should be used.

### *Disclaimer*

This plugin is still in it's early stage, so some major bugs can occur.
