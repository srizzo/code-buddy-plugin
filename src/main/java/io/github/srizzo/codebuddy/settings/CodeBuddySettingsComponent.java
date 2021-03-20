package io.github.srizzo.codebuddy.settings;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;

public class CodeBuddySettingsComponent {

    private final JPanel myMainPanel;
    private final JBCheckBox myToggleBlockSelectionOnModifierKeyPressStatus = new JBCheckBox("Pressing ⌥ toggles between Selection and Block Selection");
    private final JBCheckBox myModifiersExitColumnSelectionModeStatus = new JBCheckBox("Pressing arrow up, arrow down, ⌃, ⇧, or ⌘ exits Column Selection Mode");
    private final JBCheckBox myArrowUpAndDownReturnToSingleCursorStatus = new JBCheckBox("Pressing arrow up or arrow down returns to single cursor");
    private final JBCheckBox myCrosshairCursorOnColumnSelectionModeStatus = new JBCheckBox("Use crosshair cursor to signal Column Selection Mode is active");
    private final JBCheckBox myHoldingModifierActivatesColumnSelectionModeStatus = new JBCheckBox("Holding ⌥ activates Column Selection Mode");

    public CodeBuddySettingsComponent() {
        myMainPanel = FormBuilder.createFormBuilder()
                .addComponent(myToggleBlockSelectionOnModifierKeyPressStatus, 1)
                .addComponent(myModifiersExitColumnSelectionModeStatus, 1)
                .addComponent(myArrowUpAndDownReturnToSingleCursorStatus, 1)
                .addComponent(myCrosshairCursorOnColumnSelectionModeStatus, 1)
                .addComponent(myHoldingModifierActivatesColumnSelectionModeStatus, 1)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return myToggleBlockSelectionOnModifierKeyPressStatus;
    }

    public boolean getToggleBlockSelectionOnModifierKeyPressStatus() {
        return myToggleBlockSelectionOnModifierKeyPressStatus.isSelected();
    }

    public void setToggleBlockSelectionOnModifierKeyPressStatus(boolean newStatus) {
        myToggleBlockSelectionOnModifierKeyPressStatus.setSelected(newStatus);
    }


    public boolean getModifiersExitColumnSelectionModeStatus() {
        return myModifiersExitColumnSelectionModeStatus.isSelected();
    }

    public void setModifiersExitColumnSelectionModeStatus(boolean newStatus) {
        myModifiersExitColumnSelectionModeStatus.setSelected(newStatus);
    }

    public boolean getArrowUpAndDownReturnToSingleCursorStatus() {
        return myArrowUpAndDownReturnToSingleCursorStatus.isSelected();
    }

    public void setArrowUpAndDownReturnToSingleCursorStatus(boolean newStatus) {
        myArrowUpAndDownReturnToSingleCursorStatus.setSelected(newStatus);
    }

    public boolean getCrosshairCursorOnColumnSelectionModeStatus() {
        return myCrosshairCursorOnColumnSelectionModeStatus.isSelected();
    }

    public void setCrosshairCursorOnColumnSelectionModeStatus(boolean newStatus) {
        myCrosshairCursorOnColumnSelectionModeStatus.setSelected(newStatus);
    }

    public boolean getHoldingModifierActivatesColumnSelectionModeStatus() {
        return myHoldingModifierActivatesColumnSelectionModeStatus.isSelected();
    }

    public void setHoldingModifierActivatesColumnSelectionModeStatus(boolean newStatus) {
        myHoldingModifierActivatesColumnSelectionModeStatus.setSelected(newStatus);
    }
}
