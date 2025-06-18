package com.tiji.elements.display.ui.widget;

import com.tiji.elements.core.Color;
import com.tiji.elements.core.Position;
import com.tiji.elements.display.DrawCalls;

public class Button extends Widget {
    private final int[] elements;
    private final int width;
    private boolean isHovered;

    private static final int BUTTON_HEIGHT = 30;
    private static final int HORIZ_PADDING = 10;
    private static final int VERT_PADDING = 5;

    private static final Color DEFAULT_COLOR = new Color(200, 200, 200);
    private static final Color HOVER_COLOR = new Color(255, 255, 255);
    private static final Color TEXT_COLOR = new Color(0, 0, 0);

    public Button(String text, Position pos, int width) {
        super(pos);
        this.elements = new int[2];
        this.width = width;
        this.isHovered = false;

        elements[0] = DrawCalls.rectangle(getPos(), width+HORIZ_PADDING*2, BUTTON_HEIGHT, DEFAULT_COLOR);
        elements[1] = DrawCalls.text(getPos().translate((int) DrawCalls.textCenter(text, width) + HORIZ_PADDING, VERT_PADDING), text, TEXT_COLOR);
    }

    public Button(String text, Position pos) {
        this(text, pos, (int) DrawCalls.textWidth(text));
    }

    protected boolean isMouseInBounds(Position mousePos) {
        return mousePos.x() >= getPos().x() && mousePos.x() <= getPos().x() + width &&
                mousePos.y() >= getPos().y() && mousePos.y() <= getPos().y() + BUTTON_HEIGHT;
    }

    @Override
    public void draw(Position mousePos) {
        if (isMouseInBounds(mousePos) != isHovered) {
            DrawCalls.forget(elements[0]);
            isHovered = !isHovered;
            elements[0] = DrawCalls.rectangle(getPos(), width+HORIZ_PADDING*2, BUTTON_HEIGHT, isHovered ? HOVER_COLOR : DEFAULT_COLOR);
        }
    }

    @Override
    public void remove() {
        for (int i : elements) DrawCalls.forget(i);
    }
}
