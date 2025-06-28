package com.tiji.elements.display.ui.widget;

import com.tiji.elements.core.Color;
import com.tiji.elements.core.Position;
import com.tiji.elements.display.DrawCalls;

import java.util.concurrent.Callable;

public class VaryingLabel extends Widget {
    Callable<String> textProvider;

    public VaryingLabel(Position pos, Callable<String> textProvider) {
        super(pos);
        this.textProvider = textProvider;
    }

    @Override
    public void draw(Position mousePos) {
        try {
            DrawCalls.text(getPos(), textProvider.call(), new Color(255, 255, 255));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove() {}
}
