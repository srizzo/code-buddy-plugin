package io.github.srizzo.codebuddy.findreplace;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.text.StringUtil;
import io.github.srizzo.codebuddy.util.FindAndReplaceUtil;
import org.jetbrains.annotations.NotNull;

public class UseSelectionForReplaceAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {

        // TODO fails on terminal
        final Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);
        final String replacement = StringUtil.defaultIfEmpty(editor.getSelectionModel().getSelectedText(), "");

        FindAndReplaceUtil.getAllFindModels(editor).stream().forEach(findModel -> {
            findModel.setStringToReplace(replacement);
        });
    }
}


