package org.newdawn.slick.input.sources.controller;

import net.java.games.input.Component;
import net.java.games.input.Component.Identifier.Axis;
import net.java.games.input.Component.Identifier.Button;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;
import net.java.games.input.Rumbler;
import org.newdawn.slick.input.sources.controller.Controller;
import org.newdawn.slick.input.sources.controller.ControllerEvent;
import org.newdawn.slick.input.sources.controller.Controllers;

import java.util.ArrayList;

/**
 * A wrapper round a JInput controller that attempts to make the interface
 * more usable.
 *
 * @author Kevin Glass
 * @author tyler
 */
public class JInputController implements Controller {
    /** The JInput controller this class is wrapping */
    private final net.java.games.input.Controller target;
    /** The index that has been assigned to this controller */
    private final int index;
    /** The Buttons that have been detected on the JInput controller */
    private final ArrayList<Component> buttons = new ArrayList<>();
    /** The Axes that have been detected on the JInput controller */
    private final ArrayList<Component> axes = new ArrayList<>();
    /** The POVs that have been detected on the JInput controller */
    private final ArrayList<Component> pov = new ArrayList<>();
    /** The rumbles exposed by the controller */
    private final Rumbler[] rumbles;
    /** The state of the buttons last check */
    private final boolean[] buttonState;
    /** The values that were read from the pov last check */
    private final float[] povValues;
    /** The values that were read from the axes last check */
    private final float[] axesValue;
    /** The maximum values read for each axis */
    private final float[] axesMax;
    /** The dead zones for each axis */
    private final float[] deadZones;
    /** The index of the X axis or -1 if no X axis is defined */
    private int xAxis = -1;
    /** The index of the Y axis or -1 if no Y axis is defined */
    private int yAxis = -1;
    /** The index of the X axis or -1 if no Z axis is defined */
    private int zAxis = -1;
    /** The index of the RX axis or -1 if no RX axis is defined */
    private int rxAxis = -1;
    /** The index of the RY axis or -1 if no RY axis is defined */
    private int ryAxis = -1;
    /** The index of the RZ axis or -1 if no RZ axis is defined */
    private int rzAxis = -1;

    /**
     * Create a new controller that wraps round a JInput controller and hopefully
     * makes it easier to use.
     *
     * @param index The index this controller has been assigned to
     * @param target The target JInput controller this class is wrapping
     */
    JInputController(int index, net.java.games.input.Controller target) {
        this.target = target;
        this.index = index;

        Component[] sourceAxes = target.getComponents();

        for ( Component sourceAxis : sourceAxes ) {
            if ( sourceAxis.getIdentifier() instanceof Button) {
                buttons.add(sourceAxis);
            } else if ( sourceAxis.getIdentifier().equals(Axis.POV) ) {
                pov.add(sourceAxis);
            } else {
                axes.add(sourceAxis);
            }
        }

        buttonState = new boolean[buttons.size()];
        povValues = new float[pov.size()];
        axesValue = new float[axes.size()];
        int buttonsCount = 0;
        int axesCount = 0;

        // initialise the state
        for ( Component sourceAxis : sourceAxes ) {
            if ( sourceAxis.getIdentifier() instanceof Button ) {
                buttonState[buttonsCount] = sourceAxis.getPollData() != 0;
                buttonsCount++;
            } else if ( sourceAxis.getIdentifier().equals(Axis.POV) ) {
                // no account for POV yet
                // pov.add(sourceAxes[i]);
            } else {
                axesValue[axesCount] = sourceAxis.getPollData();
                if ( sourceAxis.getIdentifier().equals(Axis.X) ) {
                    xAxis = axesCount;
                }
                if ( sourceAxis.getIdentifier().equals(Axis.Y) ) {
                    yAxis = axesCount;
                }
                if ( sourceAxis.getIdentifier().equals(Axis.Z) ) {
                    zAxis = axesCount;
                }
                if ( sourceAxis.getIdentifier().equals(Axis.RX) ) {
                    rxAxis = axesCount;
                }
                if ( sourceAxis.getIdentifier().equals(Axis.RY) ) {
                    ryAxis = axesCount;
                }
                if ( sourceAxis.getIdentifier().equals(Axis.RZ) ) {
                    rzAxis = axesCount;
                }

                axesCount++;
            }
        }

        axesMax = new float[axes.size()];
        deadZones = new float[axes.size()];

        for (int i=0;i<axesMax.length;i++) {
            axesMax[i] = 1.0f;
            deadZones[i] = 0.05f;
        }

        rumbles = target.getRumblers();
    }

    /*
     * @see org.lwjgl.input.Controller#getName()
     */
    public String getName() {
        return target.getName();
    }

