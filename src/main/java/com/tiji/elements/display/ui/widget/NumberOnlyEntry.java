package com.tiji.elements.display.ui.widget;

import com.tiji.elements.core.Position;

public class NumberOnlyEntry extends Entry {
    public NumberOnlyEntry(Position pos, int width, String text) {
        super(pos, width, text);
    }

    public NumberOnlyEntry(Position pos, int width) {
        super(pos, width);
    }

    @Override
    public void charTyped(char c) {
        if (c >= '0' && c <= '9') super.charTyped(c);
    }
}
