package org.newdawn.slick.tests.util;

import java.io.File;

import org.testng.annotations.BeforeTest;

public abstract class TestWithLWJGL {

    @BeforeTest
    public void setLWJGLLibraryPath() {
        System.setProperty("org.lwjgl.librarypath",
            new File(".", "target" + File.separator + "natives").getAbsolutePath());
    }

}
