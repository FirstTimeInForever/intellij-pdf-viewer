package com.jetbrains.rdclient.fileEditors

import com.intellij.openapi.fileEditor.ex.FileEditorWithProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.rd.ide.model.FileEditorModel
import com.jetbrains.rd.util.lifetime.Lifetime

/**
 * hidden api of code with me client
 * <br>
 * Created by liudongmiao on 2024-09-30.
 */
interface FrontendFileEditorModelHandler {
  fun accept(project: Project, file: VirtualFile, model: FileEditorModel): Boolean
  fun createEditorWithProvider(project: Project, lifetime: Lifetime, file: VirtualFile, model: FileEditorModel): FileEditorWithProvider
}
