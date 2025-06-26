package com.tiji.elements.core;

import com.tiji.elements.Game;

import java.util.ArrayList;

abstract public class Element {
    private Position position;
    private int temperature;
    public long lastTickTime;
    public long lastNeighborUpdateTime;
    private final int swapPriority;

    protected void swapWith(Position pos) {
        Game.world.swapElement(this.position, pos);
    }
    protected final Element getElementAt(Position pos) {
        if (Game.world.isOutOfBounds(pos)) return null;
        return Game.world.getElement(pos);
    }
    public final void setPosition(Position position) {
        this.position = position;
    }
    public Element(Position position, int swapPriority) {
        this.position = position;
        this.temperature = 0;
        this.swapPriority = swapPriority;
        if (Game.world != null) this.lastNeighborUpdateTime = Game.world.getTickCount();
    }
    public final Position getPosition() {
        return position;
    }
    public final int getTemperature() {
        return temperature;
    }
    public final void setTemperature(int temperature) {
        this.temperature = temperature;
    }
    protected void updateNeighbors(int recursiveDepth, ArrayList<Position> updatedElements) {
        Element[] neighbors = getNeighbor();
        for (Element neighbor : neighbors) {
            if (neighbor != null && !updatedElements.contains(neighbor.position)) {
                updatedElements.add(neighbor.getPosition());
                if (recursiveDepth > 0) neighbor.updateNeighbors(recursiveDepth - 1, updatedElements);
                else neighbor.updateSelf();
            }
        }
        updateSelf();
    }
    protected void updateNeighbors(int recursiveDepth) {
        updateNeighbors(recursiveDepth, new ArrayList<>());
    }
    protected void updateNeighbors() {
        updateNeighbors(0);
    }
    public void updateSelf() {
        this.lastNeighborUpdateTime = Game.world.getTickCount();
    }
    protected Element[] getNeighbor() {
        Element up = getElementAt(new Position(position, 0, 1));
        Element down = getElementAt(new Position(position, 0, -1));
        Element left = getElementAt(new Position(position, -1, 0));
        Element right = getElementAt(new Position(position, 1, 0));

        return new Element[]{up, down, left, right};
    }
    protected boolean canSwapWith(Element other) {
        if (other == null) return false;
        else return this.swapPriority > other.swapPriority;
    }
    public void convertTo(ElementFactory element) {
        Game.world.setElement(getPosition(), element.call(getPosition()));
        Game.world.addDiff(this.position);
    }
    public abstract void tick();
    public abstract Color displayedColor();
    public abstract String getDebugInfo();
    public abstract String getTranslationKey();
    public Color getGlowColor() {
        return new Color(0, 0, 0);
    }
    public int getGlowIntensity() {
        return 0;
    }
}
