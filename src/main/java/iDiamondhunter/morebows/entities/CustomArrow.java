package iDiamondhunter.morebows.entities;

import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_ENDER;
import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_FIRE;
import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_FROST;
import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_NOT_CUSTOM;

import iDiamondhunter.morebows.MoreBows;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

/**
 * This entity is a custom arrow.
 * A large portion of logic around these arrows
 * is handled in the MoreBows class with SubscribeEvents.
 * TODO much of this is really out of date
 */
public final class CustomArrow extends PersistentProjectileEntity implements FlyingItemEntity {

    /** The type of this arrow. */
    public static final TrackedData<Byte> trackedType = DataTracker.registerData(CustomArrow.class, TrackedDataHandlerRegistry.BYTE);

    /** If this is the first time this arrow has hit a block. */
    private boolean firstBlockHit = true;

    /**
     * Don't use this.
     * TODO I think I can't remove these constructors, but I'm not sure.
     *
     * @param entityType the entity type
     * @param world      used in super construction
     * @deprecated Don't use this
     */
    public CustomArrow(EntityType<CustomArrow> entityType, World world) {
        super(entityType, world);
    }

    /**
     * Don't use this.
     * TODO I think I can't remove these constructors, but I'm not sure.
     *
     * @param world used in super construction
     * @param x     used in super construction
     * @param y     used in super construction
     * @param z     used in super construction
     * @deprecated Don't use this
     */
    public CustomArrow(World world, double x, double y, double z) {
        super(MoreBows.CUSTOM_ARROW, x, y, z, world);
    }

    /**
     * Don't use this.
     * TODO I think I can't remove these constructors, but I'm not sure.
     *
     * @param world used in super construction
     * @param owner used in super construction
     * @deprecated Don't use this
     */
    public CustomArrow(World world, LivingEntity owner) {
        super(MoreBows.CUSTOM_ARROW, owner, world);
    }

    /**
     * A constructor that gives the CustomArrow an ArrowType.
     *
     * @param world used in super construction
     * @param owner used in super construction
     * @param type  the type of arrow
     */
    public CustomArrow(World world, LivingEntity owner, byte type) {
        super(MoreBows.CUSTOM_ARROW, owner, world);
        dataTracker.set(trackedType, type);
    }

    /**
     * Returns an itemstack of {@link net.minecraft.item.Items#ARROW
     * the default arrow item}.
     *
     * @return an itemstack of {@link net.minecraft.item.Items#ARROW
     *         the default arrow item}
     */
    @Override
    protected ItemStack asItemStack() {
        return new ItemStack(Items.ARROW);
    }

    /**
     * Used to render frost arrows as snowballs.
     *
     * @return an itemstack of {@link net.minecraft.item.Items#SNOWBALL
     *         the default snowball item}
     */
    @Override
    public ItemStack getStack() {
        return new ItemStack(Items.SNOWBALL);
    }

