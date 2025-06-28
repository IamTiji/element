package com.tiji.elements.display.ui;

import com.tiji.elements.Game;
import com.tiji.elements.core.Color;
import com.tiji.elements.core.ElementFactory;
import com.tiji.elements.core.Position;
import com.tiji.elements.display.DrawCalls;
import com.tiji.elements.display.ui.widget.*;

import java.util.ArrayList;

public class BrushEditor implements AbstractUI {
    private static final int UI_WIDTH = 600;
    private static final int UI_HEIGHT = 400;

    private static final ArrayList<BrushElement> cachedBrushes = new ArrayList<>();

    private final ArrayList<Widget> widgets = new ArrayList<>();

    private final Slider brushSize;

    private record BrushElement(String name, Color[] color, ElementFactory elementFactory) {}

    private static class ElementButton extends HoverText {
        BrushElement element;
        int[] widgetElements = new int[17];
        Runnable onClick;
        Position lastMousePos;

        public ElementButton(Position pos, BrushElement element, Runnable onClick) {
            super(pos);
            this.element = element;
            this.onClick = onClick;

            widgetElements[16] = DrawCalls.rectangle(pos.translate(-2, -2), 28, 28, new Color(255, 255, 255));
            for (int i = 0; i < 16; i++) {
                widgetElements[i] = DrawCalls.rectangle(pos.translate((i % 4) * 6, (i / 4) * 6), 6, 6, element.color[i]);
            }
        }

        @Override
        protected String getHoverText() {
            return Game.translationHandler.translate(element.name());
        }

        @Override
        protected boolean shouldShowHoverText() {
            return isPointInside(lastMousePos, 6*4, 6*4);
        }

        @Override
        public void draw(Position mousePos) {
            lastMousePos = mousePos;
            super.draw(mousePos);
        }

        @Override
        public void remove() {
            for (int id : widgetElements) {
                DrawCalls.forget(id);
            }
            DrawCalls.popTemporaryDrawing();
        }

        @Override
        public void mouseClick(Position pos) {
            super.mouseClick(pos);
            if (isPointInside(pos, 6*4, 6*4)) {
                onClick.run();
            }
        }
    }

    public BrushEditor(int screenWidth, int screenHeight) {
        widgets.add(new Box(new Position((screenWidth  - UI_WIDTH ) / 2, (screenHeight - UI_HEIGHT) / 2),
                UI_WIDTH, UI_HEIGHT));

        if (cachedBrushes.isEmpty()) {
            for (ElementFactory elementFactory : Game.elementRegistry) {
                Color[] colors = new Color[16];
                for (int i = 0; i < 16; i++) {
                    colors[i] = elementFactory.call(new Position(i % 4, i / 4)).displayedColor();
                }
                cachedBrushes.add(new BrushElement(elementFactory.call(new Position(-1, -1)).getTranslationKey(),
                        colors, elementFactory));
            }
        }

        int y = (screenHeight - UI_HEIGHT) / 2 + 24;
        int x = (screenWidth - UI_WIDTH) / 2 + 24;
        for (BrushElement brush : cachedBrushes) {
            widgets.add(new ElementButton(new Position(x, y), brush, () -> Game.paintElement = brush.elementFactory));

            x += 36;
            if (x > ((screenWidth + UI_WIDTH) / 2)) {
                x = (screenWidth - UI_WIDTH) / 2 + 4;
                y += 36;
            }
        }
        brushSize = new Slider(new Position((screenWidth - UI_WIDTH) / 2 + 10, (screenHeight + UI_HEIGHT) / 2 - 30),
                300, 1, 15, Game.brushSize);
        widgets.add(brushSize);
        widgets.add(new Label(brushSize.getPos().translate(0, -20), Game.translationHandler.translate("ui.brush_size")));
        widgets.add(new VaryingLabel(brushSize.getPos().translate(310, 0), () -> String.valueOf(brushSize.getValue())));
    }

    @Override
    public void render(int mouseX, int mouseY, int screenWidth, int screenHeight) {
        DrawCalls.popTemporaryDrawing();
        DrawCalls.startTemporaryDrawing();
        widgets.forEach(widget -> widget.draw(new Position(mouseX, mouseY)));

        Game.brushSize = brushSize.getValue();
    }

    @Override
    public void mouseClicked(int x, int y) {
        widgets.forEach(widget -> widget.mouseClick(new Position(x, y)));
    }

    @Override
    public void mouseReleased(int x, int y) {
        widgets.forEach(widget -> widget.mouseRelease(new Position(x, y)));
    }

    @Override
    public void close() {
        widgets.forEach(Widget::remove);
    }
}
