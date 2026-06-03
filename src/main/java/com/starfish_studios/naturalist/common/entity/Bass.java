package com.starfish_studios.naturalist.common.entity;


import com.starfish_studios.naturalist.core.registry.NaturalistRegistry;
import com.starfish_studios.naturalist.core.registry.NaturalistSoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.fish.AbstractSchoolingFish;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import com.starfish_studios.naturalist.common.entity.core.NaturalistGeoEntity;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.state.AnimationTest;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Bass extends AbstractSchoolingFish implements NaturalistGeoEntity {
    // region VARIABLES
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    protected static final RawAnimation SWIM = RawAnimation.begin().thenLoop("animation.sf_nba.bass.swim");
    protected static final RawAnimation FLOP = RawAnimation.begin().thenLoop("animation.sf_nba.bass.flop");
    // endregion

    public Bass(EntityType<? extends AbstractSchoolingFish> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public int getMaxSchoolSize() {
        return 5;
    }

    @Override
    public @NotNull ItemStack getBucketItemStack() {
        return new ItemStack(NaturalistRegistry.BASS_BUCKET.get());
    }

    @Override
    protected SoundEvent getAmbientSound() {
        // SALMON_AMBIENT is an empty sound event in modern MC (registered, no audio bound),
        // which makes the engine log "Unable to play empty soundEvent". Return null for
        // silence and no warning. Hurt/death/flop kept.
        return null;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SALMON_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return SoundEvents.SALMON_HURT;
    }

    @Override
    protected @NotNull SoundEvent getFlopSound() {
        return NaturalistSoundEvents.BASS_FLOP.get();
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    protected @NotNull PlayState predicate(final AnimationTest<Bass> event) {
        if (!this.isInWater()) {
            event.setAnimation(FLOP);
        } else {
            event.setAnimation(SWIM);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(final AnimatableManager.@NotNull ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("controller", 2, this::predicate).setAnimationSpeed(0.225).setSoundKeyframeHandler(event -> {}));
    }

}
