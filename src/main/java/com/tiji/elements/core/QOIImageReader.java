package com.tiji.elements.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class QOIImageReader {
    private QOIImageReader() {
        throw new UnsupportedOperationException();
    }

    public static Image read(InputStream inputStream) throws IOException {
        if (!Arrays.equals(inputStream.readNBytes(4), new byte[]{'q', 'o', 'i', 'f'})) throw new IOException("Invalid QOI file format");
        int width = readInt(inputStream);
        int height = readInt(inputStream);
        inputStream.skip(2); // Skip colorspace & channel count

        Image image = new Image(width, height, new Color(255, 255, 255));

        Color previousColor = new Color(0, 0, 0);
        int previousAlpha = 255;
        Color[] indexedColors = new Color[64];
        int run = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (run > 0) {
                    run--;
                    image.setPixelAt(new Position(x, y), previousColor);
                    continue;
                }

                int b = inputStream.read();
                int flag = b >> 6;

                Color color = new Color(0, 0, 0);

                if (b == 0xff || b == 0xfe) { // QOI_OP_RGB
                    color = new Color(inputStream.read(), inputStream.read(), inputStream.read());
                    if (b == 0xff) previousAlpha = inputStream.read(); // QOI_OP_RGBA
                } else if (flag == 0b11) { // QOI_OP_RUN
                    run = b & 0x3f;
                    color = previousColor;
                } else if (flag == 0b10) { // QOI_OP_LUMA
                    int dg = (b & 0x3f) - 32;
                    int second = inputStream.read();
                    int dr = ((second & 0xf0) >> 4) + dg - 8;
                    int db = (second & 0x0f) + dg - 8;
                    color = new Color((previousColor.red() + dr) & 0xff,
                            (previousColor.green() + dg) & 0xff,
                            (previousColor.blue() + db) & 0xff);
                } else if (flag == 0b01) { // QOI_OP_DIFF
                    color = new Color((previousColor.red() + ((b & 0x30) >> 4) - 2) & 0xff,
                            (previousColor.green() + ((b & 0x0c) >> 2) - 2) & 0xff,
                            (previousColor.blue() + (b & 0x03) - 2) & 0xff);
                } else if (flag == 0b00) { // QOI_OP_INDEX
                    Color indexedColor = indexedColors[b & 0x3f];
                    if (indexedColor != null) {
                        color = indexedColor;
                    } else throw new IOException("Invalid indexed color index");
                }
                int hash = (color.red() * 3 + color.green() * 5 + color.blue() * 7 + previousAlpha * 11) % 64;
                indexedColors[hash] = color;
                image.setPixelAt(new Position(x, y), color);
                previousColor = color;
            }
        }
        return image;
    }

    private static int readInt(InputStream inputStream) throws IOException {
        int b1 = inputStream.read();
        int b2 = inputStream.read();
        int b3 = inputStream.read();
        int b4 = inputStream.read();
        return (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
    }
}
