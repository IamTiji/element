package com.tiji.elements.core;

import com.tiji.elements.Game;
import com.tiji.elements.settings.fields.Language;
import com.tiji.elements.utils.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class World {
    private final Element[][] world;
    private final Chunk[][] chunks;
    private final int width;
    private final int height;
    private int tickCount = 0;
    private final BlockingQueue<Chunk> taskQueue;
    private long lastTickStartTime = 0;
    private boolean addingQueue = false;
    private final ChangesBuffer changes;
    private final int chunkX;
    public final Chunk[][] chunksDebug;
    private final int chunkY;

    public World(int width, int height, ElementFactory initialElement) {
        Logger.info("Creating new world with dimensions: " + width + "x" + height);

        this.width = width;
        this.height = height;
        this.world = new Element[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                try {
                    this.world[x][y] = initialElement.call(new Position(x, y));
                } catch (Exception e) {
                    throw new RuntimeException("Failed to initialize element at (" + x + ", " + y + ")", e);
                }
            }
        }
        chunkX = this.width / Chunk.CHUNK_SIZE;
        chunkY = this.height / Chunk.CHUNK_SIZE;
        this.chunks = new Chunk[chunkX][chunkY];
        for (int x = 0; x < chunkX; x++) {
            for (int y = 0; y < chunkY; y++) {
                chunks[x][y] = new Chunk(x, y);
            }
        }
        this.chunksDebug = new Chunk[chunkX][chunkY];
        for (int x = 0; x < chunkX; x++) {
            System.arraycopy(chunks[x], 0, chunksDebug[x], 0, chunkY);
        }
        taskQueue = new ArrayBlockingQueue<>(chunkY*chunkX);
        changes = new ChangesBuffer();
    }

    public void init() {
        Logger.info("Starting %s simulation worker thread(s)...", Game.THREAD_COUNT);
        scheduleTasks();
        for (int i = 0; i < Game.THREAD_COUNT; i++) {
            new Thread(this::threadWorker, "Simulation-worker-"+i).start();
        }
    }

    public Element[][] getWorld() {
        return world;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public synchronized void swapElement(Position pos1, Position pos2) {
        Element temp = world[pos1.x()][pos1.y()];
        world[pos1.x()][pos1.y()] = world[pos2.x()][pos2.y()];
        world[pos2.x()][pos2.y()] = temp;
        world[pos1.x()][pos1.y()].setPosition(pos1);
        world[pos2.x()][pos2.y()].setPosition(pos2);

        addDiff(pos1);
        addDiff(pos2);
    }

    public Element getElement(Position pos) {
        return world[pos.x()][pos.y()];
    }

    public void setElement(Position pos, Element element) {
        world[pos.x()][pos.y()] = element;
    }

    public long getTickCount() {
        return tickCount;
    }

    public void scheduleTasks() {
        lastTickStartTime = System.currentTimeMillis();
        ArrayList<Chunk> items = new ArrayList<>();
        for (Chunk[] chunkRow : chunks) {
            for (Chunk chunk : chunkRow) {
                if (chunk.shouldTick()) {
                    items.add(chunk);
                }
            }
        }
        taskQueue.addAll(items);
        addingQueue = false;
    }

    private void threadWorker() {
        for (;;) {
            try {
                Chunk task = taskQueue.take();
                ArrayList<Element> taskUnpacked = new ArrayList<>(Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE);

                for (int x = task.getX(); x < task.getX() + Chunk.CHUNK_SIZE; x++) {
                    for (int y = task.getY(); y < task.getY() + Chunk.CHUNK_SIZE; y++) {
                        if (!(world[x][y] instanceof Untickable)) {
                            taskUnpacked.add(world[x][y]);
                        };
                    }
                }
                Collections.shuffle(taskUnpacked);
                for (Element element : taskUnpacked) {
                    element.tick();
                }

                boolean shouldReschedule;
                synchronized (this) {
                    shouldReschedule = taskQueue.isEmpty() && !addingQueue;
                    if (shouldReschedule) {
                        addingQueue = true;
                        reschedule();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void reschedule() throws InterruptedException {
        changes.swapBuffer();
        long tickCalcTime = System.currentTimeMillis() - lastTickStartTime;
        if (Game.TARGET_MSPT*2 < tickCalcTime) Logger.warning("Tick took too long! %d ms, target: %d ms", tickCalcTime, Game.TARGET_MSPT);
        Thread.sleep(Math.max(0, Game.TARGET_MSPT - tickCalcTime));
        tickCount++;

        for (int x = 0; x < chunkX; x++) {
            for (int y = 0; y < chunkY; y++) {
                if (chunks[x][y].shouldTick()) continue;
                boolean left =   x > 0          && chunks[x - 1][y].state.equals(Chunk.ChunkState.RUNNING);
                boolean right =  x < chunkX - 1 && chunks[x + 1][y].state.equals(Chunk.ChunkState.RUNNING);
                boolean top =    y > 0          && chunks[x][y - 1].state.equals(Chunk.ChunkState.RUNNING);
                boolean bottom = y < chunkY - 1 && chunks[x][y + 1].state.equals(Chunk.ChunkState.RUNNING);
                boolean neighborUpdate = left || right || top || bottom;

                if (neighborUpdate) {
                    chunks[x][y].updateState(Chunk.ChunkState.UPDATED);
                }
            }
        }
        for (int x = 0; x < chunkX; x++) {
            for (int y = 0; y < chunkY; y++) {
                chunksDebug[x][y].updateState(chunks[x][y].state);
            }
        }
        scheduleTasks();
        if (taskQueue.isEmpty()) {
            taskQueue.add(chunks[0][0]);
        }
        for (Chunk[] chunkRow : chunks) {
            for (Chunk chunk : chunkRow) {
                chunk.updateState(Chunk.ChunkState.IDLE);
            }
        }
    }

    public boolean isOutOfBounds(Position pos) {
        return pos.x() < 0 || pos.x() >= width || pos.y() < 0 || pos.y() >= height;
    }

    public Position[] pollDiff() {
        return changes.pollChanges().toArray(new Position[0]);
    }

    public void addDiff(Position pos) {
        changes.addChange(pos);
    }

    public void updateChunk(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) return;
        chunks[x / Chunk.CHUNK_SIZE][y / Chunk.CHUNK_SIZE].updateState(Chunk.ChunkState.RUNNING);
    }

    public Chunk getChunkIn(int x, int y) {
        return chunks[x / Chunk.CHUNK_SIZE][y / Chunk.CHUNK_SIZE];
    }
    public Chunk getDebugChunk(int x, int y) {
        return chunksDebug[x / Chunk.CHUNK_SIZE][y / Chunk.CHUNK_SIZE];
    }
}
