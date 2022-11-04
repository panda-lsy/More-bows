package iDiamondhunter.morebows.entities;

import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_FROST;
import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_NOT_CUSTOM;

import iDiamondhunter.morebows.MoreBows;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

/**
 * This entity is a custom arrow.
 * A large portion of logic around these arrows
 * is handled in the MoreBows class with SubscribeEvents.
 * TODO much of this is out of date
 */
public final class CustomArrow extends ArrowEntity {

    /** If this is the first time this arrow has hit a block. */
    private boolean firstBlockHit = true;
    /**
     * The type of this arrow. In an ideal world, this would be final,
     * but this is not an ideal world. See readSpawnData.
     */
    //public byte type = ARROW_TYPE_NOT_CUSTOM;

    private static final TrackedData<Byte> trackedType = DataTracker.registerData(CustomArrow.class, TrackedDataHandlerRegistry.BYTE);

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        dataTracker.startTracking(trackedType, ARROW_TYPE_NOT_CUSTOM);
    }

    /**
     * Don't use this.
     * TODO I think I can't remove these constructors, but I'm not sure.
     *
     * @deprecated Don't use this
     * @param worldIn used in super construction
     */
    @Deprecated
    @SuppressWarnings("unused")
    public CustomArrow(EntityType<? extends CustomArrow> entityType, World worldIn) {
        super(entityType, worldIn);
    }

    /**
     * Don't use this.
     * TODO I think I can't remove these constructors, but I'm not sure.
     *
     * @deprecated Don't use this
     * @param worldIn used in super construction
     * @param x       used in super construction
     * @param y       used in super construction
     * @param z       used in super construction
     */
    @Deprecated
    @SuppressWarnings("unused")
    public CustomArrow(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    /**
     * Don't use this.
     * TODO I think I can't remove these constructors, but I'm not sure.
     *
     * @deprecated Don't use this
     * @param worldIn used in super construction
     * @param shooter used in super construction
     */
    @Deprecated
    @SuppressWarnings("unused")
    public CustomArrow(World worldIn, LivingEntity shooter) {
        super(worldIn, shooter);
    }

    /**
     * A constructor that gives the CustomArrow an ArrowType.
     *
     * @param worldIn used in super construction
     * @param shooter used in super construction
     * @param type    the type of arrow
     */
    public CustomArrow(World worldIn, LivingEntity shooter, byte type) {
        super(worldIn, shooter);
        dataTracker.set(trackedType, type);
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

    /** TODO review a bunch of this logic, some of it should be updated. */
    @Override
    public void tick() {
        super.tick();

        if (dataTracker.get(trackedType) == ARROW_TYPE_FROST) {
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
            } else if (super.isCritical()) {
                final Vec3d currentVelocity = getVelocity();
                final double motionX = currentVelocity.x;
                final double motionY = currentVelocity.y;
                final double motionZ = currentVelocity.z;

                for (int i = 0; i < 4; ++i) {
                    world.addParticle(ParticleTypes.SPLASH, getX() + ((motionX * i) / 4.0), getY() + ((motionY * i) / 4.0), getZ() + ((motionZ * i) / 4.0), -motionX, -motionY + 0.2, -motionZ);
                }
            }
        }
    }

    /**
     * Read the CustomArrow from NBT.
     *
     * @param compound the NBT tag
     */
    @Override
    public void readCustomDataFromNbt(NbtCompound compound) {
        super.readCustomDataFromNbt(compound);
        dataTracker.set(trackedType, compound.getByte("type"));
    }

    /**
     * Write the CustomArrow to NBT.
     *
     * @param compound the NBT tag
     */
    @Override
    public void writeCustomDataToNbt(NbtCompound compound) {
        super.writeCustomDataToNbt(compound);
        compound.putByte("type", dataTracker.get(trackedType));
    }

}
