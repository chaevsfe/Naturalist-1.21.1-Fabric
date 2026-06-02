package com.starfish_studios.naturalist.common.entity.core;

import java.util.Optional;
import java.util.UUID;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.scores.PlayerTeam;
import org.jetbrains.annotations.Nullable;

public abstract class TamableNaturalistAnimal extends NaturalistAnimal implements OwnableEntity {
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID;
    protected static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> DATA_OWNERUUID_ID;
    private boolean orderedToSit;

    protected TamableNaturalistAnimal(EntityType<? extends TamableNaturalistAnimal> entityType, Level level) {
        super(entityType, level);
        this.reassessTameGoals();
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_FLAGS_ID, (byte)0);
        builder.define(DATA_OWNERUUID_ID, Optional.empty());
    }

    protected void addAdditionalSaveData(ValueOutput view) {
        super.addAdditionalSaveData(view);
        EntityReference<LivingEntity> ownerRef = this.getOwnerReference();
        if (ownerRef != null) {
            EntityReference.store(ownerRef, view, "Owner");
        }
        view.putBoolean("Sitting", this.orderedToSit);
    }

    protected void readAdditionalSaveData(ValueInput view) {
        super.readAdditionalSaveData(view);
        EntityReference<LivingEntity> ownerRef = EntityReference.readWithOldOwnerConversion(view, "Owner", this.level());
        if (ownerRef != null) {
            this.setOwnerReference(ownerRef);
            this.setTame(true);
        }
        this.orderedToSit = view.getBooleanOr("Sitting", false);
        this.setInSittingPose(this.orderedToSit);
    }

    public boolean canBeLeashed() {
        return !this.isLeashed();
    }

    protected void spawnTamingParticles(boolean tamed) {
        ParticleOptions particleOptions = ParticleTypes.HEART;
        if (!tamed) {
            particleOptions = ParticleTypes.SMOKE;
        }

        for(int i = 0; i < 7; ++i) {
            double d = this.random.nextGaussian() * 0.02;
            double e = this.random.nextGaussian() * 0.02;
            double f = this.random.nextGaussian() * 0.02;
            this.level().addParticle(particleOptions, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), d, e, f);
        }

    }

    public void handleEntityEvent(byte id) {
        if (id == 7) {
            this.spawnTamingParticles(true);
        } else if (id == 6) {
            this.spawnTamingParticles(false);
        } else {
            super.handleEntityEvent(id);
        }

    }

    public boolean isTame() {
        return ((Byte)this.entityData.get(DATA_FLAGS_ID) & 4) != 0;
    }

    public void setTame(boolean tamed) {
        byte b = (Byte)this.entityData.get(DATA_FLAGS_ID);
        if (tamed) {
            this.entityData.set(DATA_FLAGS_ID, (byte)(b | 4));
        } else {
            this.entityData.set(DATA_FLAGS_ID, (byte)(b & -5));
        }

        this.reassessTameGoals();
    }

    protected void reassessTameGoals() {
    }

    public boolean isInSittingPose() {
        return ((Byte)this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
    }

    public void setInSittingPose(boolean sitting) {
        byte b = (Byte)this.entityData.get(DATA_FLAGS_ID);
        if (sitting) {
            this.entityData.set(DATA_FLAGS_ID, (byte)(b | 1));
        } else {
            this.entityData.set(DATA_FLAGS_ID, (byte)(b & -2));
        }

    }

    @Nullable
    public EntityReference<LivingEntity> getOwnerReference() {
        return this.entityData.get(DATA_OWNERUUID_ID).orElse(null);
    }

    public void setOwnerReference(@Nullable EntityReference<LivingEntity> ref) {
        this.entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable(ref));
    }

    @Nullable
    public UUID getOwnerUUID() {
        EntityReference<LivingEntity> ref = this.getOwnerReference();
        return ref != null ? ref.getUUID() : null;
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        if (uuid != null) {
            this.setOwnerReference(EntityReference.of(uuid));
        } else {
            this.setOwnerReference(null);
        }
    }

    @Nullable
    public LivingEntity getOwner() {
        EntityReference<LivingEntity> ref = this.getOwnerReference();
        return ref != null ? ref.getEntity(this.level(), LivingEntity.class) : null;
    }

    public void tame(Player player) {
        this.setTame(true);
        this.setOwnerReference(EntityReference.of(player));
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayer)player, this);
        }

    }

    public boolean canAttack(LivingEntity target) {
        return this.isOwnedBy(target) ? false : super.canAttack(target);
    }

    public boolean isOwnedBy(LivingEntity entity) {
        return entity == this.getOwner();
    }

    public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
        return true;
    }

    public PlayerTeam getTeam() {
        if (this.isTame()) {
            LivingEntity livingEntity = this.getOwner();
            if (livingEntity != null) {
                return livingEntity.getTeam();
            }
        }

        return super.getTeam();
    }

    // isAlliedTo(Entity) is now final in Entity class; override Team version instead
    @Override
    public boolean isAlliedTo(@org.jetbrains.annotations.NotNull net.minecraft.world.scores.Team team) {
        if (this.isTame()) {
            LivingEntity livingEntity = this.getOwner();
            if (livingEntity != null) {
                return livingEntity.isAlliedTo(team);
            }
        }
        return super.isAlliedTo(team);
    }

    public void die(DamageSource damageSource) {
        // Death messages handled by vanilla
        super.die(damageSource);
    }

    public boolean isOrderedToSit() {
        return this.orderedToSit;
    }

    public void setOrderedToSit(boolean orderedToSit) {
        this.orderedToSit = orderedToSit;
    }

    static {
        DATA_FLAGS_ID = SynchedEntityData.defineId(TamableNaturalistAnimal.class, EntityDataSerializers.BYTE);
        DATA_OWNERUUID_ID = SynchedEntityData.defineId(TamableNaturalistAnimal.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);
    }
}
