package io.github.srizzo.codebuddy.util;

import com.intellij.find.EditorSearchSession;
import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FindAndReplaceUtil {
    public static List<FindModel> getAllFindModels(Editor editor) {
        @Nullable FindManager findManager = getFindManager(editor);
        if (findManager == null) return Collections.emptyList();

        @Nullable EditorSearchSession editorSearchSession = EditorSearchSession.get(editor);

        List<FindModel> findModels = new ArrayList<>();

        FindModel findInFileModel = findManager.getFindInFileModel();
        FindModel findNextModel = findManager.getFindNextModel();
        if (findNextModel == null) {
            findNextModel = new FindModel();
            findManager.setFindNextModel(findNextModel);
        }
        findNextModel.copyFrom(findInFileModel);

        findModels.add(findManager.getFindInProjectModel());
        findModels.add(findInFileModel);
        findModels.add(findNextModel);

        if (editorSearchSession != null) {
            findModels.add(editorSearchSession.getFindModel());
        }

        return findModels;
    }

    @Nullable
    public static FindManager getFindManager(Editor editor) {
        @Nullable final Project project = editor.getProject();
        if (project == null) return null;
        return FindManager.getInstance(project);
    }
}
