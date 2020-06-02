package org.newdawn.slick.input;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import com.google.common.collect.*;
import org.lwjgl.glfw.*;
import org.newdawn.slick.*;
import org.newdawn.slick.input.sources.controller.Controller;
import org.newdawn.slick.input.sources.controller.Controllers;
import org.newdawn.slick.input.sources.keymaps.USKeyboard;
import org.newdawn.slick.util.Log;

import static org.newdawn.slick.GameContainer.GAME_WINDOW;
import static org.newdawn.slick.input.sources.keymaps.USKeyboard.KEYBOARD_SIZE;

/**
 * A wrapped for all keyboard, mouse and controller input
 *
 * @author kevin
 * @author tyler
 */
public class Input {
	private static final Log LOG = new Log(USKeyboard.class);

	/** The controller index to pass to check all controllers */
	public static final int ANY_CONTROLLER = -1;
	
	/** The maximum number of buttons on controllers */
	private static final int MAX_BUTTONS = 100;

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
	
	/** True if the controllers system has been initialised */
	private static boolean controllersInitialized = false;
	/** The list of controllers */
	private static final ArrayList<Controller> controllers = new ArrayList<>();
	/** Key Pressed bindings */
	// TODO make this a multimap so I can bind multiple keyPresses
	public static final Multimap<Integer, KeyPress> keyPressBindings = ArrayListMultimap.create();
	public static final Multiset<Integer> pressedKeys = HashMultiset.create(KEYBOARD_SIZE);

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
	/** The listener to nofiy of controller events */
	protected ArrayList<ControllerListener> controllerListeners = new ArrayList<>();
	/** The current value of the wheel */
	private int wheel;
	/** True if key repeat is enabled */
	private boolean keyRepeat;
	/** The initial delay for key repeat starts */
	private int keyRepeatInitial;
	/** The interval of key repeat */
	private int keyRepeatInterval;
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

	public static void disableControllers() {
	   controllersInitialized = true;
	}
	
	public Input() {
		init();
	}
	
	public void setDoubleClickInterval(int delay) {
		doubleClickDelay = delay;
	}

	public void setMouseClickTolerance (int mouseClickTolerance) {
		this.mouseClickTolerance = mouseClickTolerance;
	}

