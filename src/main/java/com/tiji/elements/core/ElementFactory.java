package com.tiji.elements.core;

@FunctionalInterface
public interface ElementFactory {
    Element call(Position position);
}
