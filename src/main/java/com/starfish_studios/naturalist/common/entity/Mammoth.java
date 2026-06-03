package com.starfish_studios.naturalist.common.entity;

import com.starfish_studios.naturalist.common.entity.core.NaturalistAnimal;
import com.starfish_studios.naturalist.common.entity.core.NaturalistGeoEntity;
import com.starfish_studios.naturalist.common.entity.core.ai.goal.BabyHurtByTargetGoal;
import com.starfish_studios.naturalist.common.entity.core.ai.goal.BabyPanicGoal;
import com.starfish_studios.naturalist.common.entity.core.ai.goal.DistancedFollowParentGoal;
import com.starfish_studios.naturalist.common.entity.core.ai.navigation.MMPathNavigatorGround;
import com.starfish_studios.naturalist.common.entity.core.ai.navigation.SmartBodyHelper;
import com.starfish_studios.naturalist.core.registry.NaturalistEntityTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.state.AnimationTest;
import software.bernie.geckolib.animation.object.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;


public class Mammoth extends NaturalistAnimal implements NeutralMob, NaturalistGeoEntity, ContainerListener, MenuProvider {
    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.sf_nba.mammoth.idle");
    protected static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.sf_nba.mammoth.walk");
    protected static final RawAnimation RUN = RawAnimation.begin().thenLoop("animation.sf_nba.mammoth.run");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    private static final EntityDataAccessor<Integer> REMAINING_ANGER_TIME = SynchedEntityData.defineId(Mammoth.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SADDLED = SynchedEntityData.defineId(Mammoth.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_CHEST = SynchedEntityData.defineId(Mammoth.class, EntityDataSerializers.BOOLEAN);
    @Nullable
    private EntityReference<LivingEntity> persistentAngerTarget;
    private SimpleContainer inventory;

    public Mammoth(EntityType<? extends NaturalistAnimal> entityType, Level level) {
        super(entityType, level);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0F);
        this.createInventory();
    }

    // region INVENTORY

    private void createInventory() {
        SimpleContainer oldInventory = this.inventory;
        this.inventory = new SimpleContainer(27);
        if (oldInventory != null) {
            oldInventory.removeListener(this);
            int count = Math.min(oldInventory.getContainerSize(), this.inventory.getContainerSize());
            for (int i = 0; i < count; i++) {
                ItemStack stack = oldInventory.getItem(i);
                if (!stack.isEmpty()) {
                    this.inventory.setItem(i, stack.copy());
                }
            }
        }
        this.inventory.addListener(this);
    }

    @Override
    public void containerChanged(Container container) {
    }

    @Override
    public @NotNull AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return ChestMenu.threeRows(syncId, playerInventory, this.inventory);
    }

    public void openCustomInventoryScreen(Player player) {
        player.openMenu(this);
    }

    // endregion

    // region SADDLE & CHEST STATE

    public boolean isSaddled() {
        return this.entityData.get(SADDLED);
    }

    public void setSaddled(boolean saddled) {
        this.entityData.set(SADDLED, saddled);
    }

    public boolean hasChest() {
        return this.entityData.get(HAS_CHEST);
    }

    public void setHasChest(boolean hasChest) {
        this.entityData.set(HAS_CHEST, hasChest);
    }

    // endregion

