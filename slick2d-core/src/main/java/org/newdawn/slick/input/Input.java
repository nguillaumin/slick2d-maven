package org.newdawn.slick.input;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.newdawn.slick.*;
import org.newdawn.slick.input.sources.controller.Controller;
import org.newdawn.slick.input.sources.controller.Controllers;
import org.newdawn.slick.input.sources.keyboard.Keyboard;
import org.newdawn.slick.input.sources.mouse.Mouse;
import org.newdawn.slick.util.Log;

import static org.newdawn.slick.GameContainer.GAME_WINDOW;

/**
 * A wrapped for all keyboard, mouse and controller input
 *
 * @author kevin
 * @author tyler
 */
public class Input {

	private static final Log LOG = new Log(Input.class);

	/** The controller index to pass to check all controllers */
	public static final int ANY_CONTROLLER = -1;
	
	/** The maximum number of buttons on controllers */
	private static final int MAX_BUTTONS = 100;
	
	public static final int KEY_ESCAPE          = GLFW.GLFW_KEY_ESCAPE;
	public static final int KEY_1               = GLFW.GLFW_KEY_1;
	public static final int KEY_2               = GLFW.GLFW_KEY_2;
	public static final int KEY_3               = GLFW.GLFW_KEY_3;
	public static final int KEY_4               = GLFW.GLFW_KEY_4;
	public static final int KEY_5               = GLFW.GLFW_KEY_5;
	public static final int KEY_6               = GLFW.GLFW_KEY_6;
	public static final int KEY_7               = GLFW.GLFW_KEY_7;
	public static final int KEY_8               = GLFW.GLFW_KEY_8;
	public static final int KEY_9               = GLFW.GLFW_KEY_9;
	public static final int KEY_0               = GLFW.GLFW_KEY_0;
	public static final int KEY_MINUS           = GLFW.GLFW_KEY_MINUS; /* - on main keyboard */
	public static final int KEY_EQUALS          = GLFW.GLFW_KEY_EQUAL;
	public static final int KEY_BACK            = GLFW.GLFW_KEY_BACKSPACE; /* backspace */
	public static final int KEY_TAB             = GLFW.GLFW_KEY_TAB;
	public static final int KEY_Q               = GLFW.GLFW_KEY_Q;
	public static final int KEY_W               = GLFW.GLFW_KEY_W;
	public static final int KEY_E               = GLFW.GLFW_KEY_E;
	public static final int KEY_R               = GLFW.GLFW_KEY_R;
	public static final int KEY_T               = GLFW.GLFW_KEY_T;
	public static final int KEY_Y               = GLFW.GLFW_KEY_Y;
	public static final int KEY_U               = GLFW.GLFW_KEY_U;
	public static final int KEY_I               = GLFW.GLFW_KEY_I;
	public static final int KEY_O               = GLFW.GLFW_KEY_O;
	public static final int KEY_P               = GLFW.GLFW_KEY_P;
	public static final int KEY_LBRACKET        = GLFW.GLFW_KEY_LEFT_BRACKET;
	public static final int KEY_RBRACKET        = GLFW.GLFW_KEY_RIGHT_BRACKET;
	public static final int KEY_RETURN          = GLFW.GLFW_KEY_ENTER; /* Enter on main keyboard */
	public static final int KEY_ENTER           = GLFW.GLFW_KEY_ENTER; /* Enter on main keyboard */
	public static final int KEY_LCONTROL        = GLFW.GLFW_KEY_LEFT_CONTROL;
	public static final int KEY_A               = GLFW.GLFW_KEY_A;
	public static final int KEY_S               = GLFW.GLFW_KEY_S;
	public static final int KEY_D               = GLFW.GLFW_KEY_D;
	public static final int KEY_F               = GLFW.GLFW_KEY_F;
	public static final int KEY_G               = GLFW.GLFW_KEY_G;
	public static final int KEY_H               = GLFW.GLFW_KEY_H;
	public static final int KEY_J               = GLFW.GLFW_KEY_J;
	public static final int KEY_K               = GLFW.GLFW_KEY_K;
	public static final int KEY_L               = GLFW.GLFW_KEY_L;
	public static final int KEY_SEMICOLON       = GLFW.GLFW_KEY_SEMICOLON;
	public static final int KEY_APOSTROPHE      = GLFW.GLFW_KEY_APOSTROPHE;
	public static final int KEY_GRAVE           = GLFW.GLFW_KEY_GRAVE_ACCENT; /* accent grave */
	public static final int KEY_LSHIFT          = GLFW.GLFW_KEY_LEFT_SHIFT;
	public static final int KEY_BACKSLASH       = GLFW.GLFW_KEY_BACKSLASH;
	public static final int KEY_Z               = GLFW.GLFW_KEY_Z;
	public static final int KEY_X               = GLFW.GLFW_KEY_X;
	public static final int KEY_C               = GLFW.GLFW_KEY_C;
	public static final int KEY_V               = GLFW.GLFW_KEY_V;
	public static final int KEY_B               = GLFW.GLFW_KEY_B;
	public static final int KEY_N               = GLFW.GLFW_KEY_N;
	public static final int KEY_M               = GLFW.GLFW_KEY_M;
	public static final int KEY_COMMA           = GLFW.GLFW_KEY_COMMA;
	public static final int KEY_PERIOD          = GLFW.GLFW_KEY_PERIOD; /* . on main keyboard */
	public static final int KEY_SLASH           = GLFW.GLFW_KEY_SLASH; /* / on main keyboard */
	public static final int KEY_RSHIFT          = GLFW.GLFW_KEY_RIGHT_SHIFT;
	public static final int KEY_MULTIPLY        = GLFW.GLFW_KEY_KP_MULTIPLY; /* * on numeric keypad */ //TODO test this
	public static final int KEY_LMENU           = GLFW.GLFW_KEY_LEFT_ALT; /* left Alt */
	public static final int KEY_SPACE           = GLFW.GLFW_KEY_SPACE;
	public static final int KEY_CAPITAL         = GLFW.GLFW_KEY_CAPS_LOCK;
	public static final int KEY_F1              = GLFW.GLFW_KEY_F1;
	public static final int KEY_F2              = GLFW.GLFW_KEY_F2;
	public static final int KEY_F3              = GLFW.GLFW_KEY_F3;
	public static final int KEY_F4              = GLFW.GLFW_KEY_F4;
	public static final int KEY_F5              = GLFW.GLFW_KEY_F5;
	public static final int KEY_F6              = GLFW.GLFW_KEY_F6;
	public static final int KEY_F7              = GLFW.GLFW_KEY_F7;
	public static final int KEY_F8              = GLFW.GLFW_KEY_F8;
	public static final int KEY_F9              = GLFW.GLFW_KEY_F9;
	public static final int KEY_F10             = GLFW.GLFW_KEY_F10;
	public static final int KEY_NUMLOCK         = GLFW.GLFW_KEY_NUM_LOCK;
	public static final int KEY_SCROLL          = GLFW.GLFW_KEY_SCROLL_LOCK; /* Scroll Lock */
	public static final int KEY_NUMPAD7         = GLFW.GLFW_KEY_KP_7;
	public static final int KEY_NUMPAD8         = GLFW.GLFW_KEY_KP_8;
	public static final int KEY_NUMPAD9         = GLFW.GLFW_KEY_KP_9;
	public static final int KEY_SUBTRACT        = GLFW.GLFW_KEY_KP_SUBTRACT; /* - on numeric keypad */
	public static final int KEY_NUMPAD4         = GLFW.GLFW_KEY_KP_4;
	public static final int KEY_NUMPAD5         = GLFW.GLFW_KEY_KP_5;
	public static final int KEY_NUMPAD6         = GLFW.GLFW_KEY_KP_6;
	public static final int KEY_ADD             = GLFW.GLFW_KEY_KP_ADD; /* + on numeric keypad */
	public static final int KEY_NUMPAD1         = GLFW.GLFW_KEY_KP_1;
	public static final int KEY_NUMPAD2         = GLFW.GLFW_KEY_KP_2;
	public static final int KEY_NUMPAD3         = GLFW.GLFW_KEY_KP_3;
	public static final int KEY_NUMPAD0         = GLFW.GLFW_KEY_KP_0;
	public static final int KEY_DECIMAL         = GLFW.GLFW_KEY_KP_DECIMAL; /* . on numeric keypad */
	public static final int KEY_F11             = GLFW.GLFW_KEY_F11;
	public static final int KEY_F12             = GLFW.GLFW_KEY_F12;
	public static final int KEY_F13             = GLFW.GLFW_KEY_F13; /*                     (NEC PC98) */
	public static final int KEY_F14             = GLFW.GLFW_KEY_F14; /*                     (NEC PC98) */
	public static final int KEY_F15             = GLFW.GLFW_KEY_F15; /*                     (NEC PC98) */
	// FIXME add JP keyboard support at some point
//	public static final int KEY_KANA            = GLFW.; /* (Japanese keyboard)            */
//	public static final int KEY_CONVERT         = GLFW.; /* (Japanese keyboard)            */
//	public static final int KEY_NOCONVERT       = GLFW.; /* (Japanese keyboard)            */
//	public static final int KEY_YEN             = GLFW.; /* (Japanese keyboard)            */
	public static final int KEY_NUMPADEQUALS    = GLFW.GLFW_KEY_EQUAL; /* = on numeric keypad (NEC PC98) */
//	public static final int KEY_CIRCUMFLEX      = GLFW.; /* (Japanese keyboard)            */
//	public static final int KEY_AT              = GLFW.; /*                     (NEC PC98) */
	public static final int KEY_COLON           = GLFW.GLFW_KEY_SEMICOLON; /*                     (NEC PC98) */
//	public static final int KEY_UNDERLINE       = GLFW.; /*                     (NEC PC98) */
//	public static final int KEY_KANJI           = GLFW.; /* (Japanese keyboard)            */
//	public static final int KEY_STOP            = GLFW.; /*                     (NEC PC98) */
//	public static final int KEY_AX              = GLFW.; /*                     (Japan AX) */
	public static final int KEY_UNLABELED       = GLFW.GLFW_KEY_UNKNOWN; /*                        (J3100) */
	public static final int KEY_NUMPADENTER     = GLFW.GLFW_KEY_KP_ENTER; /* Enter on numeric keypad */
	public static final int KEY_RCONTROL        = GLFW.GLFW_KEY_RIGHT_CONTROL;
//	public static final int KEY_NUMPADCOMMA     = GLFW.; /* , on numeric keypad (NEC PC98) */
	public static final int KEY_DIVIDE          = GLFW.GLFW_KEY_KP_DIVIDE; /* / on numeric keypad */
//	public static final int KEY_SYSRQ           = GLFW.; // todo no clue what this is
	public static final int KEY_RMENU           = GLFW.GLFW_KEY_RIGHT_ALT; /* right Alt */
	public static final int KEY_PAUSE           = GLFW.GLFW_KEY_PAUSE; /* Pause */
	public static final int KEY_HOME            = GLFW.GLFW_KEY_HOME; /* Home on arrow keypad */
	public static final int KEY_UP              = GLFW.GLFW_KEY_UP; /* UpArrow on arrow keypad */
	public static final int KEY_PRIOR           = GLFW.GLFW_KEY_PAGE_UP; /* PgUp on arrow keypad */
	public static final int KEY_LEFT            = GLFW.GLFW_KEY_LEFT; /* LeftArrow on arrow keypad */
	public static final int KEY_RIGHT           = GLFW.GLFW_KEY_RIGHT; /* RightArrow on arrow keypad */
	public static final int KEY_END             = GLFW.GLFW_KEY_END; /* End on arrow keypad */
	public static final int KEY_DOWN            = GLFW.GLFW_KEY_DOWN; /* DownArrow on arrow keypad */
//	public static final int KEY_NEXT            = GLFW.; /* PgDn on arrow keypad */
	public static final int KEY_INSERT          = GLFW.GLFW_KEY_INSERT; /* Insert on arrow keypad */
	public static final int KEY_DELETE          = GLFW.GLFW_KEY_DELETE; /* Delete on arrow keypad */
	public static final int KEY_LWIN            = GLFW.GLFW_KEY_LEFT_SUPER; /* Left Windows key */
	public static final int KEY_RWIN            = GLFW.GLFW_KEY_RIGHT_SUPER; /* Right Windows key */
//	public static final int KEY_APPS            = GLFW.; /* AppMenu key */
//	public static final int KEY_POWER           = GLFW.;
//	public static final int KEY_SLEEP           = GLFW.;