    /** Initializes the data tracker. Used to track the arrow's type. */
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        dataTracker.startTracking(trackedType, ARROW_TYPE_NOT_CUSTOM);
    }

    /**
     * This may not accurately return whether an arrow is critical or not.
     * This is to hide crit particle trails,
     * when a custom arrow has a custom particle trail.
     *
     * @return true if critical (mostly)
     */
    @Override
    @Environment(EnvType.CLIENT)
    public boolean isCritical() {
        return (dataTracker.get(trackedType) != ARROW_TYPE_FROST) && super.isCritical();
        /*
         * Obviously, you're just a bad shot :D
         * This is a hack to prevent the vanilla crit particles from displaying
         * for frost arrows, so that they can have a custom particle trail.
         * The vanilla code to display the arrow particle trail is inside onUpdate,
         * but the easiest way to get around this is just to pretend
         * the arrow isn't critical. I've made this only effect client-side logic,
         * which means that things like critical hits still function.
         */
    }

    /**
     * Read the CustomArrow from NBT.
     *
     * @param nbt the NBT tag
     */
    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        dataTracker.set(trackedType, nbt.getByte("type"));
    }

    /** TODO review a bunch of this logic, some of it should be updated. */
    @Override
    public void tick() {
        final byte arrType = dataTracker.get(trackedType);

        if ((arrType == ARROW_TYPE_FROST) && super.isCritical()) {
            final Vec3d currentVelocity = getVelocity();
            final double motionX = currentVelocity.x;
            final double motionY = currentVelocity.y;
            final double motionZ = currentVelocity.z;

            for (int i = 0; i < 4; ++i) {
                world.addParticle(ParticleTypes.SPLASH, getX() + ((motionX * i) / 4.0), getY() + ((motionY * i) / 4.0), getZ() + ((motionZ * i) / 4.0), -motionX, -motionY + 0.2, -motionZ);
            }
        }

        super.tick();

        if (arrType == ARROW_TYPE_FROST) {
            if ((age == 1) && MoreBows.configGeneralInst.frostArrowsShouldBeCold) {
                // TODO Fix
                //isImmuneToFire = true;
                extinguish();
            }

            if (inGroundTime > 0) {
                if (inGroundTime == 1) {
                    pickupType = PickupPermission.DISALLOWED;
                }

                /*
                 * Shrinks the size of the frost arrow if it's in the ground,
                 * and the mod is in old rendering mode.
                 */
                if (firstBlockHit && world.isClient && MoreBows.configGeneralInst.oldFrostArrowRendering) {
                    // TODO fix
                    //setSize(0.1F, 0.1F);
                    firstBlockHit = false;
                }

                if (inGroundTime <= 3) {
                    world.addParticle(ParticleTypes.ITEM_SNOWBALL, getX(), getY(), getZ(), 0.0, 0.0, 0.0);
                }

                /*
                 * Behavior of older versions of More Bows
                 * TODO Possibly implement this
                 *
                 * <pre>
                 * if (Block.isEqualTo(test, Blocks.water)) {
                 *     this.worldObj.setBlockMetadataWithNotify(tempThingX, tempThingY, tempThingZ, Block.getIdFromBlock(Blocks.ice), 3);
                 * }
                 * </pre>
                 */
                if (inGroundTime <= 31) {
                    world.addParticle(ParticleTypes.SPLASH, getX(), getY(), getZ(), 0.0, 0.0, 0.0);
                }

                /*
                 * Responsible for adding snow layers on top the block the arrow hits,
                 * or "freezing" the water it's in by setting the block to ice.
                 */
                if (inGroundTime == 65) {
                    BlockPos inBlockPos = getBlockPos();
                    BlockState inBlockState = world.getBlockState(inBlockPos);
                    Block inBlock = inBlockState.getBlock();
                    final BlockState defaultSnowState = Blocks.SNOW.getDefaultState();

                    /*
                     * If this arrow is inside an air block,
                     * and a snow layer can be placed at this location,
                     * place a snow layer on top of that block.
                     *
                     * If this arrow is inside a snow layer,
                     * increment the snow layer.
                     *
                     * If this arrow is inside water, replace the water with ice.
                     */

                    if (inBlockState.isAir() && defaultSnowState.canPlaceAt(world, inBlockPos)) {
                        world.setBlockState(inBlockPos, defaultSnowState);
                        world.emitGameEvent(GameEvent.BLOCK_PLACE, inBlockPos, GameEvent.Emitter.of(this, defaultSnowState));
                    } else if (inBlock == Blocks.WATER) {
                        /*
                         * TODO Check if the earlier event or this one is the correct one.
                         * Consider using world.canBlockFreezeWater(inBlockPos).
                         * Also: bouncy arrow on ice, a bit like stone skimming? Could be cool.
                         */
                        final BlockState defaultIce = Blocks.ICE.getDefaultState();
                        world.setBlockState(inBlockPos, defaultIce);
                        world.emitGameEvent(GameEvent.BLOCK_CHANGE, inBlockPos, GameEvent.Emitter.of(this, defaultIce));
                    } else if (inBlock == Blocks.SNOW) {
                        int currentSnowLevel = 8;

                        for (int upCount = 0; (upCount < 1024) && (inBlock == Blocks.SNOW) && ((currentSnowLevel = inBlockState.<Integer>get(SnowBlock.LAYERS)) > 7); upCount++) {
                            inBlockPos = inBlockPos.up();
                            inBlockState = world.getBlockState(inBlockPos);
                            inBlock = inBlockState.getBlock();
                        }

                        if (currentSnowLevel < 8) {
                            final BlockState extraSnow = inBlockState.with(SnowBlock.LAYERS, currentSnowLevel + 1);
                            world.setBlockState(inBlockPos, extraSnow, 10);
                            world.emitGameEvent(GameEvent.BLOCK_CHANGE, inBlockPos, GameEvent.Emitter.of(this, extraSnow));
                        } else if (inBlockState.isAir() && defaultSnowState.canPlaceAt(world, inBlockPos)) {
                            world.setBlockState(inBlockPos, defaultSnowState);
                            world.emitGameEvent(GameEvent.BLOCK_PLACE, inBlockPos, GameEvent.Emitter.of(this, defaultSnowState));
                        }
                    }
                }

                if (inGroundTime >= 65) {
                    discard();
                }
            }
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        final byte arrType = dataTracker.get(trackedType);
        final Entity entityHit = entityHitResult.getEntity();

        if ((arrType == ARROW_TYPE_FROST) ) {
            if (MoreBows.configGeneralInst.frostArrowsShouldBeCold) {
                if (entityHit instanceof BlazeEntity) {
                    setDamage(getDamage() * 3);
                }

                entityHit.extinguish();
            }

            if (entityHit instanceof final LivingEntity entityHitLiving) {
                if (!MoreBows.configGeneralInst.oldFrostArrowMobSlowdown) {
                    entityHitLiving.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 300, 2));
                } else {
                    entityHitLiving.slowMovement(Blocks.COBWEB.getDefaultState(), new Vec3d(0.25, 0.05f, 0.25));
                }
            }
        }

        final DefaultParticleType part;
        final int amount;
        final double velocity;
        final boolean randDisp;

        switch (arrType) {
            case ARROW_TYPE_ENDER -> {
                part = ParticleTypes.PORTAL;
                amount = 3;
                randDisp = true;
                velocity = 1.0;
            }

            case ARROW_TYPE_FIRE -> {
                part = isOnFire() ? ParticleTypes.FLAME : ParticleTypes.SMOKE;
                amount = 5;
                randDisp = true;
                velocity = 0.05;
            }

            case ARROW_TYPE_FROST -> {
                part = ParticleTypes.SPLASH;
                amount = 1;
                randDisp = false;
                velocity = 0.01;
            }

            default -> {
                part = ParticleTypes.EXPLOSION;
                amount = 20;
                randDisp = true;
                velocity = 0.0;
            }
        }

        // TODO replace with client-side method
        for (int i = 0; i < amount; i++) {
            MoreBows.tryPart(world, entityHit, part, randDisp, velocity);
        }

        super.onEntityHit(entityHitResult);
    }

    /**
     * Write the CustomArrow to NBT.
     *
     * @param nbt the NBT tag
     */
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putByte("type", dataTracker.get(trackedType));
    }

}
