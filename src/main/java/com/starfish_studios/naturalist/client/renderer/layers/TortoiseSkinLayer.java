package com.starfish_studios.naturalist.client.renderer.layers;

import com.starfish_studios.naturalist.Naturalist;
import com.starfish_studios.naturalist.common.entity.Tortoise;
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

public class TortoiseSkinLayer<R extends LivingEntityRenderState & GeoRenderState> extends GeoRenderLayer<Tortoise, Void, R> {
    private static final Identifier DONATELLO = Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/tortoise/donatello.png");
    private static final Identifier LEONARDO = Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/tortoise/leonardo.png");
    private static final Identifier MICHELANGELO = Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/tortoise/michelangelo.png");
    private static final Identifier RAPHAEL = Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/tortoise/raphael.png");

    public static final DataTicket<String> TORTOISE_NAME = DataTicket.create("tortoise_name", String.class);

    public TortoiseSkinLayer(GeoRenderer<Tortoise, Void, R> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void addRenderData(Tortoise animatable, Void relatedObject, R renderState, float partialTick) {
        renderState.addGeckolibData(TORTOISE_NAME, animatable.getName().getString());
    }

    @Override
    public void submitRenderTask(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        if (!renderPassInfo.willRender())
            return;

        String name = renderPassInfo.renderState().getOrDefaultGeckolibData(TORTOISE_NAME, "");
        Identifier skin = switch (name) {
            case "Donatello" -> DONATELLO;
            case "Leonardo" -> LEONARDO;
            case "Michelangelo" -> MICHELANGELO;
            case "Raphael" -> RAPHAEL;
            default -> null;
        };
        if (skin != null) {
            RenderType renderType = RenderTypes.entityCutoutNoCull(skin);
            this.renderer.submitRenderTasks(renderPassInfo, renderTasks.order(1), renderType);
        }
    }
}
