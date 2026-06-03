package com.starfish_studios.naturalist.client.renderer;

import com.starfish_studios.naturalist.client.model.DeerModel;
import com.starfish_studios.naturalist.common.entity.Deer;
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
public class DeerRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<Deer, R> {
    public static final DataTicket<Boolean> IS_BABY = DataTicket.create("deer_is_baby", Boolean.class);
    public static final DataTicket<Boolean> DEER_EATING = DataTicket.create("deer_eating", Boolean.class);

    public DeerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DeerModel());
        this.shadowRadius = 0.8F;
    }

    @Override
    public void extractRenderState(Deer entity, R renderState, float partialTick) {
        super.extractRenderState(entity, renderState, partialTick);
        renderState.addGeckolibData(DeerModel.IS_BABY, entity.isBaby());
        renderState.addGeckolibData(IS_BABY, entity.isBaby());
        renderState.addGeckolibData(DEER_EATING, entity.isEating());
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
        // Head tracking (skip when eating)
        Boolean eating = renderPassInfo.renderState().getGeckolibData(DEER_EATING);
        if (eating == null || !eating) {
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
