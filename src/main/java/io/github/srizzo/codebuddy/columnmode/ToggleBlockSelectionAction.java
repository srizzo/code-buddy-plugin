package io.github.srizzo.codebuddy.columnmode;

import com.intellij.ide.IdeEventQueue;
import com.intellij.ide.lightEdit.LightEditCompatible;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.DumbAware;
import io.github.srizzo.codebuddy.settings.CodeBuddySettingsState;
import io.github.srizzo.codebuddy.util.BlockSelectionUtil;
import io.github.srizzo.codebuddy.util.RunActionUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class ToggleBlockSelectionAction extends ToggleAction implements DumbAware, LightEditCompatible {
    private static final String ACTION_TOGGLE_MULTI_CARET_ACTION = ToggleBlockSelectionAction.class.getName();

    private static final AtomicBoolean actionStarted = new AtomicBoolean(false);
    private static final AtomicLong actionStartedAt = new AtomicLong(0);
    private static final boolean DONT_STOP = false;

    static {
        IdeEventQueue.getInstance().addDispatcher(event -> handle(event), ApplicationManager.getApplication());
    }

    public ToggleBlockSelectionAction() {
        this.setEnabledInModalContext(true);
    }

    private static boolean handle(AWTEvent event) {
        if (!CodeBuddySettingsState.getInstance().toggleBlockSelectionOnModifierKeyPressStatus) return DONT_STOP;

        if (event instanceof KeyEvent) {
            KeyEvent keyEvent = (KeyEvent) event;
            if (keyEvent.getKeyCode() == BlockSelectionUtil.getMultiCaretActionKeyCode()) {
                switch (keyEvent.getID()) {
                    case KeyEvent.KEY_PRESSED:
                        if (keyEvent.getModifiers() != BlockSelectionUtil.getMultiCaretActionKeyModifier()) break;
                        actionStarted.set(true);
                        actionStartedAt.set(keyEvent.getWhen());
                        break;
                    case KeyEvent.KEY_RELEASED:
                        if (keyEvent.getModifiers() != 0) break;
                        if (actionStarted.get() && keyEvent.getWhen() - actionStartedAt.get() < 200) {
                            RunActionUtil.runAction(keyEvent, ACTION_TOGGLE_MULTI_CARET_ACTION,
                                    ActionPlaces.KEYBOARD_SHORTCUT);
                        }
                }
            } else {
                actionStarted.set(false);
            }
        }

        return DONT_STOP;
    }

    private static EditorEx getEditor(@NotNull AnActionEvent e) {
        return (EditorEx) e.getData(CommonDataKeys.EDITOR);
    }

    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean state) {
        EditorEx editor = getEditor(e);
        if (!editor.getCaretModel().supportsMultipleCarets()) return;

        if (state) {
            BlockSelectionUtil.selectionToBlockSelection(editor);
        } else {
            BlockSelectionUtil.blockSelectionToSelection(editor);
        }
    }

    @Override
    public boolean isSelected(@NotNull AnActionEvent e) {
        return BlockSelectionUtil.editorHasBlockSelection(getEditor(e));
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        EditorEx editor = getEditor(e);
        if (editor != null && !editor.isOneLineMode()) {
            e.getPresentation().setEnabledAndVisible(BlockSelectionUtil.editorHasSelection(editor) || BlockSelectionUtil.editorHasBlockSelection(editor));
            super.update(e);
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }
}
