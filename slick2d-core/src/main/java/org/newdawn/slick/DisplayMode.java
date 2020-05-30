package org.newdawn.slick;

public class DisplayMode {

    public enum Opt {
        WINDOWED(0L),
        FULLSCREEN(1L),
        BORDERLESS(2L);

        private final long mode;

        Opt(long num) {
            this.mode = num;
        }

        public long toLong() {
            return mode;
        }
    }

    private static Opt DISPLAY_MODE = Opt.WINDOWED;
    private static int X_RES = 1600;
    private static int Y_RES = 900;

    public static Opt getDisplayType() {
        return DISPLAY_MODE;
    }

    public static int getWidth() {
        return X_RES;
    }

    public static int getHeight() {
        return Y_RES;
    }

    public static void setDisplayMode(int width, int height, Opt mode) {
        DISPLAY_MODE = mode;
        X_RES = width;
        Y_RES = height;
    }

    public static void setDisplayType(Opt mode) {
        DISPLAY_MODE = mode;
    }

    public static void setResolution(int width, int height) {
        X_RES = width;
        Y_RES = height;
    }

}
