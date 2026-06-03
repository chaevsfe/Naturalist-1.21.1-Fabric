package com.starfish_studios.naturalist.common.entity;

import com.starfish_studios.naturalist.common.entity.core.EggLayingAnimal;
import com.starfish_studios.naturalist.common.entity.core.HidingAnimal;
import com.starfish_studios.naturalist.common.entity.core.ai.goal.EggLayingBreedGoal;
import com.starfish_studios.naturalist.common.entity.core.ai.goal.HideGoal;
import com.starfish_studios.naturalist.common.entity.core.ai.goal.LayEggGoal;
import com.starfish_studios.naturalist.common.entity.core.ai.navigation.MMPathNavigatorGround;
import com.starfish_studios.naturalist.common.entity.core.ai.navigation.SmartBodyHelper;
import com.starfish_studios.naturalist.core.platform.CommonPlatformHelper;
import com.starfish_studios.naturalist.core.registry.NaturalistEntityTypes;
import com.starfish_studios.naturalist.core.registry.NaturalistRegistry;
import com.starfish_studios.naturalist.core.registry.NaturalistSoundEvents;
import com.starfish_studios.naturalist.core.registry.NaturalistTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.starfish_studios.naturalist.common.entity.core.NaturalistGeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.state.AnimationTest;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.state.KeyFrameEvent;
import software.bernie.geckolib.cache.animation.keyframeevent.SoundKeyframeData;
import software.bernie.geckolib.animation.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class Tortoise extends TamableAnimal implements NaturalistGeoEntity, HidingAnimal, EggLayingAnimal {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private static final java.util.function.Predicate<net.minecraft.world.item.ItemStack> TEMPT_ITEMS = (stack) -> stack.is(NaturalistTags.ItemTags.TORTOISE_TEMPT_ITEMS);
    private static final EntityDataAccessor<Integer> VARIANT_ID = SynchedEntityData.defineId(Tortoise.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> HAS_EGG = SynchedEntityData.defineId(Tortoise.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LAYING_EGG = SynchedEntityData.defineId(Tortoise.class, EntityDataSerializers.BOOLEAN);
    int layEggCounter;
    boolean isDigging;

    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.sf_nba.tortoise.idle");
    protected static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.sf_nba.tortoise.walk");
    protected static final RawAnimation SIT = RawAnimation.begin().thenLoop("animation.sf_nba.tortoise.sit");
    protected static final RawAnimation HIDE = RawAnimation.begin().thenPlay("animation.sf_nba.tortoise.hide").thenLoop("animation.sf_nba.tortoise.hide_idle");
    protected static final RawAnimation DIG = RawAnimation.begin().thenLoop("animation.sf_nba.tortoise.dig");
    protected static final RawAnimation HURT = RawAnimation.begin().thenLoop("animation.sf_nba.tortoise.hurt");


    public Tortoise(@NotNull EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.TEMPT_RANGE, 10).add(Attributes.MOVEMENT_SPEED, 0.17f).add(Attributes.MAX_HEALTH, 20.0).add(Attributes.ATTACK_DAMAGE, 2.0).add(Attributes.KNOCKBACK_RESISTANCE, 0.6);
    }


    @Override
    protected @NotNull BodyRotationControl createBodyControl() {
        return new SmartBodyHelper(this);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        return new MMPathNavigatorGround(this, level);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.SHIELD_BLOCK.value();
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        Tortoise tortoise = NaturalistEntityTypes.TORTOISE.get().create(level, net.minecraft.world.entity.EntitySpawnReason.BREEDING);
        if (otherParent instanceof Tortoise tortoiseParent) {
            assert tortoise != null;
            if (this.getVariant() == tortoiseParent.getVariant()) {
                tortoise.setVariant(this.getVariant());
            } else {
                tortoise.setVariant(this.random.nextBoolean() ? tortoiseParent.getVariant() : this.getVariant());
            }
            LivingEntity owner = this.random.nextBoolean() ? tortoiseParent.getOwner() : this.getOwner();
            if (owner != null) {
                tortoise.setOwnerReference(net.minecraft.world.entity.EntityReference.of(owner));
            }
        }
        return tortoise;
    }

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason reason, @Nullable SpawnGroupData spawnData) {
        Holder<Biome> holder = level.getBiome(this.blockPosition());
        if (holder.is(Biomes.SWAMP) || holder.is(Biomes.MANGROVE_SWAMP)) {
            this.setVariant(1);
        } else if (holder.is(BiomeTags.IS_JUNGLE) || holder.is(Biomes.DARK_FOREST)) {
            this.setVariant(2);
        } else {
            this.setVariant(0);
        }
        return super.finalizeSpawn(level, difficulty, reason, spawnData);
    }

    @Override
    public void setTame(boolean tamed, boolean applyEffect) {
        super.setTame(tamed, applyEffect);
        if (tamed) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(30.0);
            this.setHealth(30.0f);
        } else {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20.0);
        }
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4.0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new EggLayingBreedGoal<>(this, 1.0));
        this.goalSelector.addGoal(1, new LayEggGoal<>(this, 1.0));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(1, new HideGoal<>(this));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.0, TEMPT_ITEMS, false));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.0, 10.0f, 5.0f));
        this.goalSelector.addGoal(4, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 10.0f));
    }

    @Override
    public boolean canMate(Animal otherAnimal) {
        if (!this.isTame()) {
            return false;
        }
        if (!(otherAnimal instanceof Tortoise tortoise)) {
            return false;
        }
        return tortoise.isTame() && super.canMate(otherAnimal);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return TEMPT_ITEMS.test(stack);
    }

    @Override
    public void knockback(double strength, double x, double z) {
        super.knockback(this.isInSittingPose() || this.canHide() ? strength / 4 : strength, x, z);
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource source, float amount) {
        return super.hurtServer(serverLevel, source, this.canHide() ? amount * 0.8F : amount);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        InteractionResult interactionResult;
        ItemStack itemStack = player.getItemInHand(hand);
        if (this.level().isClientSide()) {
            if (this.isTame() && this.isOwnedBy(player)) {
                return InteractionResult.SUCCESS;
            }
            if (this.isFood(itemStack) && (this.getHealth() < this.getMaxHealth() || !this.isTame())) {
                return InteractionResult.SUCCESS;
            }
//            if (itemStack.is(CommonPlatformHelper.getShearsTag())) {
//                return InteractionResult.SUCCESS;
//            }
            return InteractionResult.PASS;
        }
        if (this.isTame()) {
            if (this.isOwnedBy(player)) {
                if (this.isFood(itemStack) && this.getHealth() < this.getMaxHealth()) {
                    this.usePlayerItem(player, hand, itemStack);
                    this.heal(3.0F);
                    return InteractionResult.CONSUME;
                }
                InteractionResult interactionResult2 = super.mobInteract(player, hand);
                if (!interactionResult2.consumesAction() || this.isBaby()) {
                    this.setOrderedToSit(!this.isOrderedToSit());
                }
                return interactionResult2;
            }
        } else if (this.isFood(itemStack)) {
            this.usePlayerItem(player, hand, itemStack);
            if (this.random.nextInt(3) == 0) {
                this.tame(player);
                this.setOrderedToSit(true);
                this.level().broadcastEntityEvent(this, (byte)7);
            } else {
                this.level().broadcastEntityEvent(this, (byte)6);
            }
            this.setPersistenceRequired();
            return InteractionResult.CONSUME;
        }
        if ((interactionResult = super.mobInteract(player, hand)).consumesAction()) {
            this.setPersistenceRequired();
        }
        return interactionResult;
    }

    @Override
    public boolean canHide() {
        if (this.isTame()) {
            return false;
        }
        if (!(this.level() instanceof ServerLevel sl)) return false;
        List<Player> players = sl.getNearbyPlayers(TargetingConditions.forNonCombat().range(5.0D).selector((livingEntity, level) -> EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity) && !livingEntity.isDiscrete() && !livingEntity.isHolding(TEMPT_ITEMS)), this, this.getBoundingBox().inflate(5.0D, 3.0D, 5.0D));
        return !players.isEmpty();
    }

    @Override
    protected float getWaterSlowDown() {
        return 0.96f;
    }

    @Override
    public double getFluidJumpThreshold() {
        return 0.4;
    }

    // ENTITY DATA

    public int getVariant() {
        return Mth.clamp(this.entityData.get(VARIANT_ID), 0, 2);
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT_ID, variant);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT_ID, 0);
        builder.define(HAS_EGG, false);
        builder.define(LAYING_EGG, false);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput view) {
        super.addAdditionalSaveData(view);
        view.putInt("Variant", this.getVariant());
        view.putBoolean("HasEgg", this.hasEgg());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput view) {
        super.readAdditionalSaveData(view);
        this.setVariant(view.getIntOr("Variant", 0));
        this.setHasEgg(view.getBooleanOr("HasEgg", false));
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        super.playStepSound(pos, state);
    }

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    private PlayState predicate(final AnimationTest<Tortoise> event) {
        if (this.isInSittingPose()) {
            event.setAnimation(SIT);
            return PlayState.CONTINUE;
        } else if (this.isLayingEgg())  {
            event.setAnimation(DIG);
            return PlayState.CONTINUE;
        } else if (this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6) {
            event.setAnimation(WALK);
            if (this.isBaby()) {
                event.setControllerSpeed((float)(0.8F * Math.max(0.1, this.getDeltaMovement().horizontalDistance() * 3.0)));
            } else {
                event.setControllerSpeed((float)(0.52F * Math.max(0.1, this.getDeltaMovement().horizontalDistance() * 3.0)));
            }
            return PlayState.CONTINUE;
        } else {
            event.setAnimation(IDLE);
            return PlayState.CONTINUE;
        }
    }

    private PlayState hidePredicate(final @NotNull AnimationTest<Tortoise> event) {
        if( this.canHide()) {
            event.setAnimation(HIDE);
            return PlayState.CONTINUE;
        }
        

        return PlayState.STOP;
    }

    private PlayState hurtPredicate(final AnimationTest<Tortoise> event) {
        if(this.hurtTime > 0) {
            event.setAnimation(HURT);
            return PlayState.CONTINUE;
        }
        
        
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(final AnimatableManager.@NotNull ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("controller", 5, this::predicate).setAnimationSpeed(1.0).setSoundKeyframeHandler(this::soundListener));
        controllers.add(new AnimationController<>("hurtController", 5, this::hurtPredicate).setAnimationSpeed(0.225).setSoundKeyframeHandler(this::soundListener));
        controllers.add(new AnimationController<>("hideController", 0, this::hidePredicate).setAnimationSpeed(0.225).setSoundKeyframeHandler(this::soundListener));
    }

    private void soundListener(KeyFrameEvent<Tortoise, SoundKeyframeData> event) {
        Tortoise animatable = event.animatable();
        if (animatable.level().isClientSide()) {
            if (event.keyframeData().getSound().equals("hide")) {
                animatable.level().playLocalSound(animatable.getX(), animatable.getY(), animatable.getZ(), NaturalistSoundEvents.TORTOISE_HIDE.get(), animatable.getSoundSource(), 0.5F, 1.0F, false);
            }
            if (event.keyframeData().getSound().equals("thud")) {
                animatable.level().playLocalSound(animatable.getX(), animatable.getY(), animatable.getZ(), NaturalistSoundEvents.TORTOISE_THUD.get(), animatable.getSoundSource(), 0.5F, 1.0F, false);
            }
        }
    }

    @Override
    public boolean hasEgg() {
        return this.entityData.get(HAS_EGG);
    }

    @Override
    public void setHasEgg(boolean hasEgg) {
        this.entityData.set(HAS_EGG, hasEgg);
    }

    @Override
    public boolean isLayingEgg() {
        return this.entityData.get(LAYING_EGG);
    }

    @Override
    public void setLayingEgg(boolean isLayingEgg) {
        this.entityData.set(LAYING_EGG, isLayingEgg);
    }

    @Override
    public int getLayEggCounter() {
        return this.layEggCounter;
    }

    @Override
    public void setLayEggCounter(int layEggCounter) {
        this.layEggCounter = layEggCounter;
    }

    @Override
    public Block getEggBlock() {
        return NaturalistRegistry.TORTOISE_EGG.get();
    }

    @Override
    public TagKey<Block> getEggLayableBlockTag() {
        return NaturalistTags.BlockTags.TORTOISE_EGG_LAYABLE_ON;
    }

    @Override
    public boolean canFallInLove() {
        return super.canFallInLove() && !this.hasEgg();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        BlockPos pos = this.blockPosition();
        if (this.isAlive() && this.isLayingEgg() && this.layEggCounter >= 1 && this.layEggCounter % 5 == 0 && this.level().getBlockState(pos.below()).is(this.getEggLayableBlockTag())) {
            this.level().levelEvent(2001, pos, Block.getId(this.level().getBlockState(pos.below())));
        }
    }
}
