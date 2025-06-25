package com.tiji.elements.elements;

import com.tiji.elements.core.Element;
import com.tiji.elements.core.Color;
import com.tiji.elements.core.Position;
import com.tiji.elements.core.Untickable;
import com.tiji.elements.elements.containers.ImmovableSolids;

public class Mud extends ImmovableSolids implements Untickable {
    public int wetness;

    public Mud(Position position) {
        super(position, 2, new Color(87, 72, 33));
        wetness = 5;
    }

    public Mud(Position position, int wetness) {
        this(position);
        this.wetness = wetness;
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
