package io.github.srizzo.codebuddy.columnmode;

import com.intellij.ide.IdeEventQueue;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import io.github.srizzo.codebuddy.settings.CodeBuddySettingsState;
import io.github.srizzo.codebuddy.util.BlockSelectionUtil;
import io.github.srizzo.codebuddy.util.RunActionUtil;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class ColumnModeStartupActivity implements ProjectActivity {
    private static final String ACTION_TOGGLE_BLOCK_SELECTION = "io.github.srizzo.codebuddy.columnmode.ToggleBlockSelectionAction";
    private static final String ACTION_ENTER_COLUMN_MODE = "io.github.srizzo.codebuddy.columnmode.EnterTemporaryColumnModeAction";
    private static final String ACTION_EXIT_COLUMN_MODE = "io.github.srizzo.codebuddy.columnmode.ExitTemporaryColumnModeAction";
    private static final String ACTION_TO_SINGLE_CARET = "io.github.srizzo.codebuddy.columnmode.ToSingleCaretAction";

    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    private static final AtomicBoolean actionStarted = new AtomicBoolean(false);
    private static final AtomicLong actionStartedAt = new AtomicLong(0);

    @Nullable
    @Override
    public Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        if (initialized.getAndSet(true)) {
            return Unit.INSTANCE;
        }

        ApplicationManager.getApplication().getService(CrosshairCursorOnColumnModeUpdater.class);

        IdeEventQueue.getInstance().addDispatcher(new IdeEventQueue.EventDispatcher() {
            @Override
            public boolean dispatch(@NotNull AWTEvent event) {
                if (!(event instanceof KeyEvent)) {
                    return false;
                }

                KeyEvent keyEvent = (KeyEvent) event;
                CodeBuddySettingsState settings = CodeBuddySettingsState.getInstance();

                int expectedKeyCode = BlockSelectionUtil.getMultiCaretActionKeyCode();
                if (keyEvent.getKeyCode() == expectedKeyCode) {
                    handleModifierKey(keyEvent, settings);
                }

                if (settings.arrowUpAndDownReturnToSingleCursorStatus) {
                    if (keyEvent.getID() == KeyEvent.KEY_PRESSED && keyEvent.getModifiersEx() == 0) {
                        if (keyEvent.getKeyCode() == KeyEvent.VK_UP || keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
                            RunActionUtil.runAction(keyEvent, ACTION_TO_SINGLE_CARET, ActionPlaces.KEYBOARD_SHORTCUT);
                        }
                    }
                }

                if (settings.modifiersExitColumnSelectionModeStatus) {
                    if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
                        switch (keyEvent.getKeyCode()) {
                            case KeyEvent.VK_UP:
                            case KeyEvent.VK_DOWN:
                            case KeyEvent.VK_SHIFT:
                            case KeyEvent.VK_CONTROL:
                            case KeyEvent.VK_META:
                                RunActionUtil.runAction(keyEvent, ACTION_EXIT_COLUMN_MODE, ActionPlaces.KEYBOARD_SHORTCUT);
                                break;
                        }
                    }
                }

                return false;
            }
        }, ApplicationManager.getApplication());

        return Unit.INSTANCE;
    }

    private static void handleModifierKey(KeyEvent keyEvent, CodeBuddySettingsState settings) {
        int expectedModifier = BlockSelectionUtil.getMultiCaretActionKeyModifier();

        switch (keyEvent.getID()) {
            case KeyEvent.KEY_PRESSED:
                if (settings.toggleBlockSelectionOnModifierKeyPressStatus) {
                    if (keyEvent.getModifiersEx() == expectedModifier) {
                        actionStarted.set(true);
                        actionStartedAt.set(keyEvent.getWhen());
                    }
                }
                if (settings.holdingModifierActivatesColumnSelectionModeStatus) {
                    RunActionUtil.runAction(keyEvent, ACTION_ENTER_COLUMN_MODE, ActionPlaces.KEYBOARD_SHORTCUT);
                }
                break;

            case KeyEvent.KEY_RELEASED:
                if (settings.toggleBlockSelectionOnModifierKeyPressStatus) {
                    if (keyEvent.getModifiersEx() == 0) {
                        long elapsed = keyEvent.getWhen() - actionStartedAt.get();
                        if (actionStarted.get() && elapsed < 200) {
                            RunActionUtil.runAction(keyEvent, ACTION_TOGGLE_BLOCK_SELECTION, ActionPlaces.KEYBOARD_SHORTCUT);
                        }
                        actionStarted.set(false);
                    }
                }
                if (settings.holdingModifierActivatesColumnSelectionModeStatus) {
                    RunActionUtil.runAction(keyEvent, ACTION_EXIT_COLUMN_MODE, ActionPlaces.KEYBOARD_SHORTCUT);
                }
                break;
        }
    }
}
