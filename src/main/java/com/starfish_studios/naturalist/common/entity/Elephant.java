package com.starfish_studios.naturalist.common.entity;

import com.starfish_studios.naturalist.common.entity.core.NaturalistAnimal;
import com.starfish_studios.naturalist.common.entity.core.ai.goal.BabyHurtByTargetGoal;
import com.starfish_studios.naturalist.common.entity.core.ai.goal.BabyPanicGoal;
import com.starfish_studios.naturalist.common.entity.core.ai.goal.DistancedFollowParentGoal;
import com.starfish_studios.naturalist.common.entity.core.ai.navigation.MMPathNavigatorGround;
import com.starfish_studios.naturalist.common.entity.core.ai.navigation.SmartBodyHelper;
import com.starfish_studios.naturalist.core.registry.NaturalistEntityTypes;
import com.starfish_studios.naturalist.core.registry.NaturalistSoundEvents;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import com.starfish_studios.naturalist.common.entity.core.NaturalistGeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.state.AnimationTest;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import org.jetbrains.annotations.Nullable;

public class Elephant extends NaturalistAnimal implements NeutralMob, NaturalistGeoEntity {
    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.sf_nba.elephant.idle");
    protected static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.sf_nba.elephant.walk");
     protected static final RawAnimation RUN = RawAnimation.begin().thenLoop("animation.sf_nba.elephant.run");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    // private static final EntityDataAccessor<Integer> DIRTY_TICKS = SynchedEntityData.defineId(Elephant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DRINKING = SynchedEntityData.defineId(Elephant.class, EntityDataSerializers.BOOLEAN);
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    private static final EntityDataAccessor<Integer> REMAINING_ANGER_TIME = SynchedEntityData.defineId(Elephant.class, EntityDataSerializers.INT);
    @org.jetbrains.annotations.Nullable
    private EntityReference<LivingEntity> persistentAngerTarget;

