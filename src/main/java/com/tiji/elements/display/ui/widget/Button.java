package com.tiji.elements.display.ui.widget;

import com.tiji.elements.core.Color;
import com.tiji.elements.core.Position;
import com.tiji.elements.display.DrawCalls;

public class Button extends Widget {
    private final int textId;
    private final Box container;

    private static final int BUTTON_HEIGHT = 30;
    private static final int VERT_PADDING = 5;

    private static final Color TEXT_COLOR = new Color(255, 255, 255);

    public Button(String text, Position pos, int width) {
        super(pos);

        container = new Box(pos, width, BUTTON_HEIGHT);
        textId = DrawCalls.text(getPos().translate((int) DrawCalls.textCenter(text, width), VERT_PADDING), text, TEXT_COLOR);
    }

    public Button(String text, Position pos) {
        this(text, pos, (int) DrawCalls.textWidth(text));
    }

    @Override
    public void draw(Position mousePos) {}

    @Override
    public void remove() {
        DrawCalls.forget(textId);
        container.remove();
    }
}
