package com.tiji.elements.core;

import com.tiji.elements.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class World {
    private final Element[][] world;
    private final int width;
    private final int height;
    private int tickCount = 0;
    private final BlockingQueue<Element> taskQueue;
    private long lastTickStartTime = 0;
    private boolean addingQueue = false;
    private final ChangesBuffer changes;

    public World(int width, int height, ElementFactory initialElement) {
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
        taskQueue = new ArrayBlockingQueue<>(width*height+1);
        changes = new ChangesBuffer();
    }

    public void init() {
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

    // Important! This method expects element position to be init
    public void setElement(Position pos, Element element) {
        world[pos.x()][pos.y()] = element;
    }

    public long getTickCount() {
        return tickCount;
    }

    public void scheduleTasks() {
        lastTickStartTime = System.currentTimeMillis();
        ArrayList<Element> items = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            items.addAll(Arrays.asList(world[x]).subList(0, height));
        }
        for (int i = 0; i < items.size(); i++) {
            int nextIndex = (int) Math.floor(Math.random() * items.size());
            Element temp = items.get(i);
            items.set(i, items.get(nextIndex));
            items.set(nextIndex, temp);
        }
        taskQueue.addAll(items);
        addingQueue = false;
    }

    private void threadWorker() {
        for (;;) {
            try {
                Element task = taskQueue.take();

                if (!(task instanceof Untickable)) {
                    task.tick();
                    task.lastTickTime = getTickCount();
                }

                boolean shouldReschedule;
                synchronized (this) {
                    shouldReschedule = taskQueue.isEmpty() && !addingQueue;
                    if (shouldReschedule) {
                        addingQueue = true;
                    }
                }
                if (shouldReschedule) {
                    changes.swapBuffer();
                    long tickCalcTime = System.currentTimeMillis() - lastTickStartTime;
                    Thread.sleep(Math.max(0, Game.TARGET_MSPT - tickCalcTime));
                    tickCount++;
                    scheduleTasks();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
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
}
