package com.mentalfrostbyte.jello.misc;

import java.io.File;
import java.util.HashMap;

public class NBSFile implements Cloneable {
    private HashMap<Integer, Class9616> map = new HashMap<>();
    private final short short1;
    private final short short2;
    private final String string1;
    private final File file;
    private final String string2;
    private final String string3;
    private final float field16345;
    private float field16346;
    private final Class8084[] field16347;
    private final int field16348;

    public NBSFile(NBSFile file) {
        this(
                file.method9957(),
                file.method9950(),
                file.method9951(),
                file.method9952(),
                file.method9953(),
                file.method9954(),
                file.method9956(),
                file.getFile(),
                file.method9960(),
                file.method9959()
        );
    }

    /** @deprecated */
    public NBSFile(float var1, HashMap<Integer, Class9616> var2, short var3, short var4, String var5, String var6, String var7, File var8) {
        this(var1, var2, var3, var4, var5, var6, var7, var8, Class9705.maxId(), new Class8084[0]);
    }

    /** @deprecated */
    public NBSFile(float var1, HashMap<Integer, Class9616> var2, short var3, short var4, String var5, String var6, String var7, File var8, Class8084[] var9) {
        this(var1, var2, var3, var4, var5, var6, var7, var8, Class9705.maxId(), var9);
    }

    public NBSFile(float var1, HashMap<Integer, Class9616> var2, short var3, short var4, String var5, String var6, String var7, File var8, int var9) {
        this(var1, var2, var3, var4, var5, var6, var7, var8, var9, new Class8084[0]);
    }

    public NBSFile(
            float var1, HashMap<Integer, Class9616> var2, short var3, short var4, String var5, String var6, String var7, File var8, int var9, Class8084[] var10
    ) {
        this.field16345 = var1;
        this.method9961(20.0F / var1);
        this.map = var2;
        this.short1 = var3;
        this.short2 = var4;
        this.string1 = var5;
        this.string2 = var6;
        this.string3 = var7;
        this.file = var8;
        this.field16348 = var9;
        this.field16347 = var10;
    }

    public HashMap<Integer, Class9616> method9950() {
        return this.map;
    }

    public short method9951() {
        return this.short1;
    }

    public short method9952() {
        return this.short2;
    }

    public String method9953() {
        return this.string1;
    }

    public String method9954() {
        return this.string2;
    }

    public File getFile() {
        return this.file;
    }

    public String method9956() {
        return this.string3;
    }

    public float method9957() {
        return this.field16345;
    }

    public float method9958() {
        return this.field16346;
    }

    public Class8084[] method9959() {
        return this.field16347;
    }

    @Override
    public NBSFile clone() {
        return new NBSFile(this);
    }

    public int method9960() {
        return this.field16348;
    }

    public void method9961(float var1) {
        this.field16346 = var1;
    }
}
