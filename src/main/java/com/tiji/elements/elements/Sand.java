package com.tiji.elements.elements;

import com.tiji.elements.core.Color;
import com.tiji.elements.core.Element;
import com.tiji.elements.core.Position;
import com.tiji.elements.elements.containers.MovableSolids;

public class Sand extends MovableSolids {
    public Sand(Position position) {
        super(position, 1, Color.ofRange(255, 255, 180, 255, 0, 0));
    }

    @Override
    public void tick() {
        Element[] neighbor = getNeighbor();
        for (Element element : neighbor) {
            if (element instanceof Water) {
                convertTo(Mud::new);
                element.convertTo(Air::new);
                return;
            } else if (element instanceof Mud mud && mud.wetness > 0) {
                mud.wetness--;
                convertTo((pos) -> new Mud(pos, Math.max(mud.wetness, 0)));
                return;
            }
        }

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
        }

        if (didSwap) updateNeighbors(2);
    }

    @Override
    public String getDebugInfo() {
        return String.format("Sand");
    }

    @Override
    public String getTranslationKey() {
        return "element.sand";
    }
}
