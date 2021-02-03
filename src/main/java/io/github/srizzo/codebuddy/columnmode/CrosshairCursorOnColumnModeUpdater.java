package io.github.srizzo.codebuddy.columnmode;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.EditorEventMulticaster;
import com.intellij.openapi.editor.ex.EditorEventMulticasterEx;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;

@Service
final public class CrosshairCursorOnColumnModeUpdater implements PropertyChangeListener, Disposable {

    public CrosshairCursorOnColumnModeUpdater() {
        EditorEventMulticaster multicaster = EditorFactory.getInstance().getEventMulticaster();
        if (multicaster instanceof EditorEventMulticasterEx) {
            ((EditorEventMulticasterEx) multicaster).
                    addPropertyChangeListener(this, this);
        }
    }

    private static EditorEx getEditor(@NotNull PropertyChangeEvent e) {
        return (EditorEx) e.getSource();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!EditorEx.PROP_COLUMN_MODE.equals(evt.getPropertyName())) return;

        EditorEx editor = getEditor(evt);
        if (Objects.equals(evt.getNewValue(), true)) {
            UIUtil.setCursor(editor.getContentComponent(), Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        } else {
            UIUtil.setCursor(editor.getContentComponent(), Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    @Override
    public void dispose() {
    }
}
