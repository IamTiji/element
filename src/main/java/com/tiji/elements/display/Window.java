package com.tiji.elements.display;

import com.tiji.elements.Game;
import com.tiji.elements.display.ui.BrushEditor;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GLDebugMessageCallback;

import static org.lwjgl.glfw.GLFW.glfwInit;

public class Window {
    long window;
    ScreenDrawer drawer;
    Runnable initAction;

    boolean mouseDown = false;
    double currentX, currentY;

    public Window(Runnable initAction) {
        this.initAction = initAction;

        if (!glfwInit()) throw new IllegalStateException("GLFW initialization failed!");
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE  ,               GLFW.GLFW_TRUE );
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE,               GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, GLFW.GLFW_TRUE );
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT   , GLFW.GLFW_TRUE );

        long monitor     = GLFW.glfwGetPrimaryMonitor();
        GLFWVidMode info = GLFW.glfwGetVideoMode(monitor);
        if (info == null) throw new IllegalStateException("Failed to get video mode!");
        int width        = 960;//info.width();
        int height       = 540;//info.height();

        window = GLFW.glfwCreateWindow(width, height,
                Game.translationHandler.translate("ui.title"),
                0L,
                0);
        if (window == 0) throw new IllegalStateException("Failed to create GLFW window!");

        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1);
        GLFW.glfwShowWindow(window);
        GLFW.glfwPollEvents();

        GL.createCapabilities();

        GL43.glEnable(GL43.GL_DEBUG_OUTPUT);
        GL43.glEnable(GL43.GL_DEBUG_OUTPUT_SYNCHRONOUS);
        GL43.glDebugMessageCallback((source, type, id, severity, length, message, _) -> {
            String readableMessage = GLDebugMessageCallback.getMessage(length, message);
            System.err.printf("%s (type: %s, severity: %s, source: %s) ID: %s\n", readableMessage, type, severity, source, id);
        }, 0);

        Game.DrawScale = width / Game.WIDTH;

        GL43.glViewport(0, 0, width, height);

        drawer = new ScreenDrawer(width, height);

        MouseEventHandler mouse = new MouseEventHandler(drawer);
        KeyboardEventHandler keyboard = new KeyboardEventHandler();

        GLFW.glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
            if (action == GLFW.GLFW_PRESS) {
                mouse.mousePressed((int) currentX, (int) currentY);
                mouseDown = true;
            } else if (action == GLFW.GLFW_RELEASE) {
                mouse.mouseReleased((int) currentX, (int) currentY);
                mouseDown = false;
            }
        });
        GLFW.glfwSetCursorPosCallback(window, (_, xpos, ypos) -> {
            if (mouseDown) {
                mouse.mouseDragged((int) xpos, (int) ypos);
            }
            currentX = xpos;
            currentY = ypos;
        });
        GLFW.glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (action == GLFW.GLFW_PRESS) {
                keyboard.keyPressed(key);
            }
        });
        keyboard.registerKeyAction(GLFW.GLFW_KEY_F1, () -> {
            synchronized (ScreenDrawer.uiConstructLock) {
                if (drawer.isUiOpen) {
                    drawer.isUiOpen = false;
                    drawer.activeUI.close();
                    drawer.activeUI = null;
                } else {
                    drawer.isUiOpen = true;
                    drawer.activeUI = new BrushEditor(width, height);
                }
            }
        });

        loop();
    }

    private void loop() {
        while (!GLFW.glfwWindowShouldClose(window)) {
            drawer.draw(window, (int) currentX, (int) currentY);
            GLFW.glfwPollEvents();
            if (initAction != null) {
                initAction.run();
                initAction = null;
            }
        }
        drawer.close();
        GLFW.glfwDestroyWindow(window);
        System.exit(0);
    }
}
