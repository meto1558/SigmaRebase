package com.mentalfrostbyte.jello.util.game.render;

import com.mentalfrostbyte.jello.managers.GuiManager;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientColors;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;

public class ImageUtil {
    public static BufferedImage applyBlur(BufferedImage sourceImage, int radius) {
        if (sourceImage != null) {
            int diameter = radius + radius;
            if (sourceImage.getWidth() > diameter) {
                if (sourceImage.getHeight() > diameter) {
                    ConvolveOp blurOp = new ConvolveOp(createGaussianKernel((float) radius));
                    BufferedImage blurred = blurOp.filter(sourceImage, null);
                    blurred = blurOp.filter(applyEdgeWrap(blurred), null);
                    blurred = applyEdgeWrap(blurred);
                    return blurred.getSubimage(radius, radius, sourceImage.getWidth() - diameter, sourceImage.getHeight() - diameter);
                } else {
                    return sourceImage;
                }
            } else {
                return sourceImage;
            }
        } else {
            return sourceImage;
        }
    }

    public static BufferedImage addImagePadding(BufferedImage sourceImage, int padding) {
        int paddedWidth = sourceImage.getWidth() + padding * 2;
        int paddedHeight = sourceImage.getHeight() + padding * 2;
        BufferedImage resizedImage = resizeImage(
                sourceImage,
                (float) paddedWidth / sourceImage.getWidth(),
                (float) paddedHeight / sourceImage.getHeight()
        );

        for (int x = 0; x < sourceImage.getWidth(); x++) {
            for (int y = 0; y < sourceImage.getHeight(); y++) {
                resizedImage.setRGB(padding + x, padding + y, sourceImage.getRGB(x, y));
            }
        }

        return resizedImage;
    }

    public static BufferedImage resizeImage(BufferedImage sourceImage, double scaleX, double scaleY) {
        BufferedImage resized = null;
        if (sourceImage != null) {
            int newHeight = (int) (sourceImage.getHeight() * scaleY);
            int newWidth = (int) (sourceImage.getWidth() * scaleX);
            resized = new BufferedImage(newWidth, newHeight, sourceImage.getType());
            Graphics2D g2d = resized.createGraphics();
            AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
            g2d.drawRenderedImage(sourceImage, scaleTransform);
        }
        return resized;
    }

