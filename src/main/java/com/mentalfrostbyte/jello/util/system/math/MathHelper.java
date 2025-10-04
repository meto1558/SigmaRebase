package com.mentalfrostbyte.jello.util.system.math;

import javax.sound.sampled.AudioFormat;
import java.nio.FloatBuffer;

public class MathHelper {
    public static float calculateBackwardTransition(float var0, float var1, float var2, float var3) {
        var0 /= var3;
        return var2 * var0 * var0 * var0 + var1;
    }

    public static float calculateTransition(float var0, float var1, float var2, float var3) {
        var0 /= var3;
        return var2 * (var0 * var0 * --var0 + 1.0F) + var1;
    }

    public static double getRandomValue() {
        return Math.random() * 1.0E-8;
    }

    public static float[] convertToPCMFloatArray(byte[] audioBytes, AudioFormat audioFormat) {
        float[] pcmValues = new float[audioBytes.length / audioFormat.getFrameSize()];

        for (int i = 0; i < audioBytes.length; i += audioFormat.getFrameSize()) {
            int sample = !audioFormat.isBigEndian() ? bytesToIntLE(audioBytes, i, audioFormat.getFrameSize()) : bytesToIntBE(audioBytes, i, audioFormat.getFrameSize());
            pcmValues[i / audioFormat.getFrameSize()] = (float) sample / 32768.0F;
        }

        return pcmValues;
    }

    public static double[] calculateAmplitudes(float[] realPart, float[] imaginaryPart) {
        double[] amplitudes = new double[realPart.length / 2];

        for (int i = 0; i < amplitudes.length; i++) {
            // Calculate magnitude using the Pythagorean theorem
            amplitudes[i] = Math.sqrt(realPart[i] * realPart[i] + imaginaryPart[i] * imaginaryPart[i]);
        }

        return amplitudes;
    }

    public static int bytesToIntLE(byte[] byteArray, int startIndex, int length) {
        int result = 0;

        for (int i = 0; i < length; i++) {
            // Extract the byte and shift it to its correct position
            int currentByte = byteArray[startIndex + i] & 0xFF;
            result += currentByte << (8 * i);
        }

        return result;
    }

    public static int bytesToIntBE(byte[] byteArray, int startIndex, int length) {
        int result = 0;

        for (int i = 0; i < length; i++) {
            // Extract the byte and shift it to its correct position
            int currentByte = byteArray[startIndex + i] & 0xFF;
            result += currentByte << (8 * (length - i - 1));
        }

        return result;
    }

    public static double generateRandomSmallValue() {
        return Math.random() * 1.0E-8;
    }

    public static double round(float number,float roundTo) {
        double rounded = Math.round(number / roundTo);

        return rounded * roundTo;
    }


    public static int applyAlpha(int color, float alpha) {
        return (int) (alpha * 255.0F) << 24 | color & 16777215;
    }

    public static float[] argbToNormalizedRGBA(int argbColor) {
        float alpha = ((argbColor >> 24) & 0xFF) / 255.0F;
        float red = ((argbColor >> 16) & 0xFF) / 255.0F;
        float green = ((argbColor >> 8) & 0xFF) / 255.0F;
        float blue = (argbColor & 0xFF) / 255.0F;

        return new float[]{red, green, blue, alpha};
    }

    public static int darkenColor(int argbColor, float darknessFactor) {
        int alpha = (argbColor >> 24) & 0xFF;
        int red = (argbColor >> 16) & 0xFF;
        int green = (argbColor >> 8) & 0xFF;
        int blue = argbColor & 0xFF;

        int darkRed = (int) (red * (1.0F - darknessFactor));
        int darkGreen = (int) (green * (1.0F - darknessFactor));
        int darkBlue = (int) (blue * (1.0F - darknessFactor));

        return (alpha << 24) | ((darkRed & 0xFF) << 16) | ((darkGreen & 0xFF) << 8) | (darkBlue & 0xFF);
    }

    public static int blendARGB(int colorA, int colorB, float weightA) {
        int alphaA = (colorA >> 24) & 0xFF;
        int redA = (colorA >> 16) & 0xFF;
        int greenA = (colorA >> 8) & 0xFF;
        int blueA = colorA & 0xFF;

        int alphaB = (colorB >> 24) & 0xFF;
        int redB = (colorB >> 16) & 0xFF;
        int greenB = (colorB >> 8) & 0xFF;
        int blueB = colorB & 0xFF;

        float weightB = 1.0F - weightA;

        int blendedAlpha = (int) (alphaA * weightA + alphaB * weightB);
        int blendedRed = (int) (redA * weightA + redB * weightB);
        int blendedGreen = (int) (greenA * weightA + greenB * weightB);
        int blendedBlue = (int) (blueA * weightA + blueB * weightB);

        return (blendedAlpha << 24) | ((blendedRed & 0xFF) << 16) | ((blendedGreen & 0xFF) << 8) | (blendedBlue & 0xFF);
    }

    public static java.awt.Color averageColors(java.awt.Color... colors) {
        if (colors != null && colors.length > 0) {
            float weight = 1.0F / colors.length;
            float redTotal = 0.0F;
            float greenTotal = 0.0F;
            float blueTotal = 0.0F;
            float alphaTotal = 0.0F;

            for (java.awt.Color color : colors) {
                if (color == null) {
                    color = java.awt.Color.BLACK;
                }

                redTotal += color.getRed() * weight;
                greenTotal += color.getGreen() * weight;
                blueTotal += color.getBlue() * weight;
                alphaTotal += color.getAlpha() * weight;
            }

            return new java.awt.Color(redTotal / 255.0F, greenTotal / 255.0F, blueTotal / 255.0F, alphaTotal / 255.0F);
        } else {
            return java.awt.Color.WHITE;
        }
    }

    public static java.awt.Color blendColors(java.awt.Color colorA, java.awt.Color colorB, float weightA) {
        float weightB = 1.0F - weightA;
        float red = (colorA.getRed() * weightA) + (colorB.getRed() * weightB);
        float green = (colorA.getGreen() * weightA) + (colorB.getGreen() * weightB);
        float blue = (colorA.getBlue() * weightA) + (colorB.getBlue() * weightB);
        return new java.awt.Color(red / 255.0F, green / 255.0F, blue / 255.0F);
    }

    public static void transformVector(FloatBuffer matrixBuffer, float[] inputVector, float[] outputVector) {
        for (int i = 0; i < 4; i++) {
            outputVector[i] = inputVector[0] * matrixBuffer.get(matrixBuffer.position() + i)
                    + inputVector[1] * matrixBuffer.get(matrixBuffer.position() + 4 + i)
                    + inputVector[2] * matrixBuffer.get(matrixBuffer.position() + 8 + i)
                    + inputVector[3] * matrixBuffer.get(matrixBuffer.position() + 12 + i);
        }
    }
}
