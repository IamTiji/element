package com.tiji.elements.display.ui;

import com.tiji.elements.core.Position;
import com.tiji.elements.display.DrawCalls;
import com.tiji.elements.display.ui.widget.Widget;

import java.util.ArrayList;

public abstract class AbstractUI {
    private final ArrayList<Widget> widgets = new ArrayList<>();

    protected void addWidget(Widget widget) {
        widgets.add(widget);
    }
    public void close() {
        DrawCalls.popTemporaryDrawing();
        widgets.forEach(Widget::remove);
    }
    public void mouseClicked(int x, int y) {
        widgets.forEach(widget -> widget.mouseClick(new Position(x, y)));
    }

    public void mouseReleased(int x, int y) {
        widgets.forEach(widget -> widget.mouseRelease(new Position(x, y)));
    }

    public void charTyped(char c) {
        widgets.forEach(widget -> widget.charTyped(c));
    }

    public void keyPress(int k) {
        widgets.forEach(widget -> widget.keyPressed(k));
    }

    public void render(int mouseX, int mouseY, int screenWidth, int screenHeight) {
        DrawCalls.popTemporaryDrawing();
        DrawCalls.startTemporaryDrawing();
        widgets.forEach(widget -> widget.draw(new Position(mouseX, mouseY)));
    }
}
