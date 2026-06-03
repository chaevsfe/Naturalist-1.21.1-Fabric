package com.starfish_studios.naturalist.client.renderer;

import com.starfish_studios.naturalist.client.model.TortoiseModel;
import com.starfish_studios.naturalist.client.renderer.layers.TortoiseSkinLayer;
import com.starfish_studios.naturalist.common.entity.Tortoise;
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
public class TortoiseRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<Tortoise, R> {
    public static final DataTicket<Boolean> IS_BABY = DataTicket.create("tortoise_is_baby", Boolean.class);

    public TortoiseRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TortoiseModel());
        this.shadowRadius = 0.8F;
        this.withRenderLayer(new TortoiseSkinLayer(this));
    }

    @Override
    public float getMotionAnimThreshold(Tortoise animatable) {
        return 0.000001f;
    }

    @Override
    public void extractRenderState(Tortoise entity, R renderState, float partialTick) {
        super.extractRenderState(entity, renderState, partialTick);
        renderState.addGeckolibData(TortoiseModel.TORTOISE_VARIANT, entity.getVariant());
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
            boneSnapshots.ifPresent("head", snapshot -> snapshot.setScale(1.4f, 1.4f, 1.4f));
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
