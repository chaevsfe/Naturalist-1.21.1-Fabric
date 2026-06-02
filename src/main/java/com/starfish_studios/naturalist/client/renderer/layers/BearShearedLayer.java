package com.starfish_studios.naturalist.client.renderer.layers;

import com.starfish_studios.naturalist.Naturalist;
import com.starfish_studios.naturalist.common.entity.Bear;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.base.RenderPassInfo;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

@Environment(EnvType.CLIENT)
public class BearShearedLayer<R extends LivingEntityRenderState & GeoRenderState> extends GeoRenderLayer<Bear, Void, R> {
    private static final Identifier LAYER = Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/bear/bear_sheared.png");
    public static final DataTicket<Boolean> IS_SHEARED = DataTicket.create("bear_is_sheared", Boolean.class);

    public BearShearedLayer(GeoRenderer<Bear, Void, R> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void addRenderData(Bear animatable, Void relatedObject, R renderState, float partialTick) {
        renderState.addGeckolibData(IS_SHEARED, animatable.isSheared());
    }

    @Override
    public void submitRenderTask(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        if (!renderPassInfo.willRender())
            return;

        Boolean sheared = renderPassInfo.renderState().getOrDefaultGeckolibData(IS_SHEARED, false);
        if (sheared) {
            RenderType renderType = RenderTypes.entityCutoutNoCull(LAYER);
            this.renderer.submitRenderTasks(renderPassInfo, renderTasks.order(1), renderType);
        }
    }
}
