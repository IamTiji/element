package com.tiji.elements.display;

import com.tiji.elements.Game;
import com.tiji.elements.core.Position;

public class PositionHelper {
    public static Position mousePointToGridPos(int mouseX, int mouseY) {
        return new Position(mouseX / Game.DrawScale,
                (Game.HEIGHT * Game.DrawScale - mouseY) / Game.DrawScale);
    }
}
