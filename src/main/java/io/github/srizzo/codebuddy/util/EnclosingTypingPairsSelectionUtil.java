package io.github.srizzo.codebuddy.util;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.refactoring.actions.BaseRefactoringAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class EnclosingTypingPairsSelectionUtil {
    public static final Map<Character, Character> TYPING_PAIRS = new HashMap<>();
    private static final Logger LOG = Logger.getInstance(EnclosingTypingPairsSelectionUtil.class);

    static {
        EnclosingTypingPairsSelectionUtil.TYPING_PAIRS.put('\'', '\'');
        EnclosingTypingPairsSelectionUtil.TYPING_PAIRS.put('"', '"');
        EnclosingTypingPairsSelectionUtil.TYPING_PAIRS.put('`', '`');
        EnclosingTypingPairsSelectionUtil.TYPING_PAIRS.put(']', '[');
        EnclosingTypingPairsSelectionUtil.TYPING_PAIRS.put(')', '(');
        EnclosingTypingPairsSelectionUtil.TYPING_PAIRS.put('}', '{');
        EnclosingTypingPairsSelectionUtil.TYPING_PAIRS.put('>', '<');
    }

    public static void selectEnclosingTypingPairs(@NotNull Editor editor, @Nullable Caret caret, DataContext dataContext) {
        if (caret == null) return;

        PsiElement elementAtCaret = getElementAtCaret(dataContext);
        if (elementAtCaret == null) return;

        Document document = editor.getDocument();
        PsiDocumentManager.getInstance(editor.getProject()).commitDocument(document);
        CharSequence contents = document.getImmutableCharSequence();

        Optional<TextRange> result = recurseTextElements(elementAtCaret, PsiElement::getParent)
                .peek(psiElement -> LOG.debug("         parent: " + PsiInspectUtil.inspect(psiElement)))
                .peek(psiElement -> LOG.debug("filtered parent: " + PsiInspectUtil.inspect(psiElement)))
                .map(candidate -> findEnclosingTypingPairs(contents, candidate, TextRange.create(caret.getSelectionStart(), caret.getSelectionEnd())))
                .filter(Objects::nonNull)
                .findFirst();

        result.ifPresent(textRange -> caret.setSelection(textRange.getStartOffset(), textRange.getEndOffset()));
    }


    private static TextRange findEnclosingTypingPairs(CharSequence contents, PsiElement candidate, TextRange currentSelectionTextRange) {
        TextRange candidateTextRange = candidate.getTextRange();

        if (isEnclosingTypingPairsElement(contents, candidateTextRange)) {
            TextRange selection = calculateSelection(candidateTextRange.getStartOffset(), candidateTextRange.getEndOffset(), currentSelectionTextRange);
            if (selection != null) return selection;
        }

        PsiElement parent = recurseTextElements(candidate.getParent(), PsiElement::getParent).findFirst().orElse(null);
        if (parent == null) return null;

        return recurseTextElements(candidate, PsiElement::getNextSibling)
                .peek(psiElement -> LOG.debug("            next sibling: " + PsiInspectUtil.inspect(psiElement)))
                .filter(nextSibling -> nextSibling.getTextLength() == 1)
                .peek(psiElement -> LOG.debug("   length 1 next sibling: " + PsiInspectUtil.inspect(psiElement)))
                .filter(nextSibling -> isTypingPairEndingChar(contents.charAt(nextSibling.getTextOffset())))
                .peek(psiElement -> LOG.debug("typing pair next sibling: " + PsiInspectUtil.inspect(psiElement)))
                .flatMap(nextSibling -> {
                    Character expectedMatchingStartingChar = TYPING_PAIRS.get(contents.charAt(nextSibling.getTextOffset()));

                    return recurseTextElements(candidate, PsiElement::getPrevSibling)
                            .peek(psiElement -> LOG.debug("         prev sibling: " + PsiInspectUtil.inspect(psiElement)))

                            .filter(previousSibling -> previousSibling.getTextLength() == 1)
                            .peek(psiElement -> LOG.debug("length 1 prev sibling: " + PsiInspectUtil.inspect(psiElement)))
                            .filter(previousSibling -> contents.charAt(previousSibling.getTextOffset()) == expectedMatchingStartingChar)
                            .peek(psiElement -> LOG.debug("   match prev sibling: " + PsiInspectUtil.inspect(psiElement)))
                            .peek(psiElement -> LOG.debug("   match prev sibling: " + PsiInspectUtil.inspect(psiElement)))
                            .map(previousSibling -> calculateSelection(previousSibling.getTextRange().getStartOffset(), nextSibling.getTextRange().getEndOffset(), currentSelectionTextRange))
                            .filter(Objects::nonNull)
                            .peek(textRange -> LOG.debug("textRange: " + contents.subSequence(textRange.getStartOffset(), textRange.getEndOffset())))
                            ;
                }).findFirst().orElse(null);
    }

    private static boolean isTypingPairEndingChar(char character) {
        return TYPING_PAIRS.containsKey(character);
    }

    private static TextRange calculateSelection(int startOffset, int endOffset, TextRange currentSelectionTextRange) {
        if (endOffset - startOffset < 2) return null;

        TextRange innerSelection = TextRange.create(startOffset + 1, endOffset - 1);
        TextRange outerSelection = TextRange.create(startOffset, endOffset);

        if (innerSelection.contains(currentSelectionTextRange) && !innerSelection.equals(currentSelectionTextRange))
            return innerSelection;

        if (outerSelection.contains(currentSelectionTextRange) && !outerSelection.equals(currentSelectionTextRange))
            return outerSelection;
        return null;
    }


    private static boolean isEnclosingTypingPairsElement(CharSequence contents, TextRange candidateTextRange) {
        char candidateStartingChar = contents.charAt(candidateTextRange.getStartOffset());
        char candidateEndingChar = contents.charAt(candidateTextRange.getEndOffset() - 1);
        return isTypingPairEndingChar(candidateEndingChar) &&
                TYPING_PAIRS.get(candidateEndingChar) == candidateStartingChar;
    }

    private static PsiElement getElementAtCaret(@NotNull DataContext dataContext) {
        Editor editor = CommonDataKeys.EDITOR.getData(dataContext);
        PsiFile psiFile = PsiUtilCore.getTemplateLanguageFile(CommonDataKeys.PSI_FILE.getData(dataContext));
        if (editor == null || psiFile == null) return null;

        return BaseRefactoringAction.getElementAtCaret(editor, psiFile);
    }

    private static Stream<PsiElement> recurseTextElements(PsiElement element, UnaryOperator<PsiElement> operator) {
        if (element == null) return Stream.empty();

        return Stream.iterate(element, operator)
                .takeWhile(Objects::nonNull)
                .takeWhile(psiElement -> psiElement.getTextRange() != null);
    }
}
