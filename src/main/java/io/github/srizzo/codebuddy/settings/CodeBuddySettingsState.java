package io.github.srizzo.codebuddy.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "io.github.srizzo.codebuddy.settings.CodeBuddySettingsState",
        storages = {@Storage("CodeBuddySettingsPlugin.xml")}
)
public class CodeBuddySettingsState implements PersistentStateComponent<CodeBuddySettingsState> {
    public boolean toggleBlockSelectionOnModifierKeyPressStatus = false;
    public boolean holdingModifierActivatesColumnSelectionModeStatus = false;
    public boolean modifiersExitColumnSelectionModeStatus = false;
    public boolean arrowUpAndDownReturnToSingleCursorStatus = false;
    public boolean crosshairCursorOnColumnSelectionModeStatus = false;

    public static CodeBuddySettingsState getInstance() {
        return ServiceManager.getService(CodeBuddySettingsState.class);
    }

    @Nullable
    @Override
    public CodeBuddySettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull CodeBuddySettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
