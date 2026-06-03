package com.starfish_studios.naturalist.client.renderer;

import com.starfish_studios.naturalist.Naturalist;
import com.starfish_studios.naturalist.client.model.SnakeModel;
import com.starfish_studios.naturalist.client.renderer.layers.SleepLayer;
import com.starfish_studios.naturalist.common.entity.Snake;
import com.starfish_studios.naturalist.core.registry.NaturalistEntityTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.BoneSnapshots;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

@Environment(EnvType.CLIENT)
public class SnakeRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<Snake, R> {
    public static final DataTicket<Boolean> SNAKE_SLEEPING = DataTicket.create("snake_sleeping", Boolean.class);
    public static final DataTicket<Boolean> SNAKE_HOLDING_ITEM = DataTicket.create("snake_holding_item", Boolean.class);
    public static final DataTicket<Boolean> SNAKE_IS_RATTLESNAKE = DataTicket.create("snake_is_rattlesnake", Boolean.class);

    public SnakeRenderer(EntityRendererProvider.@NotNull Context renderManager) {
        super(renderManager, new SnakeModel());
        this.shadowRadius = 0.4F;
        this.withRenderLayer(new SleepLayer<>(this, Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "models/entity/snake.geo.json"), Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/snake/snake_sleep.png")));
    }

    @Override
    public float getMotionAnimThreshold(Snake animatable) {
        return 0.000001f;
    }

    @Override
    public void extractRenderState(Snake entity, R renderState, float partialTick) {
        super.extractRenderState(entity, renderState, partialTick);
        renderState.addGeckolibData(SnakeModel.SNAKE_TYPE, entity.getType());
        renderState.addGeckolibData(SNAKE_SLEEPING, entity.isSleeping());
        renderState.addGeckolibData(SNAKE_HOLDING_ITEM, !entity.getMainHandItem().isEmpty());
        renderState.addGeckolibData(SNAKE_IS_RATTLESNAKE, entity.getType().equals(NaturalistEntityTypes.RATTLESNAKE.get()));
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<R> renderPassInfo, BoneSnapshots boneSnapshots) {
        super.adjustModelBonesForRender(renderPassInfo, boneSnapshots);

        // Head tracking (skip when sleeping)
        Boolean sleeping = renderPassInfo.renderState().getGeckolibData(SNAKE_SLEEPING);
        if (sleeping == null || !sleeping) {
            float pitch = renderPassInfo.getOrDefaultGeckolibData(DataTickets.ENTITY_PITCH, 0f);
            float yaw = renderPassInfo.getOrDefaultGeckolibData(DataTickets.ENTITY_YAW, 0f);
            float netHeadYaw = yaw;
            boneSnapshots.ifPresent("head", snapshot -> {
                snapshot.setRotX(-pitch * Mth.DEG_TO_RAD);
                snapshot.setRotY(-netHeadYaw * Mth.DEG_TO_RAD);
            });
        }

        // Scale tail2 when holding an item (swallowed prey)
        Boolean holdingItem = renderPassInfo.renderState().getGeckolibData(SNAKE_HOLDING_ITEM);
        if (holdingItem != null && holdingItem) {
            boneSnapshots.ifPresent("tail2", snapshot -> snapshot.setScale(1.5f, 1.5f, 1f));
        }

        // Hide tail4 (rattle) for non-rattlesnakes
        Boolean isRattlesnake = renderPassInfo.renderState().getGeckolibData(SNAKE_IS_RATTLESNAKE);
        if (isRattlesnake == null || !isRattlesnake) {
            boneSnapshots.ifPresent("tail4", snapshot -> snapshot.skipRender(true));
        }
    }
}
