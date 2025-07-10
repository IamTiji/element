package com.tiji.elements.display.ui.widget;

import com.tiji.elements.Game;
import com.tiji.elements.core.Color;
import com.tiji.elements.core.Position;
import com.tiji.elements.display.DrawCalls;
import org.lwjgl.glfw.GLFW;

public class KeybindButton extends Widget {
    private int keybind;
    private String label;

    private final Box container;
    private final int width;

    private static final int BUTTON_HEIGHT = 30;
    private static final int VERT_PADDING = 5;

    public KeybindButton(Position pos, int keybind, int width) {
        super(pos);

        setKeybindName(keybind);
        this.keybind = keybind;

        this.container = new Box(pos, width, BUTTON_HEIGHT);

        this.width = width;
    }

    private void setKeybindName(int keybind) {
        this.label = GLFW.glfwGetKeyName(keybind, 0);
        if (this.label == null) {
            this.label = Game.translationHandler.translate("keybind." + keybind);
        }
    }

    @Override
    public void draw(Position mousePos) {
        DrawCalls.text(pos.translate((int) DrawCalls.textCenter(label, this.width), VERT_PADDING), label, new Color(255, 255, 255));
    }

    @Override
    public void remove() {
        container.remove();
    }

    @Override
    public void mouseClick(Position pos) {
        if (isPointInside(pos, width, BUTTON_HEIGHT)) {
            if (focused) {
                dropFocus();
                setKeybindName(keybind);
            } else {
                requestFocus();
                label = Game.translationHandler.translate("keybind.listening");
            }
        }
    }

    @Override
    public void keyPressed(int k) {
        if (focused) {
            keybind = k;
            setKeybindName(keybind);
            dropFocus();
        }
    }
}
