package com.developcollect.core.utils;


import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_ProfileRGB;

public class DisplayUtil {

    public static String[] getScreenDescriptions() {
        GraphicsDevice[] screens = getScreenDevices();
        String[] descriptions = new String[screens.length];
        for (int i = 0; i < screens.length; i++) {
            descriptions[i] = screens[i].getIDstring();
        }
        return descriptions;
    }

    public static DisplayMode getDisplayMode(int screenNumber) {
        GraphicsDevice[] screens = getScreenDevices();
        if (screenNumber >= 0 && screenNumber < screens.length) {
            return screens[screenNumber].getDisplayMode();
        } else {
            return null;
        }
    }

    public static double getGamma(int screenNumber) {
        GraphicsDevice[] screens = getScreenDevices();
        if (screenNumber >= 0 && screenNumber < screens.length) {
            return getGamma(screens[screenNumber]);
        } else {
            return 0.0;
        }
    }

    public static double getDefaultGamma() {
        return getGamma(getDefaultScreenDevice());
    }

    public static double getGamma(GraphicsDevice screen) {
        ColorSpace cs = screen.getDefaultConfiguration().getColorModel().getColorSpace();
        if (cs.isCS_sRGB()) {
            return 2.2;
        } else {
            try {
                return ((ICC_ProfileRGB) ((ICC_ColorSpace) cs).getProfile()).getGamma(0);
            } catch (RuntimeException e) {
            }
        }
        return 0.0;
    }

    public static GraphicsDevice getScreenDevice(int screenNumber) throws Exception {
        GraphicsDevice[] screens = getScreenDevices();
        if (screenNumber >= screens.length) {
            throw new Exception("CanvasFrame Error: Screen number " + screenNumber + " not found. " +
                    "There are only " + screens.length + " screens.");
        }
        return screens[screenNumber];//.getDefaultConfiguration();
    }

    public static GraphicsDevice[] getScreenDevices() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
    }

    public static GraphicsDevice getDefaultScreenDevice() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    }


}
