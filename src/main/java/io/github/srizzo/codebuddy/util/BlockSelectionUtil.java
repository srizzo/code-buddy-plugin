package io.github.srizzo.codebuddy.util;

import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.util.SystemInfo;
import org.jetbrains.annotations.NotNull;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class BlockSelectionUtil {
    public static boolean editorHasSelection(EditorEx editor) {
        return editor.getCaretModel().getCaretCount() == 1 && editor.getSelectionModel().hasSelection();
    }

    public static boolean editorHasBlockSelection(EditorEx editor) {
        List<CaretState> caretStates = editor.getCaretModel().getCaretsAndSelections();
        if (caretStates.size() < 2) return false;

        CaretState firstCaret = caretStates.get(0);

        for (CaretState otherCaret : caretStates.subList(1, caretStates.size())) {
            if (firstCaret.getCaretPosition().column != otherCaret.getCaretPosition().column) return false;
            if (otherCaret.getSelectionStart().column == otherCaret.getSelectionEnd().column) continue;
            if (firstCaret.getSelectionStart().column != otherCaret.getSelectionStart().column) return false;
        }

        return true;
    }

    public static void selectionToBlockSelection(EditorEx editor) {
        SelectionModel selectionModel = editor.getSelectionModel();
        CaretModel caretModel = editor.getCaretModel();

        if (!selectionModel.hasSelection()) return;

        int selectionStart = selectionModel.getSelectionStart();
        int selectionEnd = selectionModel.getSelectionEnd();

        LogicalPosition logicalSelectionStart = editor.offsetToLogicalPosition(selectionStart);
        LogicalPosition logicalSelectionEnd = editor.offsetToLogicalPosition(selectionEnd);

        int caretOffset = caretModel.getOffset();
        LogicalPosition blockStart = selectionStart == caretOffset ? logicalSelectionEnd : logicalSelectionStart;
        LogicalPosition blockEnd = selectionStart == caretOffset ? logicalSelectionStart : logicalSelectionEnd;

        List<CaretState> caretStates = calcBlockFromSelectionCaretStates(editor, blockStart, blockEnd);
        editor.getCaretModel().setCaretsAndSelections(caretStates);
    }

    public static void blockSelectionToSelection(EditorEx editor) {
        SelectionModel selectionModel = editor.getSelectionModel();
        CaretModel caretModel = editor.getCaretModel();

        List<Caret> allCarets = caretModel.getAllCarets();
        Caret firstCaret = allCarets.get(0);
        Caret lastCaret = allCarets.get(allCarets.size() - 1);
        if (firstCaret == caretModel.getPrimaryCaret()) {
            Caret tmp = firstCaret;
            firstCaret = lastCaret;
            lastCaret = tmp;
        }

        int selStart = firstCaret.getLeadSelectionOffset();
        int selEnd = lastCaret.getSelectionStart() == lastCaret.getLeadSelectionOffset() ? lastCaret.getSelectionEnd() : lastCaret.getSelectionStart();

        caretModel.removeSecondaryCarets();
        selectionModel.setSelection(selStart, selEnd);
        editor.setColumnMode(false);
    }

    private static List<CaretState> calcBlockFromSelectionCaretStates(@NotNull Editor editor,
                                                                      @NotNull LogicalPosition blockStart,
                                                                      @NotNull LogicalPosition blockEnd) {
        int startLine = Math.max(Math.min(blockStart.line, editor.getDocument().getLineCount() - 1), 0);
        int endLine = Math.max(Math.min(blockEnd.line, editor.getDocument().getLineCount() - 1), 0);
        int step = endLine < startLine ? -1 : 1;
        int count = 1 + Math.abs(endLine - startLine);
        List<CaretState> caretStates = new LinkedList();
        boolean hasSelection = false;
        int line = startLine;

        for (int i = 0; i < count; line += step) {
            int startColumn = blockStart.column;
            int endColumn = blockEnd.column;
            int lineEndOffset = editor.getDocument().getLineEndOffset(line);
            LogicalPosition lineEndPosition = editor.offsetToLogicalPosition(lineEndOffset);
            int lineWidth = lineEndPosition.column;
            LogicalPosition startPosition = new LogicalPosition(line, editor.isColumnMode() ? startColumn : Math.min(startColumn, lineWidth));
            LogicalPosition endPosition = new LogicalPosition(line, editor.isColumnMode() ? endColumn : Math.min(endColumn, lineWidth));
            int startOffset = editor.logicalPositionToOffset(startPosition);
            int endOffset = editor.logicalPositionToOffset(endPosition);

            caretStates.add(new CaretState(endPosition, startPosition, endPosition));
            hasSelection |= startOffset != endOffset;
            ++i;
        }

        if (hasSelection && !editor.isColumnMode()) {
            Iterator caretStateIterator = caretStates.iterator();

            while (caretStateIterator.hasNext()) {
                CaretState state = (CaretState) caretStateIterator.next();
                if (state.getSelectionStart().equals(state.getSelectionEnd())) {
                    caretStateIterator.remove();
                }
            }
        }

        return caretStates;
    }

    public static int getMultiCaretActionKeyCode() {
        return SystemInfo.isMac ? KeyEvent.VK_ALT : KeyEvent.VK_CONTROL;
    }

    public static int getMultiCaretActionKeyModifier() {
        return SystemInfo.isMac ? InputEvent.ALT_DOWN_MASK : InputEvent.CTRL_DOWN_MASK;
    }

    public static boolean isBlockSelectionActive() {
        // For now, always return true when handler is set - we'll track this properly later
        return true;
    }
}
