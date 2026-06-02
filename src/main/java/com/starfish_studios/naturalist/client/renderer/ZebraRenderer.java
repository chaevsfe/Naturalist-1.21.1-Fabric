package com.starfish_studios.naturalist.client.renderer;

import com.starfish_studios.naturalist.Naturalist;
import com.starfish_studios.naturalist.client.model.ZebraModel;
import com.starfish_studios.naturalist.common.entity.Zebra;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EquineRenderState;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class ZebraRenderer extends AbstractHorseRenderer<Zebra, EquineRenderState, ZebraModel> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "zebra"), "main");
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/zebra.png");

    public ZebraRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context, new ZebraModel(context.bakeLayer(LAYER_LOCATION)), new ZebraModel(context.bakeLayer(LAYER_LOCATION)));
    }

    @Override
    public @NotNull EquineRenderState createRenderState() {
        return new EquineRenderState();
    }

    @Override
    public @NotNull Identifier getTextureLocation(@NotNull EquineRenderState renderState) {
        return TEXTURE;
}
}
