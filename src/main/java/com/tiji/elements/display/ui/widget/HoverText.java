package com.tiji.elements.display.ui.widget;

import com.tiji.elements.core.Color;
import com.tiji.elements.core.Position;
import com.tiji.elements.display.DrawCalls;

public abstract class HoverText extends Widget {
    private int[] hover;
    private int lastMouseX, lastMouseY;

    public HoverText(Position pos) {
        super(pos);
    }

    protected abstract String getHoverText();

    @Override
    public void mouseMove(Position pos) {
        lastMouseX = pos.x();
        lastMouseY = pos.y();
    }

    @Override
    public void draw(Position pos) {
        if (hover != null) {
            for (int i : hover) DrawCalls.forget(i);
        }
        if (pos != null) {
            hover = new int[3];

            String hoverText = getHoverText();
            float textSize = DrawCalls.textWidth(hoverText);
            hover[0] = DrawCalls.rectangle(pos.translate(9, -1), (int) textSize + 22, 32, new Color(255, 255, 255));
            hover[1] = DrawCalls.rectangle(pos.translate(10, 0), (int) textSize + 20, 30, new Color(0, 0, 0));
            hover[2] = DrawCalls.text(pos.translate(10, 20), hoverText, new Color(255, 255, 255));
        } else hover = null;
    }
}
