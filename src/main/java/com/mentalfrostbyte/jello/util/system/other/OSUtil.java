package com.mentalfrostbyte.jello.util.system.other;

public class OSUtil {

    public static boolean hasAndroidFS() {
        return new java.io.File("/storage/emulated/0").exists() || new java.io.File("/sdcard").exists();
    }

}
