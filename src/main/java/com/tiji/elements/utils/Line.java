package com.tiji.elements.utils;

import com.tiji.elements.core.Position;

import java.util.ArrayList;

public class Line {
    public static Position[] getLinePoints(Position start, Position end) {
        int x0 = start.x();
        int y0 = start.y();
        int x1 = end.x();
        int y1 = end.y();

        int dx = Math.abs(x1 - x0);
        int dy = -Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx + dy;

        ArrayList<Position> points = new ArrayList<>();

        while (true) {
            points.add(new Position(x0, y0));
            if (x0 == x1 && y0 == y1) break;
            int e2 = 2 * err;
            if (e2 >= dy) {
                err += dy;
                x0 += sx;
            }
            if (e2 <= dx) {
                err += dx;
                y0 += sy;
            }
        }

        return points.toArray(new Position[0]);
    }
}
