package io.github.srizzo.codebuddy.findreplace;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.codeInsight.hint.HintManagerImpl;
import com.intellij.codeInsight.hint.HintUtil;
import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.find.FindResult;
import com.intellij.find.FindUtil;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actions.SelectOccurrencesActionHandler;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.LightweightHint;
import io.github.srizzo.codebuddy.CodeBuddyBundle;
import io.github.srizzo.codebuddy.util.FindAndReplaceUtil;
import io.github.srizzo.codebuddy.util.SelectionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.PatternSyntaxException;

public class FindAllAction extends EditorAction {
    protected FindAllAction() {
        super(new Handler());
    }

    private static class Handler extends SelectOccurrencesActionHandler {
        private static void showHint(final Editor editor, String message) {
            final LightweightHint hint = new LightweightHint(HintUtil.createInformationLabel(message));
            HintManagerImpl.getInstanceImpl().showEditorHint(hint,
                    editor,
                    HintManager.DEFAULT,
                    HintManager.DEFAULT,
                    0,
                    false);
        }

        @Override
        public void doExecute(@NotNull final Editor editor, @Nullable Caret c, DataContext dataContext) {
            Caret caret = c == null ? editor.getCaretModel().getPrimaryCaret() : c;

            FindManager findManager = FindAndReplaceUtil.getFindManager(editor);
            if (findManager == null) return;

            Project project = editor.getProject();
            if (project == null) return;

            final FindModel findModel = findManager.getFindInFileModel();
            String stringToFind = findModel.getStringToFind();

            if (StringUtil.isEmpty(stringToFind)) {
                showHint(editor, CodeBuddyBundle.message("nothing.to.search.for"));
                return;
            }

            int caretShiftFromSelectionStart = caretOffSetInSelectedOccurrences(caret, stringToFind);

            List<FindResult> results = new ArrayList<>();
            CharSequence documentContents = editor.getDocument().getImmutableCharSequence();
            TextRange[] selectionRanges = SelectionUtil.getSelectionRanges(editor);
            TextRange[] rangesToSearchIn = getRangesToSearchIn(editor, selectionRanges, stringToFind);

            for (TextRange textRange : rangesToSearchIn) {
                findInRange(documentContents, textRange, editor, findModel, results);
            }

            FindUtil.selectSearchResultsInEditor(editor, results.iterator(), caretShiftFromSelectionStart);

            TextRange[] newSelectionRanges = results.stream()
                    .map(findResult -> TextRange.create(findResult.getStartOffset(), findResult.getEndOffset()))
                    .toArray(TextRange[]::new);

            if (Arrays.equals(selectionRanges, newSelectionRanges)) {
                showHint(editor, CodeBuddyBundle.message("no.more.occurrences.of.0", stringToFind));
            } else {
                editor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);

                showHint(editor, CodeBuddyBundle.message("0.occurrences.of.1", results.size(), stringToFind));
            }
        }

        @NotNull
        private TextRange[] getRangesToSearchIn(Editor editor, @NotNull TextRange[] selectionRanges, String stringToFind) {
            boolean noCarets = selectionRanges.length == 0;
            boolean noSelection = selectionRanges.length == 1 && selectionRanges[0].getLength() == 0;
            boolean onlyMatchingResultsSelected = Arrays.stream(selectionRanges)
                    .allMatch(textRange -> stringToFind.equals(editor.getDocument().getText(textRange)));
            if (noCarets || noSelection || onlyMatchingResultsSelected) {
                return new TextRange[]{TextRange.create(0, editor.getDocument().getTextLength())};
            }

            return selectionRanges;
        }

        private int caretOffSetInSelectedOccurrences(@NotNull Caret caret, String stringToFind) {
            if (StringUtil.equals(caret.getSelectedText(), stringToFind)) {
                return caret.getOffset() - caret.getSelectionStart();
            }
            return 0;
        }

        private void findInRange(@NotNull CharSequence documentContents, @NotNull TextRange range, @NotNull Editor editor, FindModel findModel, List<? super FindResult> results) {
            VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(editor.getDocument());

            int offset = range.getStartOffset();
            int maxOffset = Math.min(range.getEndOffset(), documentContents.length());
            FindManager findManager = FindManager.getInstance(editor.getProject());

            while (true) {
                FindResult result;
                try {
                    CharSequence bombedCharSequence = StringUtil.newBombedCharSequence(documentContents, 3000);
                    result = findManager.findString(bombedCharSequence, offset, findModel, virtualFile);
                    ((StringUtil.BombedCharSequence) bombedCharSequence).defuse();
                } catch (PatternSyntaxException | ProcessCanceledException e) {
                    result = null;
                }
                if (result == null || !result.isStringFound()) break;
                final int newOffset = result.getEndOffset();
                if (result.getEndOffset() > maxOffset) break;
                if (offset == newOffset) {
                    if (offset < maxOffset - 1) {
                        offset++;
                    } else {
                        results.add(result);
                        break;
                    }
                } else {
                    offset = newOffset;
                    if (offset == result.getStartOffset()) ++offset; // skip zero width result
                }
                results.add(result);
            }
        }
    }
}
