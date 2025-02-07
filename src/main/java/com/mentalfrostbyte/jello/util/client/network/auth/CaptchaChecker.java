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
    private boolean completed;
    private final long field36883 = System.currentTimeMillis();
    private boolean field36884 = true;
    private String answer = "";
    private BufferedImage captchaResourceImage;
    private Texture captchaImage;

    public CaptchaChecker(final String uid, final boolean completed) {
        this.uid = uid;
        this.completed = completed;
        new Thread(() -> {
            try {
                final URL input = new URL("http://localhost/captcha/" + uid + ".png");
                this.captchaResourceImage = ImageIO.read(input);
            } catch (final IOException ex) {
            }
        }).start();
    }

    public void finalize() throws Throwable {
        try {
            if (this.captchaImage != null) {
                Client.getInstance().addTexture(this.captchaImage);
            }
        }
        finally {
            super.finalize();
        }
    }

    public Texture getCaptchaImage() {
        if (this.captchaImage == null && this.captchaResourceImage != null) {
            try {
                this.captchaImage = BufferedImageUtil.getTexture("", this.captchaResourceImage);
            }
            catch (final IOException ex) {
                ex.printStackTrace();
            }
        }
        return this.captchaImage;
    }

    public boolean isActuallyCompleted() {
        return this.completed;
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
