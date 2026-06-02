package com.starfish_studios.naturalist.client.renderer;

import com.starfish_studios.naturalist.client.model.DragonflyModel;
import com.starfish_studios.naturalist.common.entity.Dragonfly;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

@Environment(EnvType.CLIENT)
public class DragonflyRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<Dragonfly, R> {
    public DragonflyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DragonflyModel());
        this.shadowRadius = 0.4F;
    }

    @Override
    public float getMotionAnimThreshold(Dragonfly animatable) {
        return 0.000001f;
    }

    @Override
    public void extractRenderState(Dragonfly entity, R renderState, float partialTick) {
        super.extractRenderState(entity, renderState, partialTick);
        renderState.addGeckolibData(DragonflyModel.DRAGONFLY_VARIANT, entity.getVariant());
    }
}
