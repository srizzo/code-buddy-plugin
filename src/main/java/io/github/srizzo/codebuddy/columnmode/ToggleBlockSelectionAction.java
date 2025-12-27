package io.github.srizzo.codebuddy.columnmode;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.DumbAware;
import io.github.srizzo.codebuddy.util.BlockSelectionUtil;
import org.jetbrains.annotations.NotNull;

public class ToggleBlockSelectionAction extends ToggleAction implements DumbAware {

    private static EditorEx getEditor(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        return editor instanceof EditorEx ? (EditorEx) editor : null;
    }

    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean state) {
        EditorEx editor = getEditor(e);
        if (editor == null || !editor.getCaretModel().supportsMultipleCarets()) {
            return;
        }

        if (state) {
            BlockSelectionUtil.selectionToBlockSelection(editor);
        } else {
            BlockSelectionUtil.blockSelectionToSelection(editor);
        }
    }

    @Override
    public boolean isSelected(@NotNull AnActionEvent e) {
        EditorEx editor = getEditor(e);
        return editor != null && BlockSelectionUtil.editorHasBlockSelection(editor);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        EditorEx editor = getEditor(e);
        if (editor != null && !editor.isOneLineMode()) {
            boolean hasSelection = BlockSelectionUtil.editorHasSelection(editor) || 
                                 BlockSelectionUtil.editorHasBlockSelection(editor);
            e.getPresentation().setEnabledAndVisible(hasSelection);
            super.update(e);
        } else {
            e.getPresentation().setEnabledAndVisible(false);
            super.update(e);
        }
    }
}
