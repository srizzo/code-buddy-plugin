package io.github.srizzo.codebuddy.twiddle;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import io.github.srizzo.codebuddy.columnmode.ExitTemporaryColumnModeAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TwiddleAction extends AnAction {
    public static final String TWIDDLE_ACTION_ID = TwiddleAction.class.getName();

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        final Editor editor = event.getData(CommonDataKeys.EDITOR);
        if (editor == null) return;

        WriteCommandAction.runWriteCommandAction(editor.getProject(), () -> {
            if (!editor.getCaretModel().supportsMultipleCarets()) return;

            List<Caret> allCarets = editor.getCaretModel().getAllCarets();
            if (allCarets.size() < 2) return;

            Caret firstCaret = allCarets.get(0);
            Caret lastCaret = allCarets.get(allCarets.size() - 1);
            String lastCaretSelectedText = lastCaret.getSelectedText();

            for (int i = allCarets.size() - 1; i > 0; i--) {
                Caret currentCaret = allCarets.get(i);
                Caret previousCaret = allCarets.get(i - 1);

                editor.getDocument().replaceString(
                        currentCaret.getSelectionStart(),
                        currentCaret.getSelectionEnd(),
                        previousCaret.getSelectedText());
            }

            editor.getDocument().replaceString(firstCaret.getSelectionStart(),
                    firstCaret.getSelectionEnd(),
                    lastCaretSelectedText);
        });
    }
}


