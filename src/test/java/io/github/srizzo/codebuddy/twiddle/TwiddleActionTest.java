package io.github.srizzo.codebuddy.twiddle;

import com.intellij.testFramework.TestDataPath;
import io.github.srizzo.codebuddy.test.BaseTestCase;

@TestDataPath("$PROJECT_ROOT/src/test/testData/twiddleAction")
public class TwiddleActionTest extends BaseTestCase {
    @Override
    protected String getTestDataPath() {
        return "src/test/testData/twiddleAction";
    }

    public void testTwiddleAction() {
        myFixture.configureByFile("twiddleAction_before.txt");
        myFixture.performEditorAction(TwiddleAction.TWIDDLE_ACTION_ID);
        myFixture.checkResultByFile("twiddleAction_after.txt");
    }
}
