package io.github.srizzo.codebuddy.util;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.keymap.impl.IdeKeyEventDispatcher;
import com.intellij.openapi.wm.IdeFocusManager;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.KeyEvent;

public class RunActionUtil {
    public static boolean runAction(KeyEvent event, String actionId, String actionPlace) {
        if (!ApplicationManager.getApplication().isDispatchThread()) {
            return false;
        }

        ActionManager actionManager = ActionManager.getInstance();
        AnAction action = actionManager.getAction(actionId);
        if (action == null) {
            return false;
        }

        // Check modal context
        if (!action.isEnabledInModalContext()) {
            Window focusedWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();
            if (focusedWindow != null && IdeKeyEventDispatcher.isModalContext(focusedWindow)) {
                return false;
            }
        }

        // Get proper data context
        DataContext dataContext = getDataContext();

        // Create AnActionEvent using the deprecated but functional API
        // TODO: Replace with modern API when available
        AnActionEvent anActionEvent = AnActionEvent.createFromAnAction(
            action, event, actionPlace, dataContext);

        // Update action presentation
        action.update(anActionEvent);
        if (!anActionEvent.getPresentation().isEnabled()) {
            return false;
        }

        // Execute action using ActionManager's modern execution mechanism
        try {
            actionManager.tryToExecute(action, event, null, actionPlace, true);
            return true;
        } catch (Exception e) {
            // Fallback: direct execution
            try {
                action.actionPerformed(anActionEvent);
                return true;
            } catch (Exception ex) {
                return false;
            }
        }
    }

    private static @NotNull DataContext getDataContext() {
        IdeFocusManager focusManager = IdeFocusManager.findInstance();
        Component focusedComponent = focusManager.getFocusOwner();
        if (focusedComponent != null) {
            return DataManager.getInstance().getDataContext(focusedComponent);
        }
        return DataContext.EMPTY_CONTEXT;
    }
}
