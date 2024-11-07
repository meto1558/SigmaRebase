/*
 * Copyright (c) 2002-2008 LWJGL Project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'LWJGL' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.lwjgl.input;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.compatibility.LWJGLImplementationUtils;
import org.lwjgl.compatibility.input.InputImplementation;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.Display;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;


/**
 * <br>
 * A raw Mouse interface. This can be used to poll the current state of the
 * mouse buttons, and determine the mouse movement delta since the last poll.
 * <p>
 * n buttons supported, n being a native limit. A scrolly wheel is also
 * supported, if one such is available. Movement is reported as delta from
 * last position or as an absolute position. If the window has been created
 * the absolute position will be clamped to 0 - width | height.
 *
 * @author cix_foo <cix_foo@users.sourceforge.net>
 * @author elias_naur <elias_naur@users.sourceforge.net>
 * @author Brian Matzon <brian@matzon.dk>
 * @version $Revision$
 * $Id$
 */
public class Mouse {
    /**
     * Internal use - event size in bytes
     */
    public static final int EVENT_SIZE = 1 + 1 + 4 + 4 + 4 + 8;

    /**
     * Has the mouse been created?
     */
    private static boolean created;

    /**
     * The mouse buttons status from the last poll
     */
    private static ByteBuffer buttons;

    /**
     * Mouse absolute X position in pixels
     */
    private static int x;

    /**
     * Mouse absolute Y position in pixels
     */
    private static int y;

    /**
     * Mouse absolute X position in pixels without any clipping
     */
    private static int absoluteX;

    /**
     * Mouse absolute Y position in pixels without any clipping
     */
    private static int absoluteY;

    /**
     * Buffer to hold the deltas dx, dy and dwheel
     */
    private static IntBuffer coordBuffer;

    /**
     * Delta X
     */
    private static int dX;

    /**
     * Delta Y
     */
    private static int dY;

    /**
     * Delta Z
     */
    private static int dWheel;

    /**
     * Number of buttons supported by the mouse
     */
    private static int buttonCount = -1;

    /**
     * Does this mouse support a scroll wheel
     */
    private static boolean hasWheel;

    /** The current native cursor, if any */
//    private static Cursor		currentCursor;

    /**
     * Button names. These are set upon create(), to names like BUTTON0, BUTTON1, etc.
     */
    private static String[] buttonName;

    /**
     * hashmap of button names, for fast lookup
     */
    private static final Map<String, Integer> buttonMap = new HashMap<String, Integer>(16);

    /**
     * Lazy initialization
     */
    private static boolean initialized;

    /**
     * The mouse button events from the last read
     */
    private static ByteBuffer readBuffer;

    /**
     * The current mouse event button being examined
     */
    private static int eventButton;

    /**
     * The current state of the button being examined in the event queue
     */
    private static boolean eventState;

    /**
     * The current delta of the mouse in the event queue
     */
    private static int eventDX;
    private static int eventDY;
    private static int eventWheel;
    /**
     * The current absolute position of the mouse in the event queue
     */
    private static int eventX;
    private static int eventY;
    private static long eventNanos;
    /**
     * The position of the mouse it was grabbed at
     */
    private static int grabX;
    private static int grabY;
    /**
     * The last absolute mouse event position (before clipping) for delta computation
     */
    private static int lastEventRawX;
    private static int lastEventRawY;

    /**
     * Buffer size in events
     */
    private static final int BUFFER_SIZE = 50;

    private static boolean isGrabbed;

    private static InputImplementation implementation;

    private static boolean clipMouseCoordinatesToWindow = !getPrivilegedBoolean("org.org.lwjgl.input.Mouse.allowNegativeMouseCoords");

    /**
     * Mouse cannot be constructed.
     */
    private Mouse() {
    }

    public static boolean isClipMouseCoordinatesToWindow() {
        return clipMouseCoordinatesToWindow;
    }

    public static void setClipMouseCoordinatesToWindow(boolean clip) {
        clipMouseCoordinatesToWindow = clip;
    }

