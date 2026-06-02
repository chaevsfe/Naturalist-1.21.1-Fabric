package com.starfish_studios.naturalist.client.model;

import com.starfish_studios.naturalist.Naturalist;
import com.starfish_studios.naturalist.common.entity.Lion;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

@Environment(EnvType.CLIENT)
public class LionModel extends GeoModel<Lion> {
    public static final DataTicket<Identifier> LION_TEXTURE = DataTicket.create("lion_texture", Identifier.class);
    private static final Identifier DEFAULT_TEXTURE = Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/lion/lion.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "models/entity/lion.geo.json");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        Identifier texture = renderState.getGeckolibData(LION_TEXTURE);
        return texture != null ? texture : DEFAULT_TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(Lion entity) {
        return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "animations/lion.rp_anim.json");
    }
}
