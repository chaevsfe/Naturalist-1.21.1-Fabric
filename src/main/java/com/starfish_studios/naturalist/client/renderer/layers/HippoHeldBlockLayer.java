package com.starfish_studios.naturalist.client.renderer.layers;

import com.mojang.datafixers.util.Either;
import com.starfish_studios.naturalist.common.entity.Hippo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.cache.model.BakedGeoModel;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.layer.builtin.BlockAndItemGeoLayer;

import java.util.Collections;
import java.util.List;

/**
 * Render layer for rendering a block in the Hippo's mouth (botjaw bone).
 * Only renders when the hippo is holding a BlockItem in its main hand.
 */
@Environment(EnvType.CLIENT)
public class HippoHeldBlockLayer<R extends LivingEntityRenderState & GeoRenderState> extends BlockAndItemGeoLayer<Hippo, Void, R> {
    public static final DataTicket<BlockState> HELD_BLOCK = DataTicket.create("hippo_held_block", BlockState.class);
    private final List<RenderData<R>> renderDataList;

    public HippoHeldBlockLayer(GeoRenderer<Hippo, Void, R> renderer) {
        super(renderer);
        this.renderDataList = List.of(
                new RenderData<>("botjaw", ItemDisplayContext.NONE, (bone, renderState) -> {
                    BlockState blockState = renderState.getGeckolibData(HELD_BLOCK);
                    if (blockState != null) {
                        return Either.right(blockState);
                    }
                    return Either.left(ItemStack.EMPTY);
                })
        );
    }

    @Override
    public void addRenderData(Hippo animatable, Void relatedObject, R renderState, float partialTick) {
        ItemStack mainHand = animatable.getItemBySlot(EquipmentSlot.MAINHAND);
        if (mainHand.getItem() instanceof BlockItem blockItem) {
            renderState.addGeckolibData(HELD_BLOCK, blockItem.getBlock().defaultBlockState());
        }
    }

    @Override
    protected List<RenderData<R>> getRelevantBones(R renderState, BakedGeoModel model) {
        BlockState blockState = renderState.getGeckolibData(HELD_BLOCK);
        if (blockState != null) {
            return renderDataList;
        }
        return Collections.emptyList();
    }
}