    /**
     * Set the position of the cursor. If the cursor is not grabbed,
     * the native cursor is moved to the new position.
     *
     * @param new_x The x coordinate of the new cursor position in OpenGL coordinates relative
     *              to the window origin.
     * @param new_y The y coordinate of the new cursor position in OpenGL coordinates relative
     *              to the window origin.
     */
    public static void setCursorPosition(int new_x, int new_y) {
        if (!isCreated())
            throw new IllegalStateException("Mouse is not created");

        eventX = new_x;
        x = new_x;
        eventY = new_y;
        y = new_y;
        if (!isGrabbed()) {
            // TODO: This is unfortunately not allowed under the wayland protocol however this does not create any major issues.
            if (!Display.isUsingWayland()) {
                implementation.setCursorPosition(x, y);
            }
        } else {
            grabX = new_x;
            grabY = new_y;
        }
    }

    /**
     * Static initialization
     */
    private static void initialize() {
        Sys.initialize();

        // Assign names to all the buttons
        buttonName = new String[16];
        for (int i = 0; i < 16; i++) {
            buttonName[i] = "BUTTON" + i;
            buttonMap.put(buttonName[i], i);
        }

        initialized = true;
    }

    private static void resetMouse() {
        dX = dY = dWheel = 0;
        readBuffer.position(readBuffer.limit());
    }

    static InputImplementation getImplementation() {
        return implementation;
    }

    /**
     * "Create" the mouse with the given custom implementation.	This is used
     * reflectively by AWTInputAdapter.
     *
     * @throws LWJGLException if the mouse could not be created for any reason
     */
    private static void create(InputImplementation impl) throws LWJGLException {
        if (created)
            return;

        if (!initialized)
            initialize();

        implementation = impl;
        implementation.createMouse();
        hasWheel = implementation.hasWheel();
        created = true;

        // set mouse buttons
        buttonCount = implementation.getButtonCount();
        buttons = BufferUtils.createByteBuffer(buttonCount);
        coordBuffer = BufferUtils.createIntBuffer(3);

        readBuffer = ByteBuffer.allocate(EVENT_SIZE * BUFFER_SIZE);
        readBuffer.limit(0);
        setGrabbed(isGrabbed);
    }

    /**
     * "Create" the mouse. The display must first have been created.
     * Initially, the mouse is not grabbed and the delta values are reported
     * with respect to the center of the display.
     *
     * @throws LWJGLException if the mouse could not be created for any reason
     */
    public static void create() throws LWJGLException {
        if (!Display.isCreated()) throw new IllegalStateException("Display must be created.");

        create(LWJGLImplementationUtils.getOrCreateInputImplementation());
    }

    /**
     * @return true if the mouse has been created
     */
    public static boolean isCreated() {
        return created;
    }

    /**
     * "Destroy" the mouse.
     */
    public static void destroy() {
        if (!created)
            return;

        created = false;
        buttons = null;
        coordBuffer = null;

        implementation.destroyMouse();
    }

    // TODO: Fix chest stealer thing..... (first join)

    /**
     * Polls the mouse for its current state. Access the polled values using the
     * get<value> methods.
     * By using this method, it is possible to "miss" mouse click events if you don't
     * poll fast enough.
     * <p>
     * To use buffered values, you have to call <code>next</code> for each event you
     * want to read. You can query which button caused the event by using
     * <code>getEventButton</code>. To get the state of that button, for that event, use
     * <code>getEventButtonState</code>.
     * <p>
     * NOTE: This method does not query the operating system for new events. To do that,
     * Display.processMessages() (or Display.update()) must be called first.
     *
     * @see Mouse#next()
     * @see Mouse#getEventButton()
     * @see Mouse#getEventButtonState()
     * @see Mouse#isButtonDown(int button)
     * @see Mouse#getX()
     * @see Mouse#getY()
     * @see Mouse#getDX()
     * @see Mouse#getDY()
     * @see Mouse#getDWheel()
     */
    public static void poll() {
        if (!created)
            throw new IllegalStateException("Mouse must be created before you can poll it");

        if (isFixClip()) {
            return;
        }

        implementation.pollMouse(coordBuffer, buttons);

        int pollX = coordBuffer.get(0);
        int pollY = coordBuffer.get(1);
        int pollWheel = coordBuffer.get(2);

        if (isGrabbed()) {
            dX += pollX;
            dY += pollY;

            x += pollX;
            y += pollY;

            absoluteX += pollX;
            absoluteY += pollY;
        } else {
            dX = pollX - absoluteX;
            dY = pollY - absoluteY;

            absoluteX = x = pollX;
            absoluteY = y = pollY;
        }

        if (clipMouseCoordinatesToWindow) {
            x = Math.min(Display.getWidth() - 1, Math.max(0, x));
            y = Math.min(Display.getHeight() - 1, Math.max(0, y));
        }

        dWheel += pollWheel;

        read();
    }

