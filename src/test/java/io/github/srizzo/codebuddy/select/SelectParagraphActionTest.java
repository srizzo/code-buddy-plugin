package io.github.srizzo.codebuddy.select;

import com.intellij.testFramework.TestDataPath;
import io.github.srizzo.codebuddy.test.BaseTestCase;
import io.github.srizzo.codebuddy.twiddle.TwiddleAction;

@TestDataPath("$PROJECT_ROOT/src/test/testData/selectParagraphAction")
public class SelectParagraphActionTest extends BaseTestCase {
    @Override
    protected String getTestDataPath() {
        return "src/test/testData/selectParagraphAction";
    }

    public void testSelectParagraphAction() {
        myFixture.configureByFile("selectParagraphAction_before.txt");
        myFixture.performEditorAction(SelectParagraphAction.SELECT_PARAGRAPH_ACTION_ID);
        myFixture.checkResultByFile("selectParagraphAction_after.txt");
    }
}
