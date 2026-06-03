package com.starfish_studios.naturalist.client.renderer;

import com.starfish_studios.naturalist.client.model.DuckModel;
import com.starfish_studios.naturalist.common.entity.Duck;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.BoneSnapshots;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

@Environment(EnvType.CLIENT)
public class DuckRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<Duck, R> {
    public static final DataTicket<Boolean> IS_BABY = DataTicket.create("duck_is_baby", Boolean.class);

    public DuckRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DuckModel());
        this.shadowRadius = 0.3F;
    }

    @Override
    public float getMotionAnimThreshold(Duck animatable) {
        return 0.000001f;
    }

    @Override
    public void extractRenderState(Duck entity, R renderState, float partialTick) {
        super.extractRenderState(entity, renderState, partialTick);
        String name = entity.hasCustomName() && entity.getCustomName() != null ? entity.getCustomName().getString() : "";
        renderState.addGeckolibData(DuckModel.DUCK_NAME, name);
        renderState.addGeckolibData(IS_BABY, entity.isBaby());
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
            boneSnapshots.ifPresent("head", snapshot -> snapshot.setScale(1.7f, 1.7f, 1.7f));
        }

        // Head tracking
        float pitch = renderPassInfo.getOrDefaultGeckolibData(DataTickets.ENTITY_PITCH, 0f);
        float yaw = renderPassInfo.getOrDefaultGeckolibData(DataTickets.ENTITY_YAW, 0f);
        float netHeadYaw = yaw;
        boneSnapshots.ifPresent("head", snapshot -> {
            snapshot.setRotX(-pitch * Mth.DEG_TO_RAD);
            snapshot.setRotY(-netHeadYaw * Mth.DEG_TO_RAD);
        });
    }
}
