package com.tiji.elements.core;

public record Color(int red, int green, int blue) {
    public Color darken(int percentage) {
        double factor = 1 - percentage / 100.0;
        short newRed = (short) (red * factor);
        short newGreen = (short) (green * factor);
        short newBlue = (short) (blue * factor);
        return new Color(newRed, newGreen, newBlue);
    }
    public Color lighten(int percentage) {
        return darken(-percentage);
    }
    public static Color ofRange(int minRed, int maxRed, int minGreen, int maxGreen, int minBlue, int maxBlue) {
        int red = minRed + (int) ((maxRed - minRed) * Math.random());
        int green = minGreen + (int) ((maxGreen - minGreen) * Math.random());
        int blue = minBlue + (int) ((maxBlue - minBlue) * Math.random());
        return new Color(red, green, blue);
    }
    public int toInt() {
        return (red << 16) | (green << 8) | blue;
    }
}
