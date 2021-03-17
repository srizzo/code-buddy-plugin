package io.github.srizzo.codebuddy.columnmode;

import com.intellij.ide.IdeEventQueue;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actions.TextComponentEditorAction;
import io.github.srizzo.codebuddy.settings.CodeBuddySettingsState;
import io.github.srizzo.codebuddy.util.RunActionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.event.KeyEvent;

public class RemoveSecondaryCaretsAction extends TextComponentEditorAction {
    private static final String ACTION_EXIT_COLUMN_MODE_ACTION = "io.github.srizzo.codebuddy.columnmode.RemoveSecondaryCaretsAction";

    static {
        IdeEventQueue.getInstance().addDispatcher(event -> handleEvent(event), ApplicationManager.getApplication());
    }

    public static final boolean DONT_STOP = false;

    public RemoveSecondaryCaretsAction() {
        super(new RemoveSecondaryCaretsAction.Handler());
    }

    private static boolean handleEvent(AWTEvent event) {
        if (!CodeBuddySettingsState.getInstance().arrowUpAndDownReturnToSingleCursorStatus) return DONT_STOP;
        if (!(event instanceof KeyEvent)) return DONT_STOP;

        KeyEvent keyEvent = (KeyEvent) event;

        if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
            switch (keyEvent.getKeyCode()) {
                case KeyEvent.VK_UP:
                case KeyEvent.VK_DOWN:
                    RunActionUtil.runAction(keyEvent, ACTION_EXIT_COLUMN_MODE_ACTION, ActionPlaces.KEYBOARD_SHORTCUT);
            }
        }

        return DONT_STOP;
    }

    private static class Handler extends EditorActionHandler {
        Handler() {
            super(false);
        }

        public void doExecute(@NotNull Editor editor, @Nullable Caret caret, DataContext dataContext) {
            if (!editor.isColumnMode()) {
                editor.getCaretModel().removeSecondaryCarets();
            }
        }
    }
}
