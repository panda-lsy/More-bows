package iDiamondhunter.morebows.entities;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import iDiamondhunter.morebows.ArrowType;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/** This entity is a custom arrow. A large portion of logic around these arrows is handled in the MoreBows class with SubscribeEvents. TODO Better documentation. Weird rotation issues seem to be happening with the fire and frost arrows, but not the ender arrows. */
public final class CustomArrow extends EntityArrow implements IEntityAdditionalSpawnData {

    private boolean crit = false;
    private byte inTicks = -1;
    private ArrowType type = ArrowType.BASE;

    // TODO I think I can't remove these constructors, but I'm not sure.
    public CustomArrow(World world) {
        super(world);
    }

    // TODO I think I can't remove these constructors, but I'm not sure.
    public CustomArrow(World world, double var1, double var2, double var3) {
        super(world, var1, var2, var3);
    }

    // TODO I think I can't remove these constructors, but I'm not sure.
    public CustomArrow(World world, EntityLivingBase living1, EntityLivingBase living2, float var1, float var2) {
        super(world, living1, living2, var1, var2);
    }

    // TODO I think I can't remove these constructors, but I'm not sure.
    public CustomArrow(World world, EntityLivingBase living, float var) {
        super(world, living, var);
    }

    /** A constructor that gives the CustomArrow an ArrowType. */
    public CustomArrow(World world, EntityLivingBase living, float var, ArrowType type) {
        this(world, living, var);
        this.type = type;
        /* if (type == ArrowType.FROST) { // I'm not sure it makes sense for a frost arrow to be on fire, but I don't think people care about it that much, and the frost bow is a bit under powered as is...
            this.extinguish();
        } */
    }

    /** This actually returns whether an arrow is critical or not. */
    public boolean getCrit() {
        return crit;
    }

    /** This may not accurately return whether an arrow is critical or not. This is to hide crit particle trails, when a custom arrow has a custom particle trail. */
    @Override
    public boolean getIsCritical() {
        if (type == ArrowType.FROST) {
            return false;
            /** Obviously, you're just a bad shot :D
             *
             *  This is an awful hack to prevent the vanilla crit particles from displaying.
             *  The vanilla code to display the arrow particle trail is buried deep inside onUpdate,
             *  and the only other options I have are to:
             *  - intercept the particles with packets,
             *  - intercept the particles with events (not feasible from what I can tell),
             *  - ASM it out,
             *  - or perform some ridiculous wrapping around the World to intercept the method to spawn particles.
             *
             *  Instead of doing that, I just prevent anything from ever knowing that it's crited,
             *  and instead I wrap around the event when the arrow attacks something. See onLivingAttackEvent() for the details,
             *  but the TLDR is that I cancel the attack and start a new one with the crit taken into account.
             *  This allows the entity to take the crit into account when deciding if it's damaged or not.
             *
             *  This is probably the lesser of these evils.
             */
        } else {
            return super.getIsCritical();
        }
    }

    /** Returns the ArrowType of this arrow. */
    public ArrowType getType() {
        return type;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (type == ArrowType.FROST) {
            /** Hack to determine when the arrow has hit the ground. inGround is a private field.
             *  Access transformers can be used for this, but they're are annoying to deal with and they aren't always safe.
             *  However, instead we can take advantage of the fact that arrowShake is always set to 7 after an arrow has hit the ground.
             *  inGround is used to store this information.
             */
            if (arrowShake == 7) {
                inTicks = 0;
                canBePickedUp = 0;
            }

            if (inTicks > -1) {
                inTicks++;

                if (inTicks <= 2) {
                    worldObj.spawnParticle("snowballpoof", posX, posY, posZ, 0.0D, 0.0D, 0.0D);
                }

                /*
                 * if (Block.isEqualTo(test, Blocks.water)) {
                 * this.worldObj.setBlockMetadataWithNotify(tempThingX, tempThingY, tempThingZ,
                 * Block.getIdFromBlock(Blocks.ice), 3); }
                 */

                if (inTicks <= 30) {
                    worldObj.spawnParticle("splash", posX, posY - 0.3D, posZ, 0.0D, 0.0D, 0.0D);
                }

                /** Responsible for adding snow layers on top the block the arrow hits, or "freezing" the water it's in by setting the block to ice. */
                if (inTicks == 64) {
                    // This is an approximation of MathHelper.floor_double. These casts to double are important, don't remove them!
                    final int arrX = (int) posX < (double) posX ? (int) posX - 1 : (int) posX;
                    final int arrY = (int) posY < (double) posY ? (int) posY - 1 : (int) posY;
                    final int arrZ = (int) posZ < (double) posZ ? (int) posZ - 1 : (int) posZ;
                    /* TODO Verify that this is the right block!
                     * Also, why does this sometimes set multiple blocks? It's the correct behavior of the original mod, but it's concerning... */
                    final Block inBlock = worldObj.getBlock(arrX, arrY, arrZ);

                    //if (Block.isEqualTo(this.field_145790_g, Blocks.snow)) {
                    /* TODO Possibly implement incrementing snow layers. */
                    if (Block.isEqualTo(inBlock, Blocks.air)) {
                        worldObj.setBlock(arrX, arrY, arrZ, Blocks.snow_layer);
                    }

                    if (Block.isEqualTo(inBlock, Blocks.water)) {
                        /* TODO Check if the earlier event or this one is the correct one.
                         * Also: bouncy arrow on ice, a bit like stone skimming? Could be cool. */
                        worldObj.setBlock(arrX, arrY, arrZ, Blocks.ice);
                    }

                    setDead();
                }
            } else if (crit) {
                for (int i = 0; i < 4; ++i) {
                    // No need for fancy server side particle handling
                    worldObj.spawnParticle("splash", posX + ((motionX * i) / 4.0D), posY + ((motionY * i) / 4.0D), posZ + ((motionZ * i) / 4.0D), -motionX, -motionY + 0.2D, -motionZ);
                }
            }
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        inTicks = tag.getByte("inTicks");
        crit = tag.getBoolean("crit");

        try {
            /** It's completely possible that the ArrowType enums might change in the future if needed. */
            type = ArrowType.valueOf(tag.getString("type"));
        } catch (final Exception e) {
            /** If we don't know what the arrow type is, just ignore the issue. */
            e.printStackTrace();
            type = ArrowType.NOT_CUSTOM;
        }
    }

    @Override
    public void readSpawnData(ByteBuf data) {
        crit = data.readBoolean();

        try {
            /** This should really not need error handling, as this data should always be right (the ordinal should be the same between compatible servers and clients), but mistakes happen sometimes. */
            type = ArrowType.values()[data.readInt()];
        } catch (final Exception e) {
            /** Although this is a very strange error, it's probably OK to ignore it. */
            e.printStackTrace();
            type = ArrowType.NOT_CUSTOM;
        }
    }

    @Override
    public void setIsCritical(boolean crit) {
        super.setIsCritical(this.crit = crit);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setByte("inTicks", inTicks);
        tag.setBoolean("crit", crit);
        /** Hopefully saving this by name instead of ordinal should help prevent issues when loading with any changes made to enum order. */
        tag.setString("type", type.name());
    }

    @Override
    public void writeSpawnData(ByteBuf data) {
        data.writeBoolean(crit);
        /** Sending the ordinal instead of the enum name should save network overhead. This should be consistent between compatible servers and clients, so it shouldn't have any issues. */
        data.writeInt(type.ordinal());
    }

}
