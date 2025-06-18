package com.tiji.elements.display;

import java.util.HashMap;

public class KeyboardEventHandler {
    HashMap<Integer, Runnable> keyActions = new HashMap<>();

    public void registerKeyAction(int keyCode, Runnable action) {
        keyActions.put(keyCode, action);
    }

    public void keyPressed(int e) {
        if (keyActions.containsKey(e)) {
            keyActions.get(e).run();
        }
    }
}
