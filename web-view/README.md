# Brief overview of web-view implementation

## bootstrap package

* Installs pdf.js library from github releases, to use the provided default viewer. For more information, see [#114](https://github.com/FirstTimeInForever/intellij-pdf-viewer/pull/114#issuecomment-2355900721) and [#116](https://github.com/FirstTimeInForever/intellij-pdf-viewer/pull/116)
* Contains base code required to start pdf.js
* Contains some style fixes for original pdf.js styles
* Starts viewer Kotlin application

## viewer package

* Fully written in Kotlin
* Has type declarations for some of pdf.js public interfaces and internals
