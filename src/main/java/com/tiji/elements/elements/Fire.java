package com.tiji.elements.elements;

import com.tiji.elements.core.Color;
import com.tiji.elements.core.Element;
import com.tiji.elements.core.Position;
import com.tiji.elements.elements.containers.Flammable;
import com.tiji.elements.elements.containers.Gas;

public class Fire extends Gas {
    private int lifeTime;
    private static final int MAX_LIFE_TIME = 30;

    public Fire(Position position) {
        super(position, -1, Color.ofRange(255, 255, 128, 255, 0, 0));
        this.lifeTime = (int) (Math.random() * (MAX_LIFE_TIME/2) + Math.random() * (MAX_LIFE_TIME/2));
    }

    @Override
    public void tick() {
        if (lifeTime-- < 0) convertTo(Air::new);

        boolean isBurning = false;
        Flammable neighbor = null;
        for (Element element : getNeighbor()) {
            if (element instanceof Flammable) {
                isBurning = true;
                neighbor = (Flammable) element;
                break;
            }
        }
        if (!isBurning) super.tick();
        else {
            reportUpdateToChunk();
            if (Math.random() < neighbor.naturalExtinguishChance()) {
                convertTo(Air::new);
            } else if (Math.random() < neighbor.destroyChance()) {
                convertTo(Fire::new);
                for (Element element : getNeighbor()) {
                    if (element instanceof Flammable) {
                        element.convertTo(Fire::new);
                    }
                }
            }
        }
    }

    @Override
    public String getDebugInfo() {
        return "Fire";
    }

    @Override
    public String getTranslationKey() {
        return "element.fire";
    }

    @Override
    public int getGlowIntensity() {
        return 255;
    }

    @Override
    public Color getGlowColor() {
        return new Color(255, 70, 0);
    }
}
