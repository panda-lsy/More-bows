package iDiamondhunter.morebows.entities;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/* This entity is a custom arrow. A large portion of logic around these arrows is handled in the MoreBows class with SubscribeEvents. TODO Better documentation. Re add custom arrow renderer for frost arrows. Weird rotation issues seem to be happening with the fire & frost arrows, but not the ender arrows. */
public class CustomArrow extends EntityArrow implements IEntityAdditionalSpawnData {

    public enum ArrowType {
        NOT_CUSTOM, BASE, FIRE, FROST;
    }

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

    // TODO I think I can't remove these constructors, but I'm not sure.
    public CustomArrow(World world, EntityLivingBase living, float var, ArrowType type) {
        this(world, living, var);
        this.type = type;
        /* if (type == ArrowType.FROST) { // I'm not sure it makes sense for a frost arrow to be on fire, but I don't think people care about it that much, and the frost bow is a bit under powered as is...
            this.extinguish();
        } */
    }

    public final boolean getCrit() {
        return crit;
    }

    /*public boolean isBurning() {
        if (type == ArrowType.FIRE) {
            return true;
        } else {
            return super.isBurning();
        }
    }*/

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

    public final ArrowType getType() {
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
                    final int arrX = MathHelper.floor_double(posX);
                    final int arrY = MathHelper.floor_double(posY);
                    final int arrZ = MathHelper.floor_double(posZ);
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

        // TODO Probably remove this
        // else if (crit && type == ArrowType.FIRE) {
        //    if (((ticksExisted + 1) % 2) == 0) {
        //    	  System.out.println("ticks existed " + this.ticksExisted);
        //        worldObj.spawnParticle("flame", posX /* + (motionX / 4.0D) */, posY /* + (motionY / 4.0D) */, posZ /* + (motionZ / 4.0D) */, (-motionX / ((8 * rand.nextGaussian() + 4))), ((-motionY + 0.2D) / ((2 * rand.nextGaussian() + 2))), (-motionZ / ((4 * rand.nextGaussian() + 4))));
        //    }
        //}
        //else if (crit && !worldObj.isRemote) { //TODO: Replace with sided proxy, make sure you're actually just supposed to spawn particles on server
        //    for (int i = 0; i < 4; ++i) {
        //        worldObj.spawnParticle("crit", posX + ((motionX * i) / 4.0D), posY + ((motionY * i) / 4.0D), posZ + ((motionZ * i) / 4.0D), -motionX, -motionY + 0.2D, -motionZ);
        //    }
        //}
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
    public void readSpawnData(ByteBuf additionalData) {
        crit = additionalData.readBoolean();

        try {
            /** This should really not need error handling, as this data should always be right (the ordinal should be the same between compatible servers and clients), but mistakes happen sometimes. */
            type = ArrowType.values()[additionalData.readInt()];
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
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeBoolean(crit);
        /** Sending the ordinal instead of the enum name should save network overhead. This should be consistent between compatible servers and clients, so it shouldn't have any issues. */
        buffer.writeInt(type.ordinal());
    }

    /*@Override
    public void extinguish() { // Might cause weird issues
        if (type != ArrowType.FIRE) {
            super.extinguish();
        }
    }*/

}
