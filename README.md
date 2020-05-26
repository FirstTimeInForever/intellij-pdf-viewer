# IntelliJ PDF Viewer Plugin

IntelliJ PDF Viewer plugin adds support for viewing PDF files in IntelliJ-based IDEs. This plugin uses recently integrated CEF (Chromium Embedded Framework) browser to render PDF documents with [PDF.js](https://mozilla.github.io/pdf.js/) library.

## Features

- Document navigation
- Presentation mode
- Text search in document*
- Document scaling
- Thumbnail view
- Sections list**
- Document information
- Auto-refresh on document change
- Pages spread (even/odd)
- Horizontal/vertical pages scroll directions

\* Search will work only in text-based documents. For example, it won't work in all-images documents (books scans).

** Sections list should be defined by document. Otherwise it will be disabled. 

## How it looks

![Plugin screenshot](images/plugin-screenshot.png)

## Note on JCEF support

Since CEF browser is still an experimental feature, there is a high chance that it is not shipped by default with your IDE. To be able to use CEF functionality you need to switch to version of JBR that supports it. See [this issue](https://youtrack.jetbrains.com/issue/IDEA-231833#focus=streamItem-27-3993099.0-0) for more details. You can learn how to switch IDE runtime [here](https://www.jetbrains.com/help/idea/switching-boot-jdk.html).

## Development

To build plugin use `buildPlugin` gradle task. This will produce ready to use `zip` archive with plugin contents.

To run/debug IDE with this plugin `runIde` task should be used.

### *Disclaimer*

This plugin is still in it's early stage, so some major bugs can occur.
