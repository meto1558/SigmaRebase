package org.lwjgl.opengl;

import net.minecraft.client.Minecraft;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

public class Display {
    // ------------------------------ Variables here ------------------------------

    private static long windowHandle = -1L;

    private static String title = "";
    private static boolean resizable;

    private static DisplayMode displayMode = new DisplayMode(640, 480, 24, 60);

    private static int width;
    private static int height;

    // Used for fullscreen
    private static int lastX;
    private static int lastY;
    private static int lastWidth;
    private static int lastHeight;

    private static long monitor;

    private static boolean windowResized;

    private static GLFWWindowSizeCallback sizeCallback = null;

    private static int samples = 0;

    private static ByteBuffer[] cachedIcons = null;

    private static boolean wayland = false;

    // ------------------------------ Functions here ------------------------------

    public static long getWindowHandle() {
        return windowHandle;
    }

    public static void update() {
        windowResized = false;

        Mouse.poll();
        Keyboard.poll();

        GLFW.glfwSwapBuffers(windowHandle);
        GLFW.glfwPollEvents();
    }

    private static ByteBuffer cloneByteBuffer(ByteBuffer original) {
        // code taken from LWJGL2 Display.java
        ByteBuffer clone = BufferUtils.createByteBuffer(original.capacity());
        int oldPosition = original.position();

        clone.put(original);
        original.position(oldPosition);
        clone.flip();

        return clone;
    }

    private static void resizeCallback(long window, int newWidth, int newHeight) {
        if (window == windowHandle) {
            windowResized = true;

            width = newWidth;
            height = newHeight;
        }
    }

    public static void setTitle(String newTitle) {
        title = newTitle;

        if (isCreated()) {
            GLFW.glfwSetWindowTitle(windowHandle, title);
        }
    }

    public static String getTitle() {
        return title;
    }


    public static void setResizable(boolean isResizable) {
        resizable = isResizable;

        if (isCreated()) {
            GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE,
                    resizable ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE
            );
        }
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public static DisplayMode[] getAvailableDisplayModes() {
        long primaryMonitor = GLFW.glfwGetPrimaryMonitor();

        if (primaryMonitor == MemoryUtil.NULL) {
            return new DisplayMode[]{};
        }

        GLFWVidMode.Buffer videoModes = GLFW.glfwGetVideoModes(primaryMonitor);

        if (videoModes == null) {
            throw new RuntimeException("No video modes found");
        }

        DisplayMode[] modes = new DisplayMode[videoModes.capacity()];

        for (int i = 0; i < videoModes.capacity(); i++) {
            GLFWVidMode mode = videoModes.get(i);

            modes[i] = new DisplayMode(
                    mode.width(), mode.height(),
                    mode.redBits() + mode.blueBits() + mode.greenBits(),
                    mode.refreshRate()
            );
        }

        return modes;
    }

    public static DisplayMode getDesktopDisplayMode() {
        DisplayMode[] displayModes = getAvailableDisplayModes();
        DisplayMode mode;

        if (displayModes.length == 0) {
            mode = null;
        } else {
            DisplayMode maxElement = displayModes[0];

            int maxValue = maxElement.getWidth() * maxElement.getHeight();

            for (DisplayMode element : displayModes) {
                int v$iv = element.getWidth() * element.getHeight();

                if (maxValue < v$iv) {
                    maxElement = element;
                    maxValue = v$iv;
                }
            }

            mode = maxElement;
        }

        return mode;
    }

    private static void destroyWindow() {
        if (sizeCallback != null) {
            sizeCallback.free();
        }

        Mouse.destroy();
        Keyboard.destroy();

        GLFW.glfwDestroyWindow(windowHandle);
    }

    public static void destroy() {
        destroyWindow();

        GLFW.glfwTerminate();
        GLFWErrorCallback callback = GLFW.glfwSetErrorCallback(null);
        if (callback != null) {
            callback.free();
        }
    }

    public static boolean isCreated() {
        return windowHandle != -1;
    }

    public static boolean isCloseRequested() {
        return GLFW.glfwWindowShouldClose(windowHandle);
    }

    public static boolean isActive() {
        return GLFW.glfwGetWindowAttrib(windowHandle, GLFW.GLFW_FOCUSED) == GLFW.GLFW_TRUE;
    }

    public static void sync(int fps) {
        Sync.sync(fps);
    }

    public static void setVSyncEnabled(boolean enabled) {
        GLFW.glfwSwapInterval(enabled ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
    }

    public static boolean wasResized() {
        return windowResized;
    }

    public static void setLocation(int x, int y) {
        GLFW.glfwSetWindowPos(windowHandle, x, y);
    }

    public static void setSamples(int i) {
        samples = i;
    }

    public static void setRawInputEnabled(boolean enabled) {
        GLFW.glfwSetInputMode(windowHandle, GLFW.GLFW_RAW_MOUSE_MOTION,
                enabled ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
    }

    public static boolean isUsingWayland() {
        return wayland;
    }
}
