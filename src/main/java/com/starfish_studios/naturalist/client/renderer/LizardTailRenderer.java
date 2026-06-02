package com.starfish_studios.naturalist.client.renderer;

import com.starfish_studios.naturalist.client.model.LizardTailModel;
import com.starfish_studios.naturalist.common.entity.LizardTail;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

@Environment(EnvType.CLIENT)
public class LizardTailRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<LizardTail, R> {
    public LizardTailRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new LizardTailModel());
        this.shadowRadius = 0.4F;
    }

    @Override
    public float getMotionAnimThreshold(LizardTail animatable) {
        return 0.000001f;
    }

    @Override
    public void extractRenderState(LizardTail entity, R renderState, float partialTick) {
        super.extractRenderState(entity, renderState, partialTick);
        renderState.addGeckolibData(LizardTailModel.LIZARD_TAIL_VARIANT, entity.getVariant());
    }
}
