package io.github.srizzo.codebuddy.findreplace;

import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.find.FindUtil;
import com.intellij.find.impl.FindResultImpl;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretState;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actions.SelectOccurrencesActionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.ReadonlyStatusHandler;
import io.github.srizzo.codebuddy.util.FindAndReplaceUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ReplaceAction extends EditorAction {
    public ReplaceAction() {
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

            String replacement = findModel.getStringToReplace();
            if (replacement == null) return;

            if (!ReadonlyStatusHandler.ensureDocumentWritable(project, editor.getDocument())) return;

            @NotNull Runnable runnable = () -> {
                List<CaretState> caretStates = editor.getCaretModel().getCaretsAndSelections();

                for (CaretState caretState : caretStates) {
                    FindUtil.doReplace(project,
                            editor.getDocument(),
                            findModel,
                            new FindResultImpl(editor.logicalPositionToOffset(caretState.getSelectionStart()),
                                    editor.logicalPositionToOffset(caretState.getSelectionEnd())),
                            replacement,
                            true,
                            new ArrayList<>());
                }
            };

            CommandProcessor.getInstance().executeCommand(project, runnable, "Replace", null);
        }
    }
}
