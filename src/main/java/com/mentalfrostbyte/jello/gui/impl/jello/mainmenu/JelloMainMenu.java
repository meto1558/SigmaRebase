package com.mentalfrostbyte.jello.gui.impl.jello.mainmenu;

import com.mentalfrostbyte.jello.Client;
import com.mentalfrostbyte.jello.gui.base.CustomGuiScreen;
import com.mentalfrostbyte.jello.gui.base.Screen;
import com.mentalfrostbyte.jello.gui.base.elements.impl.Button;
import com.mentalfrostbyte.jello.gui.base.elements.impl.Label;
import com.mentalfrostbyte.jello.gui.base.elements.impl.TextButton;
import com.mentalfrostbyte.jello.gui.impl.jello.altmanager.AltManagerScreen;
import com.mentalfrostbyte.jello.gui.impl.jello.buttons.GetPremiumButton;
import com.mentalfrostbyte.jello.gui.impl.jello.buttons.TextButtonWithImage;
import com.mentalfrostbyte.jello.gui.impl.jello.viamcp.JelloPortalScreen;
import com.mentalfrostbyte.jello.managers.GuiManager;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientColors;
import com.mentalfrostbyte.jello.util.client.render.theme.ColorHelper;
import com.mentalfrostbyte.jello.util.client.render.ResourceRegistry;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil2;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import com.mentalfrostbyte.jello.util.client.render.Resources;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viamcp.protocolinfo.ProtocolInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.OptionsScreen;
import net.minecraft.client.gui.screen.WorldSelectionScreen;
import net.minecraft.realms.RealmsBridgeScreen;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;

import java.util.Comparator;
import java.util.List;

public class JelloMainMenu extends CustomGuiScreen {
    private final Button singleplayerButton;
    private final Button multiplayerButton;
    private final Button realmsButton;
    private final Button optionsButton;
    private final Button altManagerButton;
    private final GetPremiumButton premiumButton;
    private final Label field21129;
    private final Label field21130;
    private final TextButtonWithImage loginButton;
    private final TextButton changelogButton;
    private final TextButton field21133;
    public int field21134 = 0;

