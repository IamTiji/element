package com.tiji.elements.display;

import com.tiji.elements.core.Color;
import com.tiji.elements.core.Position;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL43;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class DrawCalls {
    private static int w;
    private static int h;

    private static final HashMap<Integer, float[]> verticesStorage = new HashMap<>();
    private static final HashMap<Integer, float[]> textVerticesStorage = new HashMap<>();

    private static int normalVertices = 0;
    private static int textVertices = 0;

    private static int lastId = 0;

    private static int vbo_normal;
    private static int vbo_text;

    private static int vao_normal;
    private static int vao_text;

    private static int shaderProgram;

    private static final int MAX_FLOAT_DATA = 10000;
    private static final int MAX_TEXT_FLOAT_DATA = 100000;
    private static final float FONT_SIZE = 5.0f;

    private static boolean updateVertices = false;
    private static boolean updateTextVertices = false;

    public static void init(int width, int height) {
        w = width;
        h = height;

        shaderProgram = ScreenDrawer.makeShaderProgram("/shader/ui.vert", "/shader/ui.frag");

        final int normalStride = 6 * Float.BYTES;
        final int textStride = 8 * Float.BYTES;

        vbo_normal = GL43.glGenBuffers();
        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, vbo_normal);
        GL43.glBufferData(GL43.GL_ARRAY_BUFFER, Float.BYTES * 6 * MAX_FLOAT_DATA, GL43.GL_STATIC_DRAW);

        vao_normal = GL43.glGenVertexArrays();
        GL43.glBindVertexArray(vao_normal);
        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, vbo_normal);

        GL43.glEnableVertexAttribArray(0); // pos
        GL43.glVertexAttribPointer(0, 3, GL43.GL_FLOAT, false, normalStride, 0);

        GL43.glEnableVertexAttribArray(1); // color
        GL43.glVertexAttribPointer(1, 3, GL43.GL_FLOAT, false, normalStride, 3 * Float.BYTES);


        vbo_text = GL43.glGenBuffers();
        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, vbo_text);
        GL43.glBufferData(GL43.GL_ARRAY_BUFFER, Float.BYTES * MAX_TEXT_FLOAT_DATA, GL43.GL_STATIC_DRAW);

        vao_text = GL43.glGenVertexArrays();
        GL43.glBindVertexArray(vao_text);
        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, vbo_text);

        GL43.glEnableVertexAttribArray(0); // pos
        GL43.glVertexAttribPointer(0, 3, GL43.GL_FLOAT, false, textStride, 0);

        GL43.glEnableVertexAttribArray(1); // tex
        GL43.glVertexAttribPointer(1, 2, GL43.GL_FLOAT, false, textStride, 3 * Float.BYTES);

        GL43.glEnableVertexAttribArray(2); // color
        GL43.glVertexAttribPointer(2, 3, GL43.GL_FLOAT, false, textStride, 5 * Float.BYTES);
    }

    public static void draw() {
        if (updateVertices) {
            updateVertices = false;
            updateVertices();
        }
        if (updateTextVertices) {
            updateTextVertices = false;
            updateTextVertices();
        }

        GL43.glUseProgram(shaderProgram);
        GL43.glBindVertexArray(vao_normal);
        GL43.glDrawArrays(GL43.GL_TRIANGLES, 0, normalVertices);

        GL43.glUseProgram(FontLoader.shaderProgram);
        GL43.glEnable(GL43.GL_BLEND);
        GL43.glBlendFunc(GL43.GL_SRC_ALPHA, GL43.GL_ONE_MINUS_SRC_ALPHA);
        GL43.glBindVertexArray(vao_text);
        GL43.glBindTexture(GL43.GL_TEXTURE_2D, FontLoader.fontAtlasPointer);
        GL43.glDrawArrays(GL43.GL_TRIANGLES, 0, textVertices);
        GL43.glDisable(GL43.GL_BLEND);
    }

    private static float[] getPos(Position pos) {
        return new float[] {
                pos.x() / (w/2f) - 1,
                1 - pos.y() / (h/2f),
                0.0f
        };
    }

    private static float convertToPixels(float value) {
        return value * (w/2f);
    }

    private static void updateVertices() {
        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(6 * MAX_FLOAT_DATA);

        normalVertices = 0;
        for (float[] data : verticesStorage.values()) {
            normalVertices += data.length / 6;
            verticesBuffer.put(data);
        }
        verticesBuffer.flip();

        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, vbo_normal);
        GL43.glBufferSubData(GL43.GL_ARRAY_BUFFER, 0, verticesBuffer);
        GL43.glBindVertexArray(vao_normal);

        resetIfEmpty();
    }

    private static void updateTextVertices() {
        FloatBuffer textVerticesBuffer = BufferUtils.createFloatBuffer(8 * MAX_TEXT_FLOAT_DATA);

        textVertices = 0;
        for (float[] data : textVerticesStorage.values()) {
            textVertices += data.length / 8;
            textVerticesBuffer.put(data);
        }
        textVerticesBuffer.flip();

        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, vbo_text);
        GL43.glBufferSubData(GL43.GL_ARRAY_BUFFER, 0, textVerticesBuffer);
        GL43.glBindVertexArray(vao_text);

        resetIfEmpty();
    }

    public static int rectangle(Position pos, int width, int height, Color color) {
        int id = ++lastId;
        float[][] packedVertices = {
                getPos(pos),
                getPos(pos.translate(width, 0)),
                getPos(pos.translate(width, height)),
                getPos(pos.translate(0, height)),
        };
        float red = color.red() / 255f;
        float green = color.green() / 255f;
        float blue = color.blue() / 255f;
        float[] vertices = {
                packedVertices[0][0], packedVertices[0][1], (float) id, red, green, blue,
                packedVertices[1][0], packedVertices[1][1], (float) id, red, green, blue,
                packedVertices[2][0], packedVertices[2][1], (float) id, red, green, blue,
                packedVertices[0][0], packedVertices[0][1], (float) id, red, green, blue,
                packedVertices[2][0], packedVertices[2][1], (float) id, red, green, blue,
                packedVertices[3][0], packedVertices[3][1], (float) id, red, green, blue,
        };
        verticesStorage.put(id, vertices);

        updateVertices = true;

        return id;
    }

    public static int text(Position pos, String text, Color color) {
        int id = ++lastId;

        float[] normalizedPosition = getPos(pos);
        ArrayList<Float> vertices = FontLoader.genTextVertices(text, normalizedPosition[0], normalizedPosition[1], color, FONT_SIZE, (float) id);
        float[] verticesArray = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            verticesArray[i] = vertices.get(i);
        }
        textVerticesStorage.put(id, verticesArray);

        updateTextVertices = true;

        return id;
    }

    public static float textWidth(String text) {
        return convertToPixels(FontLoader.getFontWidth(text, FONT_SIZE));
    }

    public static float textCenter(String text, int width) {
        return (width - textWidth(text)) / 2;
    }

    public static void forget(int id) {
        if (verticesStorage.remove(id) != null) updateVertices = true;
        if (textVerticesStorage.remove(id) != null) updateTextVertices = true;
    }

    private static void resetIfEmpty() {
        if (normalVertices == 0 && textVertices == 0) {
            lastId = 0;
        }
    }
}
