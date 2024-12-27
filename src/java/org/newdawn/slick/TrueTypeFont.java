package org.newdawn.slick;

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
    private final int field31944 = 0;
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

    public TrueTypeFont(java.awt.Font font, boolean antiAlias, char[] additionalChars) {
        GLUtils.checkGLContext();
        this.font = font;
        this.fontSize = font.getSize();
        this.antiAlias = antiAlias;

        this.createSet(additionalChars);
    }

    public TrueTypeFont(java.awt.Font font, boolean antiAlias) {
        this(font, antiAlias, null);
    }

    private BufferedImage getFontImage(char ch) {
        BufferedImage tempfontImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) tempfontImage.getGraphics();
        if (this.antiAlias) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        g.setFont(this.font);
        this.fontMetrics = g.getFontMetrics();
        int charWidth = this.fontMetrics.charWidth(ch);
        if (charWidth <= 0) {
            charWidth = 1;
        }

        int charHeight = this.fontMetrics.getHeight();
        if (charHeight <= 0) {
            charHeight = this.fontSize;
        }

        BufferedImage fontImage = new BufferedImage(charWidth, charHeight, 2);
        Graphics2D gt = (Graphics2D) fontImage.getGraphics();
        if (this.antiAlias) {
            gt.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        gt.setFont(this.font);
        gt.setColor(java.awt.Color.WHITE);
        gt.drawString(String.valueOf(ch), 0, this.fontMetrics.getAscent());
        return fontImage;
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

            BufferedImage imgTemp = new BufferedImage(this.textureWidth, this.textureHeight, 2);
            Graphics2D g = (Graphics2D) imgTemp.getGraphics();
            g.setColor(new java.awt.Color(255, 255, 255, 1));
            g.fillRect(0, 0, this.textureWidth, this.textureHeight);
            int rowHeight = 0;
            int positionX = 0;
            int positionY = 0;
            int customCharsLength = customCharsArray != null ? customCharsArray.length : 0;

            for (int i = 0; i < 256 + customCharsLength; i++) {
                char ch = i < 256 ? (char) i : customCharsArray[i - 256];
                BufferedImage fontImage = this.getFontImage(ch);
                IntObject newIntObject = new IntObject();
                newIntObject.width = fontImage.getWidth();
                newIntObject.height = fontImage.getHeight();

                if (positionX + newIntObject.width >= this.textureWidth) {
                    positionX = 0;
                    positionY += rowHeight;
                    rowHeight = 0;
                }

                newIntObject.storedX = positionX;
                newIntObject.storedY = positionY;
                if (newIntObject.height > this.fontHeight) {
                    this.fontHeight = newIntObject.height;
                }

                if (newIntObject.height > rowHeight) {
                    rowHeight = newIntObject.height + 1;
                }

                g.drawImage(fontImage, positionX, positionY, null);
                positionX += newIntObject.width + 1;
                if (i < 256) {
                    this.charArray[i] = newIntObject;
                } else {
                    this.customChars.put(ch, newIntObject);
                }
            }

            this.fontTexture = BufferedImageUtil.getTexture(this.font.toString(), imgTemp);
        } catch (IOException e) {
            System.err.println("Failed to create font.");
            e.printStackTrace();
        }
    }

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

    @Override
    public int getWidth(String whatchars) {
        int totalwidth = 0;
        IntObject intObject = null;
        char currentChar = 0;
        if (whatchars != null) {
            for (int var7 = 0; var7 < whatchars.length(); var7++) {
                currentChar = whatchars.charAt(var7);
                if (currentChar >= 256) {
                    intObject = (IntObject) this.customChars.get(currentChar);
                } else {
                    intObject = this.charArray[currentChar];
                }

                if (intObject != null) {
                    totalwidth += intObject.width;
                }
            }

            return totalwidth;
        } else {
            return 0;
        }
    }

    public int getHeight() {
        return this.fontHeight;
    }

    @Override
    public int getHeight(String HeightString) {
        return this.fontHeight;
    }

    @Override
    public int getLineHeight() {
        return this.fontHeight;
    }

    @Override
    public void drawString(float x, float y, String whatchars, Color color) {
        this.drawString(x, y, whatchars, color, 0, whatchars.length() - 1);
    }

    @Override
    public void drawString(float x, float y, String whatchars, Color color, int startIndex, int endIndex) {
        color.bind();
        this.fontTexture.bind();
        IntObject intObject = null;

        GL.glBegin(SGL.GL_QUADS);
        int totalwidth = 0;

        for (int i = 0; i < whatchars.length(); i++) {
            char charCurrent = whatchars.charAt(i);
            if (charCurrent >= 256) {
                intObject = (IntObject) this.customChars.get(charCurrent);
            } else {
                intObject = this.charArray[charCurrent];
            }

            if (intObject != null) {
                if (i >= startIndex || i <= endIndex) {
                    this.drawQuad(
                            x + (float) totalwidth,
                            y,
                            x + (float) totalwidth + (float) intObject.width,
                            y + (float) intObject.height,
                            (float) intObject.storedX,
                            (float) intObject.storedY,
                            (float) (intObject.storedX + intObject.width),
                            (float) (intObject.storedY + intObject.height)
                    );
                }

                totalwidth += intObject.width;
            }
        }

        GL.glEnd();
    }

    @Override
    public void drawString(float x, float y, String whatchars) {
        this.drawString(x, y, whatchars, Color.white);
    }

    public static class IntObject {
        public int width;
        public int asd;
        public int height;
        public int storedX;
        public int storedY;
    }
}
