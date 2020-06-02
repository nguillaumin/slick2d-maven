package org.newdawn.slick;

import org.newdawn.slick.util.Log;

public class KeyPress {
    private static final Log LOG = new Log(KeyPress.class);

    public interface Action {
        void doAction();
    }

    private final boolean repeatEnabled;
    private final Action action;

    public KeyPress(boolean repeatEnabled, Action action) {
        this.repeatEnabled = repeatEnabled;
        this.action = action;
    }

    public static KeyPress of(boolean repeatEnabled, Action action) {
        return new KeyPress(repeatEnabled, action);
    }

    public boolean repeatEnabled() {
        return repeatEnabled;
    }

    public boolean doAction() {
        action.doAction();
        return true;
    }

    private static int NOT_FOUND_KEY = -1;
    private static final KeyPress NOT_FOUND = new KeyPress(false, () -> LOG.info("No key found: {}", NOT_FOUND_KEY));
    public static KeyPress notFound(int key) {
        NOT_FOUND_KEY = key;
        return NOT_FOUND;
    }
}
