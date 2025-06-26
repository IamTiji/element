package com.tiji.elements.display;

import com.tiji.elements.Game;
import com.tiji.elements.core.Position;
import com.tiji.elements.utils.Line;

public class MouseEventHandler {
    private Position previous;
    private final ScreenDrawer screenDrawer;
    private static final int brushSize = 3;

    public MouseEventHandler(ScreenDrawer screenDrawer) {
        this.screenDrawer = screenDrawer;
    }

    private void drawElement(Position position) {
        for (int i = 0; i <= brushSize; i++) {
            for (int j = 0; j <= brushSize; j++) {
                Position newPosition = new Position(position, i, j);
                if (!Game.world.isOutOfBounds(newPosition)) {
                    Game.world.setElement(newPosition, Game.paintElement.call(newPosition));
                    Game.world.addDiff(newPosition);
                    Game.world.updateChunk(newPosition.x(), newPosition.y());
                }
            }
        }
    }

    public void mouseDragged(int x, int y) {
        if (!screenDrawer.isUiOpen) {
            Position position = PositionHelper.mousePointToGridPos(x, y);
            if (Game.world.isOutOfBounds(position)) return;
            if (previous == null) {
                Game.world.setElement(position, Game.paintElement.call(position));
            } else {
                Position[] line = Line.getLinePoints(previous, position);
                for (Position p : line) {
                    drawElement(p);
                }
            }
            previous = position;
        }
    }

    public void mousePressed(int x, int y) {
        if (screenDrawer.isUiOpen) {
            screenDrawer.activeUI.mouseClicked(x, y);
        }
    }

    public void mouseReleased(int x, int y) {
        if (!screenDrawer.isUiOpen) {
            previous = null;
        } else {
            screenDrawer.activeUI.mouseReleased(x, y);
        }
    }
}