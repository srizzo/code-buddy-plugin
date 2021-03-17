package io.github.srizzo.codebuddy.columnmode;

import com.intellij.ide.IdeEventQueue;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actions.TextComponentEditorAction;
import com.intellij.openapi.editor.ex.EditorEx;
import io.github.srizzo.codebuddy.settings.CodeBuddySettingsState;
import io.github.srizzo.codebuddy.util.BlockSelectionUtil;
import io.github.srizzo.codebuddy.util.RunActionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.event.KeyEvent;

public class ExitColumnModeAction extends TextComponentEditorAction {
    public static final String ACTION_EXIT_COLUMN_MODE_ACTION = "io.github.srizzo.codebuddy.columnmode.ExitColumnModeAction";

    static {
        IdeEventQueue.getInstance().addDispatcher(event -> handleEvent(event), ApplicationManager.getApplication());
    }

    public static final boolean DONT_STOP = false;

    public ExitColumnModeAction() {
        super(new ExitColumnModeAction.Handler());
    }

    private static boolean handleEvent(AWTEvent event) {
        if (!CodeBuddySettingsState.getInstance().modifiersExitColumnSelectionModeStatus) return DONT_STOP;
        if (!(event instanceof KeyEvent)) return DONT_STOP;

        KeyEvent keyEvent = (KeyEvent) event;

        if (keyEvent.getID() == KeyEvent.KEY_PRESSED &&
                keyEvent.getKeyCode() != BlockSelectionUtil.getMultiCaretActionKeyCode()) {
            RunActionUtil.runAction(keyEvent, ACTION_EXIT_COLUMN_MODE_ACTION,
                    ActionPlaces.KEYBOARD_SHORTCUT);
        }

        return DONT_STOP;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null)
            return;
        if (!editor.isColumnMode()) {
            e.getPresentation().setEnabledAndVisible(false);
        } else {
            super.update(e);
        }
    }

    private static class Handler extends EditorActionHandler {
        Handler() {
            super(false);
        }

        public void doExecute(@NotNull Editor editor, @Nullable Caret caret, DataContext dataContext) {
            if (editor instanceof EditorEx && editor.isColumnMode()) {
                ((EditorEx) editor).setColumnMode(false);
            }
        }
    }
}
