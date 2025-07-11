package com.tiji.elements.display;

import com.tiji.elements.Game;
import com.tiji.elements.core.Chunk;
import com.tiji.elements.core.Color;
import com.tiji.elements.core.Element;
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
import java.nio.ShortBuffer;

public class ScreenDrawer {
    public boolean isUiOpen = false;
    public AbstractUI activeUI;

    static int windowWidth, windowHeight;

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
    int vao, vbo, ebo, fbo;
    int glow1PassTexture, glow0PassTexture;
    int debugTexture;

    ByteBuffer worldTexture;
    ByteBuffer glowWorldTexture;
    ByteBuffer debugDataBuffer;
    int texturePointer;

    int shaderProgram, glowShaderProgram, debugShaderProgram;

    public static final Object uiConstructLock = new Object();
    public static final float[] projectionMatrix = {
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, -1e-4f, 0,
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
        GL43.glProgramUniform1i(shaderProgram, GL43.glGetUniformLocation(shaderProgram, "width"), windowWidth);
        GL43.glProgramUniform1i(shaderProgram, GL43.glGetUniformLocation(shaderProgram, "height"), windowHeight);

        return shaderProgram;
    }

    public ScreenDrawer(int width, int height) {
        windowWidth = width;
        windowHeight = height;

        shaderProgram = makeShaderProgram("/shader/element.vert", "/shader/element.frag");
        glowShaderProgram = makeShaderProgram("/shader/glow.vert", "/shader/glow.frag");
        debugShaderProgram = makeShaderProgram("/shader/debug.vert", "/shader/debug.frag");

        worldTexture = BufferUtils.createByteBuffer(Game.WIDTH * Game.HEIGHT * 4);
        glowWorldTexture = BufferUtils.createByteBuffer(Game.WIDTH * Game.HEIGHT * 4);
        if (Game.isDevelopment) debugDataBuffer = BufferUtils.createByteBuffer(Game.WIDTH * Game.HEIGHT * 4);

        for (int y = 0; y < Game.HEIGHT; y++) {
            for (int x = 0; x < Game.WIDTH; x++) {
                Element element = Game.world.getElement(new Position(x, y));
                Color color = element.displayedColor();
                worldTexture.put((byte) color.red());
                worldTexture.put((byte) color.green());
                worldTexture.put((byte) color.blue());
                worldTexture.put((byte) 255);

                Color glow = element.getGlowColor();
                glowWorldTexture.put((byte) glow.red());
                glowWorldTexture.put((byte) glow.green());
                glowWorldTexture.put((byte) glow.blue());
                glowWorldTexture.put((byte) element.getGlowIntensity());

                if (Game.isDevelopment) {
                    debugDataBuffer.put((byte) 0);
                    debugDataBuffer.put((byte) (Game.world.getDebugChunk(x, y).state.equals(Chunk.ChunkState.UPDATED) ? 255 : 0));
                    debugDataBuffer.put((byte) (Game.world.getDebugChunk(x, y).state.equals(Chunk.ChunkState.RUNNING) ? 255 : 0));
                    debugDataBuffer.put((byte) 127);
                }
            }
        }
        worldTexture.flip();
        glowWorldTexture.flip();

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

        glow1PassTexture = GL43.glGenTextures();
        GL43.glBindTexture(GL43.GL_TEXTURE_2D, glow1PassTexture);
        GL43.glTexParameteri(GL43.GL_TEXTURE_2D, GL43.GL_TEXTURE_WRAP_T, GL43.GL_CLAMP_TO_EDGE);
        GL43.glTexParameteri(GL43.GL_TEXTURE_2D, GL43.GL_TEXTURE_WRAP_S, GL43.GL_CLAMP_TO_EDGE);
        GL43.glTexParameteri(GL43.GL_TEXTURE_2D, GL43.GL_TEXTURE_MIN_FILTER, GL43.GL_NEAREST);
        GL43.glTexParameteri(GL43.GL_TEXTURE_2D, GL43.GL_TEXTURE_MAG_FILTER, GL43.GL_NEAREST);
        GL43.glTexImage2D(GL43.GL_TEXTURE_2D, 0, GL43.GL_RGBA, width, height, 0, GL43.GL_RGBA, GL43.GL_UNSIGNED_BYTE, (ByteBuffer) null);

        glow0PassTexture = GL43.glGenTextures();
        GL43.glBindTexture(GL43.GL_TEXTURE_2D, glow0PassTexture);
        GL43.glTexParameteri(GL43.GL_TEXTURE_2D, GL43.GL_TEXTURE_WRAP_S, GL43.GL_CLAMP_TO_EDGE);
        GL43.glTexParameteri(GL43.GL_TEXTURE_2D, GL43.GL_TEXTURE_WRAP_T, GL43.GL_CLAMP_TO_EDGE);
        GL43.glTexParameteri(GL43.GL_TEXTURE_2D, GL43.GL_TEXTURE_MIN_FILTER, GL43.GL_NEAREST);
        GL43.glTexParameteri(GL43.GL_TEXTURE_2D, GL43.GL_TEXTURE_MAG_FILTER, GL43.GL_NEAREST);
        GL43.glTexImage2D(GL43.GL_TEXTURE_2D, 0, GL43.GL_RGBA, Game.WIDTH, Game.HEIGHT, 0, GL43.GL_RGBA, GL43.GL_UNSIGNED_BYTE, glowWorldTexture);

        fbo = GL43.glGenFramebuffers();
        GL43.glBindFramebuffer(GL43.GL_FRAMEBUFFER, fbo);
        GL43.glFramebufferTexture2D(GL43.GL_FRAMEBUFFER, GL43.GL_COLOR_ATTACHMENT0, GL43.GL_TEXTURE_2D, glow1PassTexture, 0);

        if (GL43.glCheckFramebufferStatus(GL43.GL_FRAMEBUFFER) != GL43.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Frame buffer object is not complete!");
        }

        GL43.glBindFramebuffer(GL43.GL_FRAMEBUFFER, 0);

        if (Game.isDevelopment) {
            debugDataBuffer.flip();

            debugTexture = GL43.glGenTextures();
            GL43.glBindTexture(GL43.GL_TEXTURE_2D, debugTexture);
            GL43.glTexImage2D(GL43.GL_TEXTURE_2D, 0, GL43.GL_RGBA, Game.WIDTH, Game.HEIGHT, 0, GL43.GL_RGBA, GL43.GL_SHORT, debugDataBuffer);
            GL43.glTexParameteri(GL43.GL_TEXTURE_2D, GL43.GL_TEXTURE_MIN_FILTER, GL43.GL_NEAREST);
            GL43.glTexParameteri(GL43.GL_TEXTURE_2D, GL43.GL_TEXTURE_MAG_FILTER, GL43.GL_NEAREST);
        }

        FontLoader.loadFont(width, height);

        DrawCalls.init(width, height);
    }

