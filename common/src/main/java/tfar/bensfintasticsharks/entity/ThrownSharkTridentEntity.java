package tfar.bensfintasticsharks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import tfar.bensfintasticsharks.init.ModEntityTypes;

public class ThrownSharkTridentEntity extends ThrownTrident {
    public ThrownSharkTridentEntity(EntityType<? extends ThrownTrident> $$0, Level $$1) {
        super($$0, $$1);
    }

    public ThrownSharkTridentEntity(Level $$0, LivingEntity $$1, ItemStack $$2) {
        super($$0, $$1, $$2);
    }

    @Override
    public EntityType<?> getType() {
        return ModEntityTypes.SHARK_TRIDENT;
    }

    @Override
    public boolean isChanneling() {
        return true;
    }

    /**
     * Vanilla ThrownTrident.onHitEntity verbatim except the hardcoded base damage
     * 8.0F → 11.0F (Ben: trident to 11; melee gets there via the attribute modifier
     * in SharkTridentItem). Uses getPickupItem() in place of the private tridentItem
     * field; dealtDamage is opened via AW/AT.
     */
    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        float f = 11.0F;
        if (entity instanceof LivingEntity livingentity) {
            f += EnchantmentHelper.getDamageBonus(this.getPickupItem(), livingentity.getMobType());
        }

        Entity owner = this.getOwner();
        DamageSource damagesource = this.damageSources().trident(this, owner == null ? this : owner);
        this.dealtDamage = true;
        SoundEvent soundevent = SoundEvents.TRIDENT_HIT;
        if (entity.hurt(damagesource, f)) {
            if (entity.getType() == EntityType.ENDERMAN) {
                return;
            }

            if (entity instanceof LivingEntity hitLiving) {
                if (owner instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects(hitLiving, owner);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity) owner, hitLiving);
                }

                this.doPostHurtEffects(hitLiving);
            }
        }

        this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
        float volume = 1.0F;
        if (this.level() instanceof ServerLevel && this.level().isThundering() && this.isChanneling()) {
            BlockPos blockpos = entity.blockPosition();
            if (this.level().canSeeSky(blockpos)) {
                LightningBolt lightningbolt = EntityType.LIGHTNING_BOLT.create(this.level());
                if (lightningbolt != null) {
                    lightningbolt.moveTo(Vec3.atBottomCenterOf(blockpos));
                    lightningbolt.setCause(owner instanceof ServerPlayer serverPlayer ? serverPlayer : null);
                    this.level().addFreshEntity(lightningbolt);
                    soundevent = SoundEvents.TRIDENT_THUNDER;
                    volume = 5.0F;
                }
            }
        }

        this.playSound(soundevent, volume, 1.0F);
    }
}
