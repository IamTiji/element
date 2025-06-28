package com.tiji.elements.elements;

import com.tiji.elements.core.Color;
import com.tiji.elements.core.Position;
import com.tiji.elements.elements.containers.Flammable;
import com.tiji.elements.elements.containers.Liquids;

public class Oil extends Liquids implements Flammable {
    public Oil(Position position) {
        super(position, 0, Color.ofRange(255, 255, 179, 220, 0, 0));
    }

    @Override
    public String getDebugInfo() {
        return "Water";
    }

    @Override
    public String getTranslationKey() {
        return "element.oil";
    }

    @Override
    public float destroyChance() {
        return 0.6f;
    }

    @Override
    public float naturalExtinguishChance() {
        return 0f;
    }
}