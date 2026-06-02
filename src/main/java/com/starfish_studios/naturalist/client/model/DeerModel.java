package com.starfish_studios.naturalist.client.model;

import com.starfish_studios.naturalist.Naturalist;
import com.starfish_studios.naturalist.common.entity.Deer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

@Environment(EnvType.CLIENT)
public class DeerModel extends GeoModel<Deer> {
    public static final DataTicket<Boolean> IS_BABY = DataTicket.create("deer_is_baby", Boolean.class);

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        if (Boolean.TRUE.equals(renderState.getOrDefaultGeckolibData(IS_BABY, false))) {
            return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "models/entity/fawn.geo.json");
        }
        return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "models/entity/deer.geo.json");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        if (Boolean.TRUE.equals(renderState.getOrDefaultGeckolibData(IS_BABY, false))) {
            return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/fawn.png");
        }
        return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/deer.png");
    }

    @Override
    public Identifier getAnimationResource(Deer deer) {
        return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "animations/deer.rp_anim.json");
    }
}
