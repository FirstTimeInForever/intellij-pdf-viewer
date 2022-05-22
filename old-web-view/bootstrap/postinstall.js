const {cp, exec, exit, test, mkdir, cat} = require("shelljs");
const fs = require('fs');

const pdfjsVersion = "2.5.207";

if (!test("-e", "node_modules/pdf.js")) {
  if (!exec(`npm install --ignore-scripts --no-save mozilla/pdf.js#v${pdfjsVersion}`)) {
    exit(1);
  }
}
// mkdir("-p", "./assets");
// cp("-R", "node_modules/pdfjs-dist/cmaps", "./assets/");
// cp("-R", "node_modules/pdf.js/web/images", "./assets/");

mkdir("-p", "./patched-assets/locale/en-US");
cp("-R", "node_modules/pdf.js/l10n/en-US/viewer.properties", "./patched-assets/locale/en-US/");
fs.appendFileSync('patched-assets/locale/en-US/viewer.properties', cat("missing-props.properties").stdout);
