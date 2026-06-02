package com.starfish_studios.naturalist.client.model;

import com.starfish_studios.naturalist.Naturalist;
import com.starfish_studios.naturalist.common.entity.Bird;
import com.starfish_studios.naturalist.core.registry.NaturalistEntityTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

@Environment(EnvType.CLIENT)
public class BirdModel extends GeoModel<Bird> {

    public static final DataTicket<EntityType<?>> BIRD_TYPE = DataTicket.create("bird_type", (Class<EntityType<?>>) (Class<?>) EntityType.class);

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        EntityType<?> type = renderState.getGeckolibData(BIRD_TYPE);
        if (type != null) {
            if (type.equals(NaturalistEntityTypes.BLUEJAY.get())) {
                return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/bird/bluejay.png");
            } else if (type.equals(NaturalistEntityTypes.CANARY.get())) {
                return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/bird/canary.png");
            } else if (type.equals(NaturalistEntityTypes.CARDINAL.get())) {
                return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/bird/cardinal.png");
            } else if (type.equals(NaturalistEntityTypes.FINCH.get())) {
                return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/bird/finch.png");
            } else if (type.equals(NaturalistEntityTypes.SPARROW.get())) {
                return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/bird/sparrow.png");
            }
        }
        return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/bird/robin.png");
    }

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "models/entity/bird.geo.json");
    }

    @Override
    public Identifier getAnimationResource(Bird bird) {
        return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "animations/bird.rp_anim.json");
    }
}
