package com.tiji.elements.display.ui.widget;

import com.tiji.elements.core.Color;
import com.tiji.elements.core.Position;
import com.tiji.elements.display.DrawCalls;

public class Slider extends Widget {
    private final int min;
    private final int max;
    private int value;
    private final int length;
    private boolean dragging = false;

    private final int track;
    private int thumb;

    private static final int SLIDER_HEIGHT = 20;
    private static final int SLIDER_TRACK_HEIGHT = 2;
    private static final int SLIDER_THUMB_SIZE = 10;

    private static final Color WHITE = new Color(255, 255, 255);

    public Slider(Position pos, int width, int min, int max, int value) {
        super(pos);

        length = width;
        this.min = min;
        this.max = max;
        this.value = value;

        track = DrawCalls.rectangle(pos.translate(0, SLIDER_HEIGHT/2 - SLIDER_TRACK_HEIGHT/2), length, SLIDER_TRACK_HEIGHT, WHITE);
    }

    public Slider(Position pos, int width, int min, int max) {
        this(pos, width, min, max, min);
    }

    @Override
    public void draw(Position mousePos) {
        if (dragging) {
            value = (int) ((mousePos.x() - pos.x()) / ((float) length / (max - min)) + min);
            value = Math.clamp(value, min, max);
        }
        thumb = DrawCalls.rectangle(pos.translate((length-SLIDER_THUMB_SIZE)*(value-min)/(max-min), 0), SLIDER_THUMB_SIZE, SLIDER_HEIGHT, WHITE);
    }

    @Override
    public void remove() {
        DrawCalls.forget(track);
    }

    @Override
    public void mouseClick(Position pos) {
        if (isPointInside(pos, length, SLIDER_HEIGHT)) {
            dragging = true;
        }
        super.mouseClick(pos);
    }

    @Override
    public void mouseRelease(Position pos) {
        if (dragging) dragging = false;
        super.mouseRelease(pos);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = Math.clamp(value, min, max);
    }
}
