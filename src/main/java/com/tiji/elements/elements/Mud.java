package com.tiji.elements.elements;

import com.tiji.elements.core.Element;
import com.tiji.elements.core.Color;
import com.tiji.elements.core.Position;
import com.tiji.elements.core.Untickable;

public class Mud extends Element implements Untickable {
    public int wetness;

    public Mud(Position position) {
        super(position, 2);
        wetness = 5;
    }

    public Mud(Position position, int wetness) {
        super(position, wetness);
        this.wetness = wetness;
    }

    @Override
    public void tick() {}

    @Override
    public Color displayedColor() {
        return new Color(87, 72, 33);
    }

    @Override
    public boolean canRest() {
        return false;
    }

    @Override
    public void restTick() {

    }

    @Override
    public String getDebugInfo() {
        return "Mud";
    }

    @Override
    public String getTranslationKey() {
        return "element.mud";
    }
}