	/** A helper for left ALT */
	public static final int KEY_LALT = KEY_LMENU;
	/** A helper for right ALT */
	public static final int KEY_RALT = KEY_RMENU;
	
	/** Control index */
	private static final int LEFT = 0;
	/** Control index */
	private static final int RIGHT = 1;
	/** Control index */
	private static final int UP = 2;
	/** Control index */
	private static final int DOWN = 3;
	/** Control index */
	private static final int BUTTON1 = 4;
	/** Control index */
	private static final int BUTTON2 = 5;
	/** Control index */
	private static final int BUTTON3 = 6;
	/** Control index */
	private static final int BUTTON4 = 7;
	/** Control index */
	private static final int BUTTON5 = 8;
	/** Control index */
	private static final int BUTTON6 = 9;
	/** Control index */
	private static final int BUTTON7 = 10;
	/** Control index */
	private static final int BUTTON8 = 11;
	/** Control index */
	private static final int BUTTON9 = 12;
	/** Control index */
	private static final int BUTTON10 = 13;
	
	/** The left mouse button indicator */
	public static final int MOUSE_LEFT_BUTTON = 0;
	/** The right mouse button indicator */
	public static final int MOUSE_RIGHT_BUTTON = 1;
	/** The middle mouse button indicator */
	public static final int MOUSE_MIDDLE_BUTTON = 2;
	
