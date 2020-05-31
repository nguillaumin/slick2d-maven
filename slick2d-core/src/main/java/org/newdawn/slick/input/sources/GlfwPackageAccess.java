package org.newdawn.slick.input.sources;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public abstract class GlfwPackageAccess {
    public static final Object global_lock;

    static {
        try {
            global_lock = AccessController.doPrivileged((PrivilegedExceptionAction<Object>) () -> {
                Field lock_field = Class.forName("org.lwjgl.opengl.GlobalLock").getDeclaredField("lock");
                lock_field.setAccessible(true);
                return lock_field.get(null);
            });
        } catch (PrivilegedActionException e) {
            throw new Error(e);
        }
    }

    // todo remove if not using this
//    static InputImplementation createImplementation() {
//        /* Use reflection since we can't make Display.getImplementation
//         * public
//         */
//        try {
//            return AccessController.doPrivileged(new PrivilegedExceptionAction<InputImplementation>() {
//                public InputImplementation run() throws Exception {
//                    Method getImplementation_method = Display.class.getDeclaredMethod("getImplementation");
//                    getImplementation_method.setAccessible(true);
//                    return (InputImplementation)getImplementation_method.invoke(null);
//                }
//            });
//        } catch (PrivilegedActionException e) {
//            throw new Error(e);
//        }
//    }
}