    public JelloMainMenu(CustomGuiScreen var1, String var2, int var3, int var4, int var5, int var6) {
        super(var1, var2, var3, var4, var5, var6);
        this.setListening(false);
        TrueTypeFont var15 = ResourceRegistry.JelloLightFont20;
        int var17 = 0;
        int var18 = 80;
        int var19 = 10;
        String prod = "© Sigma Prod";
        StringBuilder clientInfo = new StringBuilder().append("Jello for Sigma ");
        Client.getInstance();
        List<ProtocolVersion> sorted = ProtocolInfo.
                PROTOCOL_INFOS
                .stream()
                .sorted(Comparator.comparing(ProtocolInfo::getProtocolVersion))
                .map(ProtocolInfo::getProtocolVersion)
                .toList();
        String oldestVersion = sorted.get(0).getIncludedVersions().stream().toList().get(0);
        List<String> newestIncludedVersions = sorted.get(sorted.size() - 1).getIncludedVersions().stream().toList();
        String newestVersion = newestIncludedVersions.get(newestIncludedVersions.size() - 1);
        this.addToList(
                this.singleplayerButton = new MainMenuButton(
                        this,
                        "Singleplayer",
                        this.method13447(var17++),
                        this.method13448(),
                        128,
                        128,
                        Resources.singleplayerPNG,
                        new ColorHelper(ClientColors.LIGHT_GREYISH_BLUE.getColor(), ClientColors.DEEP_TEAL.getColor())
                )
        );
        String version = clientInfo
                .append(Client.FULL_VERSION).append("  -  ").append(oldestVersion).append(" to ").append(newestVersion).toString();
        this.addToList(
                this.multiplayerButton = new MainMenuButton(
                        this,
                        "Multiplayer",
                        this.method13447(var17++),
                        this.method13448(),
                        128,
                        128,
                        Resources.multiplayerPNG,
                        new ColorHelper(ClientColors.LIGHT_GREYISH_BLUE.getColor(), ClientColors.DEEP_TEAL.getColor())
                )
        );
        this.addToList(
                this.realmsButton = new MainMenuButton(
                        this,
                        "Realms",
                        this.method13447(var17++),
                        this.method13448(),
                        128,
                        128,
                        Resources.shopPNG,
                        new ColorHelper(ClientColors.LIGHT_GREYISH_BLUE.getColor(), ClientColors.DEEP_TEAL.getColor())
                )
        );
        this.addToList(
                this.optionsButton = new MainMenuButton(
                        this,
                        "Options",
                        this.method13447(var17++),
                        this.method13448(),
                        128,
                        128,
                        Resources.optionsPNG,
                        new ColorHelper(ClientColors.LIGHT_GREYISH_BLUE.getColor(), ClientColors.DEEP_TEAL.getColor())
                )
        );
        this.addToList(
                this.altManagerButton = new MainMenuButton(
                        this,
                        "Alt Manager",
                        this.method13447(var17++),
                        this.method13448(),
                        128,
                        128,
                        Resources.altPNG,
                        new ColorHelper(ClientColors.LIGHT_GREYISH_BLUE.getColor(), ClientColors.DEEP_TEAL.getColor())
                )
        );
        this.addToList(
                this.field21130 = new Label(
                        this, "Copyright", 10, this.getHeightA() - 31, var15.getWidth(prod), 128, new ColorHelper(ClientColors.LIGHT_GREYISH_BLUE.getColor()), prod, var15
                )
        );
        this.addToList(
                this.field21129 = new Label(
                        this,
                        "Version",
                        this.getWidthA() - var15.getWidth(version) - 9,
                        this.getHeightA() - 31,
                        128,
                        128,
                        new ColorHelper(ClientColors.LIGHT_GREYISH_BLUE.getColor()),
                        version,
                        var15
                )
        );
        this.field21130.field20779 = true;
        this.field21129.field20779 = true;
        this.addToList(
                this.changelogButton = new TextButton(
                        this, "changelog", 432, 24, 110, 50, new ColorHelper(RenderUtil2.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.7F)), "Changelog", ResourceRegistry.JelloLightFont20
                )
        );
        this.addToList(
                this.field21133 = new TextButton(
                        this, "quit", 30, 24, 50, 50, new ColorHelper(RenderUtil2.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.4F)), "Exit", ResourceRegistry.JelloLightFont20
                )
        );
        this.field21133.doThis((var1x, var2x) -> {
            ((MainMenuScreen) this.getParent()).method13341();
            new Thread(() -> {
                try {
                    Thread.sleep(2000L);
                    Minecraft.getInstance().shutdown();
                } catch (InterruptedException e) {
                    Minecraft.getInstance().shutdown();
                }
            }).start();
        });
        this.addToList(this.loginButton = new TextButtonWithImage(this, "Account", 0, var19, 0, var18, "Log in"));
        this.addToList(this.premiumButton = new GetPremiumButton(this, "pre", 0, 0, 240, 100));
        this.premiumButton.method13247((var1x, var2x) -> {
            if (Client.getInstance().networkManager.encryptor != null) {
                ((MainMenuScreen)this.getParent()).animateNext();
            } else {
                this.displayScreen(new RegisterScreen());
            }
        });
        this.changelogButton.doThis((var1x, var2x) -> ((MainMenuScreen) this.getParent()).animateIn());
        this.singleplayerButton.doThis((var1x, var2x) -> this.displayGUI(new WorldSelectionScreen(Minecraft.getInstance().currentScreen)));
        this.multiplayerButton.doThis((var1x, var2x) -> this.displayGUI(new JelloPortalScreen(Minecraft.getInstance().currentScreen)));
        this.optionsButton.doThis((var1x, var2x) -> this.displayGUI(new OptionsScreen(Minecraft.getInstance().currentScreen, Minecraft.getInstance().gameSettings)));
        this.altManagerButton.doThis((var1x, var2x) -> this.displayScreen(new AltManagerScreen()));
        this.realmsButton.doThis((var1x, var2x) -> this.method13443());
        this.loginButton.doThis((var1x, var2x) -> {
            if (Client.getInstance().networkManager.encryptor != null) {
                ((MainMenuScreen)this.getParent()).logout();
            } else {
                this.displayScreen(new RegisterScreen());
            }
        });
        this.field21130.doThis((var1x, var2x) -> {
            if (this.field21134++ > 8) {
                Client.getInstance().guiManager.handleScreen(new RegisterScreen());
            }
        });
    }

    public void method13443() {
        RealmsBridgeScreen var3 = new RealmsBridgeScreen();
        var3.func_231394_a_(Minecraft.getInstance().currentScreen);
        this.playClickSound();
    }

    @Override
    public void draw(float partialTicks) {
        this.method13224();
        Texture largeLogo = Resources.logoLargePNG;
        int imageWidth = largeLogo.getImageWidth();
        int imageHeight = largeLogo.getImageHeight();
        if (GuiManager.scaleFactor > 1.0F) {
            largeLogo = Resources.logoLarge2xPNG;
        }

        RenderUtil.drawImage(
                (float) (this.getWidthA() / 2 - imageWidth / 2),
                (float) (this.getHeightA() / 2 - imageHeight),
                (float) imageWidth,
                (float) imageHeight,
                Resources.logoLargePNG,
                RenderUtil2.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), partialTicks)
        );
        super.draw(partialTicks);
    }

    @Override
    public void updatePanelDimensions(int newHeight, int newWidth) {
        this.premiumButton.setEnabled(!Client.getInstance().networkManager.isPremium());
        int var5 = 30;
        int var6 = 90;
        this.changelogButton.setXA(var6 + (!Client.getInstance().networkManager.isPremium() ? 202 : 0));
        this.field21133.setXA(var5 + (!Client.getInstance().networkManager.isPremium() ? 202 : 0));
        super.updatePanelDimensions(newHeight, newWidth);
    }

    public void playClickSound() {
        Client.getInstance().soundManager.play("clicksound");
    }

    public void displayGUI(net.minecraft.client.gui.screen.Screen var1) {
        Minecraft.getInstance().displayGuiScreen(var1);
        this.playClickSound();
    }

    public void displayScreen(Screen var1) {
        Client.getInstance().guiManager.handleScreen(var1);
        this.playClickSound();
    }

    private int method13447(int var1) {
        return this.getWidthA() / 2 - 305 + var1 * 128 + var1 * -6;
    }

    private int method13448() {
        return this.getHeightA() / 2 + 14;
    }
}
