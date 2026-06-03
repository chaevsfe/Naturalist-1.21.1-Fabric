package com.starfish_studios.naturalist.client.renderer;

import com.starfish_studios.naturalist.client.model.ElephantModel;
import com.starfish_studios.naturalist.common.entity.Elephant;
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
public class ElephantRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<Elephant, R> {
    public static final DataTicket<Boolean> IS_BABY = DataTicket.create("elephant_is_baby", Boolean.class);

    public ElephantRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ElephantModel());
        this.shadowRadius = 1.5F;
    }

    @Override
    public float getMotionAnimThreshold(Elephant animatable) {
        return 0.000001f;
    }

    @Override
    public void extractRenderState(Elephant entity, R renderState, float partialTick) {
        super.extractRenderState(entity, renderState, partialTick);
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
            boneSnapshots.ifPresent("head", snapshot -> snapshot.setScale(1.3f, 1.3f, 1.3f));
            boneSnapshots.ifPresent("left_ear", snapshot -> snapshot.setScale(1.2f, 1.2f, 1.2f));
            boneSnapshots.ifPresent("right_ear", snapshot -> snapshot.setScale(1.2f, 1.2f, 1.2f));
            boneSnapshots.ifPresent("tusks", snapshot -> snapshot.setScale(0, 0, 0));
            boneSnapshots.ifPresent("trunk4", snapshot -> snapshot.setScale(0, 0, 0));
        } else {
            boneSnapshots.ifPresent("baby_tusks", snapshot -> snapshot.setScale(0, 0, 0));
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