    private static void read() {
        readBuffer.compact();
        implementation.readMouse(readBuffer);
        readBuffer.flip();
    }

    /**
     * See if a particular mouse button is down.
     *
     * @param button The index of the button you wish to test (0..getButtonCount-1)
     * @return true if the specified button is down
     */
    public static boolean isButtonDown(int button) {
        if (!created)
            throw new IllegalStateException("Mouse must be created before you can poll the button state");

        if (button >= buttonCount || button < 0)
            return false;
        else
            return buttons.get(button) == 1;
    }

    /**
     * Gets a button's name
     *
     * @param button The button
     * @return a String with the button's human readable name in it or null if the button is unnamed
     */
    public static String getButtonName(int button) {
        if (button >= buttonName.length || button < 0)
            return null;
        else
            return buttonName[button];
    }

    /**
     * Get's a button's index. If the button is unrecognised then -1 is returned.
     *
     * @param buttonName The button name
     */
    public static int getButtonIndex(String buttonName) {
        Integer ret = buttonMap.get(buttonName);

        if (ret == null)
            return -1;
        else
            return ret;
    }

    /**
     * Gets the next mouse event. You can query which button caused the event by using
     * <code>getEventButton()</code> (if any). To get the state of that key, for that event, use
     * <code>getEventButtonState</code>. To get the current mouse delta values use <code>getEventDX()</code>
     * and <code>getEventDY()</code>.
     *
     * @return true if a mouse event was read, false otherwise
     * @see Mouse#getEventButton()
     * @see Mouse#getEventButtonState()
     */
    public static boolean next() {
        if (!created)
            throw new IllegalStateException("Mouse must be created before you can read events");

        if (isFixClip()) {
            setFixClip(false);
            return false;
        }

        if (readBuffer.hasRemaining()) {
            eventButton = readBuffer.get();
            eventState = readBuffer.get() != 0;

            if (isGrabbed()) {
                eventDX = readBuffer.getInt();
                eventDY = readBuffer.getInt();

                eventX += eventDX;
                eventY += eventDY;

                lastEventRawX = eventX;
                lastEventRawY = eventY;
            } else {
                int newEventX = readBuffer.getInt();
                int newEventY = readBuffer.getInt();

                eventDX = newEventX - lastEventRawX;
                eventDY = newEventY - lastEventRawY;

                eventX = newEventX;
                eventY = newEventY;

                lastEventRawX = newEventX;
                lastEventRawY = newEventY;
            }

            if (clipMouseCoordinatesToWindow) {
                eventX = Math.min(Display.getWidth() - 1, Math.max(0, eventX));
                eventY = Math.min(Display.getHeight() - 1, Math.max(0, eventY));
            }

            eventWheel = readBuffer.getInt();
            eventNanos = readBuffer.getLong();

            return true;
        } else
            return false;
    }

    /**
     * @return Current events button. Returns -1 if no button state was changed
     */
    public static int getEventButton() {
        return eventButton;
    }

    /**
     * Get the current events button state.
     *
     * @return Current events button state.
     */
    public static boolean getEventButtonState() {
        return eventState;
    }

    /**
     * @return Current events delta x.
     */
    public static int getEventDX() {
        return eventDX;
    }

    /**
     * @return Current events delta y.
     */
    public static int getEventDY() {
        return eventDY;
    }

    /**
     * @return Current events absolute x.
     */
    public static int getEventX() {
        return eventX;
    }

    /**
     * @return Current events absolute y.
     */
    public static int getEventY() {
        return eventY;
    }

    /**
     * @return Current events delta z
     */
    public static int getEventDWheel() {
        return eventWheel;
    }

    /**
     * Gets the time in nanoseconds of the current event.
     * Only useful for relative comparisons with other
     * Mouse events, as the absolute time has no defined
     * origin.
     *
     * @return The time in nanoseconds of the current event
     */
    public static long getEventNanoseconds() {
        return eventNanos;
    }

    /**
     * Retrieves the absolute position. It will be clamped to
     * 0...width-1.
     *
     * @return Absolute x axis position of mouse
     */
    public static int getX() {
        return x;
    }

    /**
     * Retrieves the absolute position. It will be clamped to
     * 0...height-1.
     *
     * @return Absolute y axis position of mouse
     */
    public static int getY() {
        return y;
    }

    /**
     * @return Movement on the x axis since last time getDX() was called.
     */
    public static int getDX() {
        int result = dX;
        dX = 0;
        return result;
    }

    /**
     * @return Movement on the y axis since last time getDY() was called.
     */
    public static int getDY() {
        int result = dY;
        dY = 0;
        return result;
    }

    /**
     * @return Movement of the wheel since last time getDWheel() was called
     */
    public static int getDWheel() {
        int result = dWheel;
        dWheel = 0;
        return result;
    }

    /**
     * @return Number of buttons on this mouse
     */
    public static int getButtonCount() {
        return buttonCount;
    }

    /**
     * @return Whether or not this mouse has wheel support
     */
    public static boolean hasWheel() {
        return hasWheel;
    }

    /**
     * @return whether or not the mouse has grabbed the cursor
     */
    public static boolean isGrabbed() {
        return isGrabbed;
    }

    /**
     * Sets whether or not the mouse has grabbed the cursor
     * (and thus hidden). If grab is false, the getX() and getY()
     * will return delta movement in pixels clamped to the display
     * dimensions, from the center of the display.
     *
     * @param grab whether the mouse should be grabbed
     */
    public static void setGrabbed(boolean grab) {
        if (isFixClip()) {
            grab = true;
        }

        boolean grabbed = isGrabbed;

        isGrabbed = grab;

        if (isCreated()) {

            if (grab && !grabbed) {
                grabX = x;
                grabY = y;
            } else if (!grab && grabbed) {
                implementation.setCursorPosition(grabX, grabY);
            }

            implementation.grabMouse(grab);

            poll();
            eventX = x;
            eventY = y;
            lastEventRawX = x;
            lastEventRawY = y;
            resetMouse();
        }
    }

    /**
     * Gets a boolean property as a privileged action.
     */
    public static boolean getPrivilegedBoolean(final String property_name) {
        return Boolean.getBoolean(property_name);
    }

    /**
     * Retrieves whether or not the mouse cursor is within the bounds of the window.
     * If the mouse cursor was moved outside the display during a drag, then the result of calling
     * this method will be true until the button is released.
     *
     * @return true if mouse is inside display, false otherwise.
     */
    public static boolean isInsideWindow() {
        return implementation.isInsideWindow();
    }


    private static boolean ignoreFirstMove;

    public static boolean isIgnoreFirstMove() {
        return ignoreFirstMove;
    }

    public static void setIgnoreFirstMove(boolean state) {
        ignoreFirstMove = state;
    }


    private static boolean fixClip;

    public static boolean isFixClip() {
        return fixClip;
    }

    public static void setFixClip(boolean state) {
        fixClip = state;
    }


    public static void setRawInput(boolean state) {
        if (GLFW.glfwRawMouseMotionSupported() &&
                !Mouse.getPrivilegedBoolean("org.org.lwjgl.input.Mouse.disableRawInput")) {
            GLFW.glfwSetInputMode(Display.getWindowHandle(), GLFW.GLFW_RAW_MOUSE_MOTION,
                    state ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        }
    }
}