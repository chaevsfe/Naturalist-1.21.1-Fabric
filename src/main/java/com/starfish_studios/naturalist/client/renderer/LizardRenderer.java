package com.starfish_studios.naturalist.client.renderer;

import com.starfish_studios.naturalist.client.model.LizardModel;
import com.starfish_studios.naturalist.common.entity.Lizard;
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
public class LizardRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<Lizard, R> {
    public static final DataTicket<Boolean> LIZARD_HAS_TAIL = DataTicket.create("lizard_has_tail", Boolean.class);

    public LizardRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new LizardModel());
        this.shadowRadius = 0.4F;
    }

    @Override
    public float getMotionAnimThreshold(Lizard animatable) {
        return 0.000001f;
    }

    @Override
    public void extractRenderState(Lizard entity, R renderState, float partialTick) {
        super.extractRenderState(entity, renderState, partialTick);
        renderState.addGeckolibData(LizardModel.LIZARD_VARIANT, entity.getVariant());
        renderState.addGeckolibData(LIZARD_HAS_TAIL, entity.hasTail());
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<R> renderPassInfo, BoneSnapshots boneSnapshots) {
        super.adjustModelBonesForRender(renderPassInfo, boneSnapshots);

        // Hide tail if detached
        Boolean hasTail = renderPassInfo.renderState().getGeckolibData(LIZARD_HAS_TAIL);
        if (hasTail != null && !hasTail) {
            boneSnapshots.ifPresent("tail", snapshot -> snapshot.skipRender(true));
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
