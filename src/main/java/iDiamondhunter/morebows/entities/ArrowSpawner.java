package iDiamondhunter.morebows.entities;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/** This entity is responsible for storing and spawning the time delayed "bonus" arrows of the ender bow. TODO Cleanup, more documentation */
public final class ArrowSpawner extends Entity {

    private static final Random rand = new Random();
    private EntityArrow[] arrows;
    private byte existed = 0;
    private float shotVelocity;

    public ArrowSpawner(World world) {
        super(world);
        noClip = true;
        preventEntitySpawning = false;
        isImmuneToFire = true;
    }

    public ArrowSpawner(World world, double posX, double posY, double posZ, float shotVelocity, EntityArrow[] arrows) {
        this(world);
        this.shotVelocity = shotVelocity;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        /*if (arrows.length != 6) {
            // TODO Fix
            //System.out.println("ArrowSpawner expects 6 arrows, got " + arrows.length + " instead! It will not spawn any arrows until I implement better methods."); // debug
            setDead();
        }*/
        this.arrows = arrows;
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
        // Executed first, to prevent weird edge cases
        if (existed > 61) {
            setDead();
            return;
        }

        existed++;

        if (!worldObj.isRemote) {
            if (arrows == null) {
                //System.out.println("Bonus ender arrows lost! Will fix this soon..."); // debug
                setDead();
                return;
            }

            if (existed == 1) {
                // First arrow
                worldObj.spawnEntityInWorld(arrows[0]);
            }

            if (existed == 61) {
                // Second batch of arrows TODO Check if accurate to older versions of the mod
                worldObj.spawnEntityInWorld(arrows[1]);
                worldObj.playSoundAtEntity(arrows[1], "mob.endermen.portal", 0.5F, (1F / ((rand.nextFloat() * 0.4F) + 1F)) + (shotVelocity * 0.4F));
                worldObj.spawnEntityInWorld(arrows[2]);
                arrows[2].posY++;
                arrows[2].posX -= 1.25;
                arrows[2].posZ += 1.75;
                worldObj.playSoundAtEntity(arrows[2], "random.bow", 1.0F, (1.0F / ((rand.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));
                worldObj.spawnEntityInWorld(arrows[3]);
                arrows[3].posY += 1.45;
                arrows[3].posX -= 2.25;
                arrows[3].posZ -= 0.75;
                worldObj.playSoundAtEntity(arrows[3], "mob.endermen.portal", 0.25F, (1F / ((rand.nextFloat() * 0.4F) + 1F)) + (shotVelocity * 0.3F));
                worldObj.spawnEntityInWorld(arrows[4]);
                arrows[4].posY += 2;
                arrows[4].posX += 0.25;
                arrows[4].posZ += 2.5;
                worldObj.playSoundAtEntity(arrows[4], "random.bow", 1.0F, (1.0F / ((rand.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));
                worldObj.spawnEntityInWorld(arrows[5]);
                arrows[5].posY += 1.75;
                arrows[5].posX += 1.75;
                arrows[5].posZ += 1.5;
                worldObj.playSoundAtEntity(arrows[5], "mob.endermen.portal", 0.5F, (1F / ((rand.nextFloat() * 0.4F) + 1F)) + (shotVelocity * 0.4F));
            }
        }
    }

    /** This method reads the entity specific data from saved NBT data, including the stored arrows. TODO Better documentation */
    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
        // Variables related to this entity
        existed = tag.getByte("existed");
        shotVelocity = tag.getFloat("shotVelocity");
        // Variables related to the arrows stored by this entity
        final int arrowsArmount = tag.getInteger("arrowsAmount");
        arrows = new EntityArrow[arrowsArmount];

        /** An over engineered system to load an arbitrary amount of entities */
        for (int i = 0; i < arrowsArmount; i++) {
            final NBTTagCompound arrTag = tag.getCompoundTag("arrows").getCompoundTag("arrow" + i);
            final String arrType = tag.getCompoundTag("arrowsType").getString("arrow" + i);

            try {
                // Attempt to spawn in the specified entity while assuming that the data saved is completely valid
                arrows[i] = (EntityArrow) EntityList.createEntityByName(arrType, worldObj);
                arrows[i].readFromNBT(arrTag);
            } catch (final Exception e) {
                // This can fail in numerous ways, so it's probably for the best just to create a new arrow.
                e.printStackTrace();
                arrows[i] = new EntityArrow(worldObj);
            }
        }
    }

    /** This method saves the entity specific data to NBT data, including the stored arrows. TODO Better documentation */
    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {
        // Variables related to this entity
        tag.setByte("existed", existed);
        tag.setFloat("shotVelocity", shotVelocity);
        // Variables related to the arrows stored by this entity
        tag.setInteger("arrowsAmount", arrows.length);
        final NBTTagCompound arrowsTag = new NBTTagCompound();
        final NBTTagCompound arrowsType = new NBTTagCompound();

        /** An over engineered system to save an arbitrary amount of entities */
        for (int i = 0; i < arrows.length; i++) {
            final NBTTagCompound arrTag = new NBTTagCompound();
            arrows[i].writeToNBT(arrTag);
            arrowsTag.setTag("arrow" + i, arrTag);
            arrowsType.setString("arrow" + i, EntityList.getEntityString(arrows[i]));
        }

        // These values are then saved with these tags
        tag.setTag("arrows", arrowsTag);
        tag.setTag("arrowsType", arrowsType);
    }

}
