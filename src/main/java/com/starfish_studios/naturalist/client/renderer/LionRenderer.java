package com.starfish_studios.naturalist.client.renderer;

import com.starfish_studios.naturalist.Naturalist;
import com.starfish_studios.naturalist.client.model.LionModel;
import com.starfish_studios.naturalist.common.entity.Lion;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.BoneSnapshots;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

@Environment(EnvType.CLIENT)
public class LionRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<Lion, R> {
    public static final DataTicket<Boolean> IS_BABY = DataTicket.create("lion_is_baby", Boolean.class);
    public static final DataTicket<Boolean> LION_HAS_MANE = DataTicket.create("lion_has_mane", Boolean.class);
    public static final DataTicket<Boolean> LION_SLEEPING = DataTicket.create("lion_sleeping", Boolean.class);
    private static final Identifier LION_TEXTURE = Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/lion/lion.png");
    private static final Identifier LION_ANGRY_TEXTURE = Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/lion/lion_angry.png");
    private static final Identifier LION_SLEEP_TEXTURE = Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/lion/lion_sleep.png");
    private static final Identifier LIONESS_TEXTURE = Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/lion/lioness.png");
    private static final Identifier LIONESS_ANGRY_TEXTURE = Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/lion/lioness_angry.png");
    private static final Identifier LIONESS_SLEEP_TEXTURE = Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/lion/lioness_sleep.png");

    public LionRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new LionModel());
        this.shadowRadius = 1.1F;
    }

    @Override
    public float getMotionAnimThreshold(Lion animatable) {
        return 0.000001f;
    }

    @Override
    public void extractRenderState(Lion entity, R renderState, float partialTick) {
        super.extractRenderState(entity, renderState, partialTick);
        Identifier texture;
        if (entity.isSleeping() && entity.hasMane() && !entity.isBaby()) {
            texture = LION_SLEEP_TEXTURE;
        } else if ((!entity.hasMane() && entity.isSleeping()) || (entity.isBaby() && entity.isSleeping())) {
            texture = LIONESS_SLEEP_TEXTURE;
        } else if ((!entity.hasMane() && !entity.isAggressive()) || entity.isBaby()) {
            texture = LIONESS_TEXTURE;
        } else if (entity.isAggressive() && !entity.isBaby() && entity.hasMane()) {
            texture = LION_ANGRY_TEXTURE;
        } else if ((!entity.hasMane() && entity.isAggressive()) || (entity.isBaby() && entity.isAggressive())) {
            texture = LIONESS_ANGRY_TEXTURE;
        } else {
            texture = LION_TEXTURE;
        }
        renderState.addGeckolibData(LionModel.LION_TEXTURE, texture);
        renderState.addGeckolibData(IS_BABY, entity.isBaby());
        renderState.addGeckolibData(LION_HAS_MANE, entity.hasMane());
        renderState.addGeckolibData(LION_SLEEPING, entity.isSleeping());
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
            boneSnapshots.ifPresent("head", snapshot -> snapshot.setScale(1.4f, 1.4f, 1.4f));
        }

        // Hide mane for lionesses and babies
        Boolean hasMane = renderPassInfo.renderState().getGeckolibData(LION_HAS_MANE);
        if (hasMane == null || !hasMane || (baby != null && baby)) {
            boneSnapshots.ifPresent("mane", snapshot -> snapshot.skipRender(true));
        }

        // Head tracking (skip when sleeping)
        Boolean sleeping = renderPassInfo.renderState().getGeckolibData(LION_SLEEPING);
        if (sleeping == null || !sleeping) {
            float pitch = renderPassInfo.getOrDefaultGeckolibData(DataTickets.ENTITY_PITCH, 0f);
            float yaw = renderPassInfo.getOrDefaultGeckolibData(DataTickets.ENTITY_YAW, 0f);
            float netHeadYaw = yaw;
            boneSnapshots.ifPresent("head", snapshot -> {
                snapshot.setRotX(-pitch * Mth.DEG_TO_RAD);
                snapshot.setRotY(-netHeadYaw * Mth.DEG_TO_RAD);
            });
        }
    }
}
