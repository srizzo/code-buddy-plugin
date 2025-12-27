package io.github.srizzo.codebuddy.columnmode;

import com.intellij.ide.IdeEventQueue;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.util.ui.UIUtil;
import io.github.srizzo.codebuddy.settings.CodeBuddySettingsState;
import io.github.srizzo.codebuddy.util.BlockSelectionUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.KeyEvent;

@Service
final public class CrosshairCursorOnColumnModeUpdater {
    public CrosshairCursorOnColumnModeUpdater() {
        IdeEventQueue.getInstance().addDispatcher(new IdeEventQueue.EventDispatcher() {
            @Override
            public boolean dispatch(@NotNull AWTEvent event) {
                CodeBuddySettingsState settings = CodeBuddySettingsState.getInstance();

                if (!settings.crosshairCursorOnColumnSelectionModeStatus) {
                    return false;
                }

                if (!settings.holdingModifierActivatesColumnSelectionModeStatus) {
                    return false;
                }

                if (!(event instanceof KeyEvent)) {
                    return false;
                }

                KeyEvent keyEvent = (KeyEvent) event;
                int expectedKeyCode = BlockSelectionUtil.getMultiCaretActionKeyCode();

                if (keyEvent.getKeyCode() != expectedKeyCode) {
                    return false;
                }

                if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
                    setCursorOnAllEditors(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                } else if (keyEvent.getID() == KeyEvent.KEY_RELEASED) {
                    setCursorOnAllEditors(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }

                return false;
            }
        }, ApplicationManager.getApplication());
    }

    private void setCursorOnAllEditors(Cursor cursor) {
        for (Editor editor : EditorFactory.getInstance().getAllEditors()) {
            if (editor instanceof EditorEx) {
                UIUtil.setCursor(((EditorEx) editor).getContentComponent(), cursor);
            }
        }
    }
}
