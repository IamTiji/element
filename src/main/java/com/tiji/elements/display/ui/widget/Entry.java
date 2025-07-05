package com.tiji.elements.display.ui.widget;

import com.tiji.elements.core.Color;
import com.tiji.elements.core.Position;
import com.tiji.elements.display.DrawCalls;
import org.lwjgl.glfw.GLFW;

public class Entry extends Widget {
    private static final int HEIGHT = 20;
    private static final int BLINK_INTERVAL = 1000;

    int width;
    Box base;
    String text;
    int cursorPosition = 0;

    public Entry(Position pos, int width, String text) {
        super(pos);
        this.width = width;
        this.text = text;

        base = new Box(pos, width, HEIGHT);
    }

    public Entry(Position pos, int width) {
        this(pos, width, "");
    }

    @Override
    public void draw(Position mousePos) {
        long currentTime = System.currentTimeMillis();
        if (currentTime % BLINK_INTERVAL < BLINK_INTERVAL / 2) {
            DrawCalls.rectangle(pos.translate((int) DrawCalls.textWidth(text.substring(0, cursorPosition)), 0), 2, HEIGHT, new Color(255, 255, 255));
        }
        DrawCalls.text(pos.translate(0, 2), text, new Color(255, 255, 255));
    }

    @Override
    public void remove() {
        base.remove();
    }

    @Override
    public void charTyped(char c) {
        String left = text.substring(0, cursorPosition);
        String right = text.substring(cursorPosition);
        text = left + c + right;
        cursorPosition++;
    }

    @Override
    public void keyPressed(int k) {
        if (k == GLFW.GLFW_KEY_BACKSPACE && !text.isEmpty() && cursorPosition > 0) {
            text = text.substring(0, cursorPosition-1) + text.substring(cursorPosition);
            cursorPosition--;
        } else if (k == GLFW.GLFW_KEY_LEFT && cursorPosition > 0) {
            cursorPosition--;
        } else if (k == GLFW.GLFW_KEY_RIGHT && cursorPosition < text.length()) {
            cursorPosition++;
        }
    }
}
