package com.mentalfrostbyte.jello.util.system.math;

import javax.sound.sampled.AudioFormat;
import java.awt.*;
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

    /**
     * Adjusts the RGB components of a color towards black by a specified factor.
     * The alpha component remains unchanged.
     *
     * @param color The original color represented as an integer, where the highest byte is the alpha component,
     *              followed by red, green, and blue components.
     * @param shift The factor by which to adjust the color towards black. A value of 0.0 will leave the color unchanged,
     *              while a value of 1.0 will result in a completely black color.
     * @return The adjusted color as an integer, with the same alpha component as the original color and RGB components
     * adjusted towards black by the specified factor.
     */
    public static int shiftTowardsBlack(int color, float shift) {
        int a = color >> 24 & 0xFF;
        int r = color >> 16 & 0xFF;
        int g = color >> 8 & 0xFF;
        int b = color & 0xFF;
        int shiftedR = (int) ((float) r * (1.0F - shift));
        int shiftedG = (int) ((float) g * (1.0F - shift));
        int shiftedB = (int) ((float) b * (1.0F - shift));
        return a << 24 | (shiftedR & 0xFF) << 16 | (shiftedG & 0xFF) << 8 | shiftedB & 0xFF;
    }

    /**
     * Adjusts the RGB components of 2 colors towards black by 1 specified factor.
     * The alpha component remains unchanged.
     *
     * @param color  The 1st original color represented as an integer, where the highest byte is the alpha component,
     *               followed by red, green, and blue components.
     * @param color2 The 2nd original color represented as an integer, where the highest byte is the alpha component,
     *               followed by red, green, and blue components.
     * @param shift  The factor by which to adjust the color towards black. A value of 0.0 will leave the color unchanged,
     *               while a value of 1.0 will result in the same color as the other.
     * @return The adjusted color as an integer, with the same alpha component as the original color and RGB components
     * adjusted towards each-other, I think...
     */
    public static int shiftTowardsOther(int color, int color2, float shift) {
        int a1 = color >> 24 & 0xFF;
        int r1 = color >> 16 & 0xFF;
        int g1 = color >> 8 & 0xFF;
        int b1 = color & 0xFF;
        int a2 = color2 >> 24 & 0xFF;
        int r2 = color2 >> 16 & 0xFF;
        int g2 = color2 >> 8 & 0xFF;
        int b2 = color2 & 0xFF;
        float factor = 1.0F - shift;
        float shiftedA = (float) a1 * shift + (float) a2 * factor;
        float shiftedR = (float) r1 * shift + (float) r2 * factor;
        float shiftedG = (float) g1 * shift + (float) g2 * factor;
        float shiftedB = (float) b1 * shift + (float) b2 * factor;
        return (int) shiftedA << 24 | ((int) shiftedR & 0xFF) << 16 | ((int) shiftedG & 0xFF) << 8 | (int) shiftedB & 0xFF;
    }

    public static float[] intColorToFloatArrayColor(int color) {
        float a = (float) (color >> 24 & 0xFF) / 255.0F;
        float r = (float) (color >> 16 & 0xFF) / 255.0F;
        float g = (float) (color >> 8 & 0xFF) / 255.0F;
        float b = (float) (color & 0xFF) / 255.0F;
        return new float[]{r, g, b, a};
    }

    /**
     * Adjusts the RGB components of a color towards white by a specified factor.
     *
     * @param original The original color represented as an integer, where the highest byte is the alpha component,
     *                 followed by red, green, and blue components.
     * @param shift    The factor by which to adjust the color towards white. A value of 0.0 will leave the color unchanged,
     *                 while a value of 1.0 will result in a completely white color.
     * @return The adjusted color as an integer, with the same alpha component as the original color and RGB components
     * adjusted towards white by the specified factor.
     */
    public static int adjustColorTowardsWhite(int original, float shift) {
        int a = original >> 24 & 0xFF;
        int r = original >> 16 & 0xFF;
        int g = original >> 8 & 0xFF;
        int b = original & 0xFF;
        int var8 = (int) ((float) r + (float) (255 - r) * shift);
        int var9 = (int) ((float) g + (float) (255 - g) * shift);
        int var10 = (int) ((float) b + (float) (255 - b) * shift);
        return a << 24 | (var8 & 0xFF) << 16 | (var9 & 0xFF) << 8 | var10 & 0xFF;
    }

    public static Color calculateAverageColor(Color... colors) {
        if (colors == null || colors.length == 0) {
            return Color.WHITE;
        }

        float weight = 1.0F / colors.length;
        float totalRed = 0.0F;
        float totalGreen = 0.0F;
        float totalBlue = 0.0F;
        float totalAlpha = 0.0F;

        for (Color color : colors) {
            if (color == null) {
                color = Color.BLACK;
            }

            totalRed += color.getRed() * weight;
            totalGreen += color.getGreen() * weight;
            totalBlue += color.getBlue() * weight;
            totalAlpha += color.getAlpha() * weight;
        }

        return new Color(totalRed / 255.0F, totalGreen / 255.0F, totalBlue / 255.0F, totalAlpha / 255.0F);
    }

    /**
     * Blends two colors by a specified factor, resulting in a color that is a mix of the two.
     * The blending is done by interpolating each RGB component separately.
     *
     * @param first  The first color to blend, represented as a {@link Color} object.
     * @param second The second color to blend, represented as a {@link Color} object.
     * @param factor The blending factor, where 0.0 results in the second color, and 1.0 results in the first color.
     *               Values between 0.0 and 1.0 will result in a mix of the two colors.
     * @return A new {@link Color} object representing the blended color.
     */
    public static Color blendColor(Color first, Color second, float factor) {
        float newFactor = 1.0F - factor;
        float blendedR = (float) first.getRed() * factor + (float) second.getRed() * newFactor;
        float blendedG = (float) first.getGreen() * factor + (float) second.getGreen() * newFactor;
        float blendedB = (float) first.getBlue() * factor + (float) second.getBlue() * newFactor;
        return new Color(blendedR / 255.0F, blendedG / 255.0F, blendedB / 255.0F);
    }

    public static float[] calculateAspectRatioFit(float sourceWidth, float sourceHeight, float targetWidth, float targetHeight) {
        float sourceAspect = sourceWidth / sourceHeight;
        float targetAspect = targetWidth / targetHeight;
        float fittedWidth;
        float fittedHeight;

        if (targetAspect > sourceAspect) {
            fittedWidth = targetWidth;
            fittedHeight = sourceHeight * targetWidth / sourceWidth;
        } else {
            fittedWidth = sourceWidth * targetHeight / sourceHeight;
            fittedHeight = targetHeight;
        }

        float offsetX = (targetWidth - fittedWidth) / 2.0F;
        float offsetY = (targetHeight - fittedHeight) / 2.0F;

        return new float[]{offsetX, offsetY, fittedWidth, fittedHeight};
    }

    public static float getAlpha(int color) {
        return (float) (color >> 24 & 0xFF) / 255.0F;
    }

    public static int applyAlpha2(int color, float alpha) {
        return (int) (alpha * 255.0F) << 24 | color & 16777215;
    }

    public static float interpolateAngle(float currentAngle, float targetAngle) {
        float wrappedDifference = net.minecraft.util.math.MathHelper.wrapDegrees(targetAngle - currentAngle);
        return currentAngle + wrappedDifference;
    }

    public static float angleDiff(float var0, float var1) {
        float var4 = Math.abs(var0 - var1) % 360.0F;
        if (var4 > 180.0F) {
            var4 = 360.0F - var4;
        }

        return var4;
    }

    public static float getShortestYawDifference(float yaw1, float yaw2) {
        yaw1 %= 360.0F;
        yaw2 %= 360.0F;

        if (yaw1 < 0.0F) {
            yaw1 += 360.0F;
        }

        if (yaw2 < 0.0F) {
            yaw2 += 360.0F;
        }

        float difference = yaw2 - yaw1;
        return (difference > 180.0F) ? difference - 360.0F : (difference < -180.0F ? difference + 360.0F : difference);
    }

    public static float getAngleDifference2(float target, float current) {
        target %= 360.0F;
        current %= 360.0F;
        if (target < 0.0F) {
            target += 360.0F;
        }

        if (current < 0.0F) {
            current += 360.0F;
        }

        float var4 = current - target;
        return !(var4 > 180.0F) ? (!(var4 < -180.0F) ? var4 : var4 + 360.0F) : var4 - 360.0F;
    }

    public static float smoothAngle(float var0, float var1, float var2) {
        float var5 = net.minecraft.util.math.MathHelper.wrapAngleTo180_float(var1 - var0);
        if (var5 > var2) {
            var5 = var2;
        }

        if (var5 < -var2) {
            var5 = -var2;
        }

        return var0 + var5;
    }

    public static float wrapAngleDifference(float var0, float var1) {
        return net.minecraft.util.math.MathHelper.wrapAngleTo180_float(-(var0 - var1));
    }

    public static float getAngleDifference(float a, float b) {
        return ((a - b) % 360.0f + 540.0f) % 360.0f - 180.0f;
    }

    public static float calculate(float current, float var1, float max) {
        float wrapped = net.minecraft.util.math.MathHelper.wrapDegrees(var1 - current);
        if (wrapped > max) {
            wrapped = max;
        }

        if (wrapped < -max) {
            wrapped = -max;
        }

        return current + wrapped;
    }
}
