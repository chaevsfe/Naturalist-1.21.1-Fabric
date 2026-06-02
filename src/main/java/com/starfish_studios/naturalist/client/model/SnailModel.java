package com.starfish_studios.naturalist.client.model;

import com.starfish_studios.naturalist.Naturalist;
import com.starfish_studios.naturalist.common.entity.Snail;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

@Environment(EnvType.CLIENT)
public class SnailModel extends GeoModel<Snail> {
    public static final DataTicket<Identifier> SNAIL_TEXTURE = DataTicket.create("snail_texture", Identifier.class);

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "models/entity/snail.geo.json");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        Identifier texture = renderState.getGeckolibData(SNAIL_TEXTURE);
        if (texture != null) {
            return texture;
        }
        return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/snail/brown.png");
    }

    @Override
    public Identifier getAnimationResource(Snail snail) {
        return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "animations/snail.rp_anim.json");
    }
}
