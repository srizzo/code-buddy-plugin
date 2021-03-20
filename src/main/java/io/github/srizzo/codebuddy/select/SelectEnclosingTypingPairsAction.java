package io.github.srizzo.codebuddy.select;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actions.TextComponentEditorAction;
import io.github.srizzo.codebuddy.util.EnclosingTypingPairsSelectionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SelectEnclosingTypingPairsAction extends TextComponentEditorAction {
    public SelectEnclosingTypingPairsAction() {
        super(new SelectEnclosingTypingPairsAction.Handler());
    }

    public static class Handler extends EditorActionHandler {

        Handler() {
            super(true);
        }

        public void doExecute(@NotNull Editor editor, @Nullable Caret caret, DataContext dataContext) {
            EnclosingTypingPairsSelectionUtil.selectEnclosingTypingPairs(editor, caret, dataContext);
        }
    }
}
