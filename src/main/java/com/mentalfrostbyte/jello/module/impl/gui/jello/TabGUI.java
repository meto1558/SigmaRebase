package com.mentalfrostbyte.jello.module.impl.gui.jello;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.event.impl.game.action.EventKeyPress;
import com.mentalfrostbyte.jello.event.impl.game.render.EventRender2D;
import com.mentalfrostbyte.jello.event.impl.game.render.EventRender2DOffset;
import com.mentalfrostbyte.jello.event.impl.game.render.EventRender3D;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.data.ModuleCategory;
import com.mentalfrostbyte.jello.module.impl.gui.jello.tabgui.AnimationEntry;
import com.mentalfrostbyte.jello.util.client.render.ResourceRegistry;
import com.mentalfrostbyte.jello.util.client.render.Resources;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientColors;

import com.mentalfrostbyte.jello.util.game.render.BlurEngine;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import com.mentalfrostbyte.jello.util.system.math.MathHelper;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import team.sdhq.eventBus.annotations.EventTarget;
import team.sdhq.eventBus.annotations.priority.HighestPriority;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class TabGUI extends Module {
    public ArrayList<AnimationEntry> animations = new ArrayList<>();
    public float animationSpeed = 1.0F;

    public HashMap<ModuleCategory, Float> categoryOffsetMap = new HashMap<>();
    public HashMap<Module, Float> moduleOffsetMap = new HashMap<>();
    public boolean isModuleListVisible = false;

    public List<ModuleCategory> categories = List.of(ModuleCategory.values());

    public int backgroundColor = MathHelper.applyAlpha(ClientColors.MID_GREY.getColor(), 0.05F);
    public int deepTealAlphaColor = MathHelper.applyAlpha(ClientColors.DEEP_TEAL.getColor(), 0.0625F);
    public int lightGreyishBlueAlphaColor = MathHelper.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.3F);

    public final Color[] topGradientColors = new Color[3];
    public final Color[] middleGradientColors = new Color[3];
    public final Color[] bottomGradientColors = new Color[3];
    public final Color[] altBottomGradientColors = new Color[3];
    public final Color[] altMiddleGradientColors = new Color[3];

    public final int x = 10;
    public int y = 90;
    public final int width = 150;
    public int height = 150;

    public final int moduleListWidth = 170;
    public int moduleListHeight = 0;
    public final int itemHeight = 30;
    public final int padding = 4;

    public ModuleCategory selectedCategory;
    public Module selectedModule;
    public int selectedCategoryIndex = 0;
    public int selectedModuleIndex = 0;
    public int selectedModuleListIndex;
    public int hoveredCategoryIndex = 0;

    public float scrollOffset = 0.0F;

    public TabGUI() {
        super(ModuleCategory.GUI, "TabGUI", "Manage mods without opening the ClickGUI");
        this.setAvailableOnClassic(false);
    }

    @EventTarget
    @HighestPriority
    public void onRender2D(EventRender2D event) {
        if (this.isEnabled() && mc.player != null) {
            if (Client.getInstance().guiManager.getHqIngameBlur()) {
                if (!Minecraft.getInstance().gameSettings.showDebugInfo) {
                    if (!Minecraft.getInstance().gameSettings.hideGUI) {
                        BlurEngine.updateRenderBounds(this.x, this.y, this.width, this.height);
                        if (this.isModuleListVisible) {
                            BlurEngine.updateRenderBounds(170, this.y, this.moduleListWidth, this.moduleListHeight);
                        }
                    }
                }
            }
        }
    }

    @EventTarget
    @HighestPriority
    public void onRender2DOffset(EventRender2DOffset event) {
        if (this.isEnabled() && mc.player != null && mc.world != null) {
            if (!Minecraft.getInstance().gameSettings.showDebugInfo) {
                if (!Minecraft.getInstance().gameSettings.hideGUI) {
                    this.height = 5 * this.itemHeight + this.padding;
                    float scrollDiff = Math.abs((float) this.getMaxScrollHeight() - this.scrollOffset);
                    boolean scrollDirectionUp = (float) this.getMaxScrollHeight() - this.scrollOffset < 0.0F;
                    this.scrollOffset = this.scrollOffset
                            + Math.min(scrollDiff, scrollDiff * 0.14F * this.animationSpeed) * (float) (!scrollDirectionUp ? 1 : -1);
                    this.y = event.getYOffset();

                    this.drawGradientBlurredRect(this.x, this.y, this.width, this.height,
                            this.topGradientColors, null, this.middleGradientColors, 1.0F);

                    RenderUtil.startScissor((float) this.x, (float) this.y, (float) this.width, (float) this.height);

                    this.renderSelectionHighlight(
                            this.x,
                            this.y - Math.round(this.scrollOffset),
                            this.categories.size() * this.itemHeight + this.padding,
                            this.width,
                            this.selectedCategoryIndex,
                            false
                    );

                    this.renderCategoryLabels(this.x, this.y - Math.round(this.scrollOffset), this.categories);

                    RenderUtil.endScissor();

                    if (this.isModuleListVisible) {
                        this.moduleListHeight = this.getModulesByCategory(this.selectedCategory).size() * this.itemHeight + this.padding;

                        this.drawGradientBlurredRect(170, this.y, this.moduleListWidth, this.moduleListHeight,
                                this.bottomGradientColors, this.altMiddleGradientColors, this.altBottomGradientColors, 1.0F);

                        this.renderSelectionHighlight(170, this.y, this.moduleListHeight, this.moduleListWidth, this.selectedModuleListIndex, true);

                        this.renderModuleLabels(170, this.y, this.getModulesByCategory(this.selectedCategory));
                    }

                    event.setYOffset(this.height + 10 + 99);
                }
            }
        }
    }

    public int getMaxScrollHeight() {
        return Math.max(this.selectedCategoryIndex * this.itemHeight - 4 * this.itemHeight, 0);
    }

    public List<Module> getModulesByCategory(ModuleCategory category) {
        return Client.getInstance().moduleManager.getModulesByCategory(category);
    }

    public void renderModuleLabels(int x, int y, List<Module> modules) {
        int index = 0;

        for (Module module : modules) {
            if (this.selectedModuleListIndex == index) {
                this.selectedModule = module;
            }

            if (!this.moduleOffsetMap.containsKey(module)) {
                this.moduleOffsetMap.put(module, 0.0F);
            }

            if (this.selectedModuleListIndex == index && this.moduleOffsetMap.get(module) < 14.0F) {
                this.moduleOffsetMap.put(module, this.moduleOffsetMap.get(module) + this.animationSpeed);
            } else if (this.selectedModuleListIndex != index && this.moduleOffsetMap.get(module) > 0.0F) {
                this.moduleOffsetMap.put(module, this.moduleOffsetMap.get(module) - this.animationSpeed);
            }

            if (module.isEnabled()) {
                RenderUtil.drawString(
                        ResourceRegistry.JelloMediumFont20,
                        (float) (x + 11) + this.moduleOffsetMap.get(module),
                        (float) (y + this.itemHeight / 2 - ResourceRegistry.JelloMediumFont20.getHeight() / 2 + 3
                                + index * this.itemHeight),
                        module.getName(),
                        ClientColors.LIGHT_GREYISH_BLUE.getColor());
            } else {
                RenderUtil.drawString(
                        ResourceRegistry.JelloLightFont20,
                        (float) (x + 11) + this.moduleOffsetMap.get(module),
                        (float) (y + this.itemHeight / 2 - ResourceRegistry.JelloLightFont20.getHeight() / 2 + 2
                                + index * this.itemHeight),
                        module.getName(),
                        ClientColors.LIGHT_GREYISH_BLUE.getColor());
            }

            index++;
        }
    }

    public void renderCategoryLabels(int x, int y, List<ModuleCategory> categories) {
        int index = 0;

        for (ModuleCategory category : categories) {
            if (this.selectedCategoryIndex == index) {
                this.selectedCategory = category;
            }

            if (!this.categoryOffsetMap.containsKey(category)) {
                this.categoryOffsetMap.put(category, 0.0F);
            }

            if (this.selectedCategoryIndex == index && this.categoryOffsetMap.get(category) < 14.0F) {
                this.categoryOffsetMap.put(category, this.categoryOffsetMap.get(category) + this.animationSpeed);
            } else if (this.selectedCategoryIndex != index && this.categoryOffsetMap.get(category) > 0.0F) {
                this.categoryOffsetMap.put(category, this.categoryOffsetMap.get(category) - this.animationSpeed);
            }

            RenderUtil.drawString(
                    ResourceRegistry.JelloLightFont20,
                    (float) (x + 11) + this.categoryOffsetMap.get(category),
                    (float) (y + this.itemHeight / 2 - ResourceRegistry.JelloLightFont20.getHeight() / 2 + 2
                            + index * this.itemHeight),
                    category.toString(),
                    -1);
            index++;
        }
    }

    public void renderSelectionHighlight(
            int x, int y, int maxHeight, int width, int itemCount, boolean isModuleList
    ) {
        int highlightOffset;

        if (isModuleList) {
            float offsetDifference = (float) (itemCount * this.itemHeight - this.hoveredCategoryIndex);

            if (this.hoveredCategoryIndex > itemCount * this.itemHeight) {
                this.hoveredCategoryIndex = (int) (this.hoveredCategoryIndex
                        + (!(offsetDifference * 0.14F * this.animationSpeed >= 1.0F)
                        ? offsetDifference * 0.14F * this.animationSpeed
                        : -this.animationSpeed));
            }

            if (this.hoveredCategoryIndex < itemCount * this.itemHeight) {
                this.hoveredCategoryIndex = (int) (this.hoveredCategoryIndex
                        + (!(offsetDifference * 0.14F * this.animationSpeed <= 1.0F)
                        ? offsetDifference * 0.14F * this.animationSpeed
                        : this.animationSpeed));
            }

            if (offsetDifference > 0.0F && this.hoveredCategoryIndex > itemCount * this.itemHeight) {
                this.hoveredCategoryIndex = itemCount * this.itemHeight;
            }

            if (offsetDifference < 0.0F && this.hoveredCategoryIndex < itemCount * this.itemHeight) {
                this.hoveredCategoryIndex = itemCount * this.itemHeight;
            }

            highlightOffset = this.hoveredCategoryIndex;
        } else {
            float offsetDifference = (float) (itemCount * this.itemHeight - this.selectedModuleIndex);

            if (this.selectedModuleIndex > itemCount * this.itemHeight) {
                this.selectedModuleIndex = (int) (this.selectedModuleIndex
                        + (!(offsetDifference * 0.14F * this.animationSpeed >= 1.0F)
                        ? offsetDifference * 0.14F * this.animationSpeed
                        : -this.animationSpeed));
            }

            if (this.selectedModuleIndex < itemCount * this.itemHeight) {
                this.selectedModuleIndex = (int) (this.selectedModuleIndex
                        + (!(offsetDifference * 0.14F * this.animationSpeed <= 1.0F)
                        ? offsetDifference * 0.14F * this.animationSpeed
                        : this.animationSpeed));
            }

            if (offsetDifference > 0.0F && this.selectedModuleIndex > itemCount * this.itemHeight) {
                this.selectedModuleIndex = itemCount * this.itemHeight;
            }

            if (offsetDifference < 0.0F && this.selectedModuleIndex < itemCount * this.itemHeight) {
                this.selectedModuleIndex = itemCount * this.itemHeight;
            }

            highlightOffset = this.selectedModuleIndex;
        }

        if (Math.round(this.scrollOffset) > 0 && this.selectedModuleIndex > 120) {
            this.selectedModuleIndex = Math.max(this.selectedModuleIndex, 120 + Math.round(this.scrollOffset));
        }

        RenderUtil.drawRect(
                (float) x,
                highlightOffset >= 0 ? (float) (highlightOffset + y) : (float) y,
                (float) (x + width),
                highlightOffset + this.padding + this.itemHeight <= maxHeight
                        ? (float) (highlightOffset + y + this.itemHeight + this.padding)
                        : (float) (y + maxHeight + this.padding),
                this.deepTealAlphaColor
        );

        RenderUtil.drawImage(
                (float) x,
                highlightOffset + this.padding + this.itemHeight <= maxHeight
                        ? (float) (highlightOffset + y + this.itemHeight - 10)
                        : (float) (y + maxHeight - 10),
                (float) width,
                14.0F,
                Resources.shadowTopPNG,
                this.lightGreyishBlueAlphaColor
        );

        RenderUtil.drawImage(
                (float) x,
                highlightOffset >= 0 ? (float) (highlightOffset + y) : (float) y,
                (float) width,
                14.0F,
                Resources.shadowBottomPNG,
                this.lightGreyishBlueAlphaColor
        );

        RenderUtil.startScissorUnscaled(
                x,
                highlightOffset >= 0 ? highlightOffset + y : y,
                x + width,
                highlightOffset + this.padding + this.itemHeight <= maxHeight
                        ? highlightOffset + y + this.itemHeight + this.padding
                        : y + maxHeight + this.padding
        );

        Iterator<AnimationEntry> iterator = this.animations.iterator();

        while (iterator.hasNext()) {
            AnimationEntry animElement = iterator.next();
            if (animElement.isModuleList == isModuleList) {
                RenderUtil.resetColors();

                if (animElement.animation.calcPercent() == 1.0F) {
                    iterator.remove();
                }
            }
        }

        RenderUtil.endScissor();
    }

    @EventTarget
    public void onRender3D(EventRender3D event) {
        if (this.isEnabled() && mc.player != null) {
            this.updateGradientColors();
            this.animationSpeed = (float) Math.max(Math.round(6.0F - (float) Minecraft.getFps() / 10.0F), 1);
        }
    }

    @EventTarget
    public void onKeyPress(EventKeyPress event) {
        if (this.isEnabled()) {
            switch (event.getKey()) {
                case GLFW.GLFW_KEY_ENTER:
                    if (this.isModuleListVisible) {
                        this.selectedModule.toggle();
                        this.animations.add(new AnimationEntry(this.isModuleListVisible));
                    }
                    break;
                case GLFW.GLFW_KEY_RIGHT:
                    this.animations.add(new AnimationEntry(this.isModuleListVisible));
                    if (this.isModuleListVisible) {
                        this.selectedModule.toggle();
                    }

                    this.isModuleListVisible = true;
                    break;
                case GLFW.GLFW_KEY_LEFT:
                    this.isModuleListVisible = false;
                    break;
                case GLFW.GLFW_KEY_DOWN:
                    if (!this.isModuleListVisible) {
                        this.selectedCategoryIndex++;
                        this.selectedModuleListIndex = 0;
                    } else {
                        this.selectedModuleListIndex++;
                    }
                    break;
                case GLFW.GLFW_KEY_UP:
                    if (!this.isModuleListVisible) {
                        this.selectedCategoryIndex--;
                        this.selectedModuleListIndex = 0;
                    } else {
                        this.selectedModuleListIndex--;
                    }
                    break;
                case GLFW.GLFW_KEY_TAB:
                case GLFW.GLFW_KEY_BACKSPACE:
                case GLFW.GLFW_KEY_INSERT:
                case GLFW.GLFW_KEY_DELETE:
                default:
                    return;
            }

            if (this.selectedCategoryIndex >= this.categories.size()) {
                this.selectedCategoryIndex = 0;
                this.selectedModuleIndex = this.selectedCategoryIndex * this.itemHeight - this.itemHeight;
            } else if (this.selectedCategoryIndex < 0) {
                this.selectedCategoryIndex = this.categories.size() - 1;
                this.selectedModuleIndex = this.selectedCategoryIndex * this.itemHeight + this.itemHeight;
            }

            if (this.selectedModuleListIndex >= this.getModulesByCategory(this.selectedCategory).size()) {
                this.selectedModuleListIndex = this.getModulesByCategory(this.selectedCategory).size() - 1;
            } else if (this.selectedModuleListIndex < 0) {
                this.selectedModuleListIndex = 0;
            }
        }
    }

    public void drawGradientBlurredRect(
            int x, int y, int width, int height,
            Color[] topColors, Color[] middleColors, Color[] bottomColors,
            float alphaMultiplier
    ) {
        boolean hqBlurEnabled = Client.getInstance().guiManager.getHqIngameBlur();
        int topColorRGB = MathHelper.averageColors(topColors).getRGB();
        int bottomColorRGB = MathHelper.averageColors(bottomColors).getRGB();

        if (middleColors != null) {
            int middleColorRGB = MathHelper.averageColors(middleColors).getRGB();
            topColorRGB = MathHelper.blendARGB(topColorRGB, middleColorRGB, 0.75F);
            bottomColorRGB = MathHelper.blendARGB(bottomColorRGB, middleColorRGB, 0.75F);
        }

        if (!hqBlurEnabled) {
            RenderUtil.drawVerticalGradientRect(x, y, x + width, y + height, topColorRGB, bottomColorRGB);
        } else {
            RenderUtil.startScissor((float) x, (float) y, (float) width, (float) height);
            BlurEngine.renderFramebufferToScreen();
            RenderUtil.endScissor();
            RenderUtil.drawRect((float) x, (float) y, (float) (x + width), (float) (y + height), this.backgroundColor);
        }

        RenderUtil.drawRoundedRect((float) x, (float) y, (float) width, (float) height, 8.0F, 0.7F * alphaMultiplier);
    }

    public void updateGradientColors() {
        if (!Client.getInstance().guiManager.getHqIngameBlur()) {
            if (!Minecraft.getInstance().gameSettings.showDebugInfo) {
                if (!Minecraft.getInstance().gameSettings.hideGUI) {
                    for (int i = 0; i < 3; i++) {
                        this.topGradientColors[i] = this.sampleAndBlendColor(this.x + this.width / 3 * i, this.y, this.topGradientColors[i]);
                        this.middleGradientColors[i] = this.sampleAndBlendColor(this.x + this.width / 3 * i, this.y + this.height, this.middleGradientColors[i]);
                        this.bottomGradientColors[i] = this.sampleAndBlendColor(this.x + this.width + 56 * i, this.y, this.bottomGradientColors[i]);
                        this.altBottomGradientColors[i] = this.sampleAndBlendColor(this.x + this.width + 56 * i, this.y + this.moduleListHeight, this.altBottomGradientColors[i]);
                        this.altMiddleGradientColors[i] = this.sampleAndBlendColor(this.x + this.width + 56 * i, this.y + this.moduleListHeight / 2, this.altMiddleGradientColors[i]);
                    }
                }
            }
        }
    }

    public Color sampleAndBlendColor(int x, int y, Color currentColor) {
        Color screenColor = RenderUtil.getScreenPixelColor(x, y);
        if (currentColor != null) {
            screenColor = MathHelper.blendColors(screenColor, currentColor, 0.08F * this.animationSpeed);
        }
        return screenColor;
    }
}
