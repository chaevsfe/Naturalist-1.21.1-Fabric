package com.starfish_studios.naturalist.client.model;

import com.starfish_studios.naturalist.Naturalist;
import com.starfish_studios.naturalist.common.entity.Tortoise;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

@Environment(EnvType.CLIENT)
public class TortoiseModel extends GeoModel<Tortoise> {
    public static final DataTicket<Integer> TORTOISE_VARIANT = DataTicket.create("tortoise_variant", Integer.class);

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "models/entity/tortoise.geo.json");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        int variant = renderState.getOrDefaultGeckolibData(TORTOISE_VARIANT, 0);
        return switch (variant) {
            case 1 -> Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/tortoise/green.png");
            case 2 -> Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/tortoise/black.png");
            default -> Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/tortoise/brown.png");
        };
    }

    @Override
    public Identifier getAnimationResource(Tortoise tortoise) {
        return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "animations/tortoise.rp_anim.json");
    }
}
