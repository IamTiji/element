package com.tiji.elements.display.ui.widget;

import com.tiji.elements.core.Color;
import com.tiji.elements.core.Position;
import com.tiji.elements.display.DrawCalls;

public abstract class HoverText extends Widget {
    public HoverText(Position pos) {
        super(pos);
    }

    protected abstract String getHoverText();
    protected abstract boolean shouldShowHoverText();

    @Override
    public void draw(Position pos) {
        if (pos != null && shouldShowHoverText()) {
            String hoverText = getHoverText();
            float textSize = DrawCalls.textWidth(hoverText);
            DrawCalls.rectangle(pos.translate(9, -1), (int) textSize + 22, 32, new Color(255, 255, 255));
            DrawCalls.rectangle(pos.translate(10, 0), (int) textSize + 20, 30, new Color(0, 0, 0));
            DrawCalls.text(pos.translate(18, 7), hoverText, new Color(255, 255, 255));
        }
    }
}
