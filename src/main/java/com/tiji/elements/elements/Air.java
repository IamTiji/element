package com.tiji.elements.elements;

import com.tiji.elements.core.Color;
import com.tiji.elements.core.Element;
import com.tiji.elements.core.Position;
import com.tiji.elements.core.Untickable;

public class Air extends Element implements Untickable {
    public Air(Position position) {
        super(position, Integer.MIN_VALUE);
    }

    @Override
    public void tick() {

    }

    @Override
    public Color displayedColor() {
        return new Color(0, 0, 0);
    }

    @Override
    public String getDebugInfo() {
        return "Air";
    }

    @Override
    public String getTranslationKey() {
        return "element.air";
    }
}
