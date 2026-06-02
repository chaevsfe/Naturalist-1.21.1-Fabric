package com.starfish_studios.naturalist.client.model;

import com.starfish_studios.naturalist.Naturalist;
import com.starfish_studios.naturalist.common.entity.Duck;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

@Environment(EnvType.CLIENT)
public class DuckModel extends GeoModel<Duck> {
    public static final DataTicket<String> DUCK_NAME = DataTicket.create("duck_name", String.class);

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "models/entity/duck.geo.json");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        String name = renderState.getOrDefaultGeckolibData(DUCK_NAME, "");
        if (name.equalsIgnoreCase("Queso")) {
            return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/duck/queso.png");
        } else if (name.equalsIgnoreCase("Donald")) {
            return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/duck/donald.png");
        }
        return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/duck/duck.png");
    }

    @Override
    public Identifier getAnimationResource(Duck animal) {
        return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "animations/duck.rp_anim.json");
    }
}
