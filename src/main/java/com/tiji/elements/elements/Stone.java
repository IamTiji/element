package com.tiji.elements.elements;

import com.tiji.elements.core.Color;
import com.tiji.elements.core.Position;
import com.tiji.elements.core.Untickable;
import com.tiji.elements.elements.containers.AcidReactable;
import com.tiji.elements.elements.containers.ImmovableSolids;

public class Stone extends ImmovableSolids implements Untickable, AcidReactable {
    public Stone(Position position) {
        super(position, 100, new Color(80, 80, 80));
    }

    @Override
    public String getDebugInfo() {
        return "Stone";
    }

    @Override
    public String getTranslationKey() {
        return "element.stone";
    }

    @Override
    public float decayChance() {
        return 0.1f;
    }
}
