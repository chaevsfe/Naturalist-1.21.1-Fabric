package com.starfish_studios.naturalist.client.model;

import com.starfish_studios.naturalist.Naturalist;
import com.starfish_studios.naturalist.common.entity.Dragonfly;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

@Environment(EnvType.CLIENT)
public class DragonflyModel extends GeoModel<Dragonfly> {
    public static final DataTicket<Integer> DRAGONFLY_VARIANT = DataTicket.create("dragonfly_variant", Integer.class);

    public static final Identifier[] TEXTURE_LOCATIONS = new Identifier[]{
            Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/dragonfly/blue.png"),
            Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/dragonfly/green.png"),
            Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/dragonfly/red.png")
    };

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "models/entity/dragonfly.geo.json");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        int variant = renderState.getOrDefaultGeckolibData(DRAGONFLY_VARIANT, 0);
        return TEXTURE_LOCATIONS[Math.min(variant, TEXTURE_LOCATIONS.length - 1)];
    }

    @Override
    public Identifier getAnimationResource(Dragonfly dragonfly) {
        return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "animations/dragonfly.rp_anim.json");
    }
}
