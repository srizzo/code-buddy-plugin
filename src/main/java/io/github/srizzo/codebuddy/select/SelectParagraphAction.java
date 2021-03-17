package io.github.srizzo.codebuddy.select;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actions.TextComponentEditorAction;
import io.github.srizzo.codebuddy.util.SelectionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SelectParagraphAction extends TextComponentEditorAction {
    public SelectParagraphAction() {
        super(new SelectParagraphAction.Handler());
    }

    private static class Handler extends EditorActionHandler {
        Handler() {
            super(true);
        }

        public void doExecute(@NotNull Editor editor, @Nullable Caret caret, DataContext dataContext) {
            SelectionUtil.selectParagraphAtCaret(caret);
        }
    }
}
