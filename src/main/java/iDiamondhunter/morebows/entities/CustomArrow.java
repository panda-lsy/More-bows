package iDiamondhunter.morebows.entities;

import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_ENDER;
import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_FIRE;
import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_FROST;
import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_NOT_CUSTOM;
import static iDiamondhunter.morebows.config.ConfigGeneral.frostArrowsShouldBeCold;
import static iDiamondhunter.morebows.config.ConfigGeneral.oldFrostArrowRendering;

import org.jetbrains.annotations.Nullable;

import iDiamondhunter.morebows.MoreBows;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This entity is a custom arrow.
 * A large portion of logic around these arrows
 * is handled in the MoreBows class with SubscribeEvents.
 * TODO much of this is out of date
 */
public final class CustomArrow extends EntityArrow implements IEntityAdditionalSpawnData {

    /** If this is the first time this arrow has hit a block. */
    private boolean firstBlockHit = true;
    /**
     * The type of this arrow. In an ideal world, this would be final,
     * but this is not an ideal world. See readSpawnData.
     */
    public byte type = ARROW_TYPE_NOT_CUSTOM;

    /**
     * Don't use this.
     * TODO I think I can't remove these constructors, but I'm not sure.
     *
     * @param worldIn used in super construction
     */
    @SuppressWarnings("unused")
    public CustomArrow(World worldIn) {
        super(worldIn);
    }

