package com.mentalfrostbyte.jello.managers.util.account;

import com.mentalfrostbyte.Client;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.BufferedImageUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class CaptchaChecker {
    private final String challengeuid;
    private boolean field36882;
    private final long field36883;
    private boolean field36884;
    private String challengeAnswer;
    private BufferedImage field36886;
    private Texture field36887;

    public CaptchaChecker(final String challengeuid, final boolean field36882) {
        this.field36883 = System.currentTimeMillis();
        this.field36884 = true;
        this.challengeAnswer = "";
        this.challengeuid = challengeuid;
        if (this.field36882 == field36882) {
            new Thread(() -> {
                try {
                    final URL input = new URL("https://jelloprg.sigmaclient.info/captcha/" + challengeuid + ".png");
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

    public Texture method30470() {
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

    public String getChallengeAnswer() {
        return this.challengeAnswer;
    }

    public void setChallengeAnswer(final String answer) {
        this.challengeAnswer = answer;
    }

    public String getChallengeUid() {
        return this.challengeuid;
    }
}
