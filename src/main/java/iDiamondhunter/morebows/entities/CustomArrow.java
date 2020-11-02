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

public class CustomArrow extends EntityArrow implements IEntityAdditionalSpawnData {

    public enum ArrowType {
        NOT_CUSTOM, BASE, FIRE, FROST;
    }
    //* TODO Attempt to merge FrostArrow with this if possible (need to create custom renderer for the "snowball") */
    private ArrowType type = ArrowType.BASE;
    private boolean crit = false;

    private byte inTicks = -1;

    // TODO I think I can't remove these constructors, but I'm not sure.
    public CustomArrow(World world) {
        super(world);
    }

    public CustomArrow(World world, double var1, double var2, double var3) {
        super(world, var1, var2, var3);
    }

    public CustomArrow(World world, EntityLivingBase living1, EntityLivingBase living2, float var1, float var2) {
        super(world, living1, living2, var1, var2);
    }

    public CustomArrow(World world, EntityLivingBase living, float var) {
        super(world, living, var);
    }

    public CustomArrow(World world, EntityLivingBase living, float var, ArrowType type) {
        this(world, living, var);

        if ((this.type = type) == ArrowType.NOT_CUSTOM /* This should never happen, NOT_CUSTOM is only used as a reference point. */) {
            setDead();
        }
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
            //System.out.println("getIsCritical false " + type);
            return false;
            /* Obviously, you're just a bad shot :D
             *
             * This is an awful hack to prevent the vanilla crit particles from displaying.
             * The vanilla code to display the arrow particle trail is buried deep inside onUpdate,
             * and the only other options I have are to:
             * - intercept the particles with packets,
             * - intercept the particles with events (not feasible from what I can tell),
             * - ASM it out,
             * - or perform some ridiculous wrapping around the World to intercept the method to spawn particles.
             *
             * Instead of doing that, I just prevent anything from ever knowing that it's crited,
             * and instead I wrap around the event when the arrow attacks something. See onLivingAttackEvent() for the details,
             * but the TLDR is that I cancel the attack and start a new one with the crit taken into account.
             * This allows the entity to take the crit into account when deciding if it's damaged or not.
             *
             * This is probably the lesser of these evils.
             */
        } else {
            //System.out.println("getIsCritical super " + type);
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
            // Hack to determine when the arrow has hit the ground. inGround is a private field.
            // Access transformers can be used for this, but they're are annoying to deal with and they aren't always safe.
            // However, instead we can take advantage of the fact that arrowShake is always set to 7 after an arrow has hit the ground.
            // inGround is used to store this information.
            if (arrowShake == 7) {
                inTicks = 0;
                canBePickedUp = 0;
            }

            if (inTicks > -1) {
                inTicks++;
                //System.out.println(getEntityId() + " in - inTicks " + inTicks);

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

                //if (this.ticksInGround >= 64 && this.ticksInGround <= 65) //Why was this the original logic?
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
                //System.out.println(getEntityId() + " very crit");
                for (int i = 0; i < 4; ++i) {
                    worldObj.spawnParticle("splash", posX + ((motionX * i) / 4.0D), posY + ((motionY * i) / 4.0D), posZ + ((motionZ * i) / 4.0D), -motionX, -motionY + 0.2D, -motionZ);
                    //MoreBows.trySpawnParticle(worldObj, this, "splash", ParicleDisplacement.TRAIL, 0);
                }

                //MoreBows.spawnParticle(this.worldObj, this, "splash", 4);
            }
        }

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
        //ArrowType.valueOf(arg0)
        //ArrowType[] allTypes = ArrowType.values();
        //type = allTypes[tag.getByte("type")];
        //type = ArrowType.valueOf(tag.getString("type"));

        if ((type = ArrowType.valueOf(tag.getString("type"))) == ArrowType.NOT_CUSTOM /* This should never happen, NOT_CUSTOM is only used as a reference point. */) {
            setDead();
        }

        crit = tag.getBoolean("crit");
        //System.out.println("load from nbt " + type.name());
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
        //int nameLength = additionalData.readInt();
        //byte[] typeBytes = additionalData.readBytes(nameLength).array();
        //String typeName = StringUtils.newStringIso8859_1(typeBytes);
        //type = ArrowType.valueOf(typeName);
        //System.out.println("readSpawnData - Recive " + typeBytes + " \nName is " + typeName);
        type = ArrowType.values()[additionalData.readInt()];
        crit = additionalData.readBoolean();
    }

    @Override
    public void setIsCritical(boolean crit) {
        /* This line of code brings a tear to my eye. It's glorious. */
        super.setIsCritical(this.crit = crit);
        //iDiamondhunter.morebows.MoreBows.modLog.info("setIsCritical " + crit);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setByte("inTicks", inTicks);
        tag.setString("type", type.name());
        tag.setBoolean("crit", crit);
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        //byte[] typeBytes = StringUtils.getBytesIso8859_1(type.name());
        //int nameLength = typeBytes.length;
        //buffer.writeInt(nameLength);
        //buffer.writeBytes(typeBytes);
        //System.out.println("writeSpawnData - Send " + typeBytes);
        buffer.writeInt(type.ordinal());
        buffer.writeBoolean(crit);
    }

}