    /*
     * @see org.lwjgl.input.Controller#getIndex()
     */
    public int getIndex() {
        return index;
    }

    /*
     * @see org.lwjgl.input.Controller#getButtonCount()
     */
    public int getButtonCount() {
        return buttons.size();
    }

    /*
     * @see org.lwjgl.input.Controller#getButtonName(int)
     */
    public String getButtonName(int index) {
        return buttons.get(index).getName();
    }

    /*
     * @see org.lwjgl.input.Controller#isButtonPressed(int)
     */
    public boolean isButtonPressed(int index) {
        return buttonState[index];
    }

    /*
     * @see org.lwjgl.input.Controller#poll()
     */
    public void poll() {
        target.poll();

        Event event = new Event();
        EventQueue queue = target.getEventQueue();

        while (queue.getNextEvent(event)) {
            // handle button event
            if (buttons.contains(event.getComponent())) {
                Component button = event.getComponent();
                int buttonIndex = buttons.indexOf(button);
                buttonState[buttonIndex] = event.getValue() != 0;

                // fire button pressed event
                Controllers.addEvent(new ControllerEvent(this,event.getNanos(),ControllerEvent.BUTTON,buttonIndex,
                        buttonState[buttonIndex],false,false,0,0));
            }

            // handle pov events
            if (pov.contains(event.getComponent())) {
                Component povComponent = event.getComponent();
                int povIndex = pov.indexOf(povComponent);
                float prevX = getPovX();
                float prevY = getPovY();
                povValues[povIndex] = event.getValue();

                if (prevX != getPovX()) {
                    Controllers.addEvent(new ControllerEvent(this,event.getNanos(),ControllerEvent.POVX,0,false,false));
                }
                if (prevY != getPovY()) {
                    Controllers.addEvent(new ControllerEvent(this,event.getNanos(),ControllerEvent.POVY,0,false,false));
                }
            }

            // handle axis updates
            if (axes.contains(event.getComponent())) {
                Component axis = event.getComponent();
                int axisIndex = axes.indexOf(axis);
                float value = axis.getPollData();
                float xaxisValue = 0;
                float yaxisValue = 0;

                // fixed dead zone since most axis don't report it :(
                if (Math.abs(value) < deadZones[axisIndex]) {
                    value = 0;
                }
                if (Math.abs(value) < axis.getDeadZone()) {
                    value = 0;
                }
                if (Math.abs(value) > axesMax[axisIndex]) {
                    axesMax[axisIndex] = Math.abs(value);
                }

                // normalize the value based on maximum value read in the past
                value /= axesMax[axisIndex];

                if (axisIndex == xAxis) {
                    xaxisValue = value;
                }
                if (axisIndex == yAxis) {
                    yaxisValue = value;
                }

                // fire event
                Controllers.addEvent(new ControllerEvent(this,event.getNanos(),ControllerEvent.AXIS,axisIndex,false,
                        axisIndex == xAxis,axisIndex == yAxis,xaxisValue,yaxisValue));
                axesValue[axisIndex] = value;
            }
        }
    }

    /*
     * @see org.lwjgl.input.Controller#getAxisCount()
     */
    public int getAxisCount() {
        return axes.size();
    }

    /*
     * @see org.lwjgl.input.Controller#getAxisName(int)
     */
    public String getAxisName(int index) {
        return axes.get(index).getName();
    }

    /*
     * @see org.lwjgl.input.Controller#getAxisValue(int)
     */
    public float getAxisValue(int index) {
        return axesValue[index];
    }

    /*
     * @see org.lwjgl.input.Controller#getXAxisValue()
     */
    public float getXAxisValue() {
        if (xAxis == -1) {
            return 0;
        }

        return getAxisValue(xAxis);
    }

    /*
     * @see org.lwjgl.input.Controller#getYAxisValue()
     */
    public float getYAxisValue() {
        if (yAxis == -1) {
            return 0;
        }

        return getAxisValue(yAxis);
    }

    /*
     * @see org.lwjgl.input.Controller#getXAxisDeadZone()
     */
    public float getXAxisDeadZone() {
        if (xAxis == -1) {
            return 0;
        }

        return getDeadZone(xAxis);
    }

    /*
     * @see org.lwjgl.input.Controller#getYAxisDeadZone()
     */
    public float getYAxisDeadZone() {
        if (yAxis == -1) {
            return 0;
        }

        return getDeadZone(yAxis);
    }

    /*
     * @see org.lwjgl.input.Controller#setXAxisDeadZone(float)
     */
    public void setXAxisDeadZone(float zone) {
        setDeadZone(xAxis,zone);
    }

