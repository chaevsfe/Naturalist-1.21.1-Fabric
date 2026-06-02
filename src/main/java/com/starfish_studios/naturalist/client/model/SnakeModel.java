package com.starfish_studios.naturalist.client.model;

import com.starfish_studios.naturalist.Naturalist;
import com.starfish_studios.naturalist.common.entity.Snake;
import com.starfish_studios.naturalist.core.registry.NaturalistEntityTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

@Environment(EnvType.CLIENT)
public class SnakeModel extends GeoModel<Snake> {
    public static final DataTicket<EntityType<?>> SNAKE_TYPE = DataTicket.create("snake_type", (Class<EntityType<?>>) (Class<?>) EntityType.class);

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "models/entity/snake.geo.json");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        EntityType<?> type = renderState.getGeckolibData(SNAKE_TYPE);
        if (type != null) {
            if (type.equals(NaturalistEntityTypes.CORAL_SNAKE.get())) {
                return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/snake/coral_snake.png");
            } else if (type.equals(NaturalistEntityTypes.RATTLESNAKE.get())) {
                return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/snake/rattlesnake.png");
            }
        }
        return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/snake/snake.png");
    }

    @Override
    public Identifier getAnimationResource(Snake snake) {
        return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "animations/snake.rp_anim.json");
    }
}
