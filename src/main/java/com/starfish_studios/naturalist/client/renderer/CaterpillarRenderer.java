package com.starfish_studios.naturalist.client.renderer;

import com.starfish_studios.naturalist.client.model.CaterpillarModel;
import com.starfish_studios.naturalist.common.entity.Alligator;
import com.starfish_studios.naturalist.common.entity.Caterpillar;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderState;

@Environment(EnvType.CLIENT)
public class CaterpillarRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<Caterpillar, R> {
    public CaterpillarRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CaterpillarModel());
        this.shadowRadius = 0.3F;
    }

    @Override
    public float getMotionAnimThreshold(Caterpillar animatable) {
        return 0.000001f;
}
}