    public void draw(long window, int mouseX, int mouseY) {
        GL43.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GL43.glClearDepth(0f);
        GL43.glClear(GL43.GL_COLOR_BUFFER_BIT | GL43.GL_DEPTH_BUFFER_BIT);

        GL43.glUseProgram(shaderProgram);

        Position[] diff = Game.world.pollDiff();
        worldTexture.limit(worldTexture.capacity());
        glowWorldTexture.limit(glowWorldTexture.capacity());
        for (Position position : diff) {
            Element element = Game.world.getElement(position);
            int index = (position.y() * Game.WIDTH + position.x()) * 4;

            Color color = element.displayedColor();
            worldTexture.put(index, (byte) color.red());
            worldTexture.put(index + 1, (byte) color.green());
            worldTexture.put(index + 2, (byte) color.blue());
            worldTexture.put(index + 3, (byte) 255); // alpha

            Color glow = element.getGlowColor();
            glowWorldTexture.put(index, (byte) glow.red());
            glowWorldTexture.put(index + 1, (byte) glow.green());
            glowWorldTexture.put(index + 2, (byte) glow.blue());
            glowWorldTexture.put(index + 3, (byte) element.getGlowIntensity());
        }
        if (diff.length > 0) {
            worldTexture.flip();
            glowWorldTexture.flip();

            GL43.glBindTexture(GL43.GL_TEXTURE_2D, texturePointer);
            GL43.glTexSubImage2D(GL43.GL_TEXTURE_2D, 0, 0, 0, Game.WIDTH, Game.HEIGHT, GL43.GL_RGBA, GL43.GL_UNSIGNED_BYTE, worldTexture);

            GL43.glBindTexture(GL43.GL_TEXTURE_2D, glow0PassTexture);
            GL43.glTexSubImage2D(GL43.GL_TEXTURE_2D, 0, 0, 0, Game.WIDTH, Game.HEIGHT, GL43.GL_RGBA, GL43.GL_UNSIGNED_BYTE, glowWorldTexture);

        }
        if (Game.isDevelopment) {
            debugDataBuffer.limit(debugDataBuffer.capacity());
            for (int x = 0; x < Game.WIDTH; x++) {
                for (int y = 0; y < Game.HEIGHT; y++) {
                    int index = (y * Game.WIDTH + x) * 4;
                    debugDataBuffer.put(index, (byte) 0);
                    debugDataBuffer.put(index + 1, (byte) (Game.world.getDebugChunk(x, y).state.equals(Chunk.ChunkState.UPDATED) ? 255 : 0));
                    debugDataBuffer.put(index + 2, (byte) (Game.world.getDebugChunk(x, y).state.equals(Chunk.ChunkState.RUNNING) ? 255 : 0));
                    debugDataBuffer.put(index + 3, (byte) 127);
                }
            }
            GL43.glBindTexture(GL43.GL_TEXTURE_2D, debugTexture);
            GL43.glTexSubImage2D(GL43.GL_TEXTURE_2D, 0, 0, 0, Game.WIDTH, Game.HEIGHT, GL43.GL_RGBA, GL43.GL_UNSIGNED_BYTE, debugDataBuffer);
        }

        GL43.glUniform1i(GL43.glGetUniformLocation(shaderProgram, "id"), 0);

        GL43.glActiveTexture(GL43.GL_TEXTURE0);
        GL43.glBindTexture(GL43.GL_TEXTURE_2D, texturePointer);

        GL43.glBindVertexArray(vao);
        GL43.glDrawElements(GL43.GL_TRIANGLES, quadIndices.length, GL43.GL_UNSIGNED_INT, 0);


        GL43.glUseProgram(glowShaderProgram);

        GL43.glUniform1i(GL43.glGetUniformLocation(glowShaderProgram, "texture_"), 0);

        GL43.glUniform1i(GL43.glGetUniformLocation(glowShaderProgram, "blurDir"), 0);

        GL43.glBindFramebuffer(GL43.GL_DRAW_FRAMEBUFFER, fbo);
        GL43.glDrawBuffer(GL43.GL_COLOR_ATTACHMENT0);
        GL43.glClear(GL43.GL_COLOR_BUFFER_BIT);

        GL43.glActiveTexture(GL43.GL_TEXTURE0);
        GL43.glBindTexture(GL43.GL_TEXTURE_2D, glow0PassTexture);

        GL43.glBindVertexArray(vao);
        GL43.glDrawElements(GL43.GL_TRIANGLES, quadIndices.length, GL43.GL_UNSIGNED_INT, 0);

        GL43.glBindFramebuffer(GL43.GL_DRAW_FRAMEBUFFER, 0);

        GL43.glEnable(GL43.GL_BLEND);
        GL43.glBlendFunc(GL43.GL_ONE, GL43.GL_ONE_MINUS_SRC_ALPHA);

        GL43.glUniform1i(GL43.glGetUniformLocation(glowShaderProgram, "blurDir"), 1);

        GL43.glActiveTexture(GL43.GL_TEXTURE0);
        GL43.glBindTexture(GL43.GL_TEXTURE_2D, glow1PassTexture);

        GL43.glDrawElements(GL43.GL_TRIANGLES, quadIndices.length, GL43.GL_UNSIGNED_INT, 0);


        if (Game.isDevelopment) {
            GL43.glUseProgram(debugShaderProgram);
            GL43.glUniform1i(GL43.glGetUniformLocation(debugShaderProgram, "texture_"), 0);

            GL43.glBlendFunc(GL43.GL_SRC_ALPHA, GL43.GL_ONE);

            GL43.glActiveTexture(GL43.GL_TEXTURE0);
            GL43.glBindTexture(GL43.GL_TEXTURE_2D, debugTexture);

            GL43.glBindVertexArray(vao);
            GL43.glDrawElements(GL43.GL_TRIANGLES, quadIndices.length, GL43.GL_UNSIGNED_INT, 0);
        }

        GL43.glDisable(GL43.GL_BLEND);


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
        GL43.glDeleteVertexArrays(vao);
        GL43.glDeleteBuffers(vbo);
        GL43.glDeleteBuffers(ebo);
        GL43.glDeleteFramebuffers(fbo);
        GL43.glDeleteProgram(shaderProgram);
        GL43.glDeleteProgram(glowShaderProgram);
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
