package org.newdawn.slick.tests.util;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public abstract class TestWithDisplay extends TestWithLWJGL {

    @BeforeClass
    public void createDisplay() throws LWJGLException {
        Display.setDisplayMode(new DisplayMode(1, 1));
        Display.create();
    }

    @AfterClass
    public void destroyDisplay() {
        Display.destroy();
    }

}
