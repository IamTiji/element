package com.tiji.elements.display;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tiji.elements.core.Color;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL43;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FontLoader {
    private static final InputStream FONT_ATLAS = FontLoader.class.getResourceAsStream("/font.png");
    private static final InputStream FONT_METADATA = FontLoader.class.getResourceAsStream("/font.json");

    private static final HashMap<Integer, CharData> charPos = new HashMap<>();

    public static int shaderProgram;
    public static int fontAtlasPointer;

    public static float maxHeight = 0;

    public record CharData(float[] atlasBounds, float[] shifts, float[] size, float advance) {}

    public static void loadFont(int w, int h) {
        final float WIDTH_MULTIPLIER = (float) h / w;

        shaderProgram = ScreenDrawer.makeShaderProgram("/shader/font.vert", "/shader/font.frag");

        if (FONT_ATLAS == null || FONT_METADATA == null) throw new RuntimeException("Font resources not found");
        try {
            BufferedImage atlas = ImageIO.read(FONT_ATLAS);
            ByteBuffer imageAtlas = BufferUtils.createByteBuffer(atlas.getWidth() * atlas.getHeight() * 3);
            for (int y = 0; y < atlas.getHeight(); y++) {
                for (int x = 0; x < atlas.getWidth(); x++) {
                    int rgb = atlas.getRGB(x, y);
                    imageAtlas.put((byte) ((rgb >> 16) & 0xFF));
                    imageAtlas.put((byte) ((rgb >> 8) & 0xFF));
                    imageAtlas.put((byte) (rgb & 0xFF));
                }
            }
            imageAtlas.flip();
            fontAtlasPointer = GL43.glGenTextures();
            GL43.glBindTexture(GL43.GL_TEXTURE_2D, fontAtlasPointer);
            GL43.glTexImage2D(GL43.GL_TEXTURE_2D, 0, GL43.GL_RGB, atlas.getWidth(), atlas.getHeight(), 0, GL43.GL_RGB, GL43.GL_UNSIGNED_BYTE, imageAtlas);
            GL43.glTexParameteri(GL43.GL_TEXTURE_2D, GL43.GL_TEXTURE_MIN_FILTER, GL43.GL_LINEAR);
            GL43.glTexParameteri(GL43.GL_TEXTURE_2D, GL43.GL_TEXTURE_MAG_FILTER, GL43.GL_LINEAR);

            JsonObject metadata = new Gson().fromJson(new String(FONT_METADATA.readAllBytes()), JsonObject.class);
            JsonArray glyphData = metadata.getAsJsonArray("glyphs");

            float imageHeight = metadata.getAsJsonObject("atlas").get("height").getAsFloat();
            float imageWidth = metadata.getAsJsonObject("atlas").get("width").getAsFloat();
            float emSize = metadata.getAsJsonObject("atlas").get("size").getAsFloat();

            float sizeMultiplierHoriz = 1 / imageWidth;
            float sizeMultiplierVert = -1 / imageHeight;
            float emMultiplier = emSize * sizeMultiplierHoriz;

            for (JsonElement glyph : glyphData) {
                JsonObject glyphObject = glyph.getAsJsonObject();
                JsonObject atlasBoundUnpacked = glyphObject.getAsJsonObject("atlasBounds");

                float[] atlasBound;
                if (atlasBoundUnpacked == null) {
                    atlasBound = new float[] {0f, 0f, 0f, 0f};
                } else {
                    atlasBound = new float[]{
                            atlasBoundUnpacked.get("left").getAsFloat() * sizeMultiplierHoriz,
                            atlasBoundUnpacked.get("bottom").getAsFloat() * sizeMultiplierVert,
                            atlasBoundUnpacked.get("right").getAsFloat() * sizeMultiplierHoriz,
                            atlasBoundUnpacked.get("top").getAsFloat() * sizeMultiplierVert
                    };
                }

                float[] size = {
                          WIDTH_MULTIPLIER *(atlasBound[2] - atlasBound[0]),
                        Math.abs(atlasBound[3] - atlasBound[1])
                };
                if (maxHeight < size[1]) maxHeight = size[1];

                JsonObject planeBoundUnpacked = glyphObject.getAsJsonObject("planeBounds");
                float[] shifts;
                if (planeBoundUnpacked == null) {
                    shifts = new float[]{0f, 0f, 0f, 0f};
                } else {
                    shifts = new float[]{
                            planeBoundUnpacked.get("left").getAsFloat() * emMultiplier,
                            planeBoundUnpacked.get("bottom").getAsFloat() * emMultiplier,
                    };
                }

                float advance = glyphObject.get("advance").getAsFloat() * emMultiplier * WIDTH_MULTIPLIER;
                charPos.put(glyphObject.get("unicode").getAsInt(), new CharData(atlasBound, shifts, size, advance));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static ArrayList<Float> genTextVertices(String text, float x, float y, Color color, float size, float z) {
        ArrayList<Float> vertices = new ArrayList<>();

        char[] charArray = text.toCharArray();

        for (char charactor : charArray) {
            CharData charData = charPos.get((int) charactor);
            if (charData == null) {
                charData = charPos.get(0x20);
            }
            float red = color.red() / 255f;
            float green = color.green() / 255f;
            float blue = color.blue() / 255f;

            float x1 = x + charData.shifts[0] * size;
            float y1 = y - maxHeight * size + charData.shifts[1] * size;
            float x2 = x1 + charData.size[0] * size;
            float y2 = y1 + charData.size[1] * size;

            vertices.addAll(List.of(
                    x1, y1, z, charData.atlasBounds[0], charData.atlasBounds[1], red, green, blue,
                    x2, y1, z, charData.atlasBounds[2], charData.atlasBounds[1], red, green, blue,
                    x2, y2, z, charData.atlasBounds[2], charData.atlasBounds[3], red, green, blue,
                    x1, y1, z, charData.atlasBounds[0], charData.atlasBounds[1], red, green, blue,
                    x2, y2, z, charData.atlasBounds[2], charData.atlasBounds[3], red, green, blue,
                    x1, y2, z, charData.atlasBounds[0], charData.atlasBounds[3], red, green, blue
            ));
            x += charData.advance * size;
        }
        return vertices;
    }

    public static float getFontWidth(String text, float size) {
        float width = 0;
        for (char charactor : text.toCharArray()) {
            CharData charData = charPos.get((int) charactor);
            if (charData == null) {
                charData = charPos.get(0x20);
            }
            width += charData.advance * size;
        }
        return width;
    }
}
