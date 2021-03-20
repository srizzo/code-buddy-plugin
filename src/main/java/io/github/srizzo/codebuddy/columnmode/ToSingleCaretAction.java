package io.github.srizzo.codebuddy.columnmode;

import com.intellij.ide.IdeEventQueue;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actions.TextComponentEditorAction;
import com.intellij.openapi.editor.ex.EditorEx;
import io.github.srizzo.codebuddy.settings.CodeBuddySettingsState;
import io.github.srizzo.codebuddy.util.RunActionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.event.KeyEvent;

public class ToSingleCaretAction extends TextComponentEditorAction {
    public static final boolean DONT_STOP = false;
    private static final String ACTION_EXIT_COLUMN_MODE_ACTION = ToSingleCaretAction.class.getName();

    static {
        IdeEventQueue.getInstance().addDispatcher(event -> handleEvent(event), ApplicationManager.getApplication());
    }

    public ToSingleCaretAction() {
        super(new ToSingleCaretAction.Handler());
    }

    private static boolean handleEvent(AWTEvent event) {
        if (!CodeBuddySettingsState.getInstance().arrowUpAndDownReturnToSingleCursorStatus) return DONT_STOP;
        if (!(event instanceof KeyEvent)) return DONT_STOP;

        KeyEvent keyEvent = (KeyEvent) event;

        if (keyEvent.getID() == KeyEvent.KEY_PRESSED && keyEvent.getModifiersEx() == 0) {
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
                ((EditorEx) editor).setColumnMode(false);
            }
        }
    }
}
