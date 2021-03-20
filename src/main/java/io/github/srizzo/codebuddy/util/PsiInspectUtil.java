package io.github.srizzo.codebuddy.util;

import com.intellij.psi.PsiElement;

public class PsiInspectUtil {

    public static String inspect(PsiElement element) {
        if (element == null) return "(null) " + element.getClass().getSimpleName();
        if (element.getTextLength() <= 0) return "(empty) " + element.getClass().getSimpleName();

        String displayText = element.getText().replaceAll("\n", "\\\\n").substring(0, Math.min(element.getTextLength(), 50)) + "";
        return lineNumber(element) + ": " + displayText + " " + element.getClass().getSimpleName();
    }

    public static int lineNumber(PsiElement element) {
        com.intellij.psi.PsiFile containingFile = element.getContainingFile();
        com.intellij.psi.PsiDocumentManager psiDocumentManager = com.intellij.psi.PsiDocumentManager.getInstance(element.getProject());
        com.intellij.openapi.editor.Document document = psiDocumentManager.getDocument(containingFile);
        if (document == null) return -1;
        int textOffset = element.getTextOffset();
        return document.getLineNumber(textOffset) + 1;
    }
}