    /*
     * @see org.lwjgl.input.Controller#setYAxisDeadZone(float)
     */
    public void setYAxisDeadZone(float zone) {
        setDeadZone(yAxis,zone);
    }

    /*
     * @see org.lwjgl.input.Controller#getDeadZone(int)
     */
    public float getDeadZone(int index) {
        return deadZones[index];
    }

    /*
     * @see org.lwjgl.input.Controller#setDeadZone(int, float)
     */
    public void setDeadZone(int index, float zone) {
        deadZones[index] = zone;
    }

    /*
     * @see org.lwjgl.input.Controller#getZAxisValue()
     */
    public float getZAxisValue() {
        if (zAxis == -1) {
            return 0;
        }

        return getAxisValue(zAxis);
    }

    /*
     * @see org.lwjgl.input.Controller#getZAxisDeadZone()
     */
    public float getZAxisDeadZone() {
        if (zAxis == -1) {
            return 0;
        }

        return getDeadZone(zAxis);
    }

    /*
     * @see org.lwjgl.input.Controller#setZAxisDeadZone(float)
     */
    public void setZAxisDeadZone(float zone) {
        setDeadZone(zAxis,zone);
    }

    /*
     * @see org.lwjgl.input.Controller#getRXAxisValue()
     */
    public float getRXAxisValue() {
        if (rxAxis == -1) {
            return 0;
        }

        return getAxisValue(rxAxis);
    }

    /*
     * @see org.lwjgl.input.Controller#getRXAxisDeadZone()
     */
    public float getRXAxisDeadZone() {
        if (rxAxis == -1) {
            return 0;
        }

        return getDeadZone(rxAxis);
    }

    /*
     * @see org.lwjgl.input.Controller#setRXAxisDeadZone(float)
     */
    public void setRXAxisDeadZone(float zone) {
        setDeadZone(rxAxis,zone);
    }

    /*
     * @see org.lwjgl.input.Controller#getRYAxisValue()
     */
    public float getRYAxisValue() {
        if (ryAxis == -1) {
            return 0;
        }

        return getAxisValue(ryAxis);
    }

    /*
     * @see org.lwjgl.input.Controller#getRYAxisDeadZone()
     */
    public float getRYAxisDeadZone() {
        if (ryAxis == -1) {
            return 0;
        }

        return getDeadZone(ryAxis);
    }

    /*
     * @see org.lwjgl.input.Controller#setRYAxisDeadZone(float)
     */
    public void setRYAxisDeadZone(float zone) {
        setDeadZone(ryAxis,zone);
    }

    /*
     * @see org.lwjgl.input.Controller#getRZAxisValue()
     */
    public float getRZAxisValue() {
        if (rzAxis == -1) {
            return 0;
        }

        return getAxisValue(rzAxis);
    }

    /*
     * @see org.lwjgl.input.Controller#getRZAxisDeadZone()
     */
    public float getRZAxisDeadZone() {
        if (rzAxis == -1) {
            return 0;
        }

        return getDeadZone(rzAxis);
    }

    /*
     * @see org.lwjgl.input.Controller#setRZAxisDeadZone(float)
     */
    public void setRZAxisDeadZone(float zone) {
        setDeadZone(rzAxis,zone);
    }

    /*
     * @see org.lwjgl.input.Controller#getPovX()
     */
    public float getPovX() {
        if (pov.size() == 0) {
            return 0;
        }

        float value = povValues[0];

        if ((value == Component.POV.DOWN_LEFT) ||
                (value == Component.POV.UP_LEFT) ||
                (value == Component.POV.LEFT)) {
            return -1;
        }
        if ((value == Component.POV.DOWN_RIGHT) ||
                (value == Component.POV.UP_RIGHT) ||
                (value == Component.POV.RIGHT)) {
            return 1;
        }

        return 0;
    }

    /*
     * @see org.lwjgl.input.Controller#getPovY()
     */
    public float getPovY() {
        if (pov.size() == 0) {
            return 0;
        }

        float value = povValues[0];

        if ((value == Component.POV.DOWN_LEFT) ||
                (value == Component.POV.DOWN_RIGHT) ||
                (value == Component.POV.DOWN)) {
            return 1;
        }
        if ((value == Component.POV.UP_LEFT) ||
                (value == Component.POV.UP_RIGHT) ||
                (value == Component.POV.UP)) {
            return -1;
        }

        return 0;
    }

    public int getRumblerCount() {
        return rumbles.length;
    }

    public String getRumblerName(int index) {
        return rumbles[index].getAxisName();
    }

    public void setRumblerStrength(int index, float strength) {
        rumbles[index].rumble(strength);
    }
}
