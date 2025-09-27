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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import java.util.EnumSet;
import net.minecraft.entity.ai.goal.Goal;

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
    //Celebrate when a player is within 4 blocks.
    this.goalSelector.add(3, new CelebrateSequenceGoal(this, 4.0));
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

/* Celebrate. */
private static class CelebrateSequenceGoal extends Goal {
    private final RK800Entity mob;
    private final double triggerRadius;
    private PlayerEntity target;

    //Phases
    private static final int PHASE_JUMP  = 0;
    private static final int PHASE_PAUSE = 1;
    private static final int PHASE_CROUCH_CIRCLE = 2;
    private static final int PHASE_SPIN  = 3;

    private int phase = PHASE_JUMP;
    private int phaseTicks = 0;
    private int cooldownTicks = 0;

    //Turnables.
    private static final int JUMP_PHASE_LEN   = 50;   //~2.5s.
    private static final int PAUSE_LEN        = 20;   //~1s.
    private static final int CROUCH_LEN       = 80;   //~4s circle.
    private static final int SPIN_LEN         = 40;   //~2s.
    private static final int JUMP_EVERY       = 6;    //Jump ~3x/s.
    private static final float SPIN_DEG_PER_T = 10f;  //Spin speed.
    private static final double CIRCLE_RADIUS = 2.2;  //Meters from player.
    private static final double CIRCLE_SPEED  = 0.25; //Radians per tick (~40°/s).
    private static final double MOVE_SPEED    = 1.1;  //Nav speed while circling.
    private static final int COOLDOWN_AFTER   = 80;   //~4s before it can trigger again.

    //Circle state.
    private double angle = 0.0;

    public CelebrateSequenceGoal(RK800Entity mob, double triggerRadius) {
        this.mob = mob;
        this.triggerRadius = triggerRadius;
        this.setControls(EnumSet.of(Control.LOOK, Control.MOVE, Control.JUMP));
    }

    @Override
    public boolean canStart() {
        if (cooldownTicks > 0) {
            cooldownTicks--;
            return false;
        }
        PlayerEntity p = mob.getWorld().getClosestPlayer(mob, triggerRadius);
        if (p == null) return false;
        this.target = p;
        this.phase = PHASE_JUMP;
        this.phaseTicks = 0;
        this.angle = 0.0;
        return true;
    }

    @Override
    public boolean shouldContinue() {
        if (target == null || !target.isAlive()) return false;
        //End spin.
        if (phase == PHASE_SPIN && phaseTicks >= SPIN_LEN) return false;
        //Cancel if player leaves area.
        return mob.squaredDistanceTo(target) <= (triggerRadius * triggerRadius) + 9.0;
    }

    @Override
    public void start() {
        mob.getNavigation().stop();
        mob.setPose(EntityPose.STANDING);
    }

    @Override
    public void stop() {
        target = null;
        mob.getNavigation().stop();
        mob.setPose(EntityPose.STANDING);
        cooldownTicks = COOLDOWN_AFTER;
    }

    @Override
    public void tick() {
        phaseTicks++;

        switch (phase) {
            case PHASE_JUMP -> {
                //Look at player.
                lookAt(target);
                //Hops.
                if (phaseTicks % JUMP_EVERY == 0) {
                    mob.getJumpControl().setActive();
                }
                if (phaseTicks >= JUMP_PHASE_LEN) nextPhase(PHASE_PAUSE);
            }

            case PHASE_PAUSE -> {
                lookAt(target);
                mob.getNavigation().stop();
                if (phaseTicks >= PAUSE_LEN) {
                    //Crouch - Circle.
                    mob.setPose(EntityPose.CROUCHING);
                    nextPhase(PHASE_CROUCH_CIRCLE);
                }
            }

            case PHASE_CROUCH_CIRCLE -> {
                if (target == null) { nextPhase(PHASE_SPIN); break; }

                //Orbit point around player.
                angle += CIRCLE_SPEED;
                double tx = target.getX() + Math.cos(angle) * CIRCLE_RADIUS;
                double tz = target.getZ() + Math.sin(angle) * CIRCLE_RADIUS;
                double ty = target.getY(); //Roughly same Y axis.

                //Walk towards orbit point.
                mob.getNavigation().startMovingTo(tx, ty, tz, MOVE_SPEED);
                //Face player while circling.
                lookAt(target);

                if (phaseTicks >= CROUCH_LEN) {
                    mob.setPose(EntityPose.STANDING);
                    mob.getNavigation().stop();
                    nextPhase(PHASE_SPIN);
                }
            }

            case PHASE_SPIN -> {
                //Spin in place.
                float newYaw = normalizeYaw(mob.getYaw() + SPIN_DEG_PER_T);
                mob.setYaw(newYaw);
                mob.setHeadYaw(newYaw);
                mob.setBodyYaw(newYaw);
                mob.setPitch(-10f);
                if (phaseTicks >= SPIN_LEN) {
                    // done; shouldContinue() will stop us
                }
            }
        }
    }

    private void nextPhase(int next) {
        this.phase = next;
        this.phaseTicks = 0;
    }

    private void lookAt(Entity target) {

        mob.getLookControl().lookAt(
            target.getX(),
            target.getY() + target.getStandingEyeHeight(),
            target.getZ(),
            30f, 30f
        );
    }

    private static float normalizeYaw(float yaw) {
        while (yaw <= -180f) yaw += 360f;
        while (yaw > 180f) yaw -= 360f;
        return yaw;
    }
}

}