package com.tiji.elements.display.ui.widget;

import com.tiji.elements.core.Position;

public abstract class Widget {
    protected Position pos;

    public Widget(Position pos) {
        this.pos = pos;
    }

    public Position getPos() {
        return pos;
    }

    public void move(Position pos) {
        this.pos = pos;
    }

    public void move(Position pos, boolean relative) {
        if (!relative) this.move(pos);
        else {
            Position current = new Position(this.pos, 0, 0);
            this.pos = current.translate(pos.x(), pos.y());
        }
    }

    public final boolean isPointInside(Position pos, int width, int height) {
        return pos.x() >= this.pos.x() && pos.x() < this.pos.x() + width &&
               pos.y() >= this.pos.y() && pos.y() < this.pos.y() + height;
    }

    public abstract void draw(Position mousePos);
    public abstract void remove();
    public void mouseClick(Position pos) {}
    public void mouseRelease(Position pos) {}
    public void keyPressed(int k) {}
    public void charTyped(char c) {}
}
