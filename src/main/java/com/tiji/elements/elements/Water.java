package com.tiji.elements.elements;

import com.tiji.elements.core.Color;
import com.tiji.elements.core.Position;
import com.tiji.elements.elements.containers.Liquids;

public class Water extends Liquids {
    public Water(Position position) {
        super(position, 1, new Color(0, 0, 255));
    }

    @Override
    public String getDebugInfo() {
        return "Water";
    }

    @Override
    public String getTranslationKey() {
        return "element.water";
    }
}