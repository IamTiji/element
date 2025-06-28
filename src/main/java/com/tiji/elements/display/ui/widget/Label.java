package com.tiji.elements.display.ui.widget;

import com.tiji.elements.core.Color;
import com.tiji.elements.core.Position;
import com.tiji.elements.display.DrawCalls;

public class Label extends Widget {
    int id;
    public Label(Position pos, String text) {
        super(pos);

        id = DrawCalls.text(pos, text, new Color(255, 255, 255));
    }

    @Override
    public void draw(Position mousePos) {}

    @Override
    public void remove() {
        DrawCalls.forget(id);
    }
}
