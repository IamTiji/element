package com.tiji.elements.elements;

import com.tiji.elements.core.Color;
import com.tiji.elements.core.Position;
import com.tiji.elements.core.Untickable;
import com.tiji.elements.elements.containers.ImmovableSolids;

public class Stone extends ImmovableSolids implements Untickable {
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
}
