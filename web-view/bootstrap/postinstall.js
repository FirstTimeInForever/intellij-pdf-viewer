const {exec, exit, test} = require("shelljs");

const pdfjsVersion = "2.6.347";

if (!test("-e", "node_modules/pdf.js")) {
  if (!exec(`npm install --ignore-scripts --no-save mozilla/pdf.js#v${pdfjsVersion}`)) {
    exit(1);
  }
}
