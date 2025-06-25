package com.tiji.elements.elements;

import com.tiji.elements.core.Color;
import com.tiji.elements.core.Position;
import com.tiji.elements.elements.containers.Flammable;
import com.tiji.elements.elements.containers.ImmovableSolids;

public class Wood extends ImmovableSolids implements Flammable {
    public Wood(Position position) {
        super(position, 100, new Color(158, 71, 0));
    }

    @Override
    public String getDebugInfo() {
        return "Wood";
    }

    @Override
    public String getTranslationKey() {
        return "element.wood";
    }

    @Override
    public float destroyChance() {
        return 0.2f;
    }

    @Override
    public float naturalExtinguishChance() {
        return 0.05f;
    }
}
