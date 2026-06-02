package com.starfish_studios.naturalist.client.renderer;

import com.starfish_studios.naturalist.Naturalist;
import com.starfish_studios.naturalist.client.model.BearModel;
import com.starfish_studios.naturalist.client.renderer.layers.BearShearedLayer;
import com.starfish_studios.naturalist.client.renderer.layers.HeldItemLayer;
import com.starfish_studios.naturalist.common.entity.Bear;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Items;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.BoneSnapshots;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

@Environment(EnvType.CLIENT)
public class BearRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<Bear, R> {
    public static final DataTicket<Boolean> IS_BABY = DataTicket.create("bear_is_baby", Boolean.class);
    public static final DataTicket<Boolean> BEAR_SKIP_HEAD = DataTicket.create("bear_skip_head", Boolean.class);
    private static final Identifier BEAR_TEXTURE = Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/bear/bear.png");
    private static final Identifier BEAR_ANGRY_TEXTURE = Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/bear/bear_angry.png");
    private static final Identifier BEAR_SLEEP_TEXTURE = Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/bear/bear_sleep.png");
    private static final Identifier BEAR_BERRIES_TEXTURE = Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/bear/bear_berries.png");
    private static final Identifier BEAR_HONEY_TEXTURE = Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, "textures/entity/bear/bear_honey.png");

    public BearRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BearModel());
        this.shadowRadius = 0.9F;
        this.withRenderLayer(new BearShearedLayer(this));
        this.withRenderLayer(new HeldItemLayer<>(this, "snout"));
    }

    @Override
    public float getMotionAnimThreshold(Bear animatable) {
        return 0.000001f;
    }

    @Override
    public void extractRenderState(Bear entity, R renderState, float partialTick) {
        super.extractRenderState(entity, renderState, partialTick);
        Identifier texture;
        if (entity.isAngry()) {
            texture = BEAR_ANGRY_TEXTURE;
        } else if (entity.isSleeping()) {
            texture = BEAR_SLEEP_TEXTURE;
        } else if (entity.isEating()) {
            if (entity.getMainHandItem().is(Items.SWEET_BERRIES)) {
                texture = BEAR_BERRIES_TEXTURE;
            } else if (entity.getMainHandItem().is(Items.HONEYCOMB)) {
                texture = BEAR_HONEY_TEXTURE;
            } else {
                texture = BEAR_TEXTURE;
            }
        } else {
            texture = BEAR_TEXTURE;
        }
        renderState.addGeckolibData(BearModel.BEAR_TEXTURE, texture);
        renderState.addGeckolibData(IS_BABY, entity.isBaby());
        renderState.addGeckolibData(BEAR_SKIP_HEAD, entity.isSleeping() || entity.isEating() || entity.isSitting());
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
            boneSnapshots.ifPresent("head", snapshot -> snapshot.setScale(1.8f, 1.8f, 1.8f));
        }

        // Head tracking (skip when sleeping, eating, or sitting)
        Boolean skipHead = renderPassInfo.renderState().getGeckolibData(BEAR_SKIP_HEAD);
        if (skipHead == null || !skipHead) {
            float pitch = renderPassInfo.getOrDefaultGeckolibData(DataTickets.ENTITY_PITCH, 0f);
            float yaw = renderPassInfo.getOrDefaultGeckolibData(DataTickets.ENTITY_YAW, 0f);
            boneSnapshots.ifPresent("head", snapshot -> {
                snapshot.setRotX(-pitch * Mth.DEG_TO_RAD);
                snapshot.setRotY(-yaw * Mth.DEG_TO_RAD);
            });
        }
    }
}
