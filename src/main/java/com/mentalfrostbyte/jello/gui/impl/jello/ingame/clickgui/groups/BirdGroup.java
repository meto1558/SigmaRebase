package com.mentalfrostbyte.jello.gui.impl.jello.ingame.clickgui.groups;

import com.mentalfrostbyte.jello.gui.combined.CustomGuiScreen;
import com.mentalfrostbyte.jello.gui.combined.AnimatedIconPanel;
import com.mentalfrostbyte.jello.util.client.render.Resources;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientColors;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import net.minecraft.client.Minecraft;
import org.newdawn.slick.opengl.Texture;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

public class BirdGroup extends AnimatedIconPanel {
    public Texture backgrnd;
    public Texture foregrnd;
    public Texture pipe;
    public Texture pipe2;
    public Texture bird;
    public TreeMap<Long, Double> field20834 = new TreeMap<Long, Double>();
    public float field20835 = 0.5F;
    public float field20836 = 0.0F;
    public long field20837 = System.currentTimeMillis();

    public BirdGroup(CustomGuiScreen var1, String var2, int var3, int var4, int var5, int var6, int var7) {
        super(var1, var2, var3, var4, 700, 512, false);
        this.backgrnd = Resources.loadTexture("com/mentalfrostbyte/gui/resources/games/bg.png");
        this.foregrnd = Resources.loadTexture("com/mentalfrostbyte/gui/resources/games/fg.png");
        this.pipe = Resources.loadTexture("com/mentalfrostbyte/gui/resources/games/pipe.png");
        this.pipe2 = Resources.loadTexture("com/mentalfrostbyte/gui/resources/games/pipe2.png");
        this.bird = Resources.loadTexture("com/mentalfrostbyte/gui/resources/games/bird.png");
    }

    private void method13183() {
    }

    private void method13184() {
        long var3 = 2200;
        long var4 = 2;
        if (this.field20834.isEmpty()) {
            this.field20834.put(System.currentTimeMillis() + (var3 * 2), 0.25 + Math.random() * 0.5);
        }

        for (long var5 = this.field20834.lastKey(); var5 < System.currentTimeMillis() + (var3 * var4); var5 = this.field20834.lastKey()) {
            this.field20834.put(var5 + var3, 0.25 + Math.random() * 0.5);
        }

        Iterator var7 = this.field20834.entrySet().iterator();

        while (var7.hasNext()) {
            Entry var8 = (Entry) var7.next();
            if ((Long) var8.getKey() < System.currentTimeMillis() - (var3 * 2)) {
                var7.remove();
            }
        }
    }

    @Override
    public void keyPressed(int keyCode) {
        super.keyPressed(keyCode);
        if (keyCode == 32) {
            this.field20836 = 0.045F;
        }
    }

    @Override
    public void draw(float partialTicks) {
        this.method13184();
        RenderUtil.startScissor(this);

        for (int var4 = 0; var4 < 3; var4++) {
            RenderUtil.drawImage((float) (this.xA + 288 * var4), (float) this.yA, 288.0F, 512.0F, this.backgrnd);
        }

        float var16 = 60.0F / (float) Minecraft.getFps();
        float var5 = 0.026F * var16;
        this.field20835 = Math.max(Math.min(1.0F, this.field20835 + this.field20836 - var5), 0.0F);
        this.field20836 = Math.max(-var5, this.field20836 * 0.965F);
        float var6 = 100;
        float var7 = 2200;
        int var8 = this.heightA - 112;

        for (Entry var10 : this.field20834.entrySet()) {
            int var11 = (int) ((Long) var10.getKey() - System.currentTimeMillis());
            float var12 = (float) var11 / 12.0F;
            float var13 = var7 / 12.0F;
            RenderUtil.drawImage(
                    (float) this.xA + var13 + var12,
					this.yA - 320 + (int) ((double) var8 * (Double) var10.getValue()) - var6 / 2,
                    52.0F,
                    320.0F,
                    this.pipe,
                    ClientColors.LIGHT_GREYISH_BLUE.getColor()
            );
            RenderUtil.drawImage(
                    (float) this.xA + var13 + var12,
					this.yA + (int) ((double) var8 * (Double) var10.getValue()) + var6 / 2,
                    52.0F,
                    320.0F,
                    this.pipe2,
                    ClientColors.LIGHT_GREYISH_BLUE.getColor()
            );
            if (var12 > -52.0F && var12 < 0.0F) {
                float var14 = (var6 - 24) / (float) var8;
                boolean var15 = (double) this.field20835 < (Double) var10.getValue() - (double) (var14 / 2.0F)
                        || (double) this.field20835 > (Double) var10.getValue() + (double) (var14 / 2.0F);
            }
        }

        float var18 = (float) (System.currentTimeMillis() % (long) 3400) / 3400;

        for (int var19 = 0; var19 < 4; var19++) {
            RenderUtil.drawImage((float) (this.xA + 288 * var19) - 288.0F * var18, (float) (this.yA + var8), 288.0F, 112.0F, this.foregrnd);
        }

        RenderUtil.drawImage(
                (float) this.xA + var7 / 12.0F,
                (float) this.yA + (float) var8 * (1.0F - this.field20835),
                40,
                20,
                this.bird,
                ClientColors.LIGHT_GREYISH_BLUE.getColor()
        );
        RenderUtil.endScissor();
        this.field20837 = System.currentTimeMillis();
        super.draw(partialTicks);
    }

    public int method13179() {
        return 0;
    }
}
