package com.mentalfrostbyte.jello.gui.impl.jello;

import com.mentalfrostbyte.jello.gui.impl.jello.ingame.buttons.keybind.Bound;
import com.thizzer.jtouchbar.item.view.TouchBarButton;
import com.thizzer.jtouchbar.item.view.TouchBarView;
import com.thizzer.jtouchbar.item.view.action.TouchBarViewAction;

public class JelloTouchAction implements TouchBarViewAction {
    public final Bound field16767;
    public final JelloTouch field16768;

    public JelloTouchAction(JelloTouch var1, Bound var2) {
        this.field16768 = var1;
        this.field16767 = var2;
    }

    @Override
    public void onCall(TouchBarView var1) {
        this.field16767.getModuleTarget().toggle();
        ((TouchBarButton) var1).setBezelColor(this.field16768.method13740(this.field16767.getModuleTarget()));
    }
}
