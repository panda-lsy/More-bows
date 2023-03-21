package iDiamondhunter.morebows.entities;

import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_FROST;
import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_NOT_CUSTOM;
import static iDiamondhunter.morebows.MoreBows.frostArrowsShouldBeCold;
import static iDiamondhunter.morebows.MoreBows.oldFrostArrowRendering;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/** This entity is a custom arrow. A large portion of logic around these arrows is handled in the MoreBows class with SubscribeEvents. */
public final class CustomArrow extends EntityArrow implements IEntityAdditionalSpawnData {

    /** If this is the first time this arrow has hit a block. */
    private boolean firstBlockHit = true;
    /** How many ticks this arrow has been in the ground for. -1 is used to indicate that the arrow has not yet hit the ground. */
    private byte inTicks = -1;
    /** The type of this arrow. In an ideal world, this would be final, but this is not an ideal world. See readSpawnData. */
    public byte type = ARROW_TYPE_NOT_CUSTOM;

    /**
     * Don't use this.
     * TODO I think I can't remove these constructors, but I'm not sure.
     *
     * @param a used in super construction
     */
    public CustomArrow(World a) {
        super(a);
    }

    /**
     * Don't use this.
     * TODO I think I can't remove these constructors, but I'm not sure.
     *
     * @param a used in super construction
     * @param b used in super construction
     * @param c used in super construction
     * @param d used in super construction
     */
    public CustomArrow(World a, double b, double c, double d) {
        super(a, b, c, d);
    }

    /**
     * Don't use this.
     * TODO I think I can't remove these constructors, but I'm not sure.
     *
     * @param a used in super construction
     * @param b used in super construction
     * @param c used in super construction
     * @param d used in super construction
     * @param e used in super construction
     */
    public CustomArrow(World a, EntityLivingBase b, EntityLivingBase c, float d, float e) {
        super(a, b, c, d, e);
    }

    /**
     * Don't use this.
     * TODO I think I can't remove these constructors, but I'm not sure.
     *
     * @param a used in super construction
     * @param b used in super construction
     * @param c used in super construction
     */
    public CustomArrow(World a, EntityLivingBase b, float c) {
        super(a, b, c);
    }

    /**
     * A constructor that gives the CustomArrow an ArrowType.
     *
     * @param a    used in super construction
     * @param b    used in super construction
     * @param c    used in super construction
     * @param type the type of arrow
     */
    public CustomArrow(World a, EntityLivingBase b, float c, byte type) {
        super(a, b, c);
        this.type = type;
    }

