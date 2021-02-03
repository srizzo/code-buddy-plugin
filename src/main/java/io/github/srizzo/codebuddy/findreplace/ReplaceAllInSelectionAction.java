package io.github.srizzo.codebuddy.findreplace;

import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.find.FindUtil;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actions.SelectOccurrencesActionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.ReadonlyStatusHandler;
import io.github.srizzo.codebuddy.util.FindAndReplaceUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReplaceAllInSelectionAction extends EditorAction {
    public ReplaceAllInSelectionAction() {
        super(new Handler());
    }

    private static class Handler extends SelectOccurrencesActionHandler {

        @Override
        public void doExecute(@NotNull final Editor editor, @Nullable Caret c, DataContext dataContext) {
            Project project = editor.getProject();
            if (project == null) return;

            FindManager findManager = FindAndReplaceUtil.getFindManager(editor);
            if (findManager == null) return;

            final FindModel findModel = findManager.getFindInFileModel();
            if (findModel == null) return;

            if (!ReadonlyStatusHandler.ensureDocumentWritable(project, editor.getDocument())) return;

            final FindModel findModelCopy = new FindModel();
            findModelCopy.copyFrom(findModel);
            findModelCopy.setPromptOnReplace(false);
            findModelCopy.setGlobal(false);

            final SelectionModel selectionModel = editor.getSelectionModel();

            final int offset = selectionModel.getBlockSelectionStarts()[0];

            FindUtil.replace(project, editor, offset, findModelCopy);
        }
    }
}
