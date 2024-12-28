package org.newdawn.slick;

import com.mentalfrostbyte.jello.util.render.ImageUtil;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;
import org.newdawn.slick.opengl.GLUtils;
import org.newdawn.slick.util.BufferedImageUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TrueTypeFont implements Font {
    private static final SGL GL = Renderer.get();
    private final int size;
    public Texture fontTexture;
    private final IntObject[] charArray = new IntObject[256];
    private final Map customChars = new HashMap();
    private final boolean antiAlias;
    private int fontSize = 0;
    private int fontHeight = 0;
    private int textureWidth = 512;
    private int textureHeight = 512;
    private final java.awt.Font font;
    private FontMetrics fontMetrics;

    public TrueTypeFont(java.awt.Font font, boolean antiAlias, char[] additionalChars, int size) {
        GLUtils.checkGLContext();
        this.font = font;
        this.fontSize = font.getSize();
        this.antiAlias = antiAlias;
        this.size = size;
        if (size > 0) {
            this.textureWidth = 1024;
            this.textureHeight = 1024;
        }

        this.createSet(additionalChars);
    }

    public TrueTypeFont(java.awt.Font font, boolean antiAlias, char[] additionalChars) {
        this(font, antiAlias, additionalChars, 0);
    }

    public TrueTypeFont(java.awt.Font font, boolean antiAlias) {
        this(font, antiAlias, null);
    }

    public TrueTypeFont(java.awt.Font font, int size) {
        this(font, true, null, size);
    }

    private BufferedImage getFontImage(char of) {
        BufferedImage bufferedImage = new BufferedImage(1, 1, 2);
        Graphics2D g2d = (Graphics2D) bufferedImage.getGraphics();
        if (this.antiAlias) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        g2d.setFont(this.font);
        this.fontMetrics = g2d.getFontMetrics();
        int charWidth = this.fontMetrics.charWidth(of);
        if (charWidth <= 0) {
            charWidth = 1;
        }

        int charHeight = this.fontMetrics.getHeight();
        if (charHeight <= 0) {
            charHeight = this.fontSize;
        }

        BufferedImage bufferedImage2 = new BufferedImage(charWidth + this.size * 2, charHeight + this.size * 2, 2);
        Graphics2D g2d2 = (Graphics2D) bufferedImage2.getGraphics();
        if (this.antiAlias) {
            g2d2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        g2d2.setFont(this.font);
        g2d2.setColor(java.awt.Color.WHITE);
        g2d2.drawString(String.valueOf(of), this.size, this.size + this.fontMetrics.getAscent());
        return this.size <= 0 ? bufferedImage2 : ImageUtil.method35033(bufferedImage2, this.size);
    }

    private int method23949(char var1) {
        BufferedImage bufferedImage = new BufferedImage(1, 1, 2);
        Graphics2D g2d = (Graphics2D) bufferedImage.getGraphics();
        if (this.antiAlias) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        g2d.setFont(this.font);
        this.fontMetrics = g2d.getFontMetrics();
        int charWidth = this.fontMetrics.charWidth(var1);
        if (charWidth <= 0) {
            charWidth = 1;
        }

        return charWidth;
    }

    private void createSet(char[] customCharsArray) {
        if (customCharsArray != null && customCharsArray.length > 0) {
            this.textureWidth *= 2;
        }

        try {
            BufferedImage bufferedImage = this.getFontImage('\u0000');
            if (bufferedImage.getHeight() > 60) {
                this.textureWidth *= 2;
                this.textureHeight *= 2;
            }

            BufferedImage bufferedImage2 = new BufferedImage(this.textureWidth, this.textureHeight, 2);
            Graphics2D g2d = (Graphics2D) bufferedImage2.getGraphics();
            g2d.setColor(new java.awt.Color(255, 255, 255, 1));
            g2d.fillRect(0, 0, this.textureWidth, this.textureHeight);
            int rowHeight = 0;
            int positionX = 0;
            int positionY = 0;
            int customCharsLength = customCharsArray != null ? customCharsArray.length : 0;

            for (int var11 = 0; var11 < 256 + customCharsLength; var11++) {
                char var12 = var11 < 256 ? (char) var11 : customCharsArray[var11 - 256];
                BufferedImage var13 = this.getFontImage(var12);
                IntObject var14 = new IntObject();
                var14.width = var13.getWidth();
                var14.height = var13.getHeight();
                if (this.size > 0) {
                    var14.actualWidth = this.method23949(var12);
                } else {
                    var14.actualWidth = var14.width;
                }

                if (positionX + var14.width >= this.textureWidth) {
                    positionX = 0;
                    positionY += rowHeight;
                    rowHeight = 0;
                }

                var14.storedX = positionX;
                var14.storedY = positionY;
                if (var14.height > this.fontHeight) {
                    this.fontHeight = var14.height;
                }

                if (var14.height > rowHeight) {
                    rowHeight = var14.height + 1;
                }

                g2d.drawImage(var13, positionX, positionY, null);
                positionX += var14.width + 1;
                if (var11 < 256) {
                    this.charArray[var11] = var14;
                } else {
                    this.customChars.put(new Character(var12), var14);
                }
            }

            this.fontTexture = BufferedImageUtil.getTexture(this.font.toString(), bufferedImage2);
        } catch (IOException var15) {
            System.err.println("Failed to create font.");
            var15.printStackTrace();
        }
    }

    /**
     * Draw a textured quad
     *
     * @param drawX
     *            The left x position to draw to
     * @param drawY
     *            The top y position to draw to
     * @param drawX2
     *            The right x position to draw to
     * @param drawY2
     *            The bottom y position to draw to
     * @param srcX
     *            The left source x position to draw from
     * @param srcY
     *            The top source y position to draw from
     * @param srcX2
     *            The right source x position to draw from
     * @param srcY2
     *            The bottom source y position to draw from
     */
    private void drawQuad(float drawX, float drawY, float drawX2, float drawY2,
                          float srcX, float srcY, float srcX2, float srcY2) {
        float DrawWidth = drawX2 - drawX;
        float DrawHeight = drawY2 - drawY;
        float TextureSrcX = srcX / textureWidth;
        float TextureSrcY = srcY / textureHeight;
        float SrcWidth = srcX2 - srcX;
        float SrcHeight = srcY2 - srcY;
        float RenderWidth = (SrcWidth / textureWidth);
        float RenderHeight = (SrcHeight / textureHeight);

        GL.glTexCoord2f(TextureSrcX, TextureSrcY);
        GL.glVertex2f(drawX, drawY);
        GL.glTexCoord2f(TextureSrcX, TextureSrcY + RenderHeight);
        GL.glVertex2f(drawX, drawY + DrawHeight);
        GL.glTexCoord2f(TextureSrcX + RenderWidth, TextureSrcY + RenderHeight);
        GL.glVertex2f(drawX + DrawWidth, drawY + DrawHeight);
        GL.glTexCoord2f(TextureSrcX + RenderWidth, TextureSrcY);
        GL.glVertex2f(drawX + DrawWidth, drawY);
    }

    /**
     * Get the width of a given String
     *
     * @param whatchars
     *            The characters to get the width of
     *
     * @return The width of the characters
     */
    public int getWidth(String whatchars) {
        int totalwidth = 0;
        IntObject intObject = null;
        int currentChar = 0;
        for (int i = 0; i < whatchars.length(); i++) {
            currentChar = whatchars.charAt(i);
            if (currentChar < 256) {
                intObject = charArray[currentChar];
            } else {
                intObject = (IntObject)customChars.get( new Character( (char) currentChar ) );
            }

            if( intObject != null )
                totalwidth += intObject.width;
        }
        return totalwidth;
    }

    /**
     * Get the font's height
     *
     * @return The height of the font
     */
    public int getHeight() {
        return fontHeight;
    }

    /**
     * Get the height of a String
     *
     * @return The height of a given string
     */
    public int getHeight(String HeightString) {
        return fontHeight;
    }

    /**
     * Get the font's line height
     *
     * @return The line height of the font
     */
    public int getLineHeight() {
        return fontHeight;
    }

    /**
     * Draw a string
     *
     * @param x
     *            The x position to draw the string
     * @param y
     *            The y position to draw the string
     * @param whatchars
     *            The string to draw
     * @param color
     *            The color to draw the text
     */
    public void drawString(float x, float y, String whatchars,
                           org.newdawn.slick.Color color) {
        drawString(x,y,whatchars,color,0,whatchars.length()-1);
    }

    /**
     * @see Font#drawString(float, float, String, org.newdawn.slick.Color, int, int)
     */
    @Override
    public void drawString(float x, float y, String string, Color color, int startIndex, int endIndex) {
        color.bind();
        this.fontTexture.bind();
        IntObject intObject = null;
        if (this.size > 0) {
            y -= (float) (this.size / 2 - 1);
            x -= (float) (this.size - 1);
        }

        GL.glBegin(7);
        int charIdx = 0;

        for (int var11 = 0; var11 < string.length(); var11++) {
            char charCurrent = string.charAt(var11);
            if (charCurrent >= 256) {
                intObject = (IntObject) this.customChars.get(charCurrent);
            } else {
                intObject = this.charArray[charCurrent];
            }

            if (intObject != null) {
                if (var11 >= startIndex || var11 <= endIndex) {
                    this.drawQuad(
                            x + (float) charIdx,
                            y,
                            x + (float) charIdx + (float) intObject.width,
                            y + (float) intObject.height,
                            (float) intObject.storedX,
                            (float) intObject.storedY,
                            (float) (intObject.storedX + intObject.width),
                            (float) (intObject.storedY + intObject.height)
                    );
                }

                charIdx += intObject.actualWidth;
            }
        }

        GL.glEnd();
    }

    @Override
    public void drawString(float x, float y, String string) {
        this.drawString(x, y, string, Color.white);
    }

    public static class IntObject {
       public int width;
       public int actualWidth;
       public int height;
       public int storedX;
       public int storedY;
    }
}