    /** This may not accurately return whether an arrow is critical or not. This is to hide crit particle trails, when a custom arrow has a custom particle trail. */
    @Override
    @SideOnly(Side.CLIENT)
    public boolean getIsCritical() {
        return (type != ARROW_TYPE_FROST) && super.getIsCritical();
        /**
         * Obviously, you're just a bad shot :D
         * This is a hack to prevent the vanilla crit particles from displaying for frost arrows, so that they can have a custom particle trail.
         * The vanilla code to display the arrow particle trail is buried deep inside onUpdate,
         * but the easiest way to get around this is just to pretend that the arrow isn't critical.
         * I've made this only effect client-side logic, which means that things like critical hits still function.
         */
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (type == ARROW_TYPE_FROST) {
            if ((ticksExisted == 1) && frostArrowsShouldBeCold) {
                isImmuneToFire = true;
                extinguish();
            }

            /**
             * Hack to determine when the arrow has hit the ground. inGround is a private field.
             * Access transformers can be used for this, but they're annoying to deal with, and they aren't always safe.
             * However, instead we can take advantage of the fact that arrowShake is always set to 7 after an arrow has hit the ground.
             * inTicks is used to store this information.
             */
            if (arrowShake == 7) {
                inTicks = 0;
                canBePickedUp = 0;
            }

            if (inTicks > -1) {
                inTicks++;

                /** Shrinks the size of the frost arrow if it's in the ground and the mod is in old rendering mode */
                if (firstBlockHit && worldObj.isRemote && oldFrostArrowRendering) {
                    setSize(0.1F, 0.1F);
                    /** For some reason, this prevents the arrow from displaying in the wrong position after the size is set. TODO Figure out why this works. */
                    setPosition(posX, posY, posZ);
                    firstBlockHit = false;
                }

                if (inTicks <= 2) {
                    worldObj.spawnParticle("snowballpoof", posX, posY, posZ, 0.0D, 0.0D, 0.0D);
                }

                /**
                 * Behavior of older versions of More Bows
                 * TODO Possibly implement this
                 *
                 * <pre>
                 * if (Block.isEqualTo(test, Blocks.water)) {
                 *     this.worldObj.setBlockMetadataWithNotify(tempThingX, tempThingY, tempThingZ, Block.getIdFromBlock(Blocks.ice), 3);
                 * }
                 * </pre>
                 */
                if (inTicks <= 30) {
                    worldObj.spawnParticle("splash", posX, posY - 0.3D, posZ, 0.0D, 0.0D, 0.0D);
                }

                /** Responsible for adding snow layers on top the block the arrow hits, or "freezing" the water it's in by setting the block to ice. */
                if (inTicks == 64) {
                    final int floorPosX = MathHelper.floor_double(posX);
                    int floorPosY = MathHelper.floor_double(posY);
                    final int floorPosZ = MathHelper.floor_double(posZ);
                    Block inBlock = worldObj.getBlock(floorPosX, floorPosY, floorPosZ);

                    /*
                     * If this arrow is inside an air block, and there is a block underneath it with a solid surface, place a snow layer on top of that block.
                     * If this arrow is inside a snow layer, and a mob isn't colliding with the snow layer, increment the snow layer.
                     * If this arrow is inside water, replace the water with ice.
                     */

                    if (inBlock.isAir(worldObj, floorPosX, floorPosY, floorPosZ) && Blocks.snow_layer.canPlaceBlockAt(worldObj, floorPosX, floorPosY, floorPosZ)) {
                        worldObj.setBlock(floorPosX, floorPosY, floorPosZ, Blocks.snow_layer);
                    } else if (inBlock == Blocks.water) {
                        /*
                         * TODO Check if the earlier event or this one is the correct one.
                         * Also: bouncy arrow on ice, a bit like stone skimming? Could be cool.
                         */
                        worldObj.setBlock(floorPosX, floorPosY, floorPosZ, Blocks.ice);
                    } else if (inBlock == Blocks.snow_layer) {
                        int layerMeta = 0;
                        int currentSnowLevel = 7;

                        for (int upCount = 0; (upCount < 1024) && (inBlock == Blocks.snow_layer) && ((currentSnowLevel = (layerMeta = worldObj.getBlockMetadata(floorPosX, floorPosY, floorPosZ)) & 7) >= 7); upCount++) {
                            floorPosY++;
                            inBlock = worldObj.getBlock(floorPosX, floorPosY, floorPosZ);
                        }

                        if (currentSnowLevel <= 6) {
                            worldObj.setBlockMetadataWithNotify(floorPosX, floorPosY, floorPosZ, (currentSnowLevel + 1) | (layerMeta & -8), 2);
                        } else if (inBlock.isAir(worldObj, floorPosX, floorPosY, floorPosZ) && Blocks.snow_layer.canPlaceBlockAt(worldObj, floorPosX, floorPosY, floorPosZ)) {
                            worldObj.setBlock(floorPosX, floorPosY, floorPosZ, Blocks.snow_layer);
                        }
                    }
                }

                if (inTicks >= 64) {
                    setDead();
                }
            } else if (super.getIsCritical()) {
                for (int i = 0; i < 4; ++i) {
                    worldObj.spawnParticle("splash", posX + ((motionX * i) / 4.0D), posY + ((motionY * i) / 4.0D), posZ + ((motionZ * i) / 4.0D), -motionX, -motionY + 0.2D, -motionZ);
                }
            }
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        inTicks = tag.getByte("inTicks");
        type = tag.getByte("type");
    }

    public void readSpawnData(ByteBuf data) {
        inTicks = data.readByte();
        type = data.readByte();
        /** See NetHandlerPlayClient.handleSpawnObject (line 414). */
        final Entity shooter = worldObj.getEntityByID(data.readInt());

        if (shooter instanceof EntityLivingBase) {
            shootingEntity = shooter;
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setByte("inTicks", inTicks);
        tag.setByte("type", type);
    }

    public void writeSpawnData(ByteBuf data) {
        data.writeByte(inTicks);
        data.writeByte(type);
        data.writeInt(shootingEntity != null ? shootingEntity.getEntityId() : -1);
    }

}
