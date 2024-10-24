package com.mentalfrostbyte.jello.utils.render;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ColorUtils {
    private static final Minecraft mc = Minecraft.getInstance();
    public static final float[] field24951 = new float[4];
    public static final float[] field24952 = new float[4];
    public static final ResourceLocation field24953 = new ResourceLocation("shaders/post/blur.json");
    private static boolean field24954 = false;

    public static int applyAlpha(int color, float alpha) {
        return (int)(alpha * 255.0F) << 24 | color & 16777215;
    }

    public static int method17691(int var0, float var1) {
        int var4 = var0 >> 24 & 0xFF;
        int var5 = var0 >> 16 & 0xFF;
        int var6 = var0 >> 8 & 0xFF;
        int var7 = var0 & 0xFF;
        int var8 = (int)((float)var5 * (1.0F - var1));
        int var9 = (int)((float)var6 * (1.0F - var1));
        int var10 = (int)((float)var7 * (1.0F - var1));
        return var4 << 24 | (var8 & 0xFF) << 16 | (var9 & 0xFF) << 8 | var10 & 0xFF;
    }

    public static List<PlayerEntity> method17680() {
        ArrayList<PlayerEntity> var2 = new ArrayList<>();
        mc.world.entitiesById.forEach((var1, var2x) -> {
            if (var2x instanceof PlayerEntity) {
                var2.add((PlayerEntity)var2x);
            }
        });
        return var2;
    }
}