    /**
     * Don't use this.
     * TODO I think I can't remove these constructors, but I'm not sure.
     *
     * @param worldIn used in super construction
     * @param x       used in super construction
     * @param y       used in super construction
     * @param z       used in super construction
     */
    @SuppressWarnings("unused")
    public CustomArrow(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    /**
     * Don't use this.
     * TODO I think I can't remove these constructors, but I'm not sure.
     *
     * @param worldIn used in super construction
     * @param shooter used in super construction
     */
    @SuppressWarnings("unused")
    public CustomArrow(World worldIn, EntityLivingBase shooter) {
        super(worldIn, shooter);
    }

    /**
     * A constructor that gives the CustomArrow an ArrowType.
     *
     * @param worldIn used in super construction
     * @param shooter used in super construction
     * @param type    the type of arrow
     */
    public CustomArrow(World worldIn, EntityLivingBase shooter, byte type) {
        super(worldIn, shooter);
        this.type = type;
    }

    /**
     * Creates the particle effects when a custom arrow hits an entity.
     *
     * @param living the entity
     */
    @Override
    protected void arrowHit(EntityLivingBase living) {
        final EnumParticleTypes part;
        final int amount;
        final double velocity;
        final boolean randDisp;

        switch (type) {
        case ARROW_TYPE_ENDER:
            part = EnumParticleTypes.PORTAL;
            amount = 3;
            randDisp = true;
            velocity = 1.0;
            break;

        case ARROW_TYPE_FIRE:
            part = isBurning() ? EnumParticleTypes.FLAME : EnumParticleTypes.SMOKE_NORMAL;
            amount = 5;
            randDisp = true;
            velocity = 0.05;
            break;

        case ARROW_TYPE_FROST:
            part = EnumParticleTypes.WATER_SPLASH;
            amount = 1;
            randDisp = false;
            velocity = 0.01;
            break;

        default:
            part = EnumParticleTypes.SUSPENDED_DEPTH;
            amount = 20;
            randDisp = true;
            velocity = 0.0;
            break;
        }

        // TODO replace with client-side method
        for (int i = 0; i < amount; i++) {
            MoreBows.tryPart(world, living, part, randDisp, velocity);
        }
    }

    /**
     * Gets the (default) arrow stack.
     *
     * @return the arrow stack
     */
    @Override
    protected ItemStack getArrowStack() {
        return new ItemStack(Items.ARROW);
    }

    /**
     * This may not accurately return whether an arrow is critical or not.
     * This is to hide crit particle trails,
     * when a custom arrow has a custom particle trail.
     *
     * @return true if critical (mostly)
     */
    @Override
    @SideOnly(Side.CLIENT)
    public boolean getIsCritical() {
        return (type != ARROW_TYPE_FROST) && super.getIsCritical();
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
    public void onUpdate() {
        super.onUpdate();

        if (type == ARROW_TYPE_FROST) {
            if ((ticksExisted == 1) && frostArrowsShouldBeCold) {
                isImmuneToFire = true;
                extinguish();
            }

            if (timeInGround > 0) {
                if (timeInGround == 1) {
                    pickupStatus = PickupStatus.DISALLOWED;
                }

                /*
                 * Shrinks the size of the frost arrow if it's in the ground,
                 * and the mod is in old rendering mode.
                 */
                if (firstBlockHit && world.isRemote && oldFrostArrowRendering) {
                    setSize(0.1F, 0.1F);
                    firstBlockHit = false;
                }

                if (timeInGround <= 3) {
                    world.spawnParticle(EnumParticleTypes.SNOWBALL, posX, posY, posZ, 0.0, 0.0, 0.0);
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
                if (timeInGround <= 31) {
                    world.spawnParticle(EnumParticleTypes.WATER_SPLASH, posX, posY, posZ, 0.0, 0.0, 0.0);
                }

                /*
                 * Responsible for adding snow layers on top the block the arrow hits,
                 * or "freezing" the water it's in by setting the block to ice.
                 */
                if (timeInGround == 65) {
                    BlockPos inBlockPos = new BlockPos(this);
                    IBlockState inBlockState = world.getBlockState(inBlockPos);
                    Block inBlock = inBlockState.getBlock();

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

                    if (inBlock.isAir(inBlockState, world, inBlockPos) && Blocks.SNOW_LAYER.canPlaceBlockAt(world, inBlockPos)) {
                        world.setBlockState(inBlockPos, Blocks.SNOW_LAYER.getDefaultState());
                    } else if (inBlock == Blocks.WATER) {
                        /*
                         * TODO Check if the earlier event or this one is the correct one.
                         * Consider using world.canBlockFreezeWater(inBlockPos).
                         * Also: bouncy arrow on ice, a bit like stone skimming? Could be cool.
                         */
                        world.setBlockState(inBlockPos, Blocks.ICE.getDefaultState());
                    } else if (inBlock == Blocks.SNOW_LAYER) {
                        int currentSnowLevel = 8;

                        while ((inBlock == Blocks.SNOW_LAYER) && ((currentSnowLevel = inBlockState.<Integer>getValue(BlockSnow.LAYERS)) > 7)) {
                            inBlockPos = inBlockPos.up();
                            inBlockState = world.getBlockState(inBlockPos);
                            inBlock = inBlockState.getBlock();
                        }

                        if (currentSnowLevel < 8) {
                            final IBlockState extraSnow = inBlockState.withProperty(BlockSnow.LAYERS, currentSnowLevel + 1);
                            world.setBlockState(inBlockPos, extraSnow, 10);
                        } else if (inBlock.isAir(inBlockState, world, inBlockPos) && Blocks.SNOW_LAYER.canPlaceBlockAt(world, inBlockPos)) {
                            world.setBlockState(inBlockPos, Blocks.SNOW_LAYER.getDefaultState());
                        }
                    }
                }

                if (timeInGround >= 65) {
                    setDead();
                }
            } else if (super.getIsCritical()) {
                for (int i = 0; i < 4; ++i) {
                    world.spawnParticle(EnumParticleTypes.WATER_SPLASH, posX + ((motionX * i) / 4.0), posY + ((motionY * i) / 4.0), posZ + ((motionZ * i) / 4.0), -motionX, -motionY + 0.2, -motionZ);
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
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        type = compound.getByte("type");
    }

    /**
     * Used to set the arrow type and the shooting entity on the client side
     * when spawning a CustomArrow.
     */
    @Override
    public void readSpawnData(ByteBuf additionalData) {
        type = additionalData.readByte();
        /* See NetHandlerPlayClient.handleSpawnObject (line 470). */
        final @Nullable Entity shooter = world.getEntityByID(additionalData.readInt());

        if (shooter instanceof EntityLivingBase) {
            shootingEntity = shooter;
        }
    }

    /**
     * Write the CustomArrow to NBT.
     *
     * @param compound the NBT tag
     */
    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setByte("type", type);
    }

    /**
     * Used to send the arrow type and the shooting entity to the client
     * when spawning a CustomArrow.
     */
    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeByte(type);
        buffer.writeInt(shootingEntity != null ? shootingEntity.getEntityId() : -1);
    }

}
