package com.tiji.elements.display.ui;

public interface AbstractUI {
    void render(int mouseX, int mouseY, int screenWidth, int screenHeight);
    void mouseClicked(int x, int y);
    void mouseReleased(int x, int y);
    void close();
}
