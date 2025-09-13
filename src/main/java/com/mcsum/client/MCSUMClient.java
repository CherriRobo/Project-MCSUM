/* Package. */
package com.mcsum.client;

/* Imports. */
import com.mcsum.client.render.RK800Renderer;
import com.mcsum.client.ui.RK800DialogScreen;
import com.mcsum.net.OpenDialogPayload;
import com.mcsum.rk800.EntityInit;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class MCSUMClient implements ClientModInitializer {
    //Toggle.
    public static boolean SLIM = false;

    @Override
    public void onInitializeClient() {
        /* Register entity renderer. */
        EntityRendererRegistry.register(EntityInit.RK800, ctx -> new RK800Renderer(ctx, SLIM));

        /* Register networking receiver. */
        ClientPlayNetworking.registerGlobalReceiver(OpenDialogPayload.ID, (payload, context) -> {
            context.client().execute(() ->
                context.client().setScreen(new RK800DialogScreen())
            );
        });
    }
}