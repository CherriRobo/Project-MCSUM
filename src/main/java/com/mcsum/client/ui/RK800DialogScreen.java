package com.mcsum.client.ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class RK800DialogScreen extends Screen {
    public RK800DialogScreen() {
        super(Text.literal("RK800"));
    }

    @Override
public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
    ctx.fill(0, 0, this.width, this.height, 0x88000000);
    super.render(ctx, mouseX, mouseY, delta);
    ctx.drawCenteredTextWithShadow(
    this.textRenderer,
    "RK800: Test",
    this.width / 2,
    this.height / 2,
    0xFFFFFFFF);
    }
}