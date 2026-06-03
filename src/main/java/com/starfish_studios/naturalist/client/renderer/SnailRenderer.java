package com.starfish_studios.naturalist.client.renderer;

import com.starfish_studios.naturalist.Naturalist;
import com.starfish_studios.naturalist.client.model.SnailModel;
import com.starfish_studios.naturalist.common.entity.Snail;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.BoneSnapshots;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

@Environment(EnvType.CLIENT)
public class SnailRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<Snail, R> {
    public static final DataTicket<Boolean> IS_BABY = DataTicket.create("snail_is_baby", Boolean.class);
    public static final DataTicket<Boolean> SNAIL_SKIP_EYES = DataTicket.create("snail_skip_eyes", Boolean.class);
    private static final Identifier GARY_TEXTURE = Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/snail/gary.png");

    public SnailRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SnailModel());
        this.shadowRadius = 0.2F;
    }

    @Override
    public float getMotionAnimThreshold(Snail animatable) {
        return 0.000001f;
    }

    @Override
    public void extractRenderState(Snail entity, R renderState, float partialTick) {
        super.extractRenderState(entity, renderState, partialTick);
        Identifier texture;
        if (entity.getName().getString().contains("Gary")) {
            texture = GARY_TEXTURE;
        } else if (entity.getSnailColor() != null) {
            int color = entity.getSnailColor().getId();
            texture = Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/snail/" + DyeColor.byId(color).getName() + ".png");
        } else {
            texture = Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/snail/brown.png");
        }
        renderState.addGeckolibData(SnailModel.SNAIL_TEXTURE, texture);
        renderState.addGeckolibData(IS_BABY, entity.isBaby());
        renderState.addGeckolibData(SNAIL_SKIP_EYES, entity.isClimbing() && entity.canHide());
    }

    @Override
    public void scaleModelForRender(RenderPassInfo<R> renderPassInfo, float widthScale, float heightScale) {
        Boolean baby = renderPassInfo.renderState().getGeckolibData(IS_BABY);
        if (baby != null && baby) {
            super.scaleModelForRender(renderPassInfo, 0.5f, 0.5f);
        } else {
            super.scaleModelForRender(renderPassInfo, widthScale, heightScale);
        }
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<R> renderPassInfo, BoneSnapshots boneSnapshots) {
        super.adjustModelBonesForRender(renderPassInfo, boneSnapshots);
        Boolean baby = renderPassInfo.renderState().getGeckolibData(IS_BABY);
        if (baby != null && baby) {
            boneSnapshots.ifPresent("eyes", snapshot -> snapshot.setScale(1.5f, 1.5f, 1.5f));
        }

        // Eye tracking (skip when climbing and hiding)
        Boolean skipEyes = renderPassInfo.renderState().getGeckolibData(SNAIL_SKIP_EYES);
        if (skipEyes == null || !skipEyes) {
            float pitch = renderPassInfo.getOrDefaultGeckolibData(DataTickets.ENTITY_PITCH, 0f);
            float yaw = renderPassInfo.getOrDefaultGeckolibData(DataTickets.ENTITY_YAW, 0f);
            float netHeadYaw = yaw;
            boneSnapshots.ifPresent("left_eye", snapshot -> {
                snapshot.setRotX(-pitch * Mth.DEG_TO_RAD);
                snapshot.setRotY(-netHeadYaw * Mth.DEG_TO_RAD);
            });
            boneSnapshots.ifPresent("right_eye", snapshot -> {
                snapshot.setRotX(-pitch * Mth.DEG_TO_RAD);
                snapshot.setRotY(-netHeadYaw * Mth.DEG_TO_RAD);
            });
        }
    }
}
