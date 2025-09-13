/* Package. */
package com.mcsum.rk800;

/* Imports. */
import com.mcsum.net.OpenDialogPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class RK800Entity extends PathAwareEntity {
    /* Constructor. */
    public RK800Entity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
        this.setCustomName(Text.literal("RK800")); //Name tag.
        this.setCustomNameVisible(true); //Name above head.
    }

    /* Attributes. */
    //Base stats.
    public static DefaultAttributeContainer.Builder createAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 20.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.25);
    }

    /* AI goals. */
    @Override
    protected void initGoals() {
        //Wander randomly.
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
        //Look at nearby players within 8 blocks.
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        //Idle look around.
        this.goalSelector.add(7, new LookAroundGoal(this));
    }

    /* Right click interaction. */
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (!this.getWorld().isClient && player instanceof ServerPlayerEntity sp) {
            ServerPlayNetworking.send(sp, new OpenDialogPayload());
        }
        return ActionResult.SUCCESS;
    }
}