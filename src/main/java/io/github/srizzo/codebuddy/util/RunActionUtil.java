package io.github.srizzo.codebuddy.util;

import com.intellij.ide.DataManager;
import com.intellij.ide.actions.ActionsCollector;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.ex.ActionManagerEx;
import com.intellij.openapi.keymap.impl.IdeKeyEventDispatcher;
import com.intellij.openapi.wm.IdeFocusManager;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.KeyEvent;

public class RunActionUtil {
    public static boolean runAction(KeyEvent event, String actionId, String actionPlace) {
        ActionManagerEx ex = ActionManagerEx.getInstanceEx();
        AnAction action = ex.getAction(actionId);
        if (action == null) return false;

        if (!action.isEnabledInModalContext()) {
            Window focusedWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();
            if (focusedWindow != null && IdeKeyEventDispatcher.isModalContext(focusedWindow)) {
                return false;
            }
        }

        AnActionEvent anActionEvent = AnActionEvent.createFromAnAction(action, event, actionPlace, getDataContext());
        action.update(anActionEvent);
        if (!anActionEvent.getPresentation().isEnabled()) return false;

        ex.fireBeforeActionPerformed(action, anActionEvent.getDataContext(), anActionEvent);
        action.actionPerformed(anActionEvent);
        ex.fireAfterActionPerformed(action, anActionEvent.getDataContext(), anActionEvent);

        ActionsCollector.getInstance().record(actionId, anActionEvent.getInputEvent(), action.getClass());
        return true;
    }

    private static @NotNull
    DataContext getDataContext() {
        IdeFocusManager focusManager = IdeFocusManager.findInstance();
        Component focusedComponent = focusManager.getFocusOwner();
        return DataManager.getInstance().getDataContext(focusedComponent);
    }

}
