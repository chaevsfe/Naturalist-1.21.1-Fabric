package com.starfish_studios.naturalist.common.item.fabric;

import com.starfish_studios.naturalist.common.entity.Butterfly;
import com.starfish_studios.naturalist.common.entity.core.Catchable;
import com.starfish_studios.naturalist.core.registry.NaturalistEntityTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CaughtMobItem extends MobBucketItem {
    private final EntityType<?> type;

    @SuppressWarnings("unchecked")
    public CaughtMobItem(EntityType<?> entitySupplier, Fluid fluid, SoundEvent emptyingSound, Properties settings) {
        super((EntityType<? extends net.minecraft.world.entity.Mob>) entitySupplier, fluid, emptyingSound, settings);
        this.type = entitySupplier;
    }


    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay tooltipDisplay, java.util.function.Consumer<Component> tooltip, TooltipFlag flagIn) {
        if (this.type == NaturalistEntityTypes.BUTTERFLY.get()) {
            CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            CompoundTag compoundnbt = customData.copyTag();
            if (compoundnbt.contains("Variant")) {
                int variantId = compoundnbt.getInt("Variant").orElse(0);
                Butterfly.Variant variant = Butterfly.Variant.getTypeById(variantId);
                tooltip.accept((Component.translatable(String.format("tooltip.naturalist.%s", variant.toString().toLowerCase())).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY)));
            }
        }
    }

    private void spawn(ServerLevel serverLevel, ItemStack itemStack, @NotNull BlockPos pos) {
        Entity entity = this.type.spawn(serverLevel, itemStack, null, pos, EntitySpawnReason.BUCKET, true, false);
        if (entity instanceof Catchable catchable) {
            CustomData customData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            catchable.loadFromHandTag(customData.copyTag());
            catchable.setFromHand(true);
        }

    }

    @Override
    public void checkExtraContent(@Nullable LivingEntity entity, Level level, ItemStack containerStack, BlockPos pos) {
        if (level instanceof ServerLevel) {
            this.spawn((ServerLevel)level, containerStack, pos);
            level.gameEvent(entity, GameEvent.ENTITY_PLACE, pos);
        }

    }


    @Override
    public @NotNull InteractionResult use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        BlockHitResult blockhitresult = getPlayerPOVHitResult(pLevel, pPlayer, ClipContext.Fluid.NONE);
        if (blockhitresult.getType() == HitResult.Type.MISS) {
            return InteractionResult.PASS;
        } else if (blockhitresult.getType() != HitResult.Type.BLOCK) {
            return InteractionResult.PASS;
        } else {
            BlockPos pos = blockhitresult.getBlockPos();
            Direction direction = blockhitresult.getDirection();
            BlockPos blockpos1 = pos.relative(direction);
            if (pLevel.mayInteract(pPlayer, pos) && pPlayer.mayUseItemAt(blockpos1, direction, itemstack)) {
                this.checkExtraContent(pPlayer, pLevel, itemstack, pos);
                this.playEmptySound(pPlayer, pLevel, pos);
                pPlayer.awardStat(Stats.ITEM_USED.get(this));
                pPlayer.setItemInHand(pHand, getEmptySuccessItem(itemstack, pPlayer));
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.FAIL;
            }
        }
    }

    public static @NotNull ItemStack getEmptySuccessItem(ItemStack bucketStack, Player player) {
        return !player.getAbilities().instabuild ? new ItemStack(Items.AIR) : bucketStack;
    }
}
