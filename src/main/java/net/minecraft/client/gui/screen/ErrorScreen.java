package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;

public class ErrorScreen extends Screen
{
    private final ITextComponent message;

    public ErrorScreen(ITextComponent p_i232277_1_, ITextComponent p_i232277_2_)
    {
        super(p_i232277_1_);
        this.message = p_i232277_2_;
    }

    protected void init()
    {
        super.init();
        this.addButton(new Button(this.width / 2 - 100, 140, 200, 20, DialogTexts.GUI_CANCEL, (p_213034_1_) ->
        {
            this.minecraft.displayGuiScreen((Screen)null);
        }));
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        this.fillGradient(matrices, 0, 0, this.width, this.height, -12574688, -11530224);
        drawCenteredString(matrices, this.font, this.title, this.width / 2, 90, 16777215);
        drawCenteredString(matrices, this.font, this.message, this.width / 2, 110, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
    }

    public boolean shouldCloseOnEsc()
    {
        return false;
    }
}
