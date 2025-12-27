package io.github.srizzo.codebuddy.columnmode;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.DumbAwareAction;
import io.github.srizzo.codebuddy.settings.CodeBuddySettingsState;
import org.jetbrains.annotations.NotNull;

public class EnterTemporaryColumnModeAction extends DumbAwareAction {

    private static EditorEx getEditor(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        return editor instanceof EditorEx ? (EditorEx) editor : null;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (!CodeBuddySettingsState.getInstance().holdingModifierActivatesColumnSelectionModeStatus) {
            return;
        }
        
        EditorEx editor = getEditor(e);
        if (editor == null) return;

        editor.setColumnMode(true);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        EditorEx editor = getEditor(e);
        boolean enabled = editor != null && !editor.isOneLineMode() && !editor.isColumnMode() &&
                         CodeBuddySettingsState.getInstance().holdingModifierActivatesColumnSelectionModeStatus;
        e.getPresentation().setEnabledAndVisible(enabled);
    }
}
