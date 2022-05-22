import "./ViewerBootstrapper";
import {ViewerBootstrapper} from "./ViewerBootstrapper";
import {ApplicationFactory} from "./ApplicationFactory";

async function bootstrapViewer() {
  const action = () => {
    const viewer = ViewerBootstrapper.defineViewer();
    return ViewerBootstrapper.load("get-file=../some.pdf");
  };
  if (document.readyState === "complete" || document.readyState === "interactive") {
    await action();
    return;
  }
  await new Promise<void>(resolve => {
    window.addEventListener("DOMContentLoaded", () => resolve(), true);
  });
  await action();
}

document.addEventListener("textlayerrendered", () => {
  console.log("asdasd");
});

function waitForIde(): Promise<void> {
  return new Promise(resolve => {
    window.addEventListener("IdeReady", () => {
      console.log("Ide is ready");
      resolve();
    }, true);
  });
}

function bootstrap(): Promise<any> {
  return Promise.all([waitForIde(), bootstrapViewer()]);
}

function main() {
  const application = ApplicationFactory.createApplication((window as any).PDFViewerApplication);
  ApplicationFactory.startApplication(application);
}

bootstrap().then(() => {
  console.log("Starting application");
  main();
});
