package iDiamondhunter.morebowsmod.entities;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
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
    private byte count = 0;
    //private EntityLivingBase creator;

    Random itemRand = new Random();
    private float power;

    public ArrowSpawner(World world) {
        super(world);
        noClip = true;
        preventEntitySpawning = false;
        isImmuneToFire = true;
    }

    public ArrowSpawner(World world, EntityLivingBase living /* TODO replace with something better */, float var, EntityArrow[] arrows) {
        this(world);
        //creator = living;
        power = var;
        posX = living.posX;
        posY = living.posY;
        posZ = living.posZ;
        this.arrows = arrows;
        worldObj.spawnEntityInWorld(arrows[0]);
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
    public boolean isEntityInvulnerable() {
        return true;
    }

    @Override
    public boolean isInvisible() {
        return true;
    }

    @Override
    public void onUpdate() {
        //iDiamondhunter.morebowsmod.MoreBows.modLog.info("cool ticks " + count);
        if (count == 60) {
            // Spawn arrows
            //iDiamondhunter.morebowsmod.MoreBows.modLog.info("ok cool");
            if (arrows != null) {
                //iDiamondhunter.morebowsmod.MoreBows.modLog.info(arrows.toString());
                worldObj.spawnEntityInWorld(arrows[1]);
                worldObj.playSoundAtEntity(arrows[1], bonusArrowSounds, 0.5F, (1F / ((itemRand.nextFloat() * 0.4F) + 1F)) + (power * 0.4F));
                worldObj.spawnEntityInWorld(arrows[2]);
                arrows[2].posY++;
                arrows[2].posX -= 1.25;
                arrows[2].posZ += 1.75;
                worldObj.playSoundAtEntity(arrows[2], defaultShotSound, 1.0F, (1.0F / ((itemRand.nextFloat() * 0.4F) + 1.2F)) + (power * 0.5F));
                worldObj.spawnEntityInWorld(arrows[3]);
                arrows[3].posY += 1.45;
                arrows[3].posX -= 2.25;
                arrows[3].posZ -= 0.75;
                worldObj.playSoundAtEntity(arrows[3], bonusArrowSounds, 0.25F, (1F / ((itemRand.nextFloat() * 0.4F) + 1F)) + (power * 0.3F));
                worldObj.spawnEntityInWorld(arrows[4]);
                arrows[4].posY += 2;
                arrows[4].posX += 0.25;
                arrows[4].posZ += 2.5;
                worldObj.playSoundAtEntity(arrows[4], defaultShotSound, 1.0F, (1.0F / ((itemRand.nextFloat() * 0.4F) + 1.2F)) + (power * 0.5F));
                worldObj.spawnEntityInWorld(arrows[5]);
                arrows[5].posY += 1.75;
                arrows[5].posX += 1.75;
                arrows[5].posZ += 1.5;
                worldObj.playSoundAtEntity(arrows[5], bonusArrowSounds, 0.5F, (1F / ((itemRand.nextFloat() * 0.4F) + 1F)) + (power * 0.4F));
            } else {
                // TODO This is probably causes issues due to proxy stuff. Figure it out when it's not the middle of the night.
                //iDiamondhunter.morebowsmod.MoreBows.modLog.info("bummer");
            }
        }

        if (count >= 60) {
            setDead();
        }

        count++;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
        count = tag.getByte("existed");
        //iDiamondhunter.morebowsmod.MoreBows.modLog.info("livin " + count);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {
        // TODO Figure out how to save arrows, they currently get lost when saving + reloading
        tag.setByte("existed", count);
        //iDiamondhunter.morebowsmod.MoreBows.modLog.info("writin " + count);
    }

}
