package com.starfish_studios.naturalist.core.platform;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ClientPlatformHelper {
    // BlockRenderLayerMap was removed from Fabric API.
    // Block render layers are now set through block model JSON or block properties.
    // The setRenderLayer method is no longer needed.

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T extends Entity> void registerEntityRenderers(@NotNull Supplier<EntityType<T>> type, EntityRendererProvider renderProvider) {
        EntityRendererRegistry.register(type.get(), renderProvider);
    }
}
