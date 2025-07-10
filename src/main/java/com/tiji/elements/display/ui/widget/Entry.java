package com.tiji.elements.display.ui.widget;

import com.tiji.elements.Game;
import com.tiji.elements.core.Color;
import com.tiji.elements.core.Position;
import com.tiji.elements.display.DrawCalls;
import org.lwjgl.glfw.GLFW;

public class Entry extends Widget {
    private static final int HEIGHT = 30;
    private static final int BLINK_INTERVAL = 1000;

    int width;
    Box base;
    String text;
    int cursorPosition = 0;
    String preedit = "";
    int preeditCursorPos = 0;

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
        String displayedText = insertTextToCursor(preedit);
        if (currentTime % BLINK_INTERVAL < BLINK_INTERVAL / 2 && focused) {
            DrawCalls.rectangle(pos.translate((int) getCursorOffset(), 5),
                    2, HEIGHT-10, new Color(255, 255, 255));
        }
        DrawCalls.text(pos.translate(0, 5), displayedText, new Color(255, 255, 255));
    }

    private float getCursorOffset() {
        return DrawCalls.textWidth(insertTextToCursor(preedit).substring(0, cursorPosition + preeditCursorPos));
    }

    private String insertTextToCursor(String toInsert) {
        String left  = text.substring(0, cursorPosition);
        String right = text.substring(cursorPosition);
        return left + toInsert + right;
    }

    private void updateCursorPosition() {
        GLFW.glfwSetPreeditCursorRectangle(Game.window.window, pos.translate((int) getCursorOffset(), 0).x(), pos.y(), 2, HEIGHT);
    }

    @Override
    public void remove() {
        base.remove();
    }

    @Override
    public void charTyped(char c) {
        if (focused) {
            text = insertTextToCursor(String.valueOf(c));
            cursorPosition++;
            updateCursorPosition();
        }
    }

    @Override
    public void keyPressed(int k) {
        if (focused) {
            if (k == GLFW.GLFW_KEY_BACKSPACE && !text.isEmpty() && cursorPosition > 0) {
                text = text.substring(0, cursorPosition - 1) + text.substring(cursorPosition);
                cursorPosition--;
            } else if (k == GLFW.GLFW_KEY_LEFT && cursorPosition > 0) {
                cursorPosition--;
            } else if (k == GLFW.GLFW_KEY_RIGHT && cursorPosition < text.length()) {
                cursorPosition++;
            }
        }
    }

    @Override
    public void preeditChange(String preedit, int caret) {
        if (focused) {
            this.preedit = preedit;
            this.preeditCursorPos = caret;
            updateCursorPosition();
        }
    }

    @Override
    public void mouseClick(Position pos) {
        if (isPointInside(pos, width, HEIGHT)) {
            requestFocus();
        }
    }
}
