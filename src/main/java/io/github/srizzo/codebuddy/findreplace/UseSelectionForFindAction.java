package io.github.srizzo.codebuddy.findreplace;

import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.text.StringUtil;
import io.github.srizzo.codebuddy.util.FindAndReplaceUtil;
import io.github.srizzo.codebuddy.util.ParagraphSelectionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.stream.Collectors;

public class UseSelectionForFindAction extends AnAction {

    private static boolean isRegularExpressionsSearch(FindManager findManager) {
        @NotNull FindModel findInFileModel = findManager.getFindInFileModel();
        return findInFileModel.isRegularExpressions();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        final @NotNull Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);

        final @Nullable FindManager findManager = FindAndReplaceUtil.getFindManager(editor);
        if (findManager == null) return;

        String[] selectedTexts = ParagraphSelectionUtil.getSelectedTexts(editor);

        final boolean isRegularExpressionsSearch;
        final String textForFind;

        switch (selectedTexts.length) {
            case 0:
                return;
            case 1:
                isRegularExpressionsSearch = isRegularExpressionsSearch(findManager);
                textForFind = isRegularExpressionsSearch ? StringUtil.escapeToRegexp(selectedTexts[0]) : selectedTexts[0];
                break;
            default:
                isRegularExpressionsSearch = true;
                textForFind = Arrays.stream(selectedTexts)
                        .distinct()
                        .map(text -> StringUtil.escapeToRegexp(text))
                        .collect(Collectors.joining("|"));
        }

        FindAndReplaceUtil.getAllFindModels(editor).stream().forEach(findModel -> {
            findModel.setRegularExpressions(isRegularExpressionsSearch);
            findModel.setStringToFind(textForFind);
        });

        enableHeadlessFindNextOccurrence(findManager);
    }

    private void enableHeadlessFindNextOccurrence(FindManager findManager) {
        findManager.setFindWasPerformed();
    }
}


