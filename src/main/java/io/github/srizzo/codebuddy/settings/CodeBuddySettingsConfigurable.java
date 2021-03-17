package io.github.srizzo.codebuddy.settings;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CodeBuddySettingsConfigurable implements Configurable {
    private CodeBuddySettingsComponent mySettingsComponent;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "CodeBuddy";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return mySettingsComponent.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mySettingsComponent = new CodeBuddySettingsComponent();
        return mySettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        CodeBuddySettingsState settings = CodeBuddySettingsState.getInstance();
        boolean modified = mySettingsComponent.getToggleBlockSelectionOnModifierKeyPressStatus() != settings.toggleBlockSelectionOnModifierKeyPressStatus
                || mySettingsComponent.getModifiersExitColumnSelectionModeStatus() != settings.modifiersExitColumnSelectionModeStatus
                || mySettingsComponent.getArrowUpAndDownReturnToSingleCursorStatus() != settings.arrowUpAndDownReturnToSingleCursorStatus
                || mySettingsComponent.getCrosshairCursorOnColumnSelectionModeStatus() != settings.crosshairCursorOnColumnSelectionModeStatus
                || mySettingsComponent.getHoldingModifierActivatesColumnSelectionModeStatus() != settings.holdingModifierActivatesColumnSelectionModeStatus;
        return modified;
    }

    @Override
    public void apply() {
        CodeBuddySettingsState settings = CodeBuddySettingsState.getInstance();
        settings.toggleBlockSelectionOnModifierKeyPressStatus = mySettingsComponent.getToggleBlockSelectionOnModifierKeyPressStatus();
        settings.modifiersExitColumnSelectionModeStatus = mySettingsComponent.getModifiersExitColumnSelectionModeStatus();
        settings.arrowUpAndDownReturnToSingleCursorStatus = mySettingsComponent.getArrowUpAndDownReturnToSingleCursorStatus();
        settings.crosshairCursorOnColumnSelectionModeStatus = mySettingsComponent.getCrosshairCursorOnColumnSelectionModeStatus();
        settings.holdingModifierActivatesColumnSelectionModeStatus = mySettingsComponent.getHoldingModifierActivatesColumnSelectionModeStatus();
    }

    @Override
    public void reset() {
        CodeBuddySettingsState settings = CodeBuddySettingsState.getInstance();
        mySettingsComponent.setToggleBlockSelectionOnModifierKeyPressStatus(settings.toggleBlockSelectionOnModifierKeyPressStatus);
        mySettingsComponent.setModifiersExitColumnSelectionModeStatus(settings.modifiersExitColumnSelectionModeStatus);
        mySettingsComponent.setArrowUpAndDownReturnToSingleCursorStatus(settings.arrowUpAndDownReturnToSingleCursorStatus);
        mySettingsComponent.setCrosshairCursorOnColumnSelectionModeStatus(settings.crosshairCursorOnColumnSelectionModeStatus);
        mySettingsComponent.setHoldingModifierActivatesColumnSelectionModeStatus(settings.holdingModifierActivatesColumnSelectionModeStatus);
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }

}
