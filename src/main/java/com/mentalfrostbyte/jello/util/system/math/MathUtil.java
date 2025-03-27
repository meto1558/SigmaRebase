package com.mentalfrostbyte.jello.util.system.math;

import com.mentalfrostbyte.jello.util.system.math.vector.Vector2d;

import java.util.ArrayList;
import java.util.List;

public class MathUtil {
    private final double field38710;

    public MathUtil(double var1) {
        if (!(var1 <= 0.0) && !(var1 >= 1.0)) {
            this.field38710 = var1;
        } else {
            throw new AssertionError("Smoothness must be between 0 and 1 (both non-inclusive)");
        }
    }

    public static float lerp(float var0, double... var1) {
        ArrayList<Vector2d> var4 = new ArrayList<>();
        var4.add(new Vector2d(0.0, 0.0));
        var4.add(new Vector2d(var1[0], var1[1]));
        var4.add(new Vector2d(var1[2], var1[3]));
        var4.add(new Vector2d(1.0, 1.0));
        MathUtil mathUtil = new MathUtil(0.0055555557F);
        return (float) mathUtil.method30789(var4, var0);
    }

    public Vector2d method30787(Vector2d var1, Vector2d var2, Vector2d var3, double var4) {
        double var8 = (1.0 - var4) * (1.0 - var4) * var1.method38553() + 2.0 * var4 * (1.0 - var4) * var2.method38553() + var4 * var4 * var3.method38553();
        double var10 = (1.0 - var4) * (1.0 - var4) * var1.method38554() + 2.0 * var4 * (1.0 - var4) * var2.method38554() + var4 * var4 * var3.method38554();
        return new Vector2d(var8, var10);
    }

    public Vector2d method30788(Vector2d var1, Vector2d var2, Vector2d var3, Vector2d var4, double var5) {
        double var9 = 1.0 - var5;
        double var11 = var9 * var9;
        double var13 = var11 * var9;
        double var15 = var1.method38553() * var13
                + var2.method38553() * 3.0 * var5 * var11
                + var3.method38553() * 3.0 * var5 * var5 * var9
                + var4.method38553() * var5 * var5 * var5;
        double var17 = var1.method38554() * var13
                + var2.method38554() * 3.0 * var5 * var11
                + var3.method38554() * 3.0 * var5 * var5 * var9
                + var4.method38554() * var5 * var5 * var5;
        return new Vector2d(var15, var17);
    }

    public double method30789(List<Vector2d> var1, float var2) {
        if (var2 == 0.0F) {
            return 0.0;
        } else {
            List var5 = this.method30790(var1);
            double var6 = 1.0;

            for (int var8 = 0; var8 < var5.size(); var8++) {
                Vector2d var9 = (Vector2d) var5.get(var8);
                if (!(var9.method38553() <= (double) var2)) {
                    break;
                }

                var6 = var9.method38554();
                Vector2d var10 = new Vector2d(1.0, 1.0);
                if (var8 + 1 < var5.size()) {
                    var10 = (Vector2d) var5.get(var8 + 1);
                }

                double var11 = var10.method38553() - var9.method38553();
                double var13 = var10.method38554() - var9.method38554();
                double var15 = (double) var2 - var9.method38553();
                double var17 = var15 / var11;
                var6 += var13 * var17;
            }

            return var6;
        }
    }

    public List<Vector2d> method30790(List<Vector2d> var1) {
        if (var1 != null) {
            if (var1.size() >= 3) {
                Vector2d var4 = var1.get(0);
                Vector2d var5 = var1.get(1);
                Vector2d var6 = var1.get(2);
                Vector2d var7 = var1.size() != 4 ? null : var1.get(3);
                ArrayList<Vector2d> var11 = new ArrayList<>();
                Vector2d var8 = var4;
                double var9 = 0.0;

                while (var9 < 1.0) {
                    var11.add(var8);
                    var8 = var7 != null ? this.method30788(var4, var5, var6, var7, var9) : this.method30787(var4, var5, var6, var9);
                    var9 += this.field38710;
                }

                return var11;
            } else {
                return null;
            }
        } else {
            throw new AssertionError("Provided list had no reference");
        }
    }

}
