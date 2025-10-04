package com.mentalfrostbyte.jello.module.data;

public enum ModuleCategory {
    RENDER("Render"),
    PLAYER("Player"),
    COMBAT("Combat"),
    WORLD("World"),
    MISC("Misc"),
    EXPLOIT("Exploit"),
    MOVEMENT("Movement"),
    GUI("Gui"),
    ITEM("Item");

    public final String name;

    ModuleCategory(String name) {
        this.name = name;
    }

}