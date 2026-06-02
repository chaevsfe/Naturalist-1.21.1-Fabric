package com.starfish_studios.naturalist.client.model;

import com.starfish_studios.naturalist.Naturalist;
import com.starfish_studios.naturalist.common.entity.Butterfly;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

@Environment(EnvType.CLIENT)
public class ButterflyModel extends GeoModel<Butterfly> {
    public static final DataTicket<String> BUTTERFLY_VARIANT_NAME = DataTicket.create("butterfly_variant_name", String.class);

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "models/entity/butterfly.geo.json");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        String variantName = renderState.getOrDefaultGeckolibData(BUTTERFLY_VARIANT_NAME, "monarch");
        return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/butterfly/" + variantName + ".png");
    }

    @Override
    public Identifier getAnimationResource(Butterfly butterfly) {
        return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "animations/butterfly.rp_anim.json");
    }
}
