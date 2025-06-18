package com.tiji.elements.display.ui;

import com.tiji.elements.Game;
import com.tiji.elements.core.Color;
import com.tiji.elements.core.ElementFactory;
import com.tiji.elements.core.Position;
import com.tiji.elements.display.DrawCalls;
import com.tiji.elements.display.ScreenDrawer;

import java.util.ArrayList;

public class BrushEditor implements AbstractUI {
    private static final int UI_WIDTH = 600;
    private static final int UI_HEIGHT = 400;

    private final int screenWidth;
    private final int screenHeight;

    private final ArrayList<Integer> screenElements = new ArrayList<>();
    private final ArrayList<Integer> hoverElements = new ArrayList<>();

    private static final ArrayList<BrushElement> cachedBrushes = new ArrayList<>();

    private record BrushElement(String name, Color[] color, ElementFactory elementFactory) {}

    private boolean isInRegion(int x, int y, int width, int height, int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width &&
                mouseY >= y && mouseY <= y + height;
    }

    public BrushEditor(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        for (int id : drawBox((screenWidth  - UI_WIDTH ) / 2,
                (screenHeight - UI_HEIGHT) / 2,
                UI_WIDTH, UI_HEIGHT)) {
            screenElements.add(id);
        }

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
            screenElements.add(DrawCalls.rectangle(new Position(x - 2, y - 2), 28, 28, new Color(255, 255, 255)));
            for (int i = 0; i < 16; i++) {
                screenElements.add(DrawCalls.rectangle(new Position(x + (i % 4) * 6, y + (i / 4) * 6), 6, 6, brush.color[i]));
            }
            x += 36;
            if (x > ((screenWidth + UI_WIDTH) / 2)) {
                x = (screenWidth - UI_WIDTH) / 2 + 4;
                y += 36;
            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY, int screenWidth, int screenHeight) {
        synchronized (ScreenDrawer.uiConstructLock) {
            for (int id : hoverElements) {
                DrawCalls.forget(id);
            }
            hoverElements.clear();

            BrushElement hoveredBrush = getHoveredBrush(mouseX, mouseY, screenWidth, screenHeight);
            if (hoveredBrush != null) {
                int boxLength = (int) DrawCalls.textWidth(Game.translationHandler.translate(hoveredBrush.name));
                for (int id : drawBox(mouseX, mouseY + 16, boxLength + 20, 28)) {
                    hoverElements.add(id);
                }
                hoverElements.add(DrawCalls.text(new Position(mouseX + 10, mouseY + 20),
                        Game.translationHandler.translate(hoveredBrush.name),
                        new Color(255, 255, 255)));
            }
        }
    }

    private BrushElement getHoveredBrush(int mouseX, int mouseY, int screenWidth, int screenHeight) {
        BrushElement hoveredBrush = null;
        int y = (screenHeight - UI_HEIGHT) / 2 + 24;
        int x = (screenWidth - UI_WIDTH) / 2 + 24;
        for (BrushElement brush : cachedBrushes) {
            if (isInRegion(x - 2, y - 2, 28, 28, mouseX, mouseY)) {
                hoveredBrush = brush;
                break;
            }
            x += 36;
            if (x > ((screenWidth + UI_WIDTH) / 2)) {
                x = (screenWidth - UI_WIDTH) / 2 + 4;
                y += 36;
            }
        }
        return hoveredBrush;
    }

    private int[] drawBox(int x, int y, int width, int height) {
        int[] ids = new int[2];

        ids[0] = DrawCalls.rectangle(new Position(x, y), width, height, new Color(255, 255, 255));
        ids[1] = DrawCalls.rectangle(new Position(x + 1, y + 1), width - 2, height - 2, new Color(0, 0, 0));

        return ids;
    }

    @Override
    public void mouseClicked(int x, int y) {
        BrushElement clickedBrush = getHoveredBrush(x, y, screenWidth, screenHeight);
        if (clickedBrush != null) {
            Game.paintElement = clickedBrush.elementFactory;
        }
    }

    @Override
    public void mouseReleased(int x, int y) {

    }

    @Override
    public void close() {
        for (int screenElement : screenElements) {
            DrawCalls.forget(screenElement);
        }
        for (int hoverElement : hoverElements) {
            DrawCalls.forget(hoverElement);
        }
    }
}
