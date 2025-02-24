package com.mentalfrostbyte.jello.gui.impl.jello.ingame.buttons;

import baritone.api.BaritoneAPI;
import baritone.api.Settings;
import com.mentalfrostbyte.jello.gui.base.elements.impl.Checkbox;
import com.mentalfrostbyte.jello.gui.base.elements.impl.Text;
import com.mentalfrostbyte.jello.gui.base.elements.impl.colorpicker.ColorPicker;
import com.mentalfrostbyte.jello.gui.combined.AnimatedIconPanel;
import com.mentalfrostbyte.jello.gui.combined.CustomGuiScreen;
import com.mentalfrostbyte.jello.gui.impl.jello.buttons.ScrollableContentPanel;
import com.mentalfrostbyte.jello.gui.impl.jello.buttons.TextField;
import com.mentalfrostbyte.jello.util.client.render.ResourceRegistry;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientColors;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil2;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class BaritoneSettings extends AnimatedIconPanel {
    public ScrollableContentPanel scrollableContentPanel;
    public TextField textField;

    public BaritoneSettings(CustomGuiScreen var1, String var2, int var3, int var4, int var5, int var6, int var7) {
        super(var1, var2, var3, var4, 100, 100, false);
        this.widthA = var5 * var7;
        this.heightA = var6 * var7;


        textField = new TextField(this, "SettingsSearch", this.widthA - 160, 0, 150, 35);
        textField.method13154("Search...");


        scrollableContentPanel = new ScrollableContentPanel(this, "SettingsPanel", 0, 45, this.widthA, this.heightA - 60);

        textField.method13151((textField1) -> {
            filterSettings(textField1.getText());
        });

        populateSettings(BaritoneAPI.getSettings().allSettings);

        this.addToList(textField);
        this.addToList(scrollableContentPanel);
    }


    private void populateSettings(List<Settings.Setting<?>> settings) {
        scrollableContentPanel.buttonList.clearChildren();
        List<Settings.Setting> settingsList = settings.stream()
                .map(setting -> (Settings.Setting) setting)
                .toList();
        int height = 10;
        for (Settings.Setting setting : settingsList) {
            Text label = new Text(scrollableContentPanel, setting.getName(), 0, height, 100, 35, Text.defaultColorHelper, setting.getName());
            if (setting.getType() == Boolean.class) {
                Checkbox checkbox = new Checkbox(scrollableContentPanel, setting.getName() + "cbx", scrollableContentPanel.getWidthA() - 80, height, 25, 25);
                checkbox.method13705((Boolean) setting.value, false);
                checkbox.onPress((checkbox1) -> {
                    setting.value = checkbox.field21369;
                });

                scrollableContentPanel.addToList(label);
                scrollableContentPanel.addToList(checkbox);

                height += 40;

            } else if (setting.getType() == Integer.class) {
                TextField textField = new TextField(scrollableContentPanel, setting.getName() + "txt", scrollableContentPanel.getWidthA() - 120, height, 60, 25);
                textField.method13154(setting.value.toString());

                textField.method13151((textField1) -> {
                    try {
                        setting.value = Integer.parseInt(textField1.getText());
                    } catch (NumberFormatException e) {
                        textField1.setText(setting.value.toString());
                    }
                });

                scrollableContentPanel.addToList(label);
                scrollableContentPanel.addToList(textField);

                height += 40;
            } else if(setting.getType() == Double.class) {
                TextField textField = new TextField(scrollableContentPanel, setting.getName() + "txt", scrollableContentPanel.getWidthA() - 120, height, 60, 25);
                textField.method13154(setting.value.toString());

                textField.method13151((textField1) -> {
                    try {
                        setting.value = Double.parseDouble(textField1.getText());
                    } catch (NumberFormatException e) {
                        textField1.setText(setting.value.toString());
                    }
                });

                scrollableContentPanel.addToList(label);
                scrollableContentPanel.addToList(textField);

                height += 40;
            } else if(setting.getType() == Float.class) {
                TextField textField = new TextField(scrollableContentPanel, setting.getName() + "txt", scrollableContentPanel.getWidthA() - 120, height, 60, 25);
                textField.method13154(setting.value.toString());

                textField.method13151((textField1) -> {
                    try {
                        setting.value = Float.parseFloat(textField1.getText());
                    } catch (NumberFormatException e) {
                        textField1.setText(setting.value.toString());
                    }
                });

                scrollableContentPanel.addToList(label);
                scrollableContentPanel.addToList(textField);

                height += 40;
            } else if(setting.getType() == Long.class) {
                TextField textField = new TextField(scrollableContentPanel, setting.getName() + "txt", scrollableContentPanel.getWidthA() - 120, height, 60, 25);
                textField.method13154(setting.value.toString());

                textField.method13151((textField1) -> {
                    try {
                        setting.value = Long.parseLong(textField1.getText());
                    } catch (NumberFormatException e) {
                        textField1.setText(setting.value.toString());
                    }
                });

                scrollableContentPanel.addToList(label);
                scrollableContentPanel.addToList(textField);

                height += 40;
            } else if(setting.getType() == String.class) {
                TextField textField = new TextField(scrollableContentPanel, setting.getName() + "txt", scrollableContentPanel.getWidthA() - 120, height, 60, 25);
                textField.method13154(setting.value.toString());

                textField.method13151((textField1) -> {
                    setting.value = textField1.getText();
                });

                scrollableContentPanel.addToList(label);
                scrollableContentPanel.addToList(textField);

                height += 40;
            } else if(setting.getType() == java.awt.Color.class) {
                ColorPicker colorPicker = new ColorPicker(scrollableContentPanel, setting.getName() + "cp", scrollableContentPanel.getWidthA() - 120, height, 60, 25, ((Color) setting.value).getRGB(), false);

                colorPicker.onPress((colorPicker1) -> {
                    setting.value = new Color(colorPicker.method13049());
                });

                scrollableContentPanel.addToList(label);
                scrollableContentPanel.addToList(colorPicker);

                height += 40;
            }
        }
    }

    private void filterSettings(String searchText) {
        List<Settings.Setting<?>> filteredSettings = BaritoneAPI.getSettings().allSettings.stream()
                .filter(setting -> setting.getName().toLowerCase().contains(searchText.toLowerCase()))
                .collect(Collectors.toList());
        populateSettings(filteredSettings);
    }

    @Override
    public void draw(float partialTicks) {
        int var6 = RenderUtil2.applyAlpha(ClientColors.DEEP_TEAL.getColor(), 0.6F);
        RenderUtil.drawString(ResourceRegistry.JelloLightFont28, (float)(this.xA), (float)(this.yA), "Setting Name", var6);
        super.draw(partialTicks);
    }
}
