package io.github.srizzo.codebuddy.columnmode;

import junit.framework.TestCase;

public class ToggleBlockSelectionActionTest extends TestCase {

    private ToggleBlockSelectionAction action;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        action = new ToggleBlockSelectionAction();
    }

    public void testActionCanBeInstantiated() {
        assertNotNull("ToggleBlockSelectionAction should be instantiable", action);
    }

    public void testActionImplementsDumbAware() {
        assertTrue("ToggleBlockSelectionAction should implement DumbAware",
                action instanceof com.intellij.openapi.project.DumbAware);
    }

    public void testActionExtendsToggleAction() {
        assertTrue("ToggleBlockSelectionAction should extend ToggleAction",
                action instanceof com.intellij.openapi.actionSystem.ToggleAction);
    }
}
