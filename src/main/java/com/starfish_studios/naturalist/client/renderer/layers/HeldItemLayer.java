package com.starfish_studios.naturalist.client.renderer.layers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.layer.builtin.ItemInHandGeoLayer;

/**
 * Wrapper around GeckoLib 5's ItemInHandGeoLayer for Naturalist entities.
 * Renders items held in the entity's main hand on a specified bone.
 */
@Environment(EnvType.CLIENT)
public class HeldItemLayer<T extends LivingEntity & GeoAnimatable, R extends LivingEntityRenderState & GeoRenderState> extends ItemInHandGeoLayer<T, Void, R> {

    /**
     * @param renderer The GeoRenderer this layer is attached to
     * @param boneName The bone name to render the held item on (used as right hand bone)
     */
    public HeldItemLayer(GeoRenderer<T, Void, R> renderer, String boneName) {
        super(renderer, boneName, boneName);
    }
}
