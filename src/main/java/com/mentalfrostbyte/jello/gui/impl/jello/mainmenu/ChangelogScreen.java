package com.mentalfrostbyte.jello.gui.impl.jello.mainmenu;

import com.google.gson.JsonArray;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.gui.base.animations.Animation;
import com.mentalfrostbyte.jello.gui.combined.CustomGuiScreen;
import com.mentalfrostbyte.jello.gui.base.elements.impl.Change;
import com.mentalfrostbyte.jello.gui.impl.jello.buttons.ScrollableContentPanel;
import com.mentalfrostbyte.jello.gui.impl.jello.mainmenu.changelog.Class576;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientColors;
import com.mentalfrostbyte.jello.util.system.math.MathHelper;
import com.mentalfrostbyte.jello.util.system.math.SmoothInterpolator;
import com.mentalfrostbyte.jello.util.client.render.ResourceRegistry;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.newdawn.slick.TrueTypeFont;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ChangelogScreen extends CustomGuiScreen {
    public Animation animation = new Animation(380, 200, Animation.Direction.BACKWARDS);
    public ScrollableContentPanel scrollPanel;
    private static JsonArray cachedChangelog;

    public ChangelogScreen(CustomGuiScreen var1, String var2, int var3, int var4, int var5, int var6) {
        super(var1, var2, var3, var4, var5, var6);
        this.setListening(false);
        this.scrollPanel = new ScrollableContentPanel(this, "scroll", 100, 200, var5 - 200, var6 - 200);
        this.scrollPanel.method13518(true);
        this.showAlert(this.scrollPanel);
        new Thread(() -> this.method13490(this.getChangelog())).start();
    }

    public void method13490(JsonArray var1) {
        if (var1 != null) {
            this.getParent().runThisOnDimensionUpdate(new Class576(this, var1));
        }
    }

    @Override
    public void updatePanelDimensions(int newHeight, int newWidth) {
        super.updatePanelDimensions(newHeight, newWidth);
        if (this.scrollPanel != null) {
            if (this.isHovered() && this.isSelfVisible()) {
                for (CustomGuiScreen var9 : this.scrollPanel.getButton().getChildren()) {
                    Change var10 = (Change) var9;
                    var10.animation2.changeDirection(Animation.Direction.FORWARDS);
                    if ((double) var10.animation2.calcPercent() < 0.5) {
                        break;
                    }
                }
            } else {
                for (CustomGuiScreen var6 : this.scrollPanel.getButton().getChildren()) {
                    Change var7 = (Change) var6;
                    var7.animation2.changeDirection(Animation.Direction.BACKWARDS);
                }
            }
        }
    }

    @Override
    public void draw(float partialTicks) {
        this.animation.changeDirection(!this.isHovered() ? Animation.Direction.BACKWARDS : Animation.Direction.FORWARDS);
        partialTicks *= this.animation.calcPercent();

        float fadeFactor = SmoothInterpolator.interpolate(this.animation.calcPercent(), 0.17f, 1.0f, 0.51f, 1.0f);

        if (this.animation.getDirection() == Animation.Direction.BACKWARDS) {
            fadeFactor = 1.0f;
        }

        this.drawBackground((int) (150.0f * (1.0f - fadeFactor)));
        this.method13225();
        RenderUtil.drawString(ResourceRegistry.JelloLightFont36, 100.0F, 100.0F, "Changelog", MathHelper.applyAlpha2(ClientColors.LIGHT_GREYISH_BLUE.getColor(), partialTicks));
        TrueTypeFont jelloLightFont25 = ResourceRegistry.JelloLightFont25;
        String versionText = "You're currently using Sigma " + Client.FULL_VERSION;
        RenderUtil.drawString(
                jelloLightFont25,
                100.0f, 150.0f,
                versionText,
                MathHelper.applyAlpha2(ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.6f * partialTicks)
        );
        super.draw(partialTicks);
    }

    public JsonArray getChangelog() {
        if (cachedChangelog != null) {
            return cachedChangelog;
        }

        try {
            HttpEntity entity = HttpClients.createDefault()
                    .execute(new HttpGet("https://jelloconnect.sigmaclient.cloud/changelog.php?v=" + Client.RELEASE_TARGET + "b" + Client.BETA_ITERATION))
                    .getEntity();

            if (entity != null) {
                try (InputStream content = entity.getContent()) {
                    String jsonString = IOUtils.toString(content, StandardCharsets.UTF_8);
                    cachedChangelog = JsonParser.parseString(jsonString).getAsJsonArray();
                    return cachedChangelog;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }
}