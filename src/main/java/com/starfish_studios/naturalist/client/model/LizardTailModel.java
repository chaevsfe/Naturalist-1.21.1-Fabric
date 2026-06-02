package com.starfish_studios.naturalist.client.model;

import com.starfish_studios.naturalist.Naturalist;
import com.starfish_studios.naturalist.common.entity.LizardTail;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

@Environment(EnvType.CLIENT)
public class LizardTailModel extends GeoModel<LizardTail> {
    public static final DataTicket<Integer> LIZARD_TAIL_VARIANT = DataTicket.create("lizard_tail_variant", Integer.class);

    public static final Identifier[] TEXTURE_LOCATIONS = new Identifier[]{
            Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/lizard/green_tail.png"),
            Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/lizard/brown_tail.png"),
            Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/lizard/beardie_tail.png"),
            Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/lizard/leopard_gecko_tail.png"),
    };

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "models/entity/lizard_tail.geo.json");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        int variant = renderState.getOrDefaultGeckolibData(LIZARD_TAIL_VARIANT, 0);
        return TEXTURE_LOCATIONS[Math.min(variant, TEXTURE_LOCATIONS.length - 1)];
    }

    @Override
    public Identifier getAnimationResource(LizardTail lizard) {
        return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "animations/lizard_tail.rp_anim.json");
    }
}
