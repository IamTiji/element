package com.tiji.elements.elements;

import com.tiji.elements.core.Element;
import com.tiji.elements.core.Color;
import com.tiji.elements.core.Position;
import com.tiji.elements.core.Untickable;

public class Stone extends Element implements Untickable {
    public Stone(Position position) {
        super(position, 100);
    }

    @Override
    public void tick() {

    }

    @Override
    public Color displayedColor() {
        return new Color(80, 80, 80);
    }

    @Override
    public boolean canRest() {
        return false;
    }

    @Override
    public void restTick() {

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
