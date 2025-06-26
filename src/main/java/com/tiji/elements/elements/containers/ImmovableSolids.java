package com.tiji.elements.elements.containers;

import com.tiji.elements.core.Color;
import com.tiji.elements.core.Element;
import com.tiji.elements.core.Position;

public abstract class ImmovableSolids extends Element {
    Color color;

    public ImmovableSolids(Position position, int swapPriority, Color color) {
        super(position, swapPriority);
        this.color = color;
    }

    @Override
    public void tick() {

    }

    @Override
    public Color displayedColor() {
        return color;
    }
}
