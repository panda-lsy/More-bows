package iDiamondhunter.morebowsmod.entities;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ArrowSpawner extends Entity {

    @Deprecated
    private final static String bonusArrowSounds = "mob.endermen.portal";
    @Deprecated
    private final static String defaultShotSound = "random.bow";
    private EntityArrow[] arrows;
    private byte existed = 0;
    //private EntityLivingBase creator;

    Random itemRand = new Random();
    private float shotVelocity;

    public ArrowSpawner(World world) {
        super(world);
        noClip = true;
        preventEntitySpawning = false;
        isImmuneToFire = true;
        //this.setDead();
    }

    public ArrowSpawner(World world, EntityLivingBase living /* TODO replace with something better */, float shotVelocity, EntityArrow[] arrows) {
        this(world);
        //creator = living;
        this.shotVelocity = shotVelocity;
        posX = living.posX;
        posY = living.posY;
        posZ = living.posZ;

        if (arrows.length != 6) {
            iDiamondhunter.morebowsmod.MoreBows.modLog.error("ArrowSpawner expects 6 arrows, got " + arrows.length + " instead! It will not spawn any arrows until I implement better methods.");
            setDead();
        }

        this.arrows = arrows;
    }

    @Override
    public boolean canAttackWithItem() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    protected void entityInit() {
        /*noClip = true;
        preventEntitySpawning = false;
        isImmuneToFire = true;*/
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getShadowSize() {
        return 0.0F;
    }

    @Override
    public boolean hitByEntity(Entity var) {
        return true;
    }

    @Override
    public boolean isBurning() {
        return false;
    }

    @Override
    public boolean canRenderOnFire() {
        return false;
    }

    @Override
    public boolean isEntityInvulnerable() {
        return true;
    }

    @Override
    public boolean isInvisible() {
        return true;
    }

    @Override
    public void onUpdate() {
        // Executed first, to prevent weird edge cases
        if (existed > 61) {
            setDead();
            return;
        }

        existed++;

        if (!worldObj.isRemote) {
            if (arrows == null) {
                iDiamondhunter.morebowsmod.MoreBows.modLog.info("Bonus ender arrows lost! Will fix this soon..."); // debug
                setDead();
                return;
            }

            if (existed == 1) {
                // First arrow
                worldObj.spawnEntityInWorld(arrows[0]);
            }

            if (existed == 61) {
                // Second batch of arrows
                worldObj.spawnEntityInWorld(arrows[1]);
                worldObj.playSoundAtEntity(arrows[1], bonusArrowSounds, 0.5F, (1F / ((itemRand.nextFloat() * 0.4F) + 1F)) + (shotVelocity * 0.4F));
                worldObj.spawnEntityInWorld(arrows[2]);
                arrows[2].posY++;
                arrows[2].posX -= 1.25;
                arrows[2].posZ += 1.75;
                worldObj.playSoundAtEntity(arrows[2], defaultShotSound, 1.0F, (1.0F / ((itemRand.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));
                worldObj.spawnEntityInWorld(arrows[3]);
                arrows[3].posY += 1.45;
                arrows[3].posX -= 2.25;
                arrows[3].posZ -= 0.75;
                worldObj.playSoundAtEntity(arrows[3], bonusArrowSounds, 0.25F, (1F / ((itemRand.nextFloat() * 0.4F) + 1F)) + (shotVelocity * 0.3F));
                worldObj.spawnEntityInWorld(arrows[4]);
                arrows[4].posY += 2;
                arrows[4].posX += 0.25;
                arrows[4].posZ += 2.5;
                worldObj.playSoundAtEntity(arrows[4], defaultShotSound, 1.0F, (1.0F / ((itemRand.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));
                worldObj.spawnEntityInWorld(arrows[5]);
                arrows[5].posY += 1.75;
                arrows[5].posX += 1.75;
                arrows[5].posZ += 1.5;
                worldObj.playSoundAtEntity(arrows[5], bonusArrowSounds, 0.5F, (1F / ((itemRand.nextFloat() * 0.4F) + 1F)) + (shotVelocity * 0.4F));
            }
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
        //iDiamondhunter.morebowsmod.MoreBows.modLog.info("reading"); //debug
        existed = tag.getByte("existed");
        shotVelocity = tag.getFloat("shotVelocity");
        final int arrowsArmount = tag.getInteger("arrowsAmount");
        arrows = new EntityArrow[arrowsArmount];

        /** An over engineered system to load an arbitrary amount of entities */
        for (int i = 0; i < arrowsArmount; i++) {
            final NBTTagCompound arrTag = tag.getCompoundTag("arrows").getCompoundTag("arrow" + i);
            final String arrType = tag.getCompoundTag("arrowsType").getString("arrow" + i);
            //iDiamondhunter.morebowsmod.MoreBows.modLog.info("arrow[" + i + "] " + arrTag.toString() + " of type " + arrType); //debug
            arrows[i] = (EntityArrow) EntityList.createEntityByName(arrType, worldObj);
            //iDiamondhunter.morebowsmod.MoreBows.modLog.info("arrow[" + i + "] " + arrows[i].toString()); //debug
            arrows[i].readFromNBT(arrTag);
        }

        //iDiamondhunter.morebowsmod.MoreBows.modLog.info("arrows " + arrows.toString() + " zero " + arrows[0].toString() + " one " + arrows[1].toString()); //debug
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {
        final NBTTagCompound arrowsTag = new NBTTagCompound();
        final NBTTagCompound arrowsTypeTag = new NBTTagCompound();

        //iDiamondhunter.morebowsmod.MoreBows.modLog.info("writing"); //debug

        /** An over engineered system to save an arbitrary amount of entities */
        for (int i = 0; i < arrows.length; i++) {
            final NBTTagCompound arrTag = new NBTTagCompound();
            arrows[i].writeToNBT(arrTag);
            //iDiamondhunter.morebowsmod.MoreBows.modLog.info("arrow[" + i + "] " + arrTag.toString() + " getEntityString " + EntityList.getEntityString(arrows[i]) + " id " + EntityList.getEntityID(arrows[i])); //debug
            arrowsTag.setTag("arrow" + i, arrTag);
            arrowsTypeTag.setString("arrow" + i, EntityList.getEntityString(arrows[i]));
        }

        //iDiamondhunter.morebowsmod.MoreBows.modLog.info("arrows " + arrowsTag.toString()); //debug
        tag.setByte("existed", existed);
        tag.setFloat("shotVelocity", shotVelocity);
        tag.setTag("arrows", arrowsTag);
        tag.setTag("arrowsType", arrowsTypeTag);
        tag.setInteger("arrowsAmount", arrows.length);
    }

}
