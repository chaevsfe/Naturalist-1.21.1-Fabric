package com.starfish_studios.naturalist.client.renderer.layers;

import com.starfish_studios.naturalist.Naturalist;
import com.starfish_studios.naturalist.common.entity.Firefly;
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
public class FireflyGlowLayer<R extends LivingEntityRenderState & GeoRenderState> extends GeoRenderLayer<Firefly, Void, R> {
    private static final Identifier GLOW = Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/firefly/glow.png");
    public static final DataTicket<Boolean> IS_GLOWING = DataTicket.create("firefly_is_glowing", Boolean.class);

    public FireflyGlowLayer(GeoRenderer<Firefly, Void, R> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void addRenderData(Firefly animatable, Void relatedObject, R renderState, float partialTick) {
        renderState.addGeckolibData(IS_GLOWING, animatable.isGlowing());
    }

    @Override
    public void submitRenderTask(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        if (!renderPassInfo.willRender())
            return;

        Boolean glowing = renderPassInfo.renderState().getOrDefaultGeckolibData(IS_GLOWING, false);
        RenderType renderType = glowing ? RenderTypes.eyes(GLOW) : RenderTypes.entityCutoutNoCull(GLOW);

        this.renderer.submitRenderTasks(renderPassInfo, renderTasks.order(1), renderType);
    }
}
