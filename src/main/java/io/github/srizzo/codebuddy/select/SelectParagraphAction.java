package io.github.srizzo.codebuddy.select;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actions.TextComponentEditorAction;
import io.github.srizzo.codebuddy.util.ParagraphSelectionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SelectParagraphAction extends TextComponentEditorAction {
    public static String SELECT_PARAGRAPH_ACTION_ID = SelectParagraphAction.class.getName();

    public SelectParagraphAction() {
        super(new SelectParagraphAction.Handler());
    }

    private static class Handler extends EditorActionHandler {
        Handler() {
            super(true);
        }

        public void doExecute(@NotNull Editor editor, @Nullable Caret caret, DataContext dataContext) {
            ParagraphSelectionUtil.selectParagraphAtCaret(caret);
        }
    }
}
