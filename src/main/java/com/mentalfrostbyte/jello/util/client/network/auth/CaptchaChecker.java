package com.mentalfrostbyte.jello.util.client.network.auth;

import com.mentalfrostbyte.Client;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.BufferedImageUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class CaptchaChecker {
    private String field36881;
    private boolean field36882;
    private long field36883;
    private boolean field36884;
    private String field36885;
    private BufferedImage field36886;
    private Texture field36887;

    public CaptchaChecker(final String field36881, final boolean field36882) {
        this.field36883 = System.currentTimeMillis();
        this.field36884 = true;
        this.field36885 = "";
        this.field36881 = field36881;
        if (this.field36882 == field36882) {
            new Thread(() -> {
                try {
                    final URL input = new URL("https://jelloprg.sigmaclient.info/captcha/" + field36881 + ".png");
                    this.field36886 = ImageIO.read(input);
                } catch (final IOException ex) {
                }
            }).start();
        }
    }

    public void finalize() throws Throwable {
        try {
            if (this.field36887 != null) {
                Client.getInstance().addTexture(this.field36887);
            }
        } finally {
            super.finalize();
        }
    }

    public Texture method30470() {
        if (this.field36887 == null && this.field36886 != null) {
            try {
                this.field36887 = BufferedImageUtil.getTexture("", this.field36886);
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        }
        return this.field36887;
    }

    public boolean method30471() {
        return this.field36882;
    }

    public boolean isCompleted() {
        return this.field36884 && this.field36883 > System.currentTimeMillis() - 300000L;
    }

    public void method30473(final boolean field36884) {
        this.field36884 = field36884;
    }

    public String method30474() {
        return this.field36885;
    }

    public void method30475(final String field36885) {
        this.field36885 = field36885;
    }

    public String method30476() {
        return this.field36881;
    }
}
