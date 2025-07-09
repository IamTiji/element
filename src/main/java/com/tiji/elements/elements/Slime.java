package com.tiji.elements.elements;

import com.tiji.elements.core.Color;
import com.tiji.elements.core.Element;
import com.tiji.elements.core.Position;
import com.tiji.elements.elements.containers.Flammable;
import com.tiji.elements.elements.containers.ImmovableSolids;

public class Slime extends ImmovableSolids implements Flammable {
    private static final float SPREAD_CHANCE = 0.2f;

    public Slime(Position position) {
        super(position, 100, Color.ofRange(200, 255, 0, 0, 255, 255));
    }

    @Override
    public void tick() {
        Element[] neighbors = getNeighbor();
        for (Element neighbor : neighbors) {
            if (neighbor instanceof Air) {
                if (Math.random() < SPREAD_CHANCE) neighbor.convertTo(Slime::new);
                else reportUpdateToChunk();
            }
        }
    }

    @Override
    public String getDebugInfo() {
        return "";
    }

    @Override
    public String getTranslationKey() {
        return "element.slime";
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
