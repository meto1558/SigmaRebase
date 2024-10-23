package com.mentalfrostbyte.jello.utils.render;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.utils.render.unmapped.TextureLoader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class Resources {

    public static Texture loadTexture(String filePath) {
        try {
            String extension = filePath.substring(filePath.lastIndexOf(".") + 1).toUpperCase();
            return loadTexture(filePath, extension);
        } catch (Exception e) {
            System.err.println(
                    "Unable to load texture " + filePath +
                    ". Please make sure it is a valid path and has a valid extension.");
            throw e;
        }
    }

    public static Texture loadTexture(String filePath, String fileType) {
        try {
            return TextureLoader.getTexture(fileType, readInputStream(filePath));
        } catch (IOException e) {
            try (InputStream inputStream = readInputStream(filePath)) {
                byte[] header = new byte[8];
                inputStream.read(header);
                StringBuilder headerInfo = new StringBuilder();
                for (int value : header) {
                    headerInfo.append(" ").append(value);
                }
                throw new IllegalStateException("Unable to load texture " + filePath + " header: " + headerInfo);
            } catch (IOException ex) {
                throw new IllegalStateException("Unable to load texture " + filePath, ex);
            }
        }
    }

    public static InputStream readInputStream(String fileName) {
        try {
            // The file path within the Minecraft assets folder
            String assetPath = "assets/minecraft/" + fileName;

            // Attempt to load the resource directly from the classpath
            InputStream resourceStream = Client.class.getClassLoader().getResourceAsStream(assetPath);

            if (resourceStream != null) {
                return resourceStream;
            } else {
                throw new IllegalStateException("Resource not found: " + assetPath);
            }
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Unable to load resource " + fileName + ". Error during resource loading.", e
            );
        }
    }

    public static Texture createScaledAndProcessedTexture2(String var0, float var1, int var2) {
        try {
            BufferedImage var5 = ImageIO.read(readInputStream(var0));
            BufferedImage var6 = new BufferedImage((int) (var1 * (float) var5.getWidth(null)), (int) (var1 * (float) var5.getHeight(null)), 2);
            Graphics2D var7 = (Graphics2D) var6.getGraphics();
            var7.scale(var1, var1);
            var7.drawImage(var5, 0, 0, null);
            var7.dispose();
            var5 = ImageUtil.method35032(ImageUtil.method35041(var6, var2), var2);
            var5 = ImageUtil.method35042(var5, 0.0F, 1.1F, 0.0F);
            return TextureUtil.method32933(var0, var5);
        } catch (IOException var8) {
            throw new IllegalStateException(
                    "Unable to find " + var0 + ". You've probably obfuscated the archive and forgot to transfer the assets or keep package names."
            );
        }
    }

}
