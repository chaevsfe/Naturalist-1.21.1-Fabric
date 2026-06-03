package com.starfish_studios.naturalist.common.entity;

import com.starfish_studios.naturalist.common.entity.core.NaturalistGeoEntity;
import com.starfish_studios.naturalist.core.registry.NaturalistTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.state.AnimationTest;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import org.jetbrains.annotations.Nullable;

public class Dragonfly extends PathfinderMob implements NaturalistGeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private static final EntityDataAccessor<Integer> VARIANT_ID = SynchedEntityData.defineId(Dragonfly.class, EntityDataSerializers.INT);
    @Nullable
    private BlockPos targetPosition;
    private int hoverTicks;

    protected static final RawAnimation FLY = RawAnimation.begin().thenLoop("animation.sf_nba.dragonfly.fly");

    public Dragonfly(@NotNull EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.setHoverTicks(30);
        this.setNoGravity(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 6.0);
    }

    public static boolean checkDragonflySpawnRules(EntityType<? extends Dragonfly> pType, ServerLevelAccessor pLevel, EntitySpawnReason pReason, BlockPos pPos, RandomSource pRandom) {
        return pLevel.getBlockState(pPos.below()).is(NaturalistTags.BlockTags.DRAGONFLIES_SPAWNABLE_ON);
    }

    public int getVariant() {
        return Mth.clamp(this.entityData.get(VARIANT_ID), 0, 2);
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT_ID, variant);
    }

    public int getHoverTicks() {
        return hoverTicks;
    }

    public void setHoverTicks(int hoverTicks) {
        this.hoverTicks = hoverTicks;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT_ID, 0);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput view) {
        super.addAdditionalSaveData(view);
        view.putInt("Variant", this.getVariant());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput view) {
        super.readAdditionalSaveData(view);
        this.setVariant(view.getIntOr("Variant", 0));
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason reason, @Nullable SpawnGroupData spawnData) {
        this.setVariant(level.getRandom().nextInt(3));
        return super.finalizeSpawn(level, difficulty, reason, spawnData);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entity) {
    }

    @Override
    protected void pushEntities() {
    }

    @Override
    public void tick() {
        super.tick();
        this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.6, 1.0));
    }

    @Override
    protected void customServerAiStep(ServerLevel serverLevel) {
        super.customServerAiStep(serverLevel);
        if (!(this.targetPosition == null || this.level().isEmptyBlock(this.targetPosition) && this.targetPosition.getY() > this.level().getMinY())) {
            this.targetPosition = null;
        }
        if (this.getHoverTicks() > 0) {
            this.setHoverTicks(Math.max(0, this.getHoverTicks() - 1));
        } else if (this.targetPosition == null || this.targetPosition.closerToCenterThan(this.position(), 2.0)) {
            this.targetPosition = new BlockPos(
                    (int)(this.getX() + this.random.nextInt(7) - this.random.nextInt(7)),
                    (int)(this.getY() + this.random.nextInt(6) - 2.0),
                    (int)(this.getZ() + this.random.nextInt(7) - this.random.nextInt(7))
            );
            this.setHoverTicks(15);
        }
        if (this.targetPosition != null && this.getHoverTicks() <= 0) {
            double x = this.targetPosition.getX() + 0.5 - this.getX();
            double y = this.targetPosition.getY() + 0.1 - this.getY();
            double z = this.targetPosition.getZ() + 0.5 - this.getZ();
            Vec3 vec3 = this.getDeltaMovement();
            Vec3 vec32 = vec3.add((Math.signum(x) * 0.5 - vec3.x) * 0.1f, (Math.signum(y) * 0.7f - vec3.y) * 0.1f, (Math.signum(z) * 0.5 - vec3.z) * 0.1f);
            this.setDeltaMovement(vec32);
            this.zza = 5.0f;
            float g = (float) (Mth.atan2(vec32.z, vec32.x) * Mth.RAD_TO_DEG) - 90.0f;
            float h = Mth.wrapDegrees(g - this.getYRot());
            this.setYRot(this.getYRot() + h);
        }
    }

    @Override
    protected Entity.@NotNull MovementEmission getMovementEmission() {
        return Entity.MovementEmission.EVENTS;
    }

    public boolean causeFallDamage(double fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    @Override
    protected void doWaterSplashEffect() {
    }

    @Override
    public float getWalkTargetValue(BlockPos blockPos) {
        if (this.level().getBlockState(blockPos).isAir() && this.level().isWaterAt(blockPos.below(2))) {
            return 10.0F;
        }
        return 0.0F;
    }

    @Override
    protected @NotNull InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(Items.CHORUS_FRUIT)) {
            this.playSound(SoundEvents.GENERIC_EAT.value());
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            if (!this.level().isClientSide()) {
                AreaEffectCloud areaEffectCloud = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
                areaEffectCloud.setOwner(this);
                areaEffectCloud.setCustomParticle(net.minecraft.core.particles.PowerParticleOption.create(ParticleTypes.DRAGON_BREATH, 1.0f));
                areaEffectCloud.setRadius(0.5f);
                areaEffectCloud.setDuration(200);
                areaEffectCloud.addEffect(new MobEffectInstance(MobEffects.INSTANT_DAMAGE, 1, 1));
                areaEffectCloud.setPos(this.getX(), this.getY(), this.getZ());
                this.level().addFreshEntity(areaEffectCloud);
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player , hand);
    }

    @Override
    public boolean isInvertedHealAndHarm() {
        return true;
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    protected PlayState predicate(final AnimationTest<Dragonfly> event) {
        event.setAnimation(FLY);
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(final AnimatableManager.@NotNull ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("controller", 0, this::predicate).setAnimationSpeed(0.225).setSoundKeyframeHandler(event -> {}));
    }

}
