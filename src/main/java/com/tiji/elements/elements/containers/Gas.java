package com.tiji.elements.elements.containers;

import com.tiji.elements.core.Color;
import com.tiji.elements.core.Element;
import com.tiji.elements.core.Position;

public abstract class Gas extends Liquids {
    public Gas(Position position, int swapPriority, Color color) {
        super(position, swapPriority, color);
    }

    @Override
    public void tick() {
        Element[] neighbor = getNeighbor();
        boolean up = canSwapWith(neighbor[0]);
        boolean left = canSwapWith(neighbor[2]);
        boolean right = canSwapWith(neighbor[3]);
        boolean leftUp = canSwapWith(getElementAt(new Position(getPosition(), -1, 1)));
        boolean rightUp = canSwapWith(getElementAt(new Position(getPosition(), 1, 1)));

        boolean didSwap = false;
        if (up) {
            swapWith(new Position(getPosition(), 0, 1));
            didSwap = true;
        } else if (left && right && leftUp && rightUp) {
            boolean shouldSwapWithLeft = Math.random() < 0.5;
            if (shouldSwapWithLeft) {
                swapWith(new Position(getPosition(), -1, 1));
            } else {
                swapWith(new Position(getPosition(), 1, 1));
            }
            didSwap = true;
        } else if (left && leftUp) {
            swapWith(new Position(getPosition(), -1, 1));
            didSwap = true;
        } else if (right && rightUp) {
            swapWith(new Position(getPosition(), 1, 1));
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
}
