package com.tiji.elements.display;

import com.tiji.elements.Game;
import com.tiji.elements.core.Color;
import com.tiji.elements.core.Position;
import com.tiji.elements.display.ui.AbstractUI;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL43;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class ScreenDrawer {
    public boolean isUiOpen = false;
    public AbstractUI activeUI;

    final int windowWidth, windowHeight;

    float[] quadVertices = {
            // Position              Tex Coord
            -1.0f, -1.0f, 0f,   0f,  0f,
             1.0f, -1.0f, 0f,   1f,  0f,
             1.0f,  1.0f, 0f,   1f,  1f,
            -1.0f,  1.0f, 0f,   0f,  1f
    };
    int[] quadIndices = {
            0, 1, 2,
            0, 2, 3
    };
    int vao, vbo, ebo;

    ByteBuffer worldTexture;
    int texturePointer;

    int shaderProgram;

    public static final Object uiConstructLock = new Object();
    public static final float[] projectionMatrix = {
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1e-4f, 0,
            0, 0, 0, 1
    };
    public static final FloatBuffer projectionMatrixBuffer = BufferUtils.createFloatBuffer(16);
    public static final int bindingPoint = 0;
    static {
        projectionMatrixBuffer.put(projectionMatrix);
        projectionMatrixBuffer.flip();

        int ubo = GL43.glGenBuffers();
        GL43.glBindBuffer(GL43.GL_UNIFORM_BUFFER, ubo);
        GL43.glBufferData(GL43.GL_UNIFORM_BUFFER, Float.BYTES * 16, GL43.GL_STATIC_DRAW);

        GL43.glBufferSubData(GL43.GL_UNIFORM_BUFFER, 0, projectionMatrixBuffer);

        GL43.glBindBufferBase(GL43.GL_UNIFORM_BUFFER, bindingPoint, ubo);
    }
    public static int makeShaderProgram(String vertexShaderPath, String fragmentShaderPath) {
        int shaderProgram = GL43.glCreateProgram();

        String vertexShaderSource;
        String fragmentShaderSource;
        try (InputStream stream = ScreenDrawer.class.getResourceAsStream(vertexShaderPath)) {
            if (stream == null) throw new RuntimeException("Unable to find vertex shader");
            vertexShaderSource = new String(stream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (InputStream stream = ScreenDrawer.class.getResourceAsStream(fragmentShaderPath)) {
            if (stream == null) throw new RuntimeException("Unable to find fragment shader");
            fragmentShaderSource = new String(stream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int vertexShader = compileShader(GL43.GL_VERTEX_SHADER, vertexShaderSource);
        int fragmentShader = compileShader(GL43.GL_FRAGMENT_SHADER, fragmentShaderSource);

        GL43.glAttachShader(shaderProgram, vertexShader);
        GL43.glAttachShader(shaderProgram, fragmentShader);

        GL43.glLinkProgram(shaderProgram);

        GL43.glDeleteShader(vertexShader);
        GL43.glDeleteShader(fragmentShader);

        GL43.glValidateProgram(shaderProgram);
        if (GL43.glGetProgrami(shaderProgram, GL43.GL_VALIDATE_STATUS) == GL43.GL_FALSE) {
            String error = GL43.glGetProgramInfoLog(shaderProgram);
            throw new RuntimeException("Shader program failed! Error: "+error);
        }

        int blockIndex = GL43.glGetUniformBlockIndex(shaderProgram, "SharedUniform");
        GL43.glUniformBlockBinding(shaderProgram, blockIndex, bindingPoint);

        return shaderProgram;
    }

    public ScreenDrawer(int width, int height) {
        shaderProgram = makeShaderProgram("/shader/element.vert", "/shader/element.frag");

        worldTexture = BufferUtils.createByteBuffer(Game.WIDTH * Game.HEIGHT * 4);

        for (int y = 0; y < Game.HEIGHT; y++) {
            for (int x = 0; x < Game.WIDTH; x++) {
                Color color = Game.world.getElement(new Position(x, y)).displayedColor();
                worldTexture.put((byte) color.red());
                worldTexture.put((byte) color.green());
                worldTexture.put((byte) color.blue());
                worldTexture.put((byte) 255);
            }
        }
        worldTexture.flip();

        texturePointer = GL43.glGenTextures();
        GL43.glBindTexture(GL43.GL_TEXTURE_2D, texturePointer);
        GL43.glTexImage2D(GL43.GL_TEXTURE_2D, 0, GL43.GL_RGBA, Game.WIDTH, Game.HEIGHT, 0, GL43.GL_RGBA, GL43.GL_UNSIGNED_BYTE, worldTexture);
        GL43.glTexParameteri(GL43.GL_TEXTURE_2D, GL43.GL_TEXTURE_MIN_FILTER, GL43.GL_NEAREST);
        GL43.glTexParameteri(GL43.GL_TEXTURE_2D, GL43.GL_TEXTURE_MAG_FILTER, GL43.GL_NEAREST);

        vao = GL43.glGenVertexArrays();
        vbo = GL43.glGenBuffers();
        ebo = GL43.glGenBuffers();

        GL43.glBindVertexArray(vao);

        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(quadVertices.length);
        verticesBuffer.put(quadVertices);
        verticesBuffer.flip();
        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, vbo);
        GL43.glBufferData(GL43.GL_ARRAY_BUFFER, verticesBuffer, GL43.GL_STATIC_DRAW);

        IntBuffer indicesBuffer = BufferUtils.createIntBuffer(quadIndices.length);
        indicesBuffer.put(quadIndices);
        indicesBuffer.flip();
        GL43.glBindBuffer(GL43.GL_ELEMENT_ARRAY_BUFFER, ebo);
        GL43.glBufferData(GL43.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL43.GL_STATIC_DRAW);

        GL43.glVertexAttribPointer(0, 3, GL43.GL_FLOAT, false, 5 * Float.BYTES, 0);
        GL43.glEnableVertexAttribArray(0);
        GL43.glVertexAttribPointer(1, 2, GL43.GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
        GL43.glEnableVertexAttribArray(1);

        this.windowWidth = width;
        this.windowHeight = height;

        FontLoader.loadFont();

        DrawCalls.init(width, height);
    }

    public void draw(long window, int mouseX, int mouseY) {
        GL43.glUseProgram(shaderProgram);

        Position[] diff = Game.world.pollDiff();
        worldTexture.limit(worldTexture.capacity());
        for (Position position : diff) {
            Color color = Game.world.getElement(position).displayedColor();
            int index = (position.y() * Game.WIDTH + position.x()) * 4;
            worldTexture.put(index, (byte) color.red());
            worldTexture.put(index + 1, (byte) color.green());
            worldTexture.put(index + 2, (byte) color.blue());
            worldTexture.put(index + 3, (byte) 255); // alpha
        }
        if (diff.length > 0) {
            worldTexture.flip();
            GL43.glBindTexture(GL43.GL_TEXTURE_2D, texturePointer);
            GL43.glTexSubImage2D(GL43.GL_TEXTURE_2D, 0, 0, 0, Game.WIDTH, Game.HEIGHT, GL43.GL_RGBA, GL43.GL_UNSIGNED_BYTE, worldTexture);
        }

        GL43.glActiveTexture(GL43.GL_TEXTURE0);
        GL43.glBindTexture(GL43.GL_TEXTURE_2D, texturePointer);

        GL43.glBindVertexArray(vao);
        GL43.glDrawElements(GL43.GL_TRIANGLES, quadIndices.length, GL43.GL_UNSIGNED_INT, 0);

        if (isUiOpen) {
            activeUI.render(mouseX, mouseY, windowWidth, windowHeight);
        }

        synchronized (uiConstructLock) {
            DrawCalls.draw();
        }

        GLFW.glfwSwapBuffers(window);
    }

    public void close() {
        GL43.glDeleteTextures(texturePointer);
    }

    public static int compileShader(int type, String source) {
        int shader = GL43.glCreateShader(type);
        GL43.glShaderSource(shader, source);
        GL43.glCompileShader(shader);

        if (GL43.glGetShaderi(shader, GL43.GL_COMPILE_STATUS) == GL43.GL_FALSE) {
            GL43.glDeleteShader(shader);
            throw new RuntimeException("Failed to compile " + (type == GL43.GL_VERTEX_SHADER ? "vertex" : "fragment") + " shader: " +
                    GL43.glGetShaderInfoLog(shader));
        }

        return shader;
    }
}
