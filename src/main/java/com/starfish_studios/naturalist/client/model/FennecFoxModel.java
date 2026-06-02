package com.starfish_studios.naturalist.client.model;

import com.starfish_studios.naturalist.Naturalist;
import com.starfish_studios.naturalist.common.entity.FennecFox;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

@Environment(EnvType.CLIENT)
public class FennecFoxModel extends GeoModel<FennecFox> {
    public static final DataTicket<Integer> FENNEC_VARIANT = DataTicket.create("fennec_variant", Integer.class);

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "models/entity/fennec_fox.geo.json");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        int variant = renderState.getOrDefaultGeckolibData(FENNEC_VARIANT, 0);
        String variantName = FennecFox.VARIANT_NAMES[Math.min(variant, FennecFox.VARIANT_NAMES.length - 1)];
        return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/fennec_fox/" + variantName + ".png");
    }

    @Override
    public Identifier getAnimationResource(FennecFox entity) {
        return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "animations/fennec_fox.rp_anim.json");
    }
}
