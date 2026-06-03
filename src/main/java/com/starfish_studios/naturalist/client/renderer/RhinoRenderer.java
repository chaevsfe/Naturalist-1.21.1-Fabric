package com.starfish_studios.naturalist.client.renderer;

import com.starfish_studios.naturalist.client.model.RhinoModel;
import com.starfish_studios.naturalist.common.entity.Rhino;
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
public class RhinoRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<Rhino, R> {
    public static final DataTicket<Boolean> IS_BABY = DataTicket.create("rhino_is_baby", Boolean.class);
    public static final DataTicket<Boolean> RHINO_SPRINTING = DataTicket.create("rhino_sprinting", Boolean.class);

    public RhinoRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new RhinoModel());
        this.shadowRadius = 1.1F;
    }

    @Override
    public float getMotionAnimThreshold(Rhino animatable) {
        return 0.000001f;
    }

    @Override
    public void extractRenderState(Rhino entity, R renderState, float partialTick) {
        super.extractRenderState(entity, renderState, partialTick);
        renderState.addGeckolibData(IS_BABY, entity.isBaby());
        renderState.addGeckolibData(RHINO_SPRINTING, entity.isSprinting());
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
            boneSnapshots.ifPresent("left_ear", snapshot -> snapshot.setScale(1.1f, 1.1f, 1.1f));
            boneSnapshots.ifPresent("right_ear", snapshot -> snapshot.setScale(1.1f, 1.1f, 1.1f));
            boneSnapshots.ifPresent("big_horn", snapshot -> snapshot.skipRender(true));
            boneSnapshots.ifPresent("small_horn", snapshot -> snapshot.skipRender(true));
        } else {
            boneSnapshots.ifPresent("baby_horn", snapshot -> snapshot.skipRender(true));
        }

        // Head tracking - yaw only, skip when sprinting
        Boolean sprinting = renderPassInfo.renderState().getGeckolibData(RHINO_SPRINTING);
        if (sprinting == null || !sprinting) {
            float yaw = renderPassInfo.getOrDefaultGeckolibData(DataTickets.ENTITY_YAW, 0f);
            float netHeadYaw = yaw;
            boneSnapshots.ifPresent("head", snapshot -> snapshot.setRotY(-netHeadYaw * Mth.DEG_TO_RAD));
        }
    }
}
