package com.mentalfrostbyte.jello.module.impl.gui.jello;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.event.impl.ClickEvent;
import com.mentalfrostbyte.jello.event.impl.EventKeyPress;
import com.mentalfrostbyte.jello.event.impl.EventRender;
import com.mentalfrostbyte.jello.gui.base.Animation;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import com.mentalfrostbyte.jello.util.ClientColors;
import com.mentalfrostbyte.jello.util.ResourceRegistry;
import com.mentalfrostbyte.jello.util.render.ColorUtils;
import com.mentalfrostbyte.jello.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import team.sdhq.eventBus.annotations.EventTarget;

import java.util.ArrayList;
import java.util.Iterator;

public class KeyStrokes extends Module {
    public int field23585 = 10;
    public int field23586 = 260;
    public ArrayList<Class7930> field23587 = new ArrayList<>();

    public KeyStrokes() {
        super(ModuleCategory.GUI, "KeyStrokes", "Shows what keybind you are pressing");
        this.setAvailableOnClassic(false);
    }

    public Keystroke getKeyStrokeForKey(int key) {
        if (key != mc.gameSettings.keyBindLeft.keyCode.getKeyCode()) {
            if (key != mc.gameSettings.keyBindRight.keyCode.getKeyCode()) {
                if (key != mc.gameSettings.keyBindForward.keyCode.getKeyCode()) {
                    if (key != mc.gameSettings.keyBindBack.keyCode.getKeyCode()) {
                        if (key != mc.gameSettings.keyBindAttack.keyCode.getKeyCode()) {
                            return key != mc.gameSettings.keyBindUseItem.keyCode.getKeyCode() ? null : Keystroke.UseItem;
                        } else {
                            return Keystroke.Attack;
                        }
                    } else {
                        return Keystroke.Back;
                    }
                } else {
                    return Keystroke.Forward;
                }
            } else {
                return Keystroke.Right;
            }
        } else {
            return Keystroke.Left;
        }
    }

    @EventTarget
    public void onRender(EventRender var1) {
        if (this.isEnabled() && mc.player != null) {
            if (!Minecraft.getInstance().gameSettings.showDebugInfo) {
                if (!Minecraft.getInstance().gameSettings.hideGUI) {
                    this.field23586 = var1.method13960();
                    if (Client.getInstance().guiManager.getGuiBlur()) {
                        for (Keystroke var7 : Keystroke.values()) {
                            Class9268 var8 = var7.method8814();
                            Class9268 var9 = var7.method8815();
                            RenderUtil.drawPortalBackground(
                                    this.field23585 + var8.field42635,
                                    this.field23586 + var8.field42636,
                                    this.field23585 + var8.field42635 + var9.field42635,
                                    this.field23586 + var8.field42636 + var9.field42636
                            );
                            // TODO: blur
//                            BlurEngine.drawBlur(this.field23585 + var8.field42635, this.field23586 + var8.field42636, var9.field42635, var9.field42636);
//                            BlurEngine.endBlur();
                            RenderUtil.endScissor();
                        }
                    }

                    for (Keystroke var19 : Keystroke.values()) {
                        Class9268 var21 = var19.method8814();
                        Class9268 var23 = var19.method8815();
                        float var10 = 1.0F;
                        float var11 = 1.0F;
                        if (Client.getInstance().guiManager.getGuiBlur()) {
                            var11 = 0.5F;
                            var10 = 0.5F;
                        }

                        // TODO
                        String var12 = RenderUtil.getKeyName(var19.bind.keyCode.getKeyCode());
                        if (var19.bind != mc.gameSettings.keyBindAttack) {
                            if (var19.bind == mc.gameSettings.keyBindUseItem) {
                                var12 = "R";
                            }
                        } else {
                            var12 = "L";
                        }

                        RenderUtil.drawRoundedRect( // TODO: check this, again
                                (float) (this.field23585 + var21.field42635),
                                (float) (this.field23586 + var21.field42636),
                                (float) (this.field23585 + var21.field42635 + var23.field42635),
                                (float) (this.field23586 + var21.field42636 + var23.field42636),
                                ColorUtils.applyAlpha(ClientColors.DEEP_TEAL.getColor(), 0.5F * var10)
                        );
                        RenderUtil.drawRoundedRect(
                                (float) (this.field23585 + var21.field42635),
                                (float) (this.field23586 + var21.field42636),
                                (float) var23.field42635,
                                (float) var23.field42636,
                                10.0F,
                                0.75F * var11
                        );
                        RenderUtil.drawString(
                                ResourceRegistry.JelloLightFont18,
                                (float) (this.field23585 + var21.field42635 + (var23.field42635 - ResourceRegistry.JelloLightFont18.getWidth(var12)) / 2),
                                (float) (this.field23586 + var21.field42636 + 12),
                                var12,
                                ClientColors.LIGHT_GREYISH_BLUE.getColor()
                        );
                    }

                    Iterator var14 = this.field23587.iterator();

                    while (var14.hasNext()) {
                        Class7930 var16 = (Class7930) var14.next();
                        Keystroke var18 = var16.field33987;
                        Class9268 var20 = var18.method8814();
                        Class9268 var22 = var18.method8815();
                        RenderUtil.drawPortalBackground(
                                this.field23585 + var20.field42635,
                                this.field23586 + var20.field42636,
                                this.field23585 + var20.field42635 + var22.field42635,
                                this.field23586 + var20.field42636 + var22.field42636
                        );
                        float var24 = 0.7F;
                        int var25 = 0;

                        for (Class7930 var28 : this.field23587) {
                            if (var28.field33987.equals(var18)) {
                                var25++;
                            }
                        }

                        if (var18.method8816().isKeyDown() && var16.field33988.calcPercent() >= var24 && var25 < 2) {
                            var16.field33988.method25318(var24);
                        }

                        float var27 = var16.field33988.calcPercent();
                        float alpha = (1.0F - var27 * (0.5F + var27 * 0.5F)) * 0.8F;
                        int var29 = ColorUtils.applyAlpha(-5658199, alpha);
                        if (Client.getInstance().guiManager.getGuiBlur()) { // TODO: check this
                            var29 = ColorUtils.applyAlpha(-1, alpha);
                        }

                        RenderUtil.method11436(
                                (float) (this.field23585 + var20.field42635 + var22.method34904() / 2),
                                (float) (this.field23586 + var20.field42636 + var22.field42636 / 2),
                                (float) (var22.method34904() - 4) * var27 + 4.0F,
                                var29
                        );
                        RenderUtil.endScissor();
                        if (var16.field33988.calcPercent() == 1.0F) {
                            var14.remove();
                        }
                    }

                    var1.method13962(160);
                }
            }
        }
    }

    @EventTarget
    public void onKeyPress(EventKeyPress event) {
        if (this.isEnabled() && mc.player != null) {
            if (this.getKeyStrokeForKey(event.getKey()) != null && !event.isPressed()) {
                this.field23587.add(new Class7930(this, this.getKeyStrokeForKey(event.getKey())));
            }
        }
    }

//    @EventTarget
//    public void onClick(ClickEvent var1) {
//        if (!this.isEnabled() || mc.player == null) {
//        }
//    }

    public enum Keystroke {
        Left(0.0F, 1.0F, mc.gameSettings.keyBindLeft),
        Right(2.0F, 1.0F, mc.gameSettings.keyBindRight),
        Forward(1.0F, 0.0F, mc.gameSettings.keyBindForward),
        Back(1.0F, 1.0F, mc.gameSettings.keyBindBack),
        Attack(0.0F, 2.0F, 74, mc.gameSettings.keyBindAttack),
        UseItem(1.02F, 2.0F, 73, mc.gameSettings.keyBindUseItem);

        public float field13914;
        public float field13915;
        public int field13916 = 48;
        public int field13917 = 48;
        public int field13918 = 3;
        public KeyBinding bind;

        private Keystroke(float var3, float var4, KeyBinding bind) {
            this.field13914 = var3;
            this.field13915 = var4;
            this.bind = bind;
        }

        private Keystroke(float var3, float var4, int var5, KeyBinding bind) {
            this.field13914 = var3;
            this.field13915 = var4;
            this.bind = bind;
            this.field13916 = var5;
        }

        public Class9268 method8814() {
            return new Class9268(
                    this, (int)(this.field13914 * (float)(this.field13916 + this.field13918)), (int)(this.field13915 * (float)(this.field13917 + this.field13918))
            );
        }

        public Class9268 method8815() {
            return new Class9268(this, this.field13916, this.field13917);
        }

        public KeyBinding method8816() {
            if (this != Left) {
                if (this != Right) {
                    if (this != Forward) {
                        if (this != Back) {
                            if (this != Attack) {
                                return this != UseItem ? null : mc.gameSettings.keyBindUseItem;
                            } else {
                                return mc.gameSettings.keyBindAttack;
                            }
                        } else {
                            return mc.gameSettings.keyBindBack;
                        }
                    } else {
                        return mc.gameSettings.keyBindForward;
                    }
                } else {
                    return mc.gameSettings.keyBindRight;
                }
            } else {
                return mc.gameSettings.keyBindLeft;
            }
        }
    }

    public static class Class7930 {
        public Keystroke field33987;
        public Animation field33988;
        public final KeyStrokes field33989;

        public Class7930(KeyStrokes var1, Keystroke var2) {
            this.field33989 = var1;
            this.field33988 = new Animation(300, 0);
            this.field33987 = var2;
        }
    }

    public static class Class9268 {
        public int field42635;
        public int field42636;
        public final Keystroke field42637;

        public Class9268(Keystroke var1, int var2, int var3) {
            this.field42637 = var1;
            this.field42635 = var2;
            this.field42636 = var3;
        }

        public int method34904() {
            return this.field42635;
        }
    }
}