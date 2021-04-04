package io.github.srizzo.codebuddy.test;

import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import io.github.srizzo.codebuddy.test.ProjectDescriptors;
import org.junit.jupiter.api.BeforeEach;

public class BaseTestCase extends BasePlatformTestCase {
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return ProjectDescriptors.LIGHT_PROJECT_DESCRIPTOR;
    }
}

