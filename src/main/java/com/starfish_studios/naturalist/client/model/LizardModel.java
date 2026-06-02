package com.starfish_studios.naturalist.client.model;

import com.starfish_studios.naturalist.Naturalist;
import com.starfish_studios.naturalist.common.entity.Lizard;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

@Environment(EnvType.CLIENT)
public class LizardModel extends GeoModel<Lizard> {
    public static final DataTicket<Integer> LIZARD_VARIANT = DataTicket.create("lizard_variant", Integer.class);

    public static final Identifier[] TEXTURE_LOCATIONS = new Identifier[]{
            Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/lizard/green.png"),
            Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/lizard/brown.png"),
            Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/lizard/beardie.png"),
            Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/lizard/leopard_gecko.png")
    };

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "models/entity/lizard.geo.json");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        int variant = renderState.getOrDefaultGeckolibData(LIZARD_VARIANT, 0);
        return TEXTURE_LOCATIONS[Math.min(variant, TEXTURE_LOCATIONS.length - 1)];
    }

    @Override
    public Identifier getAnimationResource(Lizard lizard) {
        return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "animations/lizard.rp_anim.json");
    }
}