	/** True if the controllers system has been initialised */
	private static boolean controllersInitialized = false;
	/** The list of controllers */
	private static final ArrayList<Controller> controllers = new ArrayList<>();

	/** The last recorded mouse x position */
	private int lastMouseX;
	/** The last recorded mouse y position */
	private int lastMouseY;
	/** THe state of the mouse buttons */
	protected boolean[] mousePressed = new boolean[10];
	/** THe state of the controller buttons */
	private final boolean[][] controllerPressed = new boolean[100][MAX_BUTTONS];
	
	/** The character values representing the pressed keys */
	protected char[] keys = new char[1024];
	/** True if the key has been pressed since last queries */
	protected boolean[] pressed = new boolean[1024];
	/** The time since the next key repeat to be fired for the key */
	protected long[] nextRepeat = new long[1024];
	
	/** The control states from the controllers */
	private boolean[][] controls = new boolean[10][MAX_BUTTONS+10];
	/** True if the event has been consumed */
	protected boolean consumed = false;
	/** A list of listeners to be notified of input events */
	protected HashSet<ControlledInputReciever> allListeners = new HashSet<>();
	/** The listeners to notify of key events */
	protected ArrayList<KeyListener> keyListeners = new ArrayList<>();
	/** The listener to add */
	protected ArrayList<KeyListener> keyListenersToAdd = new ArrayList<>();
	/** The listeners to notify of mouse events */
	protected ArrayList<MouseListener> mouseListeners = new ArrayList<>();
	/** The listener to add */
	protected ArrayList<MouseListener> mouseListenersToAdd = new ArrayList<>();
	/** The listener to nofiy of controller events */
	protected ArrayList<ControllerListener> controllerListeners = new ArrayList<>();
	/** The current value of the wheel */
	private int wheel;
	/** The height of the display */
	private int height;

	/** True if key repeat is enabled */
	private boolean keyRepeat;
	/** The initial delay for key repeat starts */
	private int keyRepeatInitial;
	/** The interval of key repeat */
	private int keyRepeatInterval;
	
	/** True if the input is currently paused */
	private boolean paused;
	/** The scale to apply to screen coordinates */
	private float scaleX = 1;
	/** The scale to apply to screen coordinates */
	private float scaleY = 1;
	/** The offset to apply to screen coordinates */
	private float xOffset = 0;
	/** The offset to apply to screen coordinates */
	private float yOoffset = 0;
	
	/** The delay before determining a single or double click */
	private int doubleClickDelay = 250;
	/** The timer running out for a single click */
	private long doubleClickTimeout = 0;
	
	/** The clicked x position */
	private int clickX;
	/** The clicked y position */
	private int clickY;
	/** The clicked button */
	private int clickButton;

	/** The x position location the mouse was pressed */
	private int pressedX = -1;
	
	/** The x position location the mouse was pressed */
	private int pressedY = -1;
	
	/** The pixel distance the mouse can move to accept a mouse click */
	private int mouseClickTolerance = 5;

	/**
	 * Disables support for controllers. This means the jinput JAR and native libs 
	 * are not required.
	 */
	public static void disableControllers() {
	   controllersInitialized = true;
	}
	
	/**
	 * Create a new input with the height of the screen
	 * 
	 * @param height The height of the screen
	 */
	public Input(int height) {
		init(height);
	}
	
	/**
	 * Set the double click interval, the time between the first
	 * and second clicks that should be interpreted as a 
	 * double click.
	 * 
	 * @param delay The delay between clicks
	 */
	public void setDoubleClickInterval(int delay) {
		doubleClickDelay = delay;
	}

	/**
	 * Set the pixel distance the mouse can move to accept a mouse click. 
	 * Default is 5.
	 * 
	 * @param mouseClickTolerance The number of pixels.
	 */
	public void setMouseClickTolerance (int mouseClickTolerance) {
		this.mouseClickTolerance = mouseClickTolerance;
	}

	/**
	 * Set the scaling to apply to screen coordinates
	 * 
	 * @param scaleX The scaling to apply to the horizontal axis
	 * @param scaleY The scaling to apply to the vertical axis
	 */
	public void setScale(float scaleX, float scaleY) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}
	
	/**
	 * Set the offset to apply to the screen coodinates
	 * 
	 * @param xoffset The offset on the x-axis
	 * @param yoffset The offset on the y-axis
	 */
	public void setOffset(float xoffset, float yoffset) {
		this.xOffset = xoffset;
		this.yOoffset = yoffset;
	}
	
	/**
	 * Reset the transformation being applied to the input to the default
	 */
	public void resetInputTransform() {
	    setOffset(0, 0);
	    setScale(1, 1);
	}
	
	/**
	 * Add a listener to be notified of input events
	 * 
	 * @param listener The listener to be notified
	 */
	public void addListener(InputListener listener) {
		addKeyListener(listener);
		addMouseListener(listener);
		addControllerListener(listener);
	}

	/**
	 * Add a key listener to be notified of key input events
	 * 
	 * @param listener The listener to be notified
	 */
	public void addKeyListener(KeyListener listener) {
		keyListenersToAdd.add(listener);
	}
	
	/**
	 * Add a key listener to be notified of key input events
	 * 
	 * @param listener The listener to be notified
	 */
	private void addKeyListenerImpl(KeyListener listener) {
		if (keyListeners.contains(listener)) {
			return;
		}
		keyListeners.add(listener);
		allListeners.add(listener);
	}

	/**
	 * Add a mouse listener to be notified of mouse input events
	 * 
	 * @param listener The listener to be notified
	 */
	public void addMouseListener(MouseListener listener) {
		mouseListenersToAdd.add(listener);
	}
	
	/**
	 * Add a mouse listener to be notified of mouse input events
	 * 
	 * @param listener The listener to be notified
	 */
	private void addMouseListenerImpl(MouseListener listener) {
		if (mouseListeners.contains(listener)) {
			return;
		}
		mouseListeners.add(listener);
		allListeners.add(listener);
	}
	
	/**
	 * Add a controller listener to be notified of controller input events
	 * 
	 * @param listener The listener to be notified
	 */
	public void addControllerListener(ControllerListener listener) {
		if (controllerListeners.contains(listener)) {
			return;
		}
		controllerListeners.add(listener);
		allListeners.add(listener);
	}
	
	/**
	 * Remove all the listeners from this input
	 */
	public void removeAllListeners() {
		removeAllKeyListeners();
		removeAllMouseListeners();
		removeAllControllerListeners();
	}

	/**
	 * Remove all the key listeners from this input
	 */
	public void removeAllKeyListeners() {
		allListeners.removeAll(keyListeners);
		keyListeners.clear();
	}

	/**
	 * Remove all the mouse listeners from this input
	 */
	public void removeAllMouseListeners() {
		allListeners.removeAll(mouseListeners);
		mouseListeners.clear();
	}

	/**
	 * Remove all the controller listeners from this input
	 */
	public void removeAllControllerListeners() {
		allListeners.removeAll(controllerListeners);
		controllerListeners.clear();
	}
	
	/**
	 * Add a listener to be notified of input events. This listener
	 * will get events before others that are currently registered
	 * 
	 * @param listener The listener to be notified
	 */
	public void addPrimaryListener(InputListener listener) {
		removeListener(listener);
		
		keyListeners.add(0, listener);
		mouseListeners.add(0, listener);
		controllerListeners.add(0, listener);
		
		allListeners.add(listener);
	}
	
	/**
	 * Remove a listener that will no longer be notified
	 * 
	 * @param listener The listen to be removed
	 */
	public void removeListener(InputListener listener) {
		removeKeyListener(listener);
		removeMouseListener(listener);
		removeControllerListener(listener);
	}

	/**
	 * Remove a key listener that will no longer be notified
	 * 
	 * @param listener The listen to be removed
	 */
	public void removeKeyListener(KeyListener listener) {
		keyListeners.remove(listener);
		keyListenersToAdd.remove(listener);
		
		if (!mouseListeners.contains(listener) && !controllerListeners.contains(listener)) {
			allListeners.remove(listener);
		}
	}

	/**
	 * Remove a controller listener that will no longer be notified
	 * 
	 * @param listener The listen to be removed
	 */
	public void removeControllerListener(ControllerListener listener) {
		controllerListeners.remove(listener);
		
		if (!mouseListeners.contains(listener) && !keyListeners.contains(listener)) {
			allListeners.remove(listener);
		}
	}

	/**
	 * Remove a mouse listener that will no longer be notified
	 * 
	 * @param listener The listen to be removed
	 */
	public void removeMouseListener(MouseListener listener) {
		mouseListeners.remove(listener);
		mouseListenersToAdd.remove(listener);
		
		if (!controllerListeners.contains(listener) && !keyListeners.contains(listener)) {
			allListeners.remove(listener);
		}
	}

	public static final ConcurrentHashMap<Integer, Keyboard.Action> keyPressBindings = new ConcurrentHashMap<>();

	/**
	 * Initialise the input system
	 * 
	 * @param height The height of the window
	 */
	public void init(int height) {
		this.height = height;
		lastMouseX = getMouseX();
		lastMouseY = getMouseY();

		// bind input map
		GLFW.glfwSetKeyCallback(GAME_WINDOW, new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				LOG.debug("pressed key: " + key + " - action: " + action);
				if (action == GLFW.GLFW_PRESS)
					keyPressBindings.get(key).doAction();
			}
		});
	}
	
	/**
	 * Get the character representation of the key identified by the specified code
	 * 
	 * @param code The key code of the key to retrieve the name of
	 * @return The name or character representation of the key requested
	 */
	public static String getKeyName(int code) {
		return Keyboard.getKeyName(code);
	}
	
	/**
	 * Check if a particular key has been pressed since this method 
	 * was last called for the specified key
	 * 
	 * @param code The key code of the key to check
	 * @return True if the key has been pressed
	 */
	public boolean isKeyPressed(int code) {
		if (pressed[code]) {
			pressed[code] = false;
			return true;
		}
		
		return false;
	}
	
	/**
	 * Check if a mouse button has been pressed since last call
	 * 
	 * @param button The button to check
	 * @return True if the button has been pressed since last call
	 */
	public boolean isMousePressed(int button) {
		if (mousePressed[button]) {
			mousePressed[button] = false;
			return true;
		}
		
		return false;
	}
	
	/**
	 * Check if a controller button has been pressed since last 
	 * time
	 * 
	 * @param button The button to check for (note that this includes directional controls first)
	 * @return True if the button has been pressed since last time
	 */
	public boolean isControlPressed(int button) {
		return isControlPressed(button, 0);
	}

	/**
	 * Check if a controller button has been pressed since last 
	 * time
	 * 
	 * @param controller The index of the controller to check
	 * @param button The button to check for (note that this includes directional controls first)
	 * @return True if the button has been pressed since last time
	 */
	public boolean isControlPressed(int button, int controller) {
		if (controllerPressed[controller][button]) {
			controllerPressed[controller][button] = false;
			return true;
		}
		
		return false;
	}
	
	/**
	 * Clear the state for isControlPressed method. This will reset all
	 * controls to not pressed
	 */
	public void clearControlPressedRecord() {
		for (int i=0;i<controllers.size();i++) {
			Arrays.fill(controllerPressed[i], false);
		}
	}
	
	/**
	 * Clear the state for the <code>isKeyPressed</code> method. This will
	 * resort in all keys returning that they haven't been pressed, until
	 * they are pressed again
	 */
	public void clearKeyPressedRecord() {
		Arrays.fill(pressed, false);
	}

	/**
	 * Clear the state for the <code>isMousePressed</code> method. This will
	 * resort in all mouse buttons returning that they haven't been pressed, until
	 * they are pressed again
	 */
	public void clearMousePressedRecord() {
		Arrays.fill(mousePressed, false);
	}
	
	/**
	 * Check if a particular key is down
	 * 
	 * @param code The key code of the key to check
	 * @return True if the key is down
	 */
	public boolean isKeyDown(int code) {
		return Keyboard.isKeyDown(code);
	}

	/**
	 * Get the absolute x position of the mouse cursor within the container
	 * 
	 * @return The absolute x position of the mouse cursor
	 */
	public int getAbsoluteMouseX() {
		return Mouse.getX();
	}

	/**
	 * Get the absolute y position of the mouse cursor within the container
	 * 
	 * @return The absolute y position of the mouse cursor
	 */
	public int getAbsoluteMouseY() {
		return height - Mouse.getY() - 1;
	}
	   
	/**
	 * Get the x position of the mouse cursor
	 * 
	 * @return The x position of the mouse cursor
	 */
	public int getMouseX() {
		return (int) ((getAbsoluteMouseX() * scaleX)+ xOffset);
	}
	
	/**
	 * Get the y position of the mouse cursor
	 * 
	 * @return The y position of the mouse cursor
	 */
	public int getMouseY() {
		return (int) ((getAbsoluteMouseY() * scaleY)+ yOoffset);
	}
	
	/**
	 * Check if a given mouse button is down
	 * 
	 * @param button The index of the button to check (starting at 0)
	 * @return True if the mouse button is down
	 */
	public boolean isMouseButtonDown(int button) {
		return Mouse.isButtonDown(button);
	}
	
	/**
	 * Check if any mouse button is down
	 * 
	 * @return True if any mouse button is down
	 */
	private boolean anyMouseDown() {
		for (int i=0;i<3;i++) {
			if (Mouse.isButtonDown(i)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Get a count of the number of controlles available
	 * 
	 * @return The number of controllers available
	 */
	public int getControllerCount() {
		try {
			initControllers();
		} catch (SlickException e) {
			throw new RuntimeException("Failed to initialise controllers");
		}
		
		return controllers.size();
	}
	
	/**
	 * Get the number of axis that are avaiable on a given controller
	 * 
	 * @param controller The index of the controller to check
	 * @return The number of axis available on the controller
	 */
	public int getAxisCount(int controller) {
		return ((Controller) controllers.get(controller)).getAxisCount();
	}
	
	/**
	 * Get the value of the axis with the given index
	 *  
	 * @param controller The index of the controller to check
	 * @param axis The index of the axis to read
	 * @return The axis value at time of reading
	 */ 
	public float getAxisValue(int controller, int axis) {
		return ((Controller) controllers.get(controller)).getAxisValue(axis);
	}

	/**
	 * Get the name of the axis with the given index
	 *  
	 * @param controller The index of the controller to check
	 * @param axis The index of the axis to read
	 * @return The name of the specified axis
	 */ 
	public String getAxisName(int controller, int axis) {
		return ((Controller) controllers.get(controller)).getAxisName(axis);
	}
	
	/**
	 * Check if the controller has the left direction pressed
	 * 
	 * @param controller The index of the controller to check
	 * @return True if the controller is pressed to the left
	 */
	public boolean isControllerLeft(int controller) {
		if (controller >= getControllerCount()) {
			return false;
		}
		
		if (controller == ANY_CONTROLLER) {
			for (int i=0;i<controllers.size();i++) {
				if (isControllerLeft(i)) {
					return true;
				}
			}
			
			return false;
		}
		
		return ((Controller) controllers.get(controller)).getXAxisValue() < -0.5f
				|| ((Controller) controllers.get(controller)).getPovX() < -0.5f;
	}

	/**
	 * Check if the controller has the right direction pressed
	 * 
	 * @param controller The index of the controller to check
	 * @return True if the controller is pressed to the right
	 */
	public boolean isControllerRight(int controller) {
		if (controller >= getControllerCount()) {
			return false;
		}

		if (controller == ANY_CONTROLLER) {
			for (int i=0;i<controllers.size();i++) {
				if (isControllerRight(i)) {
					return true;
				}
			}
			
			return false;
		}
		
		return ((Controller) controllers.get(controller)).getXAxisValue() > 0.5f
   				|| ((Controller) controllers.get(controller)).getPovX() > 0.5f;
	}

	/**
	 * Check if the controller has the up direction pressed
	 * 
	 * @param controller The index of the controller to check
	 * @return True if the controller is pressed to the up
	 */
	public boolean isControllerUp(int controller) {
		if (controller >= getControllerCount()) {
			return false;
		}

		if (controller == ANY_CONTROLLER) {
			for (int i=0;i<controllers.size();i++) {
				if (isControllerUp(i)) {
					return true;
				}
			}
			
			return false;
		}
		return ((Controller) controllers.get(controller)).getYAxisValue() < -0.5f
		   		|| ((Controller) controllers.get(controller)).getPovY() < -0.5f;
	}

	/**
	 * Check if the controller has the down direction pressed
	 * 
	 * @param controller The index of the controller to check
	 * @return True if the controller is pressed to the down
	 */
	public boolean isControllerDown(int controller) {
		if (controller >= getControllerCount()) {
			return false;
		}

		if (controller == ANY_CONTROLLER) {
			for (int i=0;i<controllers.size();i++) {
				if (isControllerDown(i)) {
					return true;
				}
			}
			
			return false;
		}
		
		return ((Controller) controllers.get(controller)).getYAxisValue() > 0.5f
			   || ((Controller) controllers.get(controller)).getPovY() > 0.5f;
	       
	}

	/**
	 * Check if controller button is pressed
	 * 
	 * @param controller The index of the controller to check
	 * @param index The index of the button to check
	 * @return True if the button is pressed
	 */
	public boolean isButtonPressed(int index, int controller) {
		if (controller >= getControllerCount()) {
			return false;
		}

		if (controller == ANY_CONTROLLER) {
			for (int i=0;i<controllers.size();i++) {
				if (isButtonPressed(index, i)) {
					return true;
				}
			}
			
			return false;
		}
		
		return ((Controller) controllers.get(controller)).isButtonPressed(index);
	}
	
	/**
	 * Check if button 1 is pressed
	 * 
	 * @param controller The index of the controller to check
	 * @return True if the button is pressed
	 */
	public boolean isButton1Pressed(int controller) {
		return isButtonPressed(0, controller);
	}

	/**
	 * Check if button 2 is pressed
	 * 
	 * @param controller The index of the controller to check
	 * @return True if the button is pressed
	 */
	public boolean isButton2Pressed(int controller) {
		return isButtonPressed(1, controller);
	}

	/**
	 * Check if button 3 is pressed
	 * 
	 * @param controller The index of the controller to check
	 * @return True if the button is pressed
	 */
	public boolean isButton3Pressed(int controller) {
		return isButtonPressed(2, controller);
	}
	
	/**
	 * Initialise the controllers system
	 * 
	 * @throws SlickException Indicates a failure to use the hardware
	 */
	public void initControllers() throws SlickException {
		if (controllersInitialized) {
			return;
		}
		
		controllersInitialized = true;
		try {
			Controllers.create();
			int count = Controllers.getControllerCount();
			
			for (int i = 0; i < count; i++) {
				Controller controller = Controllers.getController(i);

				if ((controller.getButtonCount() >= 3) && (controller.getButtonCount() < MAX_BUTTONS)) {
					controllers.add(controller);
				}
			}
			
			LOG.info("Found "+controllers.size()+" controllers");
			for (int i=0;i<controllers.size();i++) {
				LOG.info(i+" : "+(controllers.get(i)).getName());
			}
		} catch (NoClassDefFoundError e) {
			// forget it, no jinput availble
		}
	}
	
	/**
	 * Notification from an event handle that an event has been consumed
	 */
	public void consumeEvent() {
		consumed = true;
	}
	
	/**
	 * A null stream to clear out those horrid errors
	 *
	 * @author kevin
	 */
	private class NullOutputStream extends OutputStream {
		/**
		 * @see java.io.OutputStream#write(int)
		 */
		public void write(int b) throws IOException {
			// null implemetnation
		}
		
	}
	
	/**
	 * Hook to allow us to translate any key character into special key 
	 * codes for easier use.
	 * 
	 * @param key The original key code
	 * @param c The character that was fired
	 * @return The key code to fire
	 */
	private int resolveEventKey(int key, char c) {
		// BUG with LWJGL - equals comes back with keycode = 0
		// See: http://slick.javaunlimited.net/viewtopic.php?t=617
		if ((c == 61) || (key == 0)) {
			return KEY_EQUALS;
		}
		
		return key;
	}
	
	/**
	 * Notification that the mouse has been pressed and hence we
	 * should consider what we're doing with double clicking
	 * 
	 * @param button The button pressed/released
	 * @param x The location of the mouse
	 * @param y The location of the mouse
	 */
	public void considerDoubleClick(int button, int x, int y) {
		if (doubleClickTimeout == 0) {
			clickX = x;
			clickY = y;
			clickButton = button;
			doubleClickTimeout = System.currentTimeMillis() + doubleClickDelay;
			fireMouseClicked(button, x, y, 1);
		} else {
			if (clickButton == button) {
				if ((System.currentTimeMillis() < doubleClickTimeout)) {
					fireMouseClicked(button, x, y, 2);
					doubleClickTimeout = 0;
				}
			}
		}
	}
	
	/**
	 * Poll the state of the input
	 * 
	 * @param width The width of the game view
	 * @param height The height of the game view
	 */
	public void poll(int width, int height) {
		if (paused) {
			clearControlPressedRecord();
			clearKeyPressedRecord();
			clearMousePressedRecord();
			
			while (Keyboard.next()) {}
			while (Mouse.next()) {}
			return;
		}

		if (!AppGameContainer.hasFocus()) {
			clearControlPressedRecord();
			clearKeyPressedRecord();
			clearMousePressedRecord();
		}
		
		// add any listeners requested since last time
		for (KeyListener keyListener : keyListenersToAdd) {
			addKeyListenerImpl(keyListener);
		}
		keyListenersToAdd.clear();
		for (MouseListener mouseListener : mouseListenersToAdd) {
			addMouseListenerImpl(mouseListener);
		}
		mouseListenersToAdd.clear();
		
		if (doubleClickTimeout != 0) {
			if (System.currentTimeMillis() > doubleClickTimeout) {
				doubleClickTimeout = 0;
			}
		}
		
		this.height = height;

		for (ControlledInputReciever listener : allListeners) {
			listener.inputStarted();
		}
		
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				int eventKey = resolveEventKey(Keyboard.getEventKey(), Keyboard.getEventCharacter());
				
				keys[eventKey] = Keyboard.getEventCharacter();
				pressed[eventKey] = true;
				nextRepeat[eventKey] = System.currentTimeMillis() + keyRepeatInitial;
				
				consumed = false;
				for (int i=0;i<keyListeners.size();i++) {
					KeyListener listener = keyListeners.get(i);
					
					if (listener.isAcceptingInput()) {
						listener.keyPressed(eventKey, Keyboard.getEventCharacter());
						if (consumed) {
							break;
						}
					}
				}
			} else {
				int eventKey = resolveEventKey(Keyboard.getEventKey(), Keyboard.getEventCharacter());
				nextRepeat[eventKey] = 0;
				
				consumed = false;
				for (int i=0;i<keyListeners.size();i++) {
					KeyListener listener = (KeyListener) keyListeners.get(i);
					if (listener.isAcceptingInput()) {
						listener.keyReleased(eventKey, keys[eventKey]);
						if (consumed) {
							break;
						}
					}
				}
			}
		}

		/** True if the display is active */
		boolean displayActive = true;
		while (Mouse.next()) {
			if (Mouse.getEventButton() >= 0) {
				if (Mouse.getEventButtonState()) {
					consumed = false;
					mousePressed[Mouse.getEventButton()] = true;

					pressedX = (int) (xOffset + (Mouse.getEventX() * scaleX));
					pressedY =  (int) (yOoffset + ((height-Mouse.getEventY()-1) * scaleY));

					for (int i=0;i<mouseListeners.size();i++) {
						MouseListener listener = (MouseListener) mouseListeners.get(i);
						if (listener.isAcceptingInput()) {
							listener.mousePressed(Mouse.getEventButton(), pressedX, pressedY);
							if (consumed) {
								break;
							}
						}
					}
				} else {
					consumed = false;
					mousePressed[Mouse.getEventButton()] = false;
					
					int releasedX = (int) (xOffset + (Mouse.getEventX() * scaleX));
					int releasedY = (int) (yOoffset + ((height-Mouse.getEventY()-1) * scaleY));
					if ((pressedX != -1) && 
					    (pressedY != -1) &&
						(Math.abs(pressedX - releasedX) < mouseClickTolerance) && 
						(Math.abs(pressedY - releasedY) < mouseClickTolerance)) {
						considerDoubleClick(Mouse.getEventButton(), releasedX, releasedY);
						pressedX = pressedY = -1;
					}

					for (int i=0;i<mouseListeners.size();i++) {
						MouseListener listener = (MouseListener) mouseListeners.get(i);
						if (listener.isAcceptingInput()) {
							listener.mouseReleased(Mouse.getEventButton(), releasedX, releasedY);
							if (consumed) {
								break;
							}
						}
					}
				}
			} else {
				if (Mouse.isGrabbed() && displayActive) {
					if ((Mouse.getEventDX() != 0) || (Mouse.getEventDY() != 0)) {
						consumed = false;
						for (int i=0;i<mouseListeners.size();i++) {
							MouseListener listener = (MouseListener) mouseListeners.get(i);
							if (listener.isAcceptingInput()) {
								if (anyMouseDown()) {
									listener.mouseDragged(0, 0, Mouse.getEventDX(), -Mouse.getEventDY());	
								} else {
									listener.mouseMoved(0, 0, Mouse.getEventDX(), -Mouse.getEventDY());
								}
								
								if (consumed) {
									break;
								}
							}
						}
					}
				}
				
				int dwheel = Mouse.getEventDWheel();
				wheel += dwheel;
				if (dwheel != 0) {
					consumed = false;
					for (int i=0;i<mouseListeners.size();i++) {
						MouseListener listener = (MouseListener) mouseListeners.get(i);
						if (listener.isAcceptingInput()) {
							listener.mouseWheelMoved(dwheel);
							if (consumed) {
								break;
							}
						}
					}
				}
			}
		}
		
		if (!displayActive || Mouse.isGrabbed()) {
			lastMouseX = getMouseX();
			lastMouseY = getMouseY();
		} else {
			if ((lastMouseX != getMouseX()) || (lastMouseY != getMouseY())) {
				consumed = false;
				for (int i=0;i<mouseListeners.size();i++) {
					MouseListener listener = (MouseListener) mouseListeners.get(i);
					if (listener.isAcceptingInput()) {
						if (anyMouseDown()) {
							listener.mouseDragged(lastMouseX ,  lastMouseY, getMouseX(), getMouseY());
						} else {
							listener.mouseMoved(lastMouseX ,  lastMouseY, getMouseX(), getMouseY());	
						}
						if (consumed) {
							break;
						}
					}
				}
				lastMouseX = getMouseX();
				lastMouseY = getMouseY();
			}
		}
		
		if (controllersInitialized) {
			for (int i=0;i<getControllerCount();i++) {
				int count = ((Controller) controllers.get(i)).getButtonCount()+3;
				count = Math.min(count, 24);
				for (int c=0;c<=count;c++) {
					if (controls[i][c] && !isControlDwn(c, i)) {
						controls[i][c] = false;
						fireControlRelease(c, i);
					} else if (!controls[i][c] && isControlDwn(c, i)) {
						controllerPressed[i][c] = true;
						controls[i][c] = true;
						fireControlPress(c, i);
					}
				}
			}
		}
		
		if (keyRepeat) {
			for (int i=0;i<1024;i++) {
				if (pressed[i] && (nextRepeat[i] != 0)) {
					if (System.currentTimeMillis() > nextRepeat[i]) {
						nextRepeat[i] = System.currentTimeMillis() + keyRepeatInterval;
						consumed = false;
						for (int j=0;j<keyListeners.size();j++) {
							KeyListener listener = (KeyListener) keyListeners.get(j);

							if (listener.isAcceptingInput()) {
								listener.keyPressed(i, keys[i]);
								if (consumed) {
									break;
								}
							}
						}
					}
				}
			}
		}

		
		Iterator all = allListeners.iterator();
		while (all.hasNext()) {
			ControlledInputReciever listener = (ControlledInputReciever) all.next();
			listener.inputEnded();
		}
		
	}
	
	/**
	 * Enable key repeat for this input context. This will cause keyPressed to get called repeatedly
	 * at a set interval while the key is pressed
	 * 
	 * @param initial The interval before key repreating starts after a key press
	 * @param interval The interval between key repeats in ms
	 * @deprecated
	 */
	public void enableKeyRepeat(int initial, int interval) {
		Keyboard.enableRepeatEvents(true);
	}

	/**
	 * Enable key repeat for this input context. Uses the system settings for repeat
	 * interval configuration.
	 */
	public void enableKeyRepeat() {
		Keyboard.enableRepeatEvents(true);
	}
	
	/**
	 * Disable key repeat for this input context
	 */
	public void disableKeyRepeat() {
		Keyboard.enableRepeatEvents(false);
	}
	
	/**
	 * Check if key repeat is enabled
	 * 
	 * @return True if key repeat is enabled
	 */
	public boolean isKeyRepeatEnabled() {
		return Keyboard.areRepeatEventsEnabled();
	}
	
	/**
	 * Fire an event indicating that a control has been pressed
	 * 
	 * @param index The index of the control pressed
	 * @param controllerIndex The index of the controller on which the control was pressed
	 */
	private void fireControlPress(int index, int controllerIndex) {
		consumed = false;
		for (int i=0;i<controllerListeners.size();i++) {
			ControllerListener listener = (ControllerListener) controllerListeners.get(i);
			if (listener.isAcceptingInput()) {
				switch (index) {
				case LEFT:
					listener.controllerLeftPressed(controllerIndex);
					break;
				case RIGHT:
					listener.controllerRightPressed(controllerIndex);
					break;
				case UP:
					listener.controllerUpPressed(controllerIndex);
					break;
				case DOWN:
					listener.controllerDownPressed(controllerIndex);
					break;
				default:
					// assume button pressed
					listener.controllerButtonPressed(controllerIndex, (index - BUTTON1) + 1);
					break;
				}
				if (consumed) {
					break;
				}
			}
		}
	}

	/**
	 * Fire an event indicating that a control has been released
	 * 
	 * @param index The index of the control released
	 * @param controllerIndex The index of the controller on which the control was released
	 */
	private void fireControlRelease(int index, int controllerIndex) {
		consumed = false;
		for (int i=0;i<controllerListeners.size();i++) {
			ControllerListener listener = (ControllerListener) controllerListeners.get(i);
			if (listener.isAcceptingInput()) {
				switch (index) {
				case LEFT:
					listener.controllerLeftReleased(controllerIndex);
					break;
				case RIGHT:
					listener.controllerRightReleased(controllerIndex);
					break;
				case UP:
					listener.controllerUpReleased(controllerIndex);
					break;
				case DOWN:
					listener.controllerDownReleased(controllerIndex);
					break;
				default:
					// assume button release
					listener.controllerButtonReleased(controllerIndex, (index - BUTTON1) + 1);
					break;
				}
				if (consumed) {
					break;
				}
			}
		}
	}
	
	/**
	 * Check if a particular control is currently pressed
	 * 
	 * @param index The index of the control
	 * @param controllerIndex The index of the control to which the control belongs
	 * @return True if the control is pressed
	 */
	private boolean isControlDwn(int index, int controllerIndex) {
		switch (index) {
		case LEFT:
			return isControllerLeft(controllerIndex);
		case RIGHT:
			return isControllerRight(controllerIndex);
		case UP:
			return isControllerUp(controllerIndex);
		case DOWN:
			return isControllerDown(controllerIndex);
		}
		
		if (index >= BUTTON1) {
			return isButtonPressed((index-BUTTON1), controllerIndex);
		}

		throw new RuntimeException("Unknown control index");
	}
	

	/**
	 * Pauses the polling and sending of input events.
	 */
	public void pause() {
		paused = true;

		// Reset all polling arrays
		clearKeyPressedRecord();
		clearMousePressedRecord();
		clearControlPressedRecord();
	}

	/**
	 * Resumes the polling and sending of input events.
	 */
	public void resume() {
		paused = false;
	}
	
	/**
	 * Notify listeners that the mouse button has been clicked
	 * 
	 * @param button The button that has been clicked 
	 * @param x The location at which the button was clicked
	 * @param y The location at which the button was clicked
	 * @param clickCount The number of times the button was clicked (single or double click)
	 */
	private void fireMouseClicked(int button, int x, int y, int clickCount) {
		consumed = false;
		for (int i=0;i<mouseListeners.size();i++) {
			MouseListener listener = (MouseListener) mouseListeners.get(i);
			if (listener.isAcceptingInput()) {
				listener.mouseClicked(button, x, y, clickCount);
				if (consumed) {
					break;
				}
			}
		}
	}
}
