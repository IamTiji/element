package com.tiji.elements.elements.containers;

import com.tiji.elements.core.Color;
import com.tiji.elements.core.Element;
import com.tiji.elements.core.Position;

public abstract class Liquids extends Element {
    private final Color color;

    public Liquids(Position position, int swapPriority, Color color) {
        super(position, swapPriority);
        this.color = color;
    }


    @Override
    public void tick() {
        Element[] neighbor = getNeighbor();
        boolean down = canSwapWith(neighbor[1]);
        boolean left = canSwapWith(neighbor[2]);
        boolean right = canSwapWith(neighbor[3]);
        boolean leftDown = canSwapWith(getElementAt(new Position(getPosition(), -1, -1)));
        boolean rightDown = canSwapWith(getElementAt(new Position(getPosition(), 1, -1)));

        boolean didSwap = false;
        if (down) {
            swapWith(new Position(getPosition(), 0, -1));
            didSwap = true;
        } else if (left && right && leftDown && rightDown) {
            boolean shouldSwapWithLeft = Math.random() < 0.5;
            if (shouldSwapWithLeft) {
                swapWith(new Position(getPosition(), -1, -1));
            } else {
                swapWith(new Position(getPosition(), 1, -1));
            }
            didSwap = true;
        } else if (left && leftDown) {
            swapWith(new Position(getPosition(), -1, -1));
            didSwap = true;
        } else if (right && rightDown) {
            swapWith(new Position(getPosition(), 1, -1));
            didSwap = true;
        } else if (left && right) {
            boolean shouldSwapWithLeft = Math.random() < 0.5;
            if (shouldSwapWithLeft) {
                swapWith(new Position(getPosition(), -1, 0));
            } else {
                swapWith(new Position(getPosition(), 1, 0));
            }
            didSwap = true;
        } else if (left) {
            swapWith(new Position(getPosition(), -1, 0));
            didSwap = true;
        } else if (right) {
            swapWith(new Position(getPosition(), 1, 0));
            didSwap = true;
        }

        if (didSwap) updateNeighbors(2);
    }

    @Override
    public Color displayedColor() {
        return color;
    }

    @Override
    public boolean canRest() {
        return lastTickTime - lastNeighborUpdateTime > 10;
    }

    @Override
    public void restTick() {

    }
}
