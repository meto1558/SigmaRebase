package com.mentalfrostbyte.jello.util;

import com.mentalfrostbyte.jello.gui.unmapped.Class2287;
import net.minecraft.client.util.InputMappings;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class MultiUtilities {

    public static final float[] field24951 = new float[4];
    public static final float[] field24952 = new float[4];

    private static void transformVector(FloatBuffer matrixBuffer, float[] inputVector, float[] outputVector) {
        for (int i = 0; i < 4; i++) {
            outputVector[i] = inputVector[0] * matrixBuffer.get(matrixBuffer.position() + i)
                    + inputVector[1] * matrixBuffer.get(matrixBuffer.position() + 4 + i)
                    + inputVector[2] * matrixBuffer.get(matrixBuffer.position() + 8 + i)
                    + inputVector[3] * matrixBuffer.get(matrixBuffer.position() + 12 + i);
        }
    }

    public static boolean projectToScreen(float x, float y, float z, FloatBuffer modelMatrix, FloatBuffer projectionMatrix, IntBuffer viewport, FloatBuffer screenCoords) {
        float[] inVector = field24951;
        float[] outVector = field24952;

        // Load input coordinates into the vector
        inVector[0] = x;
        inVector[1] = y;
        inVector[2] = z;
        inVector[3] = 1.0F;

        // Apply the model and projection transformations
        transformVector(modelMatrix, inVector, outVector);
        transformVector(projectionMatrix, outVector, inVector);

        // Perform perspective division if the w-component is non-zero
        if ((double) inVector[3] != 0.0) {
            inVector[3] = 1.0F / inVector[3] * 0.5F;
            inVector[0] = inVector[0] * inVector[3] + 0.5F;
            inVector[1] = inVector[1] * inVector[3] + 0.5F;
            inVector[2] = inVector[2] * inVector[3] + 0.5F;

            // Map to screen coordinates using the viewport
            screenCoords.put(0, inVector[0] * (float) viewport.get(viewport.position() + 2) + (float) viewport.get(viewport.position() + 0));
            screenCoords.put(1, inVector[1] * (float) viewport.get(viewport.position() + 3) + (float) viewport.get(viewport.position() + 1));
            screenCoords.put(2, inVector[2]);

            return true;
        } else {
            return false;
        }
    }

    public static String method17736(int var0) {
        for (Class2287 var6 : Class2287.values()) {
            if (var6.field15204 == var0) {
                return var6.field15201;
            }
        }

        InputMappings.Input var7 = InputMappings.getInputByCode(var0, 0);
        String[] var8 = var7.getTranslationKey().split("\\.");
        if (var8.length != 0) {
            String var9 = var8[var8.length - 1];
            if (!var9.isEmpty()) {
                String var10 = "";
                if (var0 <= 4) {
                    var10 = "Mouse ";
                }

                return var10 + var9.substring(0, 1).toUpperCase() + var9.substring(1);
            } else {
                return "Unknown";
            }
        } else {
            return "Unknown";
        }
    }
}
