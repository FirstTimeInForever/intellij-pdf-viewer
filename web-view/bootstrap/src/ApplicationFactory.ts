const bundle = require("../application/viewer");
const applicationPackage = bundle.com.firsttimeinforever.intellij.pdf.viewer.application;

export interface Application {
  // run(): void;
}

export const ApplicationFactory: {
  createApplication: (viewer: any) => Application;
  startApplication: (application: Application) => void;
} = applicationPackage.ApplicationFactory;
