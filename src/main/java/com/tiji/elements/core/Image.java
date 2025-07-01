package com.tiji.elements.core;

public class Image {
    short[] rawData;
    int width;
    int height;

    public Image(short[] rawData, int width, int height) {
        this.rawData = rawData;
        this.width = width;
        this.height = height;
    }

    public Image(int width, int height, Color color) {
        rawData = new short[width * height * 3];
        for (int i = 0; i < rawData.length; i += 3) {
            rawData[i] = (short) color.red();
            rawData[i + 2] = (short) color.blue();
            rawData[i + 1] = (short) color.green();
        }
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Color getPixelAt(Position pos) {
        int index = ((pos.y() * width) + pos.x()) * 3;
        return new Color(rawData[index], rawData[index + 1], rawData[index + 2]);
    }

    public void setPixelAt(Position pos, Color color) {
        int index = ((pos.y() * width) + pos.x()) * 3;
        rawData[index] = (short) color.red();
        rawData[index + 2] = (short) color.blue();
        rawData[index + 1] = (short) color.green();
    }

    public String toString() {
        final int MAX_WIDTH = 100;
        final int MAX_HEIGHT = 100;
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < Math.min(MAX_HEIGHT, height); y += 2) {
            for (int x = 0; x < Math.min(MAX_WIDTH, width); x++) {
                Color firstPixel = getPixelAt(new Position(x, y));
                Color secondPixel = getPixelAt(new Position(x, y + 1));
                sb.append(String.format("\u001b[48;2;%d;%d;%dm\u001b[0m[38;2;%d;%d;%dm▀",
                        firstPixel.red(), firstPixel.green(), firstPixel.blue(),
                        secondPixel.red(), secondPixel.green(), secondPixel.blue()));
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
