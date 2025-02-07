package com.mentalfrostbyte.jello.gui.impl.jello.altmanager;

import com.mentalfrostbyte.jello.gui.base.CustomGuiScreen;
import com.mentalfrostbyte.jello.gui.impl.jello.buttons.ScrollableContentPanel;
import com.mentalfrostbyte.jello.managers.util.account.microsoft.Account;
import net.minecraft.client.Minecraft;

import java.util.List;

public class Class1428 implements Runnable {
    public final AltManagerScreen altList;
    public final List<Account> field7662;
    public final boolean field7663;
    public final AltManagerScreen parent;

    public Class1428(AltManagerScreen parent, AltManagerScreen altList, List var3, boolean var4) {
        this.parent = parent;
        this.altList = altList;
        this.field7662 = var3;
        this.field7663 = var4;
    }

    @Override
    public void run() {
        int var3 = 0;
        if (AltManagerScreen.method13382(this.parent) != null) {
            var3 = AltManagerScreen.method13382(this.parent).method13513();
            this.altList.method13236(AltManagerScreen.method13382(this.parent));
        }

        CustomGuiScreen var4 = this.altList.method13221("alts");
        if (var4 != null) {
            this.altList.method13236(var4);
        }

        this.altList
                .showAlert(
                        AltManagerScreen.method13383(
                                this.parent,
                                new ScrollableContentPanel(
                                        this.altList,
                                        "alts",
                                        0,
                                        114,
                                        (int) ((float) Minecraft.getInstance().getMainWindow().getWidth() * AltManagerScreen.method13384(this.parent)) - 4,
                                        Minecraft.getInstance().getMainWindow().getHeight() - 119 - AltManagerScreen.getTitleOffset(this.parent)
                                )
                        )
                );

        for (Account var6 : this.field7662) {
            AltManagerScreen.method13386(this.parent, var6, this.field7663);
        }

        AltManagerScreen.method13382(this.parent).method13512(var3);
        AltManagerScreen.method13382(this.parent).setListening(false);
        AltManagerScreen.method13382(this.parent).method13515(false);
    }
}
