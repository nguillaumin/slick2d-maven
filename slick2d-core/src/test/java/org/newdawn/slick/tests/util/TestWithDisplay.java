package org.newdawn.slick.tests.util;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_TRUE;

public abstract class TestWithDisplay extends TestWithLWJGL {

    private long window = -1L;

    @BeforeClass
    public void createDisplay() {
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        window = glfwCreateWindow(100, 100, "LWJGL 3 - Display Test", 0, 0);
    }

    @AfterClass
    public void destroyDisplay() {
        org.lwjgl.glfw.GLFW.glfwDestroyWindow(window);
    }

}
