package org.newdawn.slick.input.sources.keyboard;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.newdawn.slick.input.sources.GlfwPackageAccess;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.newdawn.slick.GameContainer.GAME_WINDOW;
import static org.newdawn.slick.input.sources.keyboard.KeyBindings.KEYBOARD_SIZE;

// FIXME setup glfw key listeners
public abstract class Keyboard {

//    static String getKeyName(int code) {
//        return GLFW.glfwGetKeyName(GLFW.GLFW_KEY_UNKNOWN, code);
//    }
//
//    static boolean isKeyDown(int code) {
//        synchronized (GlfwPackageAccess.global_lock) {
//            if (!created)
//                throw new IllegalStateException("Keyboard must be created before you can query key state");
//            return keyDownBuffer.get(key) != 0;
//        }
//        return GLFW.glfwGetKey(GAME_WINDOW, code) == GLFW.GLFW_PRESS;
//    }

    /** Internal use - event size in bytes */
    public static final int EVENT_SIZE = 4 + 1 + 4 + 8 + 1;

    /** Buffer size in events */
    private static final int BUFFER_SIZE = 50;

    /** Key names */
    private static final String[] keyName = new String[KEYBOARD_SIZE];
    private static final Map<String, Integer> keyMap = new HashMap<String, Integer>(253);
    private static int counter;

    static {
        // Use reflection to find out key names
        Field[] fields = Keyboard.class.getFields();
        try {
            for ( Field field : fields ) {
                if ( Modifier.isStatic(field.getModifiers())
                        && Modifier.isPublic(field.getModifiers())
                        && Modifier.isFinal(field.getModifiers())
                        && field.getType().equals(int.class)
                        && field.getName().startsWith("KEY_")
                        && !field.getName().endsWith("WIN") ) { /* Don't use deprecated names */

                    int key = field.getInt(null);
                    String name = field.getName().substring(4);
                    keyName[key] = name;
                    keyMap.put(name, key);
                    counter++;
                }

            }
        } catch (Exception e) {
        }

    }

    /** The number of keys supported */
    private static final int keyCount = counter;

    /** Has the keyboard been created? */
    private static boolean created = true;

    /** Are repeat events enabled? */
    private static boolean repeat_enabled;

    /** The keys status from the last poll */
    private static final ByteBuffer keyDownBuffer = BufferUtils.createByteBuffer(KEYBOARD_SIZE);

    /**
     * The key events from the last read: a sequence of pairs of key number,
     * followed by state. The state is followed by
     * a 4 byte code point representing the translated character.
     */
    private static ByteBuffer readBuffer;

    /** current event */
    private static KeyEvent current_event = new KeyEvent();

    /** scratch event */
    private static KeyEvent tmp_event = new KeyEvent();

    /**
     * Keyboard cannot be constructed.
     */
    private Keyboard() {
    }

    /**
     * "Create" the keyboard. The display must first have been created. The
     * reason for this is so the keyboard has a window to "focus" in.
     *
     */
    public static void create()  {
        synchronized (GlfwPackageAccess.global_lock) {
            if (GAME_WINDOW == -1L) throw new IllegalStateException("Display must be created.");

            // FIXME if really need to create implementation
//            create(GlfwPackageAccess.createImplementation());
        }
    }

    private static void reset() {
        readBuffer.limit(0);
        for (int i = 0; i < keyDownBuffer.remaining(); i++)
            keyDownBuffer.put(i, (byte)0);
        current_event.reset();
    }

    /**
     * @return true if the keyboard has been created
     */
    public static boolean isCreated() {
        synchronized (GlfwPackageAccess.global_lock) {
            return created;
        }
    }

    /**
     * "Destroy" the keyboard
     */
    public static void destroy() {
        synchronized (GlfwPackageAccess.global_lock) {
            if (!created)
                return;
            created = false;
            reset();
        }
    }

    public static void poll() {
        synchronized (GlfwPackageAccess.global_lock) {
            if (!created)
                throw new IllegalStateException("Keyboard must be created before you can poll the device");
            GLFW.glfwPollEvents();
//            read();
        }
    }

    /**
     * Checks to see if a key is down.
     * @param key Keycode to check
     * @return true if the key is down according to the last poll()
     */
    public static boolean isKeyDown(int key) {
        synchronized (GlfwPackageAccess.global_lock) {
            if (!created)
                throw new IllegalStateException("Keyboard must be created before you can query key state");
            return keyDownBuffer.get(key) != 0;
        }
    }

    public static synchronized String getKeyName(int key) {
        return keyName[key];
    }

    /**
     * Gets the next keyboard event. You can query which key caused the event by using
     * <code>getEventKey</code>. To get the state of that key, for that event, use
     * <code>getEventKeyState</code> - finally use <code>getEventCharacter</code> to get the
     * character for that event.
     *
     * @see Keyboard#getEventKey()
     * @see Keyboard#getEventKeyState()
     * @see Keyboard#getEventCharacter()
     * @return true if a keyboard event was read, false otherwise
     */
    public static boolean next() {
        synchronized (GlfwPackageAccess.global_lock) {
            boolean result;
            while ((result = readNext(current_event)) && current_event.repeat && !repeat_enabled) ;
            return result;
        }
    }

    public static void enableRepeatEvents(boolean enable) {
        synchronized (GlfwPackageAccess.global_lock) {
            repeat_enabled = enable;
        }
    }

    public static boolean areRepeatEventsEnabled() {
        synchronized (GlfwPackageAccess.global_lock) {
            return repeat_enabled;
        }
    }

    private static boolean readNext(KeyEvent event) {
        if (readBuffer.hasRemaining()) {
            event.key = readBuffer.getInt() & 0xFF;
            event.state = readBuffer.get() != 0;
            event.character = readBuffer.getInt();
            event.nanos = readBuffer.getLong();
            event.repeat = readBuffer.get() == 1;
            return true;
        } else
            return false;
    }

    public static int getKeyCount() {
        return keyCount;
    }

    public static char getEventCharacter() {
        synchronized (GlfwPackageAccess.global_lock) {
            return (char)current_event.character;
        }
    }

    public static int getEventKey() {
        synchronized (GlfwPackageAccess.global_lock) {
            return current_event.key;
        }
    }

    public static boolean getEventKeyState() {
        synchronized (GlfwPackageAccess.global_lock) {
            return current_event.state;
        }
    }

    public static long getEventNanoseconds() {
        synchronized (GlfwPackageAccess.global_lock) {
            return current_event.nanos;
        }
    }

    public static boolean isRepeatEvent() {
        synchronized (GlfwPackageAccess.global_lock) {
            return current_event.repeat;
        }
    }

    private static final class KeyEvent {
        /** The current keyboard character being examined */
        private int character;

        /** The current keyboard event key being examined */
        private int key;

        /** The current state of the key being examined in the event queue */
        private boolean state;

        /** The current event time */
        private long nanos;

        /** Is the current event a repeated event? */
        private boolean repeat;

        private void reset() {
            character = 0;
            key = 0;
            state = false;
            repeat = false;
        }
    }
}
