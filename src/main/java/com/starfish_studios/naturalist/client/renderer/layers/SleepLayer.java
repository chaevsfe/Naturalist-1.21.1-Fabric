package com.starfish_studios.naturalist.client.renderer.layers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.base.RenderPassInfo;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

@Environment(EnvType.CLIENT)
public class SleepLayer<T extends LivingEntity & GeoAnimatable, R extends LivingEntityRenderState & GeoRenderState> extends GeoRenderLayer<T, Void, R> {
    private final Identifier model;
    private final Identifier sleepLayer;

    public static final DataTicket<Boolean> IS_SLEEPING = DataTicket.create("entity_is_sleeping", Boolean.class);

    public SleepLayer(GeoRenderer<T, Void, R> entityRendererIn, Identifier model, Identifier sleepLayer) {
        super(entityRendererIn);
        this.model = model;
        this.sleepLayer = sleepLayer;
    }

    @Override
    public void addRenderData(T animatable, Void relatedObject, R renderState, float partialTick) {
        renderState.addGeckolibData(IS_SLEEPING, animatable.isSleeping());
    }

    @Override
    public void submitRenderTask(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        if (!renderPassInfo.willRender())
            return;

        Boolean sleeping = renderPassInfo.renderState().getOrDefaultGeckolibData(IS_SLEEPING, false);
        if (sleeping) {
            RenderType renderType = RenderTypes.entityCutoutNoCull(sleepLayer);
            this.renderer.submitRenderTasks(renderPassInfo, renderTasks.order(1), renderType);
        }
    }
}
