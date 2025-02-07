package com.mentalfrostbyte.jello.gui.impl.jello.mainmenu.changelog;

import com.mentalfrostbyte.jello.gui.base.CustomGuiScreen;
import com.mentalfrostbyte.jello.gui.base.animations.Animation;
import com.mentalfrostbyte.jello.gui.base.elements.impl.Label;
import com.mentalfrostbyte.jello.util.client.render.ResourceRegistry;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientColors;
import com.mentalfrostbyte.jello.util.client.render.theme.ColorHelper;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil2;
import com.mentalfrostbyte.jello.util.system.math.MathUtils;
import org.lwjgl.opengl.GL11;
import totalcross.json.JSONArray;
import totalcross.json.JSONException;
import totalcross.json.JSONObject;

public class Change extends CustomGuiScreen {
    public Animation animation2 = new Animation(370, 150, Animation.Direction.BACKWARDS);

    public Change(CustomGuiScreen var1, String var2, JSONObject object) throws JSONException {
        super(var1, var2);
        this.setWidthA(this.getParent().getWidthA());
        int var6 = 0;
        if (object.has("deprecated")) {
            GL11.glTexEnvi(8960, 8704, 260);
        }

        String title = object.getString("title");
        JSONArray changesArray = object.getJSONArray("changes");
        this.addToList(new Label(this, "title", 0, var6, 0, 0, ColorHelper.field27961, title, ResourceRegistry.JelloMediumFont40));
        var6 += 55;

        for (int i = 0; i < changesArray.length(); i++) {
            String change = " - " + changesArray.getString(i);
            this.addToList(
                    new Label(
                            this,
                            "change" + i,
                            0,
                            var6,
                            0,
                            0,
                            new ColorHelper(0, 0, 0, RenderUtil2.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.8F)),
                            change,
                            ResourceRegistry.JelloLightFont20
                    )
            );
            var6 += 22;
        }

        var6 += 75;
        this.setHeightA(var6);
    }

    @Override
    public void draw(float partialTicks) {
        float var4 = MathUtils.lerp(this.animation2.calcPercent(), 0.17, 1.0, 0.51, 1.0);
        this.drawBackground((int) ((1.0F - var4) * 100.0F));
        this.method13225();
        partialTicks *= this.animation2.calcPercent();
        super.draw(partialTicks);
    }
}
