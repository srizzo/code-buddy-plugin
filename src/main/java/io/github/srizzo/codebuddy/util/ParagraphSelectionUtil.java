package io.github.srizzo.codebuddy.util;

import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ParagraphSelectionUtil {
    public static void selectParagraphAtCaret(@NotNull Caret caret) {
        Editor editor = caret.getEditor();
        int caretLineNumber = caret.getLogicalPosition().line;
        Document document = editor.getDocument();
        if (caretLineNumber >= document.getLineCount()) {
            return;
        }

        LogicalPosition selectionStart = lineStart(editor, caret.getSelectionStartPosition());
        LogicalPosition selectionEnd = lineEnd(editor, caret.getSelectionEndPosition());

        while (hasPreviousLine(selectionStart) &&
                !isBlank(getLineText(editor, previousLine(selectionStart))) &&
                !isBlank(getLineText(editor, previousLine(selectionStart)))) {
            selectionStart = previousLine(selectionStart);
        }

        if (hasNextLine(document, selectionEnd) && isBlank(getLineText(editor, selectionEnd))) {
            selectionEnd = nextLine(selectionEnd);
        }

        while (hasNextLine(document, selectionEnd) && !isBlank(getLineText(editor, selectionEnd))) {
            selectionEnd = nextLine(selectionEnd);
        }

        if (!hasNextLine(document, selectionEnd)) {
            selectionEnd = lineEnd(editor, selectionEnd);
        }

        editor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);
        caret.removeSelection();
        caret.setSelection(editor.logicalPositionToOffset(selectionStart), editor.logicalPositionToOffset(selectionEnd));
    }


    private static boolean isBlank(String string) {
        if (string == null) return true;
        return string.trim().isEmpty();
    }

    @NotNull
    private static LogicalPosition lineEnd(@NotNull Editor editor, @NotNull LogicalPosition logicalPosition) {
        return editor.offsetToLogicalPosition(editor.getDocument().getLineEndOffset(logicalPosition.line));
    }

    @NotNull
    private static LogicalPosition lineEnd(@NotNull Editor editor, @NotNull VisualPosition visualPosition) {
        return editor.offsetToLogicalPosition(EditorUtil.getNotFoldedLineEndOffset(editor, editor.visualPositionToOffset(visualPosition)));
    }

    @NotNull
    private static LogicalPosition lineStart(@NotNull Editor editor, @NotNull VisualPosition visualPosition) {
        return new LogicalPosition(editor.visualToLogicalPosition(visualPosition).line, 0);
    }

    @NotNull
    private static LogicalPosition previousLine(@NotNull LogicalPosition position) {
        return new LogicalPosition(position.line - 1, 0);
    }

    private static boolean hasPreviousLine(@NotNull LogicalPosition position) {
        return position.line > 0;
    }

    private static boolean hasNextLine(@NotNull Document document, @NotNull LogicalPosition position) {
        return position.line < document.getLineCount() - 1;
    }

    private static LogicalPosition nextLine(@NotNull LogicalPosition position) {
        return new LogicalPosition(position.line + 1, 0);
    }

    @NotNull
    private static String getLineText(@NotNull Editor editor, @NotNull LogicalPosition position) {
        Document document = editor.getDocument();
        return document.getText(TextRange.create(document.getLineStartOffset(position.line), document.getLineEndOffset(position.line)));
    }

    @NotNull
    public static TextRange[] getSelectionRanges(@NotNull Editor editor) {
        return editor
                .getCaretModel()
                .getCaretsAndSelections()
                .stream()
                .map(caretState -> TextRange.create(
                        editor.logicalPositionToOffset(caretState.getSelectionStart()),
                        editor.logicalPositionToOffset(caretState.getSelectionEnd()))).
                        toArray(TextRange[]::new);
    }

    @NotNull
    public static String[] getSelectedTexts(Editor editor) {
        return Arrays.stream(getSelectionRanges(editor))
                .map(textRange -> editor.getDocument().getText(textRange))
                .filter(text -> !StringUtil.isEmpty(text))
                .toArray(String[]::new);
    }


}
