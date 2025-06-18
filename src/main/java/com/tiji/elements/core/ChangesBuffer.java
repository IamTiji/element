package com.tiji.elements.core;

import java.util.ArrayList;

public class ChangesBuffer {
    ArrayList<Position> outBuffer;
    ArrayList<Position> inBuffer;
    final Object lock = new Object();

    public ChangesBuffer() {
        outBuffer = new ArrayList<>();
        inBuffer = new ArrayList<>();
    }

    public void addChange(Position position) {
        synchronized (lock) {
            inBuffer.add(position);
        }
    }

    public void swapBuffer() {
        synchronized (lock) {
            outBuffer.addAll(inBuffer);
            inBuffer = new ArrayList<>();
        }
    }

    public ArrayList<Position> pollChanges() {
        synchronized (lock) {
            ArrayList<Position> changes = new ArrayList<>(outBuffer);
            outBuffer.clear();
            return changes;
        }
    }
}
