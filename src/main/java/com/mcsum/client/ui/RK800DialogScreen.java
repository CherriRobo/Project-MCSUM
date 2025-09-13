/* Package. */
package com.mcsum.client.ui;

/* Imports. */
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class RK800DialogScreen extends Screen {

    private enum DialogState {
        INTRO,
        DEBUG,
        BIRTHDAY_CHECK,
        BIRTHDAY_YES,
        BIRTHDAY_NO
    }

    private DialogState state = DialogState.INTRO;

    public RK800DialogScreen() {
        super(Text.literal("RK800"));
    }

    /* Lifecycle. */

    @Override
    protected void init() {
        rebuildButtons();
    }

    @Override
    public void close() {
        this.client.setScreen(null);
    }

    /* State and buttons. */

    private void setState(DialogState next) {
        this.state = next;
        this.clearChildren();
        rebuildButtons();
    }

    private void rebuildButtons() {
        int cx = this.width / 2;
        int cy = this.height / 2;

        switch (state) {
            case INTRO -> {
                this.addDrawableChild(
                    ButtonWidget.builder(Text.literal("Hello Connor! (Sum)"), b -> setState(DialogState.BIRTHDAY_CHECK))
                        .dimensions(cx - 100, cy + 24, 200, 20).build()
                );
                this.addDrawableChild(
                    ButtonWidget.builder(Text.literal("Debug"), b -> setState(DialogState.DEBUG))
                        .dimensions(cx - 100, cy + 48, 200, 20).build()
                );
                this.addDrawableChild(
                    ButtonWidget.builder(Text.literal("Cancel"), b -> this.close())
                        .dimensions(cx - 100, cy + 72, 200, 20).build()
                );
            }
            case DEBUG -> {
                this.addDrawableChild(
                    ButtonWidget.builder(Text.literal("Back"), b -> setState(DialogState.INTRO))
                        .dimensions(cx - 100, cy + 96, 200, 20).build()
                );
            }
            case BIRTHDAY_CHECK -> {
                this.addDrawableChild(
                    ButtonWidget.builder(Text.literal("Yes!"), b -> setState(DialogState.BIRTHDAY_YES))
                        .dimensions(cx - 100, cy + 48, 200, 20).build()
                );
                this.addDrawableChild(
                    ButtonWidget.builder(Text.literal("No."), b -> setState(DialogState.BIRTHDAY_NO))
                        .dimensions(cx - 100, cy + 72, 200, 20).build()
                );
            }
            case BIRTHDAY_YES, BIRTHDAY_NO -> {
                this.addDrawableChild(
                    ButtonWidget.builder(Text.literal("Close"), b -> this.close())
                        .dimensions(cx - 100, cy + 96, 200, 20).build()
                );
            }
        }
    }

    /* Render. */

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, 0x88000000);

        int boxW = Math.min(320, this.width - 40);
        int left = (this.width - boxW) / 2;
        int top = this.height / 2 - 70;

        // Title.
        ctx.drawCenteredTextWithShadow(this.textRenderer, "RK800", this.width / 2, top - 24, 0xFFFFFFFF);

        // Body by state.
        String body = switch (state) {
            case INTRO -> """
                    RK800: Hello, I am Connor, the Android sent by Cyberlife.
                    """;
            case DEBUG -> buildDebugText();
            case BIRTHDAY_CHECK -> """
                    RK800: I believe you are Sum. I've been sent a request to meet you by someone you call 'Dani'.

                    I believe it is your birthday, correct?
                    """;
            case BIRTHDAY_YES -> """
                    Happy birthday! I do not feel connection in the way humans do but I... wish you love? For your birthday.
                    You are someone dear to Dani. Dear to me.

                    Please follow me.

                    (Initiate: AI pathway.)
                    """;
            case BIRTHDAY_NO -> """
                    Ah! It may be late or early. Regardless. I... wish you love? For your birthday.
                    You are someone dear to Dani. Dear to me.

                    Please follow me.

                    (Initiate: AI pathway.)
                    """;
        };

        drawWrapped(ctx, body, left, top, boxW, 0xFFFFFFFF);
        super.render(ctx, mouseX, mouseY, delta);
    }

    /* Helpers. */

    private void drawWrapped(DrawContext ctx, String text, int x, int y, int width, int color) {
        for (String rawLine : text.split("\n")) {
            if (rawLine.isEmpty()) {
                y += this.textRenderer.fontHeight + 2;
                continue;
            }
            var wrapped = this.textRenderer.wrapLines(Text.literal(rawLine), width);
            for (var line : wrapped) {
                ctx.drawText(this.textRenderer, line, x, y, color, false);
                y += this.textRenderer.fontHeight + 2;
            }
        }
    }

    private String buildDebugText() {
        MinecraftClient mc = MinecraftClient.getInstance();
        var player = mc.player;
        var world = mc.world;

        String pos = (player != null)
                ? String.format("(%.2f, %.2f, %.2f)", player.getX(), player.getY(), player.getZ())
                : "(n/a)";

        String dim = (world != null && world.getRegistryKey() != null)
                ? world.getRegistryKey().getValue().toString()
                : "(n/a)";

        int fps = mc.getCurrentFps();
        int entities = (world != null) ? world.getRegularEntityCount() : 0;

        return """
                Debug information:
                -----------------
                FPS: %d
                Player: %s
                Dimension: %s
                Entities (client): %d
                """.formatted(fps, pos, dim, entities);
    }
}