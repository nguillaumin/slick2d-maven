package org.newdawn.slick.input.sources;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public interface InputImplementation {
    /*
     * Mouse methods
     */
    /** Query of wheel support */
    boolean hasWheel();

    /** Query of button count */
    int getButtonCount();

    /**
     * Method to create the mouse.
     */
    void createMouse();

    /**
     * Method the destroy the mouse
     */
    void destroyMouse();

    /**
     * Method to poll the mouse
     */
    void pollMouse(IntBuffer coord_buffer, ByteBuffer buttons);

    /**
     * Method to read the keyboard buffer
     */
    void readMouse(ByteBuffer buffer);

    void grabMouse(boolean grab);

    /**
     * Function to determine native cursor support
     */
    int getNativeCursorCapabilities();

    /** Method to set the native cursor position */
    void setCursorPosition(int x, int y);

    /** Method to set the native cursor */
    void setNativeCursor(Object handle);

    /** Method returning the minimum cursor size */
    int getMinCursorSize();

    /** Method returning the maximum cursor size */
    int getMaxCursorSize();

    /*
     * Keyboard methods
     */

    /**
     * Method to create the keyboard
     */
    void createKeyboard();

    /**
     * Method to destroy the keyboard
     */
    void destroyKeyboard();

    /**
     * Method to poll the keyboard.
     *
     * @param keyDownBuffer the address of a 256-byte buffer to place
     * key states in.
     */
    void pollKeyboard(ByteBuffer keyDownBuffer);

    /**
     * Method to read the keyboard buffer
     */
    void readKeyboard(ByteBuffer buffer);

//	int isStateKeySet(int key);

    /** Native cursor handles */
    Object createCursor(int width, int height, int xHotspot, int yHotspot, int numImages, IntBuffer images, IntBuffer delays);

    void destroyCursor(Object cursor_handle);

    int getWidth();

    int getHeight();

    boolean isInsideWindow();
}