	public void setScale(float scaleX, float scaleY) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}
	
	public void setOffset(float xoffset, float yoffset) {
		this.xOffset = xoffset;
		this.yOoffset = yoffset;
	}
	
	public void resetInputTransform() {
	    setOffset(0, 0);
	    setScale(1, 1);
	}
	
	public void addListener(InputListener listener) {
		addMouseListener(listener);
		addControllerListener(listener);
	}

	public void addMouseListener(MouseListener listener) {
		if (mouseListeners.contains(listener)) {
			return;
		}
		mouseListeners.add(listener);
		allListeners.add(listener);
	}
	
	public void addControllerListener(ControllerListener listener) {
		if (controllerListeners.contains(listener)) {
			return;
		}
		controllerListeners.add(listener);
		allListeners.add(listener);
	}

	public void removeAllKeyListeners() {
		allListeners.removeAll(keyListeners);
		keyListeners.clear();
	}

	public void removeAllMouseListeners() {
		allListeners.removeAll(mouseListeners);
		mouseListeners.clear();
	}

	public void removeAllControllerListeners() {
		allListeners.removeAll(controllerListeners);
		controllerListeners.clear();
	}
	
	public void addPrimaryListener(InputListener listener) {
		removeListener(listener);
		
		keyListeners.add(0, listener);
		mouseListeners.add(0, listener);
		controllerListeners.add(0, listener);
		
		allListeners.add(listener);
	}
	
	public void removeListener(InputListener listener) {
		removeKeyListener(listener);
		removeMouseListener(listener);
		removeControllerListener(listener);
	}

	public void removeKeyListener(KeyListener listener) {
		keyListeners.remove(listener);
		keyListenersToAdd.remove(listener);
		
		if (!mouseListeners.contains(listener) && !controllerListeners.contains(listener)) {
			allListeners.remove(listener);
		}
	}

	public void removeControllerListener(ControllerListener listener) {
		controllerListeners.remove(listener);
		
		if (!mouseListeners.contains(listener) && !keyListeners.contains(listener)) {
			allListeners.remove(listener);
		}
	}

	public void removeMouseListener(MouseListener listener) {
		mouseListeners.remove(listener);

		if (!controllerListeners.contains(listener) && !keyListeners.contains(listener)) {
			allListeners.remove(listener);
		}
	}

	public static void bindKeyPress(int boundKey, KeyPress.Action event) {
		bindKeyPress(boundKey, true, event);
	}

	public static void bindKeyPress(int boundKey, boolean enableRepeatPress, KeyPress.Action event) {
		keyPressBindings.put(boundKey, KeyPress.of(enableRepeatPress, event));
	}

	private void bindKeyInput() {
		GLFW.glfwSetKeyCallback(GAME_WINDOW, (window, key, scancode, action, mods) -> {
			if (action == GLFW.GLFW_PRESS) {
				if (keyPressBindings.containsKey(key)) {
					pressedKeys.add(key);
				}
				LOG.trace("pressed key: " + key + " - action: " + action);
			} else if (action == GLFW.GLFW_REPEAT) {
				pressedKeys.add(key);
				LOG.trace("repeated key: " + key + " - action: " + action);
			} else if (action == GLFW.GLFW_RELEASE) {
				pressedKeys.setCount(key, 0);
				LOG.trace("released key: " + key + " - action: " + action);
			}
		});
	}

	private void bindMouseMovement() {
		GLFW.glfwSetCursorPosCallback(GAME_WINDOW, (window, xpos, ypos) -> {
			mouseListeners.forEach(mouseListener -> {
				if (mouseListener.isAcceptingInput()) {
					if (anyMouseDown()) {
						mouseListener.mouseDragged(lastMouseX ,  lastMouseY, (int) xpos, (int) ypos);
					} else {
						mouseListener.mouseMoved(lastMouseX ,  lastMouseY, (int) xpos, (int) ypos);
					}
				}
			});
			lastMouseX = (int) xpos;
			lastMouseY = (int) ypos;
		});
	}

	private void bindMouseInput() {
		GLFW.glfwSetMouseButtonCallback(GAME_WINDOW, (window, button, action, mods) -> {
			Map<Integer, Integer> pressedMap = new HashMap<>();
			if (action == GLFW.GLFW_PRESS) {
				mouseListeners.forEach(mouseListener ->  {
					if (mouseListener.isAcceptingInput()) {
						mouseListener.mousePressed(button, lastMouseX, lastMouseY);
						pressedMap.put(button, 1);
					}
				});
			} else if (action == GLFW.GLFW_RELEASE) {
				mouseListeners.forEach(mouseListener ->  {
					if (mouseListener.isAcceptingInput()) {
						mouseListener.mouseReleased(button, lastMouseX, lastMouseY);
					}
				});
			} else if (action == GLFW.GLFW_REPEAT) {
				mouseListeners.forEach(mouseListener -> {
					if (mouseListener.isAcceptingInput()) {
						pressedMap.put(button, pressedMap.get(button) + 1);
						mouseListener.mouseClicked(button, lastMouseX, lastMouseY, pressedMap.get(button));
					}
				});
			}
		});
	}

	public void init() {
		bindKeyInput();
		bindMouseMovement();
		bindMouseInput();
	}

	public static String getKeyName(int code) {
		return GLFW.glfwGetKeyName(GLFW.GLFW_KEY_UNKNOWN, code);
	}
	
	public boolean isKeyPressed(int code) {
		if (pressed[code]) {
			pressed[code] = false;
			return true;
		}
		
		return false;
	}
	
	public boolean isMousePressed(int button) {
		if (mousePressed[button]) {
			mousePressed[button] = false;
			return true;
		}
		
		return false;
	}
	
	public boolean isControlPressed(int button) {
		return isControlPressed(button, 0);
	}

	public boolean isControlPressed(int button, int controller) {
		if (controllerPressed[controller][button]) {
			controllerPressed[controller][button] = false;
			return true;
		}
		
		return false;
	}
	
	public void clearControlPressedRecord() {
		for (int i=0;i<controllers.size();i++) {
			Arrays.fill(controllerPressed[i], false);
		}
	}
	
	public void clearKeyPressedRecord() {
		Arrays.fill(pressed, false);
	}

	public void clearMousePressedRecord() {
		Arrays.fill(mousePressed, false);
	}
	
	public boolean isKeyDown(int code) {
		return GLFW.glfwGetKey(GAME_WINDOW, code) == GLFW.GLFW_PRESS;
	}

	public int getAbsoluteMouseX() {
		return lastMouseX;
	}

	public int getAbsoluteMouseY() {
		return lastMouseY;
	}

	public int getMouseX() {
		return (int) ((getAbsoluteMouseX() * scaleX)+ xOffset);
	}
	
	public int getMouseY() {
		return (int) ((getAbsoluteMouseY() * scaleY)+ yOoffset);
	}
	
	public boolean isMouseButtonDown(int button) {
		return GLFW.glfwGetMouseButton(GAME_WINDOW, button) == GLFW.GLFW_PRESS;
	}
	
	private boolean anyMouseDown() {
		for (int i=0;i<3;i++) {
			if (GLFW.glfwGetMouseButton(GAME_WINDOW, i) == GLFW.GLFW_PRESS) {
				return true;
			}
		}

		return false;
	}
	
	public int getControllerCount() {
		try {
			initControllers();
		} catch (SlickException e) {
			throw new RuntimeException("Failed to initialise controllers");
		}
		
		return controllers.size();
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
		
		return (controllers.get(controller)).getXAxisValue() < -0.5f
				|| (controllers.get(controller)).getPovX() < -0.5f;
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
		
		return controllers.get(controller).getYAxisValue() > 0.5f
			   || controllers.get(controller).getPovY() > 0.5f;
	       
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
	
	private boolean takeAction(int key, KeyPress press) {
		int count = pressedKeys.count(key);
		if (count == 1) {
			return true;
		}

		return press.repeatEnabled();
	}

	public void poll() {
		if (!AppGameContainer.hasFocus()) {
			clearControlPressedRecord();
			clearKeyPressedRecord();
			clearMousePressedRecord();
			pressedKeys.clear();
		}

		pressedKeys.elementSet().forEach(key -> Optional.ofNullable(keyPressBindings.get(key))
				.orElse(Collections.singleton(KeyPress.notFound(key))).stream()
				.filter(keyPress -> takeAction(key, keyPress))
				.filter(KeyPress::doAction)
				.findFirst().ifPresent((ignored) -> pressedKeys.add(key)));

		if (doubleClickTimeout != 0) {
			if (System.currentTimeMillis() > doubleClickTimeout) {
				doubleClickTimeout = 0;
			}
		}
		
		for (ControlledInputReciever listener : allListeners) {
			listener.inputStarted();
		}

		// TODO this is currently always false, and I think it still works
//		while (Keyboard.next()) {
//			if (Keyboard.getEventKeyState()) {
//				int eventKey = resolveEventKey(Keyboard.getEventKey(), Keyboard.getEventCharacter());
//
//				keys[eventKey] = Keyboard.getEventCharacter();
//				pressed[eventKey] = true;
//				nextRepeat[eventKey] = System.currentTimeMillis() + keyRepeatInitial;
//
//				consumed = false;
//				for (int i=0;i<keyListeners.size();i++) {
//					KeyListener listener = keyListeners.get(i);
//
//					if (listener.isAcceptingInput()) {
//						listener.keyPressed(eventKey, Keyboard.getEventCharacter());
//						if (consumed) {
//							break;
//						}
//					}
//				}
//			} else {
//				int eventKey = resolveEventKey(Keyboard.getEventKey(), Keyboard.getEventCharacter());
//				nextRepeat[eventKey] = 0;
//
//				consumed = false;
//				for (int i=0;i<keyListeners.size();i++) {
//					KeyListener listener = keyListeners.get(i);
//					if (listener.isAcceptingInput()) {
//						listener.keyReleased(eventKey, keys[eventKey]);
//						if (consumed) {
//							break;
//						}
//					}
//				}
//			}
//		}

		// TODO don't think this does anything since not supplying the buffer -- would like to translate to callback events
//		while (Mouse.next()) {
//			if (Mouse.getEventButton() >= 0) {
//				if (Mouse.getEventButtonState()) {
//					consumed = false;
//					mousePressed[Mouse.getEventButton()] = true;
//
//					pressedX = (int) (xOffset + (Mouse.getEventX() * scaleX));
//					pressedY =  (int) (yOoffset + ((height-Mouse.getEventY()-1) * scaleY));
//
//					for (int i=0;i<mouseListeners.size();i++) {
//						MouseListener listener = (MouseListener) mouseListeners.get(i);
//						if (listener.isAcceptingInput()) {
//							listener.mousePressed(Mouse.getEventButton(), pressedX, pressedY);
//							if (consumed) {
//								break;
//							}
//						}
//					}
//				} else {
//					consumed = false;
//					mousePressed[Mouse.getEventButton()] = false;
//
//					int releasedX = (int) (xOffset + (Mouse.getEventX() * scaleX));
//					int releasedY = (int) (yOoffset + ((height-Mouse.getEventY()-1) * scaleY));
//					if ((pressedX != -1) &&
//					    (pressedY != -1) &&
//						(Math.abs(pressedX - releasedX) < mouseClickTolerance) &&
//						(Math.abs(pressedY - releasedY) < mouseClickTolerance)) {
//						considerDoubleClick(Mouse.getEventButton(), releasedX, releasedY);
//						pressedX = pressedY = -1;
//					}
//
//					for (int i=0;i<mouseListeners.size();i++) {
//						MouseListener listener = (MouseListener) mouseListeners.get(i);
//						if (listener.isAcceptingInput()) {
//							listener.mouseReleased(Mouse.getEventButton(), releasedX, releasedY);
//							if (consumed) {
//								break;
//							}
//						}
//					}
//				}
//			} else {
//				if (Mouse.isGrabbed()) {
//					if ((Mouse.getEventDX() != 0) || (Mouse.getEventDY() != 0)) {
//						consumed = false;
//						for (int i=0;i<mouseListeners.size();i++) {
//							MouseListener listener = mouseListeners.get(i);
//							if (listener.isAcceptingInput()) {
//								if (anyMouseDown()) {
//									listener.mouseDragged(0, 0, Mouse.getEventDX(), -Mouse.getEventDY());
//								} else {
//									listener.mouseMoved(0, 0, Mouse.getEventDX(), -Mouse.getEventDY());
//								}
//
//								if (consumed) {
//									break;
//								}
//							}
//						}
//					}
//				}
//
//				int dwheel = Mouse.getEventDWheel();
//				wheel += dwheel;
//				if (dwheel != 0) {
//					consumed = false;
//					for (int i=0;i<mouseListeners.size();i++) {
//						MouseListener listener = (MouseListener) mouseListeners.get(i);
//						if (listener.isAcceptingInput()) {
//							listener.mouseWheelMoved(dwheel);
//							if (consumed) {
//								break;
//							}
//						}
//					}
//				}
//			}
//		}

//		if ((lastMouseX != getMouseX()) || (lastMouseY != getMouseY())) {
//			consumed = false;
//			for (int i=0;i<mouseListeners.size();i++) {
//				MouseListener listener = mouseListeners.get(i);
//				if (listener.isAcceptingInput()) {
//					if (anyMouseDown()) {
//						listener.mouseDragged(lastMouseX ,  lastMouseY, getMouseX(), getMouseY());
//					} else {
//						listener.mouseMoved(lastMouseX ,  lastMouseY, getMouseX(), getMouseY());
//					}
//					if (consumed) {
//						break;
//					}
//				}
//			}
//			lastMouseX = getMouseX();
//			lastMouseY = getMouseY();
//		}
		
		if (controllersInitialized) {
			for (int i=0;i<getControllerCount();i++) {
				int count = (controllers.get(i)).getButtonCount()+3;
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
							KeyListener listener = keyListeners.get(j);

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

		for (ControlledInputReciever listener : allListeners) {
			listener.inputEnded();
		}
		
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
			ControllerListener listener = controllerListeners.get(i);
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
			ControllerListener listener = controllerListeners.get(i);
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

	private void fireMouseClicked(int button, int x, int y, int clickCount) {
		consumed = false;
		for (int i=0;i<mouseListeners.size();i++) {
			MouseListener listener = mouseListeners.get(i);
			if (listener.isAcceptingInput()) {
				listener.mouseClicked(button, x, y, clickCount);
				if (consumed) {
					break;
				}
			}
		}
	}
}
