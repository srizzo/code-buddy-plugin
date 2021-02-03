package io.github.srizzo.codebuddy;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public final class CodeBuddyBundle extends DynamicBundle {
    public static final String BUNDLE = "messages.CodeBuddyBundle";

    private static final CodeBuddyBundle INSTANCE = new CodeBuddyBundle();

    private CodeBuddyBundle() {
        super(BUNDLE);
    }

    public static @Nls
    @NotNull
    String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
        return INSTANCE.getMessage(key, params);
    }
}