    // region BASE INFO / GOALS

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
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6D)
                .add(Attributes.FOLLOW_RANGE, 15.0D);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason reason, @Nullable SpawnGroupData spawnData) {
        AgeableMobGroupData ageableMobGroupData;
        if (spawnData == null) {
            spawnData = new AgeableMobGroupData(true);
        }
        if ((ageableMobGroupData = (AgeableMobGroupData) spawnData).getGroupSize() > 1) {
            this.setAge(-24000);
        }
        ageableMobGroupData.increaseGroupSizeByOne();
        RandomSource random = level.getRandom();
        this.getAttribute(Attributes.FOLLOW_RANGE).addPermanentModifier(new AttributeModifier(Identifier.fromNamespaceAndPath("naturalist", "random_spawn_bonus"), random.triangle(0.0, 0.11485000000000001), AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        return spawnData;
    }

    @Override
    public boolean canFreeze() {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return NaturalistEntityTypes.MAMMOTH.get().create(serverLevel, net.minecraft.world.entity.EntitySpawnReason.BREEDING);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MammothMeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(2, new BabyPanicGoal(this, 1.3D));
        this.goalSelector.addGoal(3, new DistancedFollowParentGoal(this, 1.2D, 24.0D, 6.0D, 12.0D));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new BabyHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(5, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    @Override
    public int getMaxHeadYRot() {
        return 35;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canBeLeashed() {
        return true;
    }

    // endregion

    // region INTERACTION

    @Override
    public @NotNull InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (this.isBaby()) {
            return super.mobInteract(player, hand);
        }

        // Saddle the mammoth
        if (!this.isSaddled() && stack.is(Items.SADDLE)) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            this.setSaddled(true);
            this.playSound(SoundEvents.HORSE_SADDLE.value(), 1.0f, 1.0f);
            return InteractionResult.SUCCESS;
        }

        // Add chest
        if (this.isSaddled() && !this.hasChest() && stack.is(Items.CHEST)) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            this.setHasChest(true);
            this.playSound(SoundEvents.MULE_CHEST, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
            return InteractionResult.SUCCESS;
        }

        // Open inventory (shift-click when has chest)
        if (this.hasChest() && player.isSecondaryUseActive()) {
            if (!this.level().isClientSide()) {
                this.openCustomInventoryScreen(player);
            }
            return InteractionResult.SUCCESS;
        }

        // Mount (when saddled, empty hand or non-special item)
        if (this.isSaddled() && !this.isVehicle()) {
            if (!this.level().isClientSide()) {
                player.startRiding(this);
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    // endregion

    // region RIDING

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        if (this.isSaddled()) {
            Entity entity = this.getFirstPassenger();
            if (entity instanceof Player player) {
                return player;
            }
        }
        return null;
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 travelVector) {
        float forward = player.zza;
        float strafe = player.xxa * 0.5F;
        if (forward <= 0.0F) {
            forward *= 0.25F;
        }
        return new Vec3(strafe, 0.0, forward);
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.9F;
    }

    @Override
    protected void tickRidden(Player player, Vec3 travelVector) {
        super.tickRidden(player, travelVector);
        this.setRot(player.getYRot(), player.getXRot() * 0.5F);
        this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
    }

    @Override
    public void travel(@NotNull Vec3 movementInput) {
        if (this.isAlive()) {
            LivingEntity controllingPassenger = this.getControllingPassenger();
            if (this.isVehicle() && controllingPassenger != null) {
                super.travel(movementInput);
                return;
            }
        }
        super.travel(movementInput);
    }

    @Override
    protected void customServerAiStep(ServerLevel serverLevel) {
        super.customServerAiStep(serverLevel);
        if (this.getControllingPassenger() == null) {
            if (this.getMoveControl().hasWanted()) {
                this.setSprinting(this.getMoveControl().getSpeedModifier() >= 1.2D);
            } else {
                this.setSprinting(false);
            }
        }
    }

    // endregion

    // region COMBAT

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

    // endregion

    // region DATA

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(REMAINING_ANGER_TIME, 0);
        builder.define(SADDLED, false);
        builder.define(HAS_CHEST, false);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput view) {
        super.addAdditionalSaveData(view);
        this.addPersistentAngerSaveData(view);
        view.putBoolean("Saddled", this.isSaddled());
        view.putBoolean("HasChest", this.hasChest());
        if (this.hasChest()) {
            NonNullList<ItemStack> items = NonNullList.withSize(this.inventory.getContainerSize(), ItemStack.EMPTY);
            for (int i = 0; i < this.inventory.getContainerSize(); i++) {
                items.set(i, this.inventory.getItem(i));
            }
            net.minecraft.world.ContainerHelper.saveAllItems(view, items);
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput view) {
        super.readAdditionalSaveData(view);
        this.readPersistentAngerSaveData(this.level(), view);
        this.setSaddled(view.getBooleanOr("Saddled", false));
        this.setHasChest(view.getBooleanOr("HasChest", false));
        if (this.hasChest()) {
            this.createInventory();
            NonNullList<ItemStack> items = NonNullList.withSize(this.inventory.getContainerSize(), ItemStack.EMPTY);
            net.minecraft.world.ContainerHelper.loadAllItems(view, items);
            for (int i = 0; i < items.size(); i++) {
                this.inventory.setItem(i, items.get(i));
            }
        }
    }

    @Override
    protected void dropEquipment(ServerLevel serverLevel) {
        super.dropEquipment(serverLevel);
        if (this.isSaddled()) {
            this.spawnAtLocation(serverLevel, new ItemStack(Items.SADDLE));
        }
        if (this.hasChest()) {
            this.spawnAtLocation(serverLevel, new ItemStack(Items.CHEST));
            for (int i = 0; i < this.inventory.getContainerSize(); i++) {
                ItemStack stack = this.inventory.getItem(i);
                if (!stack.isEmpty()) {
                    this.spawnAtLocation(serverLevel, stack);
                }
            }
        }
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction callback) {
        super.positionRider(passenger, callback);
        callback.accept(passenger, this.getX(), this.getY() + this.getBbHeight() + 0.3, this.getZ());
        if (passenger instanceof LivingEntity livingEntity) {
            livingEntity.yBodyRot = this.yBodyRot;
        }
    }

    // endregion

    // region SOUNDS

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.POLAR_BEAR_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return SoundEvents.RAVAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.POLAR_BEAR_DEATH;
    }

    @Override
    protected void playStepSound(@NotNull BlockPos pos, @NotNull BlockState state) {
        this.playSound(SoundEvents.RAVAGER_STEP, 0.3F, 0.7F);
    }

    // endregion

    // region ANGER

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide()) {
            this.updatePersistentAnger((ServerLevel) this.level(), true);
        }
    }

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

    // endregion

    // region GECKOLIB

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    private @NotNull PlayState predicate(final AnimationTest<Mammoth> event) {
        if (this.isBaby() || this.getTarget() != null) {
            event.setControllerSpeed((float)(0.52F * Math.max(0.1, this.getDeltaMovement().horizontalDistance() * 3.0)));
        }
        if (event.isMoving()) {
            if (this.isSprinting()) {
                event.controller().setAnimation(RUN);
            } else {
                event.controller().setAnimation(WALK);
            }
        } else {
            event.controller().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    private PlayState swingPredicate(final @NotNull AnimationTest<Mammoth> event) {
        if (this.swinging && event.controller().getPlayState() == PlayState.STOP) {
            event.controller().reset();
            
            event.controller().setAnimation(RawAnimation.begin().thenPlay("animation.sf_nba.mammoth.swing"));
            this.swinging = false;
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("controller", 5, this::predicate).setAnimationSpeed(1.0).setSoundKeyframeHandler(event -> {}));
        controllers.add(new AnimationController<>("swingController", 0, this::swingPredicate).setAnimationSpeed(0.225).setSoundKeyframeHandler(event -> {}));
    }

    // endregion

    static class MammothMeleeAttackGoal extends MeleeAttackGoal {
        public MammothMeleeAttackGoal(PathfinderMob pathfinderMob, double speedMultiplier, boolean followingTargetEvenIfNotSeen) {
            super(pathfinderMob, speedMultiplier, followingTargetEvenIfNotSeen);
        }

        @Override
        protected boolean canPerformAttack(LivingEntity target) {
            double reachSqr = Mth.square(this.mob.getBbWidth());
            double distSqr = this.mob.distanceToSqr(target);
            return distSqr <= reachSqr && this.isTimeToAttack();
        }
    }
}
