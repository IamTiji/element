package com.tiji.elements.core;

public final class Position {
    private final int x;
    private final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position(Position current, int dx, int dy) {
        this(current.x + dx, current.y + dy);
    }

    public Position translate(int dx, int dy) {
        return new Position(x + dx, y + dy);
    }

    public int distanceTo(Position other) {
        int dx = x - other.x;
        int dy = y - other.y;
        return (int) Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }
}
