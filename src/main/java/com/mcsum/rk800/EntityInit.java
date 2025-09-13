/* Package. */
package com.mcsum.rk800;

/* Imports. */
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class EntityInit {
    /* EntityType definition. */
    public static final EntityType<RK800Entity> RK800 = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier.of("mcsum", "rk800"),
        FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, RK800Entity::new)
            .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
            .build(
                RegistryKey.of(Registries.ENTITY_TYPE.getKey(), Identifier.of("mcsum", "rk800"))
            )
    );

    /* Attributes. */
    public static void register() {
        FabricDefaultAttributeRegistry.register(RK800, RK800Entity.createAttributes());
    }
}