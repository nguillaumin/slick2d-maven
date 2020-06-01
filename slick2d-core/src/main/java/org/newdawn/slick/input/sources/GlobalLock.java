package org.newdawn.slick.input.sources;

/**
 * This class contains the global lock that LWJGL will use to
 * synchronize access to Display.
 */
final class GlobalLock {
    static final Object lock = new Object();
}
