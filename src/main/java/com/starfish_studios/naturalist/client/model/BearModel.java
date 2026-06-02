package com.starfish_studios.naturalist.client.model;

import com.starfish_studios.naturalist.Naturalist;
import com.starfish_studios.naturalist.common.entity.Bear;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

@Environment(EnvType.CLIENT)
public class BearModel extends GeoModel<Bear> {
    public static final DataTicket<Identifier> BEAR_TEXTURE = DataTicket.create("bear_texture", Identifier.class);
    private static final Identifier DEFAULT_TEXTURE = Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/bear/bear.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "models/entity/bear.geo.json");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        Identifier texture = renderState.getGeckolibData(BEAR_TEXTURE);
        return texture != null ? texture : DEFAULT_TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(Bear bear) {
        return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "animations/bear.rp_anim.json");
    }
}
