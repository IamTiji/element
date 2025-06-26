package com.tiji.elements.core;

public class Chunk {
    public static final int CHUNK_SIZE = 10;

    int x, y;
    public ChunkState state;

    public enum ChunkState{
        RUNNING,
        UPDATED,
        IDLE
    }

    public boolean shouldTick() {
        return state != ChunkState.IDLE;
    }

    public void updateState(ChunkState state) {
        this.state = state;
    }

    public Chunk(int x, int y) {
        this.x = x;
        this.y = y;
        this.state = ChunkState.RUNNING;
    }

    private Chunk(int x, int y, ChunkState state) {
        this.x = x;
        this.y = y;
        this.state = state;
    }

    public int getX() {
        return x * CHUNK_SIZE;
    }

    public int getY() {
        return y * CHUNK_SIZE;
    }
}
