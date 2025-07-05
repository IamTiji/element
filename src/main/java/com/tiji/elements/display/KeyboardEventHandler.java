package com.tiji.elements.display;

import java.util.HashMap;

public class KeyboardEventHandler {
    private final HashMap<Integer, Runnable> keyActions = new HashMap<>();
    private final ScreenDrawer drawer;

    public KeyboardEventHandler(ScreenDrawer drawer) {
        this.drawer = drawer;
    }

    public void registerKeyAction(int keyCode, Runnable action) {
        keyActions.put(keyCode, action);
    }

    public void clearKeyActions() {
        keyActions.clear();
    }

    public void keyPressed(int e) {
        if (keyActions.containsKey(e)) {
            keyActions.get(e).run();
        } else {
            if (drawer.isUiOpen) {
                drawer.activeUI.keyPress(e);
            }
        }
    }

    public void charTyped(char e) {
        if (drawer.isUiOpen) {
            drawer.activeUI.charTyped(e);
        }
    }
}
