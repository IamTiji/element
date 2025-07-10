package com.tiji.elements.display.ui.widget;

import com.tiji.elements.core.Color;
import com.tiji.elements.core.Position;
import com.tiji.elements.display.DrawCalls;

import java.util.List;

public class OptionDropdown extends Widget {
    private final List<String> options;
    private final int HEIGHT_EXPANDED;
    private final int width;

    private boolean expanded = false;
    private String selectedOption;

    private static final int HEIGHT = 30;

    public OptionDropdown(Position pos, List<String> options, int width, String selectedOption) {
        super(pos);
        this.options = options;
        this.width = width;
        this.selectedOption = selectedOption;
        HEIGHT_EXPANDED = options.size() * 30 + 35;
    }

    public OptionDropdown(Position pos, List<String> options, int width) {
        this(pos, options, width, options.getFirst());
    }

    @Override
    public void draw(Position mousePos) {
        int half_height = HEIGHT / 2;

        DrawCalls.rectangle(getPos(), width, expanded ? HEIGHT_EXPANDED : HEIGHT, new Color(255, 255, 255));
        DrawCalls.rectangle(getPos().translate(1, 1), width - 2, (expanded ? HEIGHT_EXPANDED : HEIGHT) - 2, new Color(0, 0, 0));
        DrawCalls.triangle(
                getPos().translate(width - 10, half_height + (expanded ? +3 : -2)),
                getPos().translate(width -  4, half_height + (expanded ? +3 : -2)),
                getPos().translate(width -  7, half_height + (expanded ? -3 : +4)),
                new Color(255, 255, 255));

        DrawCalls.text(getPos().translate(7, half_height - 9), selectedOption, new Color(255, 255, 255));

        if (expanded) {
            DrawCalls.rectangle(getPos().translate(0, HEIGHT), width, 1, new Color(255, 255, 255));
            int index = (mousePos.y() - getPos().y() - HEIGHT) / HEIGHT;
            index = Math.clamp(index, 0, options.size() - 1);
            DrawCalls.rectangle(getPos().translate(1, index * HEIGHT + HEIGHT + 1), width - 2, HEIGHT - 2, new Color(30, 30, 30));

            int y = getPos().y() + half_height - 9;
            for (String option : options) {
                y += 30;
                DrawCalls.text(new Position(getPos().x() + 7, y), option, new Color(255, 255, 255));
            }
        }
    }

    @Override
    public void remove() {

    }

    @Override
    public void mouseClick(Position pos) {
        if (isPointInside(pos, width, expanded ? HEIGHT_EXPANDED : HEIGHT)) {
            if (expanded) {
                int index = (pos.y() - getPos().y() - HEIGHT) / HEIGHT;
                if (index >= 0) {
                    selectedOption = options.get(Math.min(index, options.size() - 1));
                }
            }
            expanded = !expanded;
        }
    }
}
