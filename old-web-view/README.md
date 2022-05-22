# Brief overview of web-view implementation

## bootstrap package

* Installs pdf.js library, compiles it and applies some patches (check `postinstall.js` script)
* Contains base code required to start pdf.js
* Contains some style fixes for original pdf.js styles
* Starts viewer Kotlin application

## viewer package

* Fully written in Kotlin
* Has type declarations for some of pdf.js public interfaces and internals