    public static BufferedImage adjustImageHSB(BufferedImage image, float hueOffset, float saturationScale, float brightnessOffset) {
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                float[] hsb = Color.RGBtoHSB(red, green, blue, null);

                float newHue = MathHelper.clamp(hsb[0] + hueOffset, 0.0F, 1.0F);
                float newSaturation = MathHelper.clamp(hsb[1] * saturationScale, 0.0F, 1.0F);
                float newBrightness = MathHelper.clamp(hsb[2] + brightnessOffset, 0.0F, 1.0F);

                int newRGB = Color.HSBtoRGB(newHue, newSaturation, newBrightness);
                image.setRGB(x, y, newRGB);
            }
        }

        return image;
    }

    public static BufferedImage applyEdgeWrap(BufferedImage inputImage) {
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();
        BufferedImage wrappedImage = new BufferedImage(height, width, inputImage.getType());

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                wrappedImage.setRGB(height - 1 - y, width - 1 - x, inputImage.getRGB(x, y));
            }
        }

        return wrappedImage;
    }

    public static Kernel createGaussianKernel(float radius) {
        int kernelRadius = (int) Math.ceil(radius);
        int kernelSize = kernelRadius * 2 + 1;
        float[] kernelData = new float[kernelSize];
        float standardDeviation = radius / 3.0F;
        float twoSigmaSquared = 2.0F * standardDeviation * standardDeviation;
        float normalizationFactor = (float) ((Math.PI * 2) * (double) standardDeviation);
        normalizationFactor = (float) Math.sqrt(normalizationFactor);
        float maxDistanceSquared = radius * radius;
        float sum = 0.0F;
        int index = 0;

        for (int offset = -kernelRadius; offset <= kernelRadius; offset++) {
            float distanceSquared = (float) (offset * offset);
            if (!(distanceSquared > maxDistanceSquared)) {
                kernelData[index] = (float) Math.exp(-distanceSquared / twoSigmaSquared) / normalizationFactor;
            } else {
                kernelData[index] = 0.0F;
            }

            sum += kernelData[index];
            index++;
        }

        for (int i = 0; i < kernelSize; i++) {
            kernelData[i] /= sum;
        }

        return new Kernel(kernelSize, 1, kernelData);
    }

    public static BufferedImage applyGaussianBlur(BufferedImage image, int blurRadius) {
        if (image == null) {
            return image;
        } else {
            ConvolveOp blurOperation = new ConvolveOp(createGaussianKernel((float) blurRadius), ConvolveOp.EDGE_NO_OP, null);
            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();
            BufferedImage extendedImage = new BufferedImage(imageWidth + blurRadius * 2, imageHeight + blurRadius * 2, image.getType());

            for (int x = 0; x < imageWidth; x++) {
                for (int y = 0; y < imageHeight; y++) {
                    extendedImage.setRGB(x + blurRadius, y + blurRadius / 2, image.getRGB(x, y));
                }
            }

            BufferedImage blurredImage = blurOperation.filter(extendedImage, null);
            blurredImage = blurOperation.filter(applyEdgeWrap(blurredImage), null);
            blurredImage = applyEdgeWrap(blurredImage);
            return blurredImage.getSubimage(blurRadius, blurRadius, extendedImage.getWidth() - blurRadius * 2, extendedImage.getHeight() - blurRadius * 2);
        }
    }

    public static BufferedImage captureScaledScreenshot(
            int x, int y, int width, int height,
            int padding, int paddingColor, int scaleDownFactor, boolean blurAfterPadding
    ) {
        final int BYTES_PER_PIXEL = 4;

        // Scale coordinates and dimensions by GUI scale factor
        x = (int) (x * GuiManager.scaleFactor);
        y = (int) (y * GuiManager.scaleFactor);
        width = (int) (width * GuiManager.scaleFactor);
        height = (int) (height * GuiManager.scaleFactor);
        padding = (int) (padding * GuiManager.scaleFactor);

        // Flip Y coordinate for OpenGL (framebuffer height - y - height)
        y = Minecraft.getInstance().getMainWindow().getFramebufferHeight() - y - height;

        if (padding <= 0) {
            padding = 1;
        }

        ByteBuffer pixelBuffer = BufferUtils.createByteBuffer(width * height * BYTES_PER_PIXEL);
        GL11.glReadPixels(x, y, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixelBuffer);

        BufferedImage screenshot = new BufferedImage(width / padding, height / padding, BufferedImage.TYPE_INT_ARGB);

        for (int px = padding / 2; px < width; px += padding) {
            for (int py = padding / 2; py < height; py += padding) {
                if (px / padding < width / padding && py / padding < height / padding) {
                    int index = (px + width * py) * BYTES_PER_PIXEL;
                    int red = pixelBuffer.get(index) & 0xFF;
                    int green = pixelBuffer.get(index + 1) & 0xFF;
                    int blue = pixelBuffer.get(index + 2) & 0xFF;
                    // Compose ARGB pixel with full alpha
                    int argb = 0xFF000000 | (red << 16) | (green << 8) | blue;
                    screenshot.setRGB(px / padding, height / padding - (py / padding + 1), argb);
                }
            }
        }

        if (padding <= 1) {
            return screenshot;
        } else {
            if (!blurAfterPadding) {
                return applyBlur(resizeWithPadding(screenshot, padding, paddingColor), padding);
            } else {
                return applyBlur(addImagePadding(screenshot, padding), padding);
            }
        }
    }

    public static BufferedImage resizeWithPadding(BufferedImage image, int padding, int paddingColor) {
        int paddedWidth = image.getWidth() + padding * 2;
        int paddedHeight = image.getHeight() + padding * 2;
        BufferedImage paddedImage = new BufferedImage(paddedWidth, paddedHeight, image.getType());

        if (paddingColor != ClientColors.DEEP_TEAL.getColor()) {
            for (int x = 0; x < paddedWidth; x++) {
                for (int y = 0; y < paddedHeight; y++) {
                    paddedImage.setRGB(x, y, paddingColor);
                }
            }
        }

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                paddedImage.setRGB(padding + x, padding + y, image.getRGB(x, y));
            }
        }

        return paddedImage;
    }

    public static BufferedImage captureScreenshotWithDefaultColor(
            int x, int y, int width, int height, int padding, int paddingSize, boolean blurAfterPadding
    ) {
        return captureScaledScreenshot(x, y, width, height, padding, paddingSize, ClientColors.DEEP_TEAL.getColor(), blurAfterPadding);
    }

    public static BufferedImage captureScreenshotWithDefaultColorNoBlur(
            int x, int y, int width, int height, int padding, int paddingSize
    ) {
        return captureScaledScreenshot(x, y, width, height, padding, paddingSize, ClientColors.DEEP_TEAL.getColor(), false);
    }

    public static Texture loadTextureFromURL(String urlString) {
        try (InputStream inputStream = getInputStreamFromURL(urlString)) {
            return TextureLoader.getTexture("PNG", inputStream);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load texture from URL: " + urlString, e);
        }
    }

    private static InputStream getInputStreamFromURL(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0"); // Avoid blocking from some servers
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.connect();

        if (connection.getResponseCode() != 200) {
            throw new IOException("Failed to load image, HTTP response code: " + connection.getResponseCode());
        }

        return new BufferedInputStream(connection.getInputStream());
    }
}
