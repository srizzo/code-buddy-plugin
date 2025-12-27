package io.github.srizzo.codebuddy.util;

import com.intellij.openapi.util.SystemInfo;
import junit.framework.TestCase;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class BlockSelectionUtilTest extends TestCase {

    public void testGetMultiCaretActionKeyCodeReturnsAltOnMac() {
        if (SystemInfo.isMac) {
            assertEquals("On Mac, multi-caret key code should be VK_ALT",
                    KeyEvent.VK_ALT, BlockSelectionUtil.getMultiCaretActionKeyCode());
        }
    }

    public void testGetMultiCaretActionKeyCodeReturnsCtrlOnNonMac() {
        if (!SystemInfo.isMac) {
            assertEquals("On non-Mac, multi-caret key code should be VK_CONTROL",
                    KeyEvent.VK_CONTROL, BlockSelectionUtil.getMultiCaretActionKeyCode());
        }
    }

    public void testGetMultiCaretActionKeyModifierReturnsAltDownMaskOnMac() {
        if (SystemInfo.isMac) {
            assertEquals("On Mac, multi-caret modifier should be ALT_DOWN_MASK",
                    InputEvent.ALT_DOWN_MASK, BlockSelectionUtil.getMultiCaretActionKeyModifier());
        }
    }

    public void testGetMultiCaretActionKeyModifierReturnsCtrlDownMaskOnNonMac() {
        if (!SystemInfo.isMac) {
            assertEquals("On non-Mac, multi-caret modifier should be CTRL_DOWN_MASK",
                    InputEvent.CTRL_DOWN_MASK, BlockSelectionUtil.getMultiCaretActionKeyModifier());
        }
    }

    public void testModifierMaskIsCompatibleWithGetModifiersEx() {
        int modifier = BlockSelectionUtil.getMultiCaretActionKeyModifier();
        assertTrue("Modifier should be a valid extended modifier mask (>= 64)",
                modifier >= InputEvent.SHIFT_DOWN_MASK);
    }
}
