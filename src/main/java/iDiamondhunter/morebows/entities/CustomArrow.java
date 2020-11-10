package iDiamondhunter.morebows.entities;

import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_FROST;
import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_NOT_CUSTOM;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/** This entity is a custom arrow. A large portion of logic around these arrows is handled in the MoreBows class with SubscribeEvents. TODO Better documentation. Weird rotation issues seem to be happening with the fire and frost arrows, but not the ender arrows. */
public final class CustomArrow extends EntityArrow implements IEntityAdditionalSpawnData {

    private boolean crit = false;
    private byte inTicks = -1;
    private byte type = ARROW_TYPE_NOT_CUSTOM;

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
    public CustomArrow(World world, EntityLivingBase living, float var, byte type) {
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
        if (type == ARROW_TYPE_FROST) {
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
    public byte getType() {
        return type;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (type == ARROW_TYPE_FROST) {
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
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        inTicks = tag.getByte("inTicks");
        crit = tag.getBoolean("crit");
        type = tag.getByte("type");
    }

    @Override
    public void readSpawnData(ByteBuf data) {
        crit = data.readBoolean();
        type = data.readByte();
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
        tag.setByte("type", type);
    }

    @Override
    public void writeSpawnData(ByteBuf data) {
        data.writeBoolean(crit);
        data.writeByte(type);
    }

}
