package io.github.srizzo.codebuddy.util;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.refactoring.actions.BaseRefactoringAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public class SelectionUtil {
    public static final Map<Character, Character> TYPING_PAIRS = new HashMap<>();

    static {
        SelectionUtil.TYPING_PAIRS.put('\'', '\'');
        SelectionUtil.TYPING_PAIRS.put('"', '"');
        SelectionUtil.TYPING_PAIRS.put('`', '`');
        SelectionUtil.TYPING_PAIRS.put(']', '[');
        SelectionUtil.TYPING_PAIRS.put(')', '(');
        SelectionUtil.TYPING_PAIRS.put('}', '{');
        SelectionUtil.TYPING_PAIRS.put('>', '<');
    }

    public static void selectEnclosingTypingPairs(@NotNull Editor editor, @Nullable Caret caret, DataContext dataContext) {
        PsiElement element = getElementAtCaret(dataContext);
        if (element == null) return;

        Document document = editor.getDocument();
        PsiDocumentManager.getInstance(editor.getProject()).commitDocument(document);
        CharSequence contents = document.getImmutableCharSequence();

        Stream<PsiElement> parentElements = getTextParents(element);

        Optional<TextRange> result = parentElements
                .filter(candidate -> enclosesCurrentSelection(candidate.getTextRange(), TextRange.create(caret.getSelectionStart(), caret.getSelectionEnd())))
                .map(candidate -> getEnclosingTypingPairs(contents, candidate.getTextRange(), TextRange.create(caret.getSelectionStart(), caret.getSelectionEnd())))
                .filter(Objects::nonNull)
                .findFirst();

        result.ifPresent(textRange -> caret.setSelection(textRange.getStartOffset(), textRange.getEndOffset()));
    }

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

    public static Stream<PsiElement> getTextParents(PsiElement element) {
        return Stream.iterate(element, it -> it.getParent()).takeWhile(SelectionUtil::hasTextParent);
    }

    public static boolean hasTextParent(PsiElement psiElement) {
        return psiElement.getParent() != null && psiElement.getParent().getTextRange() != null;
    }

    public static TextRange getEnclosingTypingPairs(CharSequence contents, TextRange candidateTextRange, TextRange currentSelectionTextRange) {
        if (candidateTextRange.getLength() < 2) return null;

        char candidateStartingChar = contents.charAt(candidateTextRange.getStartOffset());
        char candidateEndingChar = contents.charAt(candidateTextRange.getEndOffset() - 1);

        if (!TYPING_PAIRS.containsKey(candidateEndingChar)) return null;



        Character matchingStartingChar = TYPING_PAIRS.get(candidateEndingChar);

        for (int i = currentSelectionTextRange.getStartOffset(); i >= candidateTextRange.getStartOffset(); i--) {
            if (contents.charAt(i) == matchingStartingChar) {
                TextRange innerSelection = TextRange.create(i + 1, candidateTextRange.getEndOffset() - 1);
                TextRange outerSelection = TextRange.create(i, candidateTextRange.getEndOffset());

                if (innerSelection.contains(currentSelectionTextRange) && !innerSelection.equals(currentSelectionTextRange))
                    return innerSelection;

                if (outerSelection.contains(currentSelectionTextRange) && !outerSelection.equals(currentSelectionTextRange))
                    return outerSelection;
            }
        }

        return null;
    }

    public static boolean enclosesCurrentSelection(TextRange candidateTextRange, TextRange currentSelectionTextRange) {
        return candidateTextRange.contains(currentSelectionTextRange) && !candidateTextRange.equals(currentSelectionTextRange);
    }

    public static PsiElement getElementAtCaret(@NotNull DataContext dataContext) {
        Editor editor = CommonDataKeys.EDITOR.getData(dataContext);
        PsiFile psiFile = PsiUtilCore.getTemplateLanguageFile(CommonDataKeys.PSI_FILE.getData(dataContext));
        if (editor == null || psiFile == null) return null;

        return BaseRefactoringAction.getElementAtCaret(editor, psiFile);
    }
}
