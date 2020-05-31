package org.newdawn.slick.input.sources.controller;

import net.java.games.input.ControllerEnvironment;

import java.util.ArrayList;

public class Controllers {
    /** The controllers available */
    private static final ArrayList<JInputController> CONTROLLERS = new ArrayList<>();

    /** The current list of events */
    private static final ArrayList<ControllerEvent> EVENTS = new ArrayList<ControllerEvent>();

    /** The number of controllers */
    private static int controllerCount;
    /** The current event */
    private static ControllerEvent event;

    /** Whether controllers were created */
    private static boolean created;

    public static void create() {
        if (created)
            return;

        ControllerEnvironment env = ControllerEnvironment.getDefaultEnvironment();

        net.java.games.input.Controller[] found = env.getControllers();
        ArrayList<net.java.games.input.Controller> controllers = new ArrayList<>();
        for ( net.java.games.input.Controller c : found ) {
            if ( (!c.getType().equals(net.java.games.input.Controller.Type.KEYBOARD)) &&
                    (!c.getType().equals(net.java.games.input.Controller.Type.MOUSE)) ) {
                controllers.add(c);
            }
        }

        for ( net.java.games.input.Controller c : controllers ) {
            createController(c);
        }

        created = true;
    }

    /**
     * Utility to create a controller based on its potential sub-controllers
     *
     * @param c The controller to add
     */
    private static void createController(net.java.games.input.Controller c) {
        net.java.games.input.Controller[] subControllers = c.getControllers();
        if (subControllers.length == 0) {
            JInputController controller = new JInputController(controllerCount,c);

            CONTROLLERS.add(controller);
            controllerCount++;
        } else {
            for ( net.java.games.input.Controller sub : subControllers ) {
                createController(sub);
            }
        }
    }

    /**
     * Get a controller from the collection
     *
     * @param index The index of the controller to retrieve
     * @return The controller requested
     */
    public static Controller getController(int index) {
        return CONTROLLERS.get(index);
    }

    /**
     * Retrieve a count of the number of controllers
     *
     * @return The number of controllers available
     */
    public static int getControllerCount() {
        return CONTROLLERS.size();
    }

    /**
     * Poll the controllers available. This will both update their state
     * and generate events that must be cleared.
     */
    public static void poll() {
        for (int i = 0; i< CONTROLLERS.size(); i++) {
            getController(i).poll();
        }
    }

    /**
     * Clear any events stored for the controllers in this set
     */
    public static void clearEvents() {
        EVENTS.clear();
    }

    /**
     * Move to the next event that has been stored.
     *
     * @return True if there is still an event to process
     */
    public static boolean next() {
        if (EVENTS.size() == 0) {
            event = null;
            return false;
        }

        event = EVENTS.remove(0);

        return event != null;
    }

    /**
     * @return True if Controllers has been created
     */
    public static boolean isCreated() {
        return created;
    }

    /**
     * Destroys any resources used by the controllers
     */
    public static void destroy() {
// 		FIXME! not currently possible to destroy a controller

//		if (!created)
//			return;
//		created = false;
//
//		// nuke each controller
//		for (int i=0;i<controllers.size();i++) {
//			//
//		}
//
//		// cleanup
//		event = null;
//		events.clear();
//		controllers.clear();
//		controllerCount = 0;
    }

    /**
     * Get the source of the current event
     *
     * @return The source of the current event
     */
    public static Controller getEventSource() {
        return event.getSource();
    }

    /**
     * Get the index of the control that caused the current event
     *
     * @return The index of the control that cause the current event
     */
    public static int getEventControlIndex() {
        return event.getControlIndex();
    }

    /**
     * Check if the current event was caused by a button
     *
     * @return True if the current event was caused by a button
     */
    public static boolean isEventButton() {
        return event.isButton();
    }

    /**
     * Check if the current event was caused by a axis
     *
     * @return True if the current event was caused by a axis
     */
    public static boolean isEventAxis() {
        return event.isAxis();
    }

    /**
     * Check if the current event was caused by movement on the x-axis
     *
     * @return True if the current event was cause by movement on the x-axis
     */
    public static boolean isEventXAxis() {
        return event.isXAxis();
    }

    /**
     * Check if the current event was caused by movement on the y-axis
     *
     * @return True if the current event was caused by movement on the y-axis
     */
    public static boolean isEventYAxis() {
        return event.isYAxis();
    }

    /**
     * Check if the current event was cause by the POV x-axis
     *
     * @return True if the current event was caused by the POV x-axis
     */
    public static boolean isEventPovX() {
        return event.isPovX();
    }

    /**
     * Check if the current event was cause by the POV x-axis
     *
     * @return True if the current event was caused by the POV x-axis
     */
    public static boolean isEventPovY() {
        return event.isPovY();
    }

    /**
     * Get the timestamp assigned to the current event
     *
     * @return The timestamp assigned to the current event
     */
    public static long getEventNanoseconds() {
        return event.getTimeStamp();
    }

    /**
     * Gets the state of the button that generated the current event
     *
     * @return True if button was down, or false if released
     */
    public static boolean getEventButtonState() {
        return event.getButtonState();
    }

    /**
     * Get the value on an X axis of the current event
     *
     * @return The value on a x axis of the current event
     */
    public static float getEventXAxisValue() {
        return event.getXAxisValue();
    }

    /**
     * Get the value on an Y axis of the current event
     *
     * @return The value on a y axis of the current event
     */
    public static float getEventYAxisValue() {
        return event.getYAxisValue();
    }

    /**
     * Add an event to the stack of events that have been caused
     *
     * @param event The event to add to the list
     */
    static void addEvent(ControllerEvent event) {
        if (event != null) {
            EVENTS.add(event);
        }
    }
}
