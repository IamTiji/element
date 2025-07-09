package com.tiji.elements.elements;

import com.tiji.elements.core.Color;
import com.tiji.elements.core.Element;
import com.tiji.elements.core.Position;
import com.tiji.elements.elements.containers.Flammable;
import com.tiji.elements.elements.containers.ImmovableSolids;

public class Virus extends ImmovableSolids implements Flammable {
    private static final float SPREAD_CHANCE = 0.2f;

    public Virus(Position position) {
        super(position, 100, Color.ofRange(255, 255, 0, 0, 20, 50));
    }

    @Override
    public void tick() {
        Element[] neighbors = getNeighbor();
        for (Element neighbor : neighbors) {
            if (!(neighbor instanceof Air || neighbor instanceof Virus) && neighbor != null) {
                if (Math.random() < SPREAD_CHANCE) neighbor.convertTo(Virus::new);
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
        return "element.virus";
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
