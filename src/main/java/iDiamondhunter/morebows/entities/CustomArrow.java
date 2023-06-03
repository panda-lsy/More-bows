package iDiamondhunter.morebows.entities;

import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_ENDER;
import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_FIRE;
import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_FROST;
import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_NOT_CUSTOM;

import iDiamondhunter.morebows.MoreBows;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

/**
 * This entity is a custom arrow.
 * A large portion of logic around these arrows
 * is handled in the MoreBows class with SubscribeEvents.
 * TODO much of this is really out of date
 */
@OnlyIn(value = Dist.CLIENT, _interface = IRendersAsItem.class)
public final class CustomArrow extends AbstractArrowEntity implements IRendersAsItem {

    /** The type of this arrow. */
    public static final DataParameter<Byte> trackedType = EntityDataManager.defineId(CustomArrow.class, DataSerializers.BYTE);


    /** If this is the first time this arrow has hit a block. */
    private boolean firstBlockHit = true;

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

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
        super(MoreBows.CUSTOM_ARROW.get(), x, y, z, world);
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
        super(MoreBows.CUSTOM_ARROW.get(), owner, world);
    }

    /**
     * A constructor that gives the CustomArrow an ArrowType.
     *
     * @param world used in super construction
     * @param owner used in super construction
     * @param type  the type of arrow
     */
    public CustomArrow(World world, LivingEntity owner, byte type) {
        super(MoreBows.CUSTOM_ARROW.get(), owner, world);
        entityData.set(trackedType, type);
    }

    /**
     * Returns an itemstack of {@link net.minecraft.item.Items#ARROW
     * the default arrow item}.
     *
     * @return an itemstack of {@link net.minecraft.item.Items#ARROW
     *         the default arrow item}
     */
    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(Items.ARROW);
    }

    /**
     * Used to render frost arrows as snowballs.
     *
     * @return an itemstack of {@link net.minecraft.item.Items#SNOWBALL
     *         the default snowball item}
     */
    @Override
    public ItemStack getItem() {
        return new ItemStack(Items.SNOWBALL);
    }

    /** Initializes the data tracker. Used to track the arrow's type. */
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(trackedType, ARROW_TYPE_NOT_CUSTOM);
    }

    /**
     * This may not accurately return whether an arrow is critical or not.
     * This is to hide crit particle trails,
     * when a custom arrow has a custom particle trail.
     *
     * @return true if critical (mostly)
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isCritArrow() {
        return (entityData.get(trackedType) != ARROW_TYPE_FROST) && super.isCritArrow();
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
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        entityData.set(trackedType, nbt.getByte("type"));
    }

    /** TODO review a bunch of this logic, some of it should be updated. */
    @Override
    public void tick() {
        final byte arrType = entityData.get(trackedType);

        if ((arrType == ARROW_TYPE_FROST) && super.isCritArrow()) {
            final Vector3d currentVelocity = getDeltaMovement();
            final double motionX = currentVelocity.x;
            final double motionY = currentVelocity.y;
            final double motionZ = currentVelocity.z;

            for (int i = 0; i < 4; ++i) {
                level.addParticle(ParticleTypes.SPLASH, getX() + ((motionX * i) / 4.0), getY() + ((motionY * i) / 4.0), getZ() + ((motionZ * i) / 4.0), -motionX, -motionY + 0.2, -motionZ);
            }
        }

        super.tick();

        if (arrType == ARROW_TYPE_FROST) {
            if ((tickCount == 1) && MoreBows.configGeneralInst.frostArrowsShouldBeCold) {
                // TODO Fix
                //isImmuneToFire = true;
                clearFire();
            }

            if (inGroundTime > 0) {
                if (inGroundTime == 1) {
                    pickup = PickupStatus.DISALLOWED;
                }

                /*
                 * Shrinks the size of the frost arrow if it's in the ground,
                 * and the mod is in old rendering mode.
                 */
                if (firstBlockHit && level.isClientSide && MoreBows.configGeneralInst.oldFrostArrowRendering) {
                    // TODO fix
                    //setSize(0.1F, 0.1F);
                    firstBlockHit = false;
                }

                if (inGroundTime <= 3) {
                    level.addParticle(ParticleTypes.ITEM_SNOWBALL, getX(), getY(), getZ(), 0.0, 0.0, 0.0);
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
                    level.addParticle(ParticleTypes.SPLASH, getX(), getY(), getZ(), 0.0, 0.0, 0.0);
                }

                /*
                 * Responsible for adding snow layers on top the block the arrow hits,
                 * or "freezing" the water it's in by setting the block to ice.
                 */
                if (inGroundTime == 65) {
                    BlockPos inBlockPos = blockPosition();
                    BlockState inBlockState = level.getBlockState(inBlockPos);
                    Block inBlock = inBlockState.getBlock();
                    final BlockState defaultSnowState = Blocks.SNOW.defaultBlockState();

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

                    //if (inBlockState.isAir() && defaultSnowState.canPlaceAt(level, inBlockPos)) {
                    if (inBlockState.isAir() && defaultSnowState.canSurvive(level, inBlockPos)) {
                        //level.setBlockState(inBlockPos, defaultSnowState);
                        level.setBlockAndUpdate(inBlockPos, defaultSnowState);
                    } else if (inBlock == Blocks.WATER) {
                        /*
                         * TODO Check if the earlier event or this one is the correct one.
                         * Consider using world.canBlockFreezeWater(inBlockPos).
                         * Also: bouncy arrow on ice, a bit like stone skimming? Could be cool.
                         */
                        final BlockState defaultIce = Blocks.ICE.defaultBlockState();
                        //world.setBlockState(inBlockPos, defaultIce);
                        level.setBlockAndUpdate(inBlockPos, defaultIce);
                    } else if (inBlock == Blocks.SNOW) {
                        int currentSnowLevel = 8;

                        for (int upCount = 0; (upCount < 1024) && (inBlock == Blocks.SNOW) && ((currentSnowLevel = inBlockState.<Integer>getValue(SnowBlock.LAYERS)) > 7); upCount++) {
                            inBlockPos = inBlockPos.above();
                            inBlockState = level.getBlockState(inBlockPos);
                            inBlock = inBlockState.getBlock();
                        }

                        if (currentSnowLevel < 8) {
                            //final BlockState extraSnow = inBlockState.with(SnowBlock.LAYERS, currentSnowLevel + 1);
                            final BlockState extraSnow = inBlockState.setValue(SnowBlock.LAYERS, currentSnowLevel + 1);
                            //level.setBlockState(inBlockPos, extraSnow, 10);
                            level.setBlock(inBlockPos, extraSnow, 10);
                            //} else if (inBlockState.isAir() && defaultSnowState.canPlaceAt(level, inBlockPos)) {
                        } else if (inBlockState.isAir() && defaultSnowState.canSurvive(level, inBlockPos)) {
                            //level.setBlockState(inBlockPos, defaultSnowState);
                            level.setBlockAndUpdate(inBlockPos, defaultSnowState);
                        }
                    }
                }

                if (inGroundTime >= 65) {
                    remove();
                }
            }
        }
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult entityHitResult) {
        final byte arrType = entityData.get(trackedType);
        final Entity entityHit = entityHitResult.getEntity();

        if (arrType == ARROW_TYPE_FROST) {
            if (MoreBows.configGeneralInst.frostArrowsShouldBeCold) {
                if (entityHit instanceof BlazeEntity) {
                    setBaseDamage(getBaseDamage() * 3.0);
                }

                entityHit.clearFire();
            }

            if (entityHit instanceof LivingEntity) {
                final LivingEntity entityHitLiving = (LivingEntity) entityHit;

                if (!MoreBows.configGeneralInst.oldFrostArrowMobSlowdown) {
                    entityHitLiving.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 300, 2));
                } else {
                    entityHitLiving.makeStuckInBlock(Blocks.COBWEB.defaultBlockState(), new Vector3d(0.25, 0.05, 0.25));
                }
            }
        }

        final BasicParticleType part;
        final int amount;
        final double velocity;
        final boolean randDisp;

        switch (arrType) {
        case ARROW_TYPE_ENDER:
            part = ParticleTypes.PORTAL;
            amount = 3;
            randDisp = true;
            velocity = 1.0;
            break;

        case ARROW_TYPE_FIRE:
            part = isOnFire() ? ParticleTypes.FLAME : ParticleTypes.SMOKE;
            amount = 5;
            randDisp = true;
            velocity = 0.05;
            break;

        case ARROW_TYPE_FROST:
            part = ParticleTypes.SPLASH;
            amount = 1;
            randDisp = false;
            velocity = 0.01;
            break;

        default:
            part = ParticleTypes.EXPLOSION;
            amount = 20;
            randDisp = true;
            velocity = 0.0;
            break;
        }

        // TODO replace with client-side method
        for (int i = 0; i < amount; i++) {
            MoreBows.tryPart(level, entityHit, part, randDisp, velocity);
        }

        super.onHitEntity(entityHitResult);
    }

    /**
     * Write the CustomArrow to NBT.
     *
     * @param nbt the NBT tag
     */
    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putByte("type", entityData.get(trackedType));
    }

}
