package com.starfish_studios.naturalist.client.renderer;

import com.starfish_studios.naturalist.client.model.ButterflyModel;
import com.starfish_studios.naturalist.common.entity.Butterfly;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

@Environment(EnvType.CLIENT)
public class ButterflyRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<Butterfly, R> {
    public ButterflyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ButterflyModel());
        this.shadowRadius = 0.4F;
    }

    @Override
    public void extractRenderState(Butterfly entity, R renderState, float partialTick) {
        super.extractRenderState(entity, renderState, partialTick);
        renderState.addGeckolibData(ButterflyModel.BUTTERFLY_VARIANT_NAME, entity.getVariant().getName());
    }
}
