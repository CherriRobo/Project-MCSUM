package com.mcsum.client.render;

import com.mcsum.rk800.RK800Entity;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.util.Identifier;

public class RK800Renderer extends MobEntityRenderer<RK800Entity, PlayerEntityRenderState, PlayerEntityModel> {
    private static final Identifier TEX = Identifier.of("mcsum", "textures/entity/rk800.png");

    public RK800Renderer(Context ctx, boolean slim) {
        super(ctx,
            new PlayerEntityModel(
                ctx.getPart(slim ? EntityModelLayers.PLAYER_SLIM : EntityModelLayers.PLAYER),
                slim
            ),
            0.5f
        );
    }

    @Override
    public PlayerEntityRenderState createRenderState() {
        return new PlayerEntityRenderState();
    }

    @Override
    public Identifier getTexture(PlayerEntityRenderState state) {
        return TEX;
    }
}