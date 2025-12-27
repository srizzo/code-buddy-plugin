package io.github.srizzo.codebuddy.columnmode;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.DumbAwareAction;
import io.github.srizzo.codebuddy.util.BlockSelectionUtil;
import org.jetbrains.annotations.NotNull;

public class ToSingleCaretAction extends DumbAwareAction {

    private static EditorEx getEditor(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        return editor instanceof EditorEx ? (EditorEx) editor : null;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        EditorEx editor = getEditor(e);
        if (editor == null) return;

        // Remove all carets except the primary one
        for (Caret caret : editor.getCaretModel().getAllCarets()) {
            if (!caret.isUpToDate()) {
                editor.getCaretModel().removeCaret(caret);
            }
        }
        
        // Ensure we have block selection converted to regular selection
        if (BlockSelectionUtil.editorHasBlockSelection(editor)) {
            BlockSelectionUtil.blockSelectionToSelection(editor);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        EditorEx editor = getEditor(e);
        boolean enabled = editor != null && !editor.isOneLineMode() && 
                         editor.getCaretModel().getAllCarets().size() > 1;
        e.getPresentation().setEnabledAndVisible(enabled);
    }
}
