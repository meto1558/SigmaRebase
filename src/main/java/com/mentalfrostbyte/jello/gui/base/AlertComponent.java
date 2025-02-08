package com.mentalfrostbyte.jello.gui.base;

public class AlertComponent {
    public ComponentType componentType;
    public String field44772;
    public int field44773;

    public AlertComponent(ComponentType componentType, String title, int var3) {
        this.componentType = componentType;
        this.field44772 = title;
        this.field44773 = var3;
    }

    public enum ComponentType {
        FIRST_LINE,
        SECOND_LINE,
        BUTTON,
        HEADER,
        TEXTBOX
    }
}
