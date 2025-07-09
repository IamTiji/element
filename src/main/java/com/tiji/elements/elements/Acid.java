package com.tiji.elements.elements;

import com.tiji.elements.core.Color;
import com.tiji.elements.core.Element;
import com.tiji.elements.core.Position;
import com.tiji.elements.elements.containers.AcidNonReactable;
import com.tiji.elements.elements.containers.AcidReactable;
import com.tiji.elements.elements.containers.Liquids;

public class Acid extends Liquids implements AcidNonReactable {
    private int lifetime;

    public Acid(Position position) {
        super(position, 1, Color.ofRange(0,0,200,255,0,0));
        this.lifetime = (int) (Math.random()*3);
    }

    @Override
    public void tick() {
        super.tick();
        Element[] neighbors = getNeighbor();
        for (Element neighbor : neighbors) {
            if (neighbor == null) continue;
            boolean shouldDecay = false;
            if (neighbor instanceof AcidReactable) {
                shouldDecay = ((AcidReactable) neighbor).decayChance() > Math.random();
                lifetime--;
            } else if (!(neighbor instanceof AcidNonReactable)) {
                shouldDecay = true;
                lifetime--;
            }
            if (shouldDecay) {
                neighbor.convertTo(Air::new);
            }
        }
        if (this.lifetime < 0) {
            convertTo(Air::new);
        }
    }

    @Override
    public String getDebugInfo() {
        return "";
    }

    @Override
    public String getTranslationKey() {
        return "element.acid";
    }
}
