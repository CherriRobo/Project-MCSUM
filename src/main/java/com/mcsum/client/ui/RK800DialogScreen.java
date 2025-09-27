/* Package. */
package com.mcsum.client.ui;

/* Imports. */
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class RK800DialogScreen extends Screen {

    /* States. */
    private enum DialogState {
        INTRO,
        MENU,
        FAQ_LOOKING_FOR,
        FAQ_CHESTS,
        ABOUT_DANI,
        BIRTHDAY,
        DEBUG
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

    /* State & Buttons. */
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
                //Single button: menu.
                this.addDrawableChild(
                    ButtonWidget.builder(Text.literal("Continue"), b -> setState(DialogState.MENU))
                        .dimensions(cx - 100, cy + 24, 200, 20).build()
                );
                this.addDrawableChild(
                    ButtonWidget.builder(Text.literal("Close"), b -> this.close())
                        .dimensions(cx - 100, cy + 48, 200, 20).build()
                );
            }

            case MENU -> {
                int y = cy - 6;
                int spacing = 22;

                this.addDrawableChild(
                    ButtonWidget.builder(Text.literal("What am I looking for?"), b -> setState(DialogState.FAQ_LOOKING_FOR))
                        .dimensions(cx - 120, y, 240, 20).build()
                ); y += spacing;

                this.addDrawableChild(
                    ButtonWidget.builder(Text.literal("Where are the treasure chests?"), b -> setState(DialogState.FAQ_CHESTS))
                        .dimensions(cx - 120, y, 240, 20).build()
                ); y += spacing;

                this.addDrawableChild(
                    ButtonWidget.builder(Text.literal("Tell me about Dani"), b -> setState(DialogState.ABOUT_DANI))
                        .dimensions(cx - 120, y, 240, 20).build()
                ); y += spacing;

                this.addDrawableChild(
                    ButtonWidget.builder(Text.literal("Happy Birthday message"), b -> setState(DialogState.BIRTHDAY))
                        .dimensions(cx - 120, y, 240, 20).build()
                ); y += spacing;

                this.addDrawableChild(
                    ButtonWidget.builder(Text.literal("Debug"), b -> setState(DialogState.DEBUG))
                        .dimensions(cx - 120, y, 240, 20).build()
                ); y += spacing;

                this.addDrawableChild(
                    ButtonWidget.builder(Text.literal("Close"), b -> this.close())
                        .dimensions(cx - 120, y, 240, 20).build()
                );
            }

            case FAQ_LOOKING_FOR, FAQ_CHESTS, ABOUT_DANI, BIRTHDAY, DEBUG -> {
                this.addDrawableChild(
                    ButtonWidget.builder(Text.literal("Back"), b -> setState(DialogState.MENU))
                        .dimensions(cx - 100, cy + 96, 200, 20).build()
                );
            }
        }
    }

    /* Render. */
    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        //Backdrop.
        ctx.fill(0, 0, this.width, this.height, 0x88000000);

        int boxW = Math.min(360, this.width - 40);
        int left = (this.width - boxW) / 2;
        int top  = this.height / 2 - 80;

        //Title.
        ctx.drawCenteredTextWithShadow(this.textRenderer, "RK800", this.width / 2, top - 24, 0xFFFFFFFF);

        //Body text per state.
        String body = switch (state) {
            case INTRO -> """
                    RK800: Hello. I am Connor — the Android sent by CyberLife.

                    I can help you with hints, directions, questions and you have a birthday message from myself and Dani.
                    """;

            case MENU -> """
                    Select an option:
                    """;

            case FAQ_LOOKING_FOR -> """
                    What am I looking for?

                    Firstly, please do enjoy the CyberLife building, here. It was constructed by Dani and Kensey through 1> month span.
                    There are hidden easter eggs such as treasure chests here.
                    
                    Talk to me again if you want further information.
                    """;

            case FAQ_CHESTS -> """
                    Where are the treasure chests?

                    Treasure chests are part of your birthday present. Hidden codes, messages and diaries
                    specifically placed for you.

                    • Floors 1–2 are DBH-themed. Look for places Connor would “investigate”.
                    • Other floors are interest-themed: check corners, behind decor, and near
                      anything that looks a bit “too deliberate”.
                    
                    Tip: If something catches your eye, it’s probably on purpose.
                    """;

            case ABOUT_DANI -> """
                    About Dani:

                    Dani set all of this up because they love you beyond words — beyond this
                    world, beyond the game, beyond any of it. Huge thank you to Kensey too for helping.
                    If you ever doubt it, talk to me or talk to Dani yourself. This world isn't going anywhere.
                    Visit as much as you want <3.
                    """;

            case BIRTHDAY -> """
                    Birthday message:

                    Hello Sum! I was sent by CyberLife on a mission given to us by Dani to make you have the best birthday ever!
                    You are dear to me, dear to Dani and dear to all that are close to you. You are only 21 years old once, so
                    hopefully this gift has lived up to its expectations. It was a lot getting my program into this game.
                    My design and coding has all been singlehandedly crafted by Dani.
                    """;

            case DEBUG -> buildDebugText();
        };

        drawWrapped(ctx, body, left, top, boxW, 0xFFFFFFFF);

        //Buttons/widgets.
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
                Debug Information
                -----------------
                FPS: %d
                Player: %s
                Dimension: %s
                Entities (client): %d
                """.formatted(fps, pos, dim, entities);
    }
}