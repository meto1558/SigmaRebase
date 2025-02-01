package com.mentalfrostbyte.jello.gui.impl.classic.altmanager;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.gui.base.CustomGuiScreen;
import com.mentalfrostbyte.jello.gui.base.Screen;
import com.mentalfrostbyte.jello.gui.base.VerticalScrollBar;
import com.mentalfrostbyte.jello.gui.impl.classic.altmanager.buttons.AltList;
import com.mentalfrostbyte.jello.gui.impl.classic.clickgui.buttons.Input;
import com.mentalfrostbyte.jello.gui.impl.classic.mainmenu.ParticleOverlay;
import com.mentalfrostbyte.jello.gui.impl.jello.buttons.TextField;
import com.mentalfrostbyte.jello.gui.unmapped.*;
import com.mentalfrostbyte.jello.managers.AccountManager;
import com.mentalfrostbyte.jello.managers.util.account.microsoft.Account;
import com.mentalfrostbyte.jello.util.ClientColors;
import com.mentalfrostbyte.jello.util.ResourceRegistry;
import com.mentalfrostbyte.jello.util.render.ColorUtils;
import com.mentalfrostbyte.jello.util.render.RenderUtil;
import com.mentalfrostbyte.jello.util.render.Resources;
import com.mentalfrostbyte.jello.util.unmapped.Class2218;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuHolder;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import totalcross.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ClassicAltScreen extends Screen {
    private AltList altList;
    public AccountManager accountManager = Client.getInstance().accountManager;
    private Input altSearchBox;
    private final ClassicAltScreenGroup altScreenGroup;
    private String status = "§7Idle...";

    public ClassicAltScreen() {
        super("Alt Manager");
        this.setListening(false);
        ParticleOverlay var3;
        this.addToList(var3 = new ParticleOverlay(this, "particles"));
        var3.method13294(true);
        ArrayList var4 = new ArrayList();
        ServerList var5 = new ServerList(Minecraft.getInstance());
        var5.loadServerList();
        int var6 = var5.countServers();

        for (int var7 = 0; var7 < var6; var7++) {
            ServerData var8 = var5.getServerData(var7);
            if (!var4.contains(var8.serverIP)) {
                var4.add(var8.serverIP);
            }
        }

        int var9 = 790;
        this.addToList(
                this.altSearchBox = new Input(
                        this,
                        "textbox",
                        (Minecraft.getInstance().getMainWindow().getWidth() - var9) / 2 - 140,
                        this.getHeightA() - 40,
                        140,
                        32,
                        TextField.field20741,
                        "",
                        "Search...",
                        ResourceRegistry.JelloLightFont18
                )
        );
        this.altSearchBox.setFont(ResourceRegistry.DefaultClientFont);
        this.altSearchBox.method13151(var1 -> this.method13402());
        this.method13403();
        this.addToList(
                this.altScreenGroup = new ClassicAltScreenGroup(this, "toolbar", (Minecraft.getInstance().getMainWindow().getWidth() - var9) / 2 + 16, this.getHeightA() - 94)
        );
        this.altScreenGroup.method13296(false);
    }

    private void method13395(Account var1) {
        int var4 = 52;
        Class4349 var5;
        this.altList
                .addToList(
                        var5 = new Class4349(this.altList, var1.getEmail(), 4, var4 * this.method13400() + 4, this.altList.getWidthA() - 8, var4, var1)
                );
        var5.doThis((var2, var3) -> {
            if (var3 == 0) {
                Class4349 var6 = this.method13406();
                if (var6 != null) {
                    var6.method13580(false);
                }

                var5.method13580(true);
                this.altScreenGroup.method13296(true);
                if (var6 != null && var6.equals(var5)) {
                    this.method13398(var5);
                }
            }
        });
    }

    public void method13396() {
        Class4349 var3 = this.method13406();
        if (var3 != null) {
            this.method13398(var3);
        }
    }

    public void method13397() {
        Class4349 var3 = this.method13406();
        if (var3 != null) {
            this.accountManager.removeAccount(var3.field21249);
            this.altScreenGroup.method13296(false);
            this.method13402();
        }
    }

    public void method13398(Class4349 var1) {
        this.method13399(var1.field21249);
    }

    public void method13399(Account var1) {
        this.status = "§bLogging in...";
        new Thread(() -> {
            if (!this.accountManager.login(var1)) {
                this.status = "§cLogin Failed!";
            } else {
                this.status = "§aLogged in. (" + var1.getName() + (!var1.isEmailAValidEmailFormat() ? "" : " - offline name") + ")";
            }
        }).start();
    }

    @Override
    public void draw(float partialTicks) {
        this.method13401();
        super.draw(partialTicks);
        RenderUtil.drawString(ResourceRegistry.DefaultClientFont, 20.0F, 20.0F, Minecraft.getInstance().getSession().getUsername(), -2236963);
        RenderUtil.drawString(
                ResourceRegistry.DefaultClientFont,
                (float) (this.getWidthA() / 2),
                20.0F,
                "Account Manager - " + this.method13400() + " alts",
                ClientColors.LIGHT_GREYISH_BLUE.getColor(),
                Class2218.field14492,
                Class2218.field14488
        );
        RenderUtil.drawString(
                ResourceRegistry.DefaultClientFont,
                (float) (this.getWidthA() / 2),
                40.0F,
                this.status,
                ClientColors.LIGHT_GREYISH_BLUE.getColor(),
                Class2218.field14492,
                Class2218.field14488,
                false
        );
    }

    private int method13400() {
        int var3 = 0;

        for (CustomGuiScreen var5 : this.altList.getChildren()) {
            if (!(var5 instanceof VerticalScrollBar)) {
                for (CustomGuiScreen var7 : var5.getChildren()) {
                    var3++;
                }
            }
        }

        return var3;
    }

    private void method13401() {
        RenderUtil.drawImage(0.0F, 0.0F, (float) this.getWidthA(), (float) this.getHeightA(), Resources.mainmenubackground);
        RenderUtil.drawRoundedRect2(0.0F, 0.0F, (float) this.getWidthA(), (float) this.getHeightA(), ColorUtils.applyAlpha(ClientColors.DEEP_TEAL.getColor(), 0.23F));
    }

    @Override
    public void keyPressed(int keyCode) {
        super.keyPressed(keyCode);
        if (keyCode == 256) {
            Minecraft.getInstance().displayGuiScreen(new MainMenuHolder());
        }
    }

    @Override
    public JSONObject toConfigWithExtra(JSONObject config) {
        this.accountManager.saveAlts();
        return config;
    }

    @Override
    public void loadConfig(JSONObject config) {
        for (CustomGuiScreen var5 : this.altList.getChildren()) {
            if (!(var5 instanceof VerticalScrollBar)) {
                for (CustomGuiScreen var7 : var5.getChildren()) {
                    this.altList.method13234(var7);
                }
            }
        }

        this.method13402();
    }

    public void method13402() {
        this.runThisOnDimensionUpdate(new DimensionUpdateListener(this));
    }

    public void method13403() {
        List<Account> var4 = Class8270.method28878(this.accountManager.getAccounts(), Class2209.field14448, "", this.altSearchBox.getTypedText());
        int var5 = 0;
        if (this.altList != null) {
            var5 = this.altList.method13513();
            this.method13236(this.altList);
        }

        CustomGuiScreen var6 = this.method13221("alts");
        if (var6 != null) {
            this.method13236(var6);
        }

        int var7 = Minecraft.getInstance().getMainWindow().getWidth() - 200;
        int var8 = this.getWidthA() - var7;
        this.showAlert(this.altList = new AltList(this, "alts", var8 / 2, 69, var7, Minecraft.getInstance().getMainWindow().getHeight() - 169));

        for (Account var10 : var4) {
            this.method13395(var10);
        }

        this.altList.method13512(var5);
        this.altList.setListening(false);
        this.altList.method13515(true);
        this.altList.method13242();
    }

    private void method13404(Object var1) {
    }

    public int method13405() {
        return Minecraft.getInstance().getMainWindow().getHeight() / 12 + 280 + Minecraft.getInstance().getMainWindow().getHeight() / 12;
    }

    public Class4349 method13406() {
        for (CustomGuiScreen var4 : this.altList.getChildren()) {
            if (!(var4 instanceof VerticalScrollBar)) {
                for (CustomGuiScreen var6 : var4.getChildren()) {
                    if (var6 instanceof Class4349) {
                        Class4349 var7 = (Class4349) var6;
                        if (var7.method13582()) {
                            return var7;
                        }
                    }
                }
            }
        }

        return null;
    }
}
