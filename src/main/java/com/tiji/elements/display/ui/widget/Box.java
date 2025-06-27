package com.tiji.elements.display.ui.widget;

import com.tiji.elements.core.Color;
import com.tiji.elements.core.Position;
import com.tiji.elements.display.DrawCalls;

public class Box extends Widget {
    int[] ids = new int[2];

    public Box(Position position, int width, int height) {
        super(position);

        ids[0] = DrawCalls.rectangle(pos, width, height, new Color(255, 255, 255));
        ids[1] = DrawCalls.rectangle(pos.translate(1, 1), width - 2, height - 2, new Color(0, 0, 0));
    }

    @Override
    public void draw(Position pos) {}

    @Override
    public void remove() {
        for (int id : ids) {
            DrawCalls.forget(id);
        }
    }
}
