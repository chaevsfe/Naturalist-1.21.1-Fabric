package com.starfish_studios.naturalist.client.renderer;

import com.starfish_studios.naturalist.client.model.BassModel;
import com.starfish_studios.naturalist.common.entity.Alligator;
import com.starfish_studios.naturalist.common.entity.Bass;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderState;

@Environment(EnvType.CLIENT)
public class BassRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<Bass, R> {
    public BassRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BassModel());
        this.shadowRadius = 0.4F;
    }

    @Override
    public float getMotionAnimThreshold(Bass animatable) {
        return 0.000001f;
}
}
