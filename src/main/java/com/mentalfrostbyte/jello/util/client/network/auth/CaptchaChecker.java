package com.mentalfrostbyte.jello.util.client.network.auth;

import com.mentalfrostbyte.jello.Client;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.BufferedImageUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class CaptchaChecker {
    private final String uid;
    private boolean field36882;
    private final long field36883 = System.currentTimeMillis();
    private boolean field36884 = true;
    private String answer = "";
    private BufferedImage field36886;
    private Texture field36887;

    public CaptchaChecker(final String uid, final boolean field36882) {
        this.uid = uid;
        this.field36882 = field36882;
        if (this.field36882 == field36882) {
            new Thread(() -> {
                try {
                    final URL input = new URL("http://localhost/captcha/" + uid + ".png");
                    this.field36886 = ImageIO.read(input);
                }
                catch (final IOException ex) {}
            }).start();
        }
    }

    public void finalize() throws Throwable {
        try {
            if (this.field36887 != null) {
                Client.getInstance().addTexture(this.field36887);
            }
        }
        finally {
            super.finalize();
        }
    }

    public Texture getCaptchaImage() {
        if (this.field36887 == null && this.field36886 != null) {
            try {
                this.field36887 = BufferedImageUtil.getTexture("", this.field36886);
            }
            catch (final IOException ex) {
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

    public String getAnswer() {
        return this.answer;
    }

    public void setAnswer(final String answer) {
        this.answer = answer;
    }

    public String getChallengeUid() {
        return this.uid;
    }
}
