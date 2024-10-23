package com.mentalfrostbyte.jello.utils.render;

public class ColorUtils {

    public static int applyAlpha(int color, float alpha) {
        return (int)(alpha * 255.0F) << 24 | color & 16777215;
    }

}