    public Elephant(EntityType<? extends NaturalistAnimal> entityType, Level level) {
        super(entityType, level);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0F);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }


    @Override
    protected @NotNull BodyRotationControl createBodyControl() {
        return new SmartBodyHelper(this);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        return new MMPathNavigatorGround(this, level);
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.2)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75D)
                .add(Attributes.FOLLOW_RANGE, 15.0D);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason reason, @Nullable SpawnGroupData spawnData) {
        AgeableMobGroupData ageableMobGroupData;
        if (spawnData == null) {
            spawnData = new AgeableMobGroupData(true);
        }
        if ((ageableMobGroupData = (AgeableMobGroupData)spawnData).getGroupSize() > 1) {
            this.setAge(-24000);
        }
        ageableMobGroupData.increaseGroupSizeByOne();
        RandomSource random = level.getRandom();
        this.getAttribute(Attributes.FOLLOW_RANGE).addPermanentModifier(new AttributeModifier(Identifier.fromNamespaceAndPath("naturalist", "random_spawn_bonus"), random.triangle(0.0, 0.11485000000000001), AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        return spawnData;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return NaturalistEntityTypes.ELEPHANT.get().create(serverLevel, net.minecraft.world.entity.EntitySpawnReason.BREEDING);
    }


    @Override
    protected void customServerAiStep(ServerLevel serverLevel) {
        super.customServerAiStep(serverLevel);
        if (this.getMoveControl().hasWanted()) {
            this.setSprinting(this.getMoveControl().getSpeedModifier() >= 1.2D);
        } else {
            this.setSprinting(false);
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Bee.class, 8.0f, 1.3, 1.3));
        this.goalSelector.addGoal(2, new ElephantMeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(3, new BabyPanicGoal(this, 1.3D));
        this.goalSelector.addGoal(4, new DistancedFollowParentGoal(this, 1.2D, 24.0D, 6.0D, 12.0D));
        // this.goalSelector.addGoal(5, new ElephantDrinkWaterGoal(this));
        // this.goalSelector.addGoal(6, new ElephantMoveToWaterGoal(this, 1.0D, 8, 4));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new BabyHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(5, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    @Override
    public int getMaxHeadYRot() {
        return 35;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return NaturalistSoundEvents.ELEPHANT_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return NaturalistSoundEvents.ELEPHANT_AMBIENT.get();
    }

    @Override
    public boolean doHurtTarget(ServerLevel serverLevel, Entity target) {
        boolean shouldHurt = super.doHurtTarget(serverLevel, target);
        if (shouldHurt && target instanceof LivingEntity livingEntity) {
            Vec3 knockbackDirection = new Vec3(this.blockPosition().getX() - target.getX(), 0.0, this.blockPosition().getZ() - target.getZ()).normalize();
            float shieldBlockModifier = livingEntity.isBlocking() ? 0.5f : 1.0f;
            livingEntity.knockback(shieldBlockModifier * 3.0D, knockbackDirection.x(), knockbackDirection.z());
            double knockbackResistance = Math.max(0.0, 1.0 - livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().add(0.0, 0.5f * knockbackResistance, 0.0));
        }
        this.playSound(SoundEvents.RAVAGER_ATTACK, 1.0f, 1.0f);
        return shouldHurt;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        // builder.define(DIRTY_TICKS, 0);
        builder.define(REMAINING_ANGER_TIME, 0);
        builder.define(DRINKING, false);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput view) {
        super.addAdditionalSaveData(view);
        this.addPersistentAngerSaveData(view);
        // view.putInt("DirtyTicks", this.getDirtyTicks());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput view) {
        super.readAdditionalSaveData(view);
        this.readPersistentAngerSaveData(this.level(), view);
        // this.setDirtyTicks(view.getIntOr("DirtyTicks", 0));
        // this.updateContainerEquipment();
    }

    // public void setDirtyTicks(int ticks) {
    //     this.entityData.set(DIRTY_TICKS, ticks);
    //  }

    // public int getDirtyTicks() {
    //     return this.entityData.get(DIRTY_TICKS);
    // }

    // public boolean isDirty() {
    //     return this.getDirtyTicks() > 0;
    // }

    /* public void setDrinking(boolean drinking) {
        this.entityData.set(DRINKING, drinking);
    }

    public boolean isDrinking() {
        return this.entityData.get(DRINKING);
    } */

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide()) {
            this.updatePersistentAnger((ServerLevel)this.level(), true);
        }
        /* if (this.level instanceof ServerLevel serverLevel) {
            if (this.isDirty()) {
                this.setDirtyTicks(this.isInWater() ? 0 : Math.max(0, this.getDirtyTicks() - 1));
            } else {
                long dayTime = serverLevel.getDayTime();
                if (dayTime > 4300 && dayTime < 11000 && this.isOnGround() && this.getRandom().nextFloat() < 0.001f && !this.isDrinking()) {
                    this.swing(InteractionHand.MAIN_HAND);
                    this.setDirtyTicks(1000);
                    serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.DIRT.defaultBlockState()), this.getX(), this.getY(), this.getZ(),
                            200, 0.5, 3.0, 0.5, 10);
                }
            }
        } */
    }

    // ANGER

    @Override
    public void startPersistentAngerTimer() {
        this.setTimeToRemainAngry(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    @Override
    public void setPersistentAngerEndTime(long pTime) {
        this.entityData.set(REMAINING_ANGER_TIME, (int) pTime);
    }

    @Override
    public long getPersistentAngerEndTime() {
        return this.entityData.get(REMAINING_ANGER_TIME);
    }

    @Override
    public void setPersistentAngerTarget(@Nullable EntityReference<LivingEntity> pTarget) {
        this.persistentAngerTarget = pTarget;
    }

    @Nullable
    @Override
    public EntityReference<LivingEntity> getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }


    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
    private @NotNull PlayState predicate(final AnimationTest<Elephant> event) {
        if (this.isBaby() || this.getTarget() != null) {
            event.setControllerSpeed((float)(0.52F * Math.max(0.1, this.getDeltaMovement().horizontalDistance() * 3.0)));
        }
        if (event.isMoving()) {
            if (this.isSprinting()) {
                event.setAnimation(RUN);
            } else {
                event.setAnimation(WALK);
            }
        } /*else if (this.isDrinking()) {
            event.setAnimation(RawAnimation.begin().thenLoop("elephant.water"));
        }*/ else {
            event.setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    private PlayState swingPredicate(final @NotNull AnimationTest<Elephant> event) {
        if (this.swinging && event.controller().getPlayState() == PlayState.STOP) {
            event.controller().reset();
            
        
            event.setAnimation(RawAnimation.begin().thenPlay("animation.sf_nba.elephant.swing"));
            this.swinging = false;
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        // data.setResetSpeedInTicks(10);
        controllers.add(new AnimationController<>("controller", 5, this::predicate).setAnimationSpeed(1.0).setSoundKeyframeHandler(event -> {}));
        controllers.add(new AnimationController<>("swingController", 0, this::swingPredicate).setAnimationSpeed(0.225).setSoundKeyframeHandler(event -> {}));
    }

    static class ElephantMeleeAttackGoal extends MeleeAttackGoal {
        public ElephantMeleeAttackGoal(PathfinderMob pathfinderMob, double speedMultiplier, boolean followingTargetEvenIfNotSeen) {
            super(pathfinderMob, speedMultiplier, followingTargetEvenIfNotSeen);
        }

        @Override
        protected boolean canPerformAttack(LivingEntity target) {
            double reachSqr = Mth.square(this.mob.getBbWidth());
            double distSqr = this.mob.distanceToSqr(target);
            return distSqr <= reachSqr && this.isTimeToAttack();
        }
    }

    /*static class ElephantMoveToWaterGoal extends MoveToBlockGoal {
        private final Elephant elephant;

        public ElephantMoveToWaterGoal(Elephant pathfinderMob, double speedModifier, int searchRange, int verticalSearchRange) {
            super(pathfinderMob, speedModifier, searchRange, verticalSearchRange);
            this.elephant = pathfinderMob;
        }

        @Override
        public boolean canUse() {
            return !this.elephant.isBaby() && this.elephant.waterPos == null && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.isReachedTarget() && super.canContinueToUse();
        }

        @Override
        protected boolean isValidTarget(LevelReader level, BlockPos pos) {
            if (level.getBlockState(pos).isFaceSturdy(level, pos, Direction.DOWN)) {
                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    if (level.getFluidState(pos.relative(direction)).is(Fluids.WATER)) {
                        this.elephant.waterPos = pos.relative(direction);
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public double acceptedDistance() {
            return 2.5D;
        }

        @Override
        public void stop() {
            this.elephant.setDrinking(true);
            super.stop();
        }
    }

    static class ElephantDrinkWaterGoal extends Goal {
        private final Elephant elephant;
        private int drinkTicks;

        public ElephantDrinkWaterGoal(Elephant elephant) {
            this.elephant = elephant;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.elephant.waterPos == null || this.elephant.distanceToSqr(Vec3.atCenterOf(this.elephant.waterPos)) > 15) {
                this.elephant.setDrinking(false);
                return false;
            }
            return this.elephant.isDrinking();
        }

        @Override
        public boolean canContinueToUse() {
            return this.drinkTicks > 0 && super.canContinueToUse();
        }

        @Override
        public void start() {
            this.drinkTicks = 150;
            if (this.elephant.waterPos != null) {
                this.elephant.getLookControl().setLookAt(Vec3.atCenterOf(this.elephant.waterPos));
            }
        }

        @Override
        public void tick() {
            this.drinkTicks--;
            if (this.elephant.waterPos != null) {
                this.elephant.getLookControl().setLookAt(Vec3.atCenterOf(this.elephant.waterPos));
            }
        }

        @Override
        public void stop() {
            this.elephant.waterPos = null;
            this.elephant.setDrinking(false);
        }
    } */
}
