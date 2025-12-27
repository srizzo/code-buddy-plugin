package io.github.srizzo.codebuddy.columnmode;

import com.intellij.ide.IdeEventQueue;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import org.jetbrains.annotations.NotNull;

import java.awt.AWTEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public class EventQueueProxy {
    private static final AtomicBoolean active = new AtomicBoolean(false);
    private static volatile Predicate<AWTEvent> currentHandler = null;
    
    private static final IdeEventQueue.EventDispatcher dispatcher = new IdeEventQueue.EventDispatcher() {
        @Override
        public boolean dispatch(@NotNull AWTEvent event) {
            Predicate<AWTEvent> handler = currentHandler;
            if (!active.get() || handler == null) {
                return true;
            }
            return handler.test(event);
        }
    };

    public static void setHandler(Predicate<AWTEvent> handler) {
        currentHandler = handler;
        if (handler != null && !active.get()) {
            activate();
        }
    }

    public static void activate() {
        if (active.get()) {
            return;
        }
        
        try {
            Disposable parentDisposable = ApplicationManager.getApplication();
            if (parentDisposable == null) {
                parentDisposable = new Disposable() {
                    @Override
                    public void dispose() {
                    }
                };
            }
            
            IdeEventQueue.getInstance().addDispatcher(dispatcher, parentDisposable);
            active.set(true);
        } catch (Exception e) {
        }
    }

    public static void deactivate() {
        if (active.get()) {
            try {
                IdeEventQueue.getInstance().removeDispatcher(dispatcher);
            } catch (Exception e) {
            }
            active.set(false);
            currentHandler = null;
        }
    }

    public static boolean isActive() {
        return active.get();
    }
}
