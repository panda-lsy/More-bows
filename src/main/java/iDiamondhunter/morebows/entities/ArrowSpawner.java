package iDiamondhunter.morebows.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/** This entity is responsible for storing and spawning the time delayed "bonus" arrows of the ender bow. */
public final class ArrowSpawner extends Entity {

    private EntityArrow[] arrows;
    private float shotVelocity;

    /**
     * @param world the world to spawn in
     */
    public ArrowSpawner(World world) {
        super(world);
        noClip = true;
        preventEntitySpawning = false;
        isImmuneToFire = true;
    }

    /**
     * This entity is responsible for storing and spawning the time delayed "bonus" arrows of the ender bow.
     *
     * @param world        the world to spawn in
     * @param posX         the x position.
     * @param posY         the y position.
     * @param posZ         the z position.
     * @param shotVelocity the velocity of the stored arrows.
     * @param arrows       the stored arrows.
     */
    public ArrowSpawner(World world, double posX, double posY, double posZ, float shotVelocity, EntityArrow[] arrows) {
        this(world);
        this.shotVelocity = shotVelocity;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        /**
         * TODO Better error handling
         *
         * <pre>
         * if (arrows.length != 6) {
         *     // System.out.println("ArrowSpawner expects 6 arrows, got " + arrows.length + " instead! It will not spawn any arrows until I implement better methods."); // debug
         *     setDead();
         * }
         * </pre>
         */
        this.arrows = arrows;
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    protected void entityInit() {
        // This space left intentionally blank
    }

    @Override
    public boolean isEntityInvulnerable() {
        return true;
    }

    @Override
    public void onUpdate() {
        // Executed first, to prevent weird edge cases
        if (ticksExisted > 61) {
            setDead();
            return;
        }

        if (!worldObj.isRemote) {
            if (ticksExisted == 1) {
                /** Check that the arrows exist before accessing them. */
                if ((arrows == null) || (arrows.length != 6)) {
                    setDead();
                    return;
                }

                // First arrow
                worldObj.spawnEntityInWorld(arrows[0]);
            }

            if (ticksExisted == 61) {
                /** Check that the arrows exist before accessing them. */
                if ((arrows == null) || (arrows.length != 6)) {
                    setDead();
                    return;
                }

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

    /** This method reads the entity specific data from saved NBT data, including the stored arrows. */
    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
        shotVelocity = tag.getFloat("shotVelocity");
        arrows = new EntityArrow[tag.getInteger("arrowsAmount")];

        /** An over engineered system to load an arbitrary amount of entities. */
        for (int i = 0; i < tag.getInteger("arrowsAmount"); i++) {
            try {
                /** Assuming the data from the written NBT tag is valid, arrows[i] is set to an arrow created by calling createEntityByName with the saved entityString. */
                arrows[i] = (EntityArrow) EntityList.createEntityByName(tag.getCompoundTag("arrowsType").getString("arrow" + i), worldObj);
                /** This arrow then reads the saved NBT data from its arrow NBT tag. */
                arrows[i].readFromNBT(tag.getCompoundTag("arrows").getCompoundTag("arrow" + i));
            } catch (final Exception e) {
                e.printStackTrace();
                /** If the data isn't valid, a new EntityArrow is created to avoid null objects. */
                arrows[i] = new EntityArrow(worldObj);
            }
        }
    }

    /** This method saves the entity specific data to NBT data, including the stored arrows. */
    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {
        tag.setFloat("shotVelocity", shotVelocity);
        tag.setInteger("arrowsAmount", arrows.length);
        /** This compound tag will contain the saved NBT data from each arrow in the "arrows" array. */
        final NBTTagCompound arrowsTag = new NBTTagCompound();
        /** This compound tag will contain the entityString of each arrow in the "arrows" array. */
        final NBTTagCompound arrowsType = new NBTTagCompound();

        /** An over engineered system to save an arbitrary amount of entities. */
        for (int i = 0; i < arrows.length; i++) {
            /** This is a temporary NBT tag variable, to store the NBT data for arrows[i]. */
            final NBTTagCompound arrTag = new NBTTagCompound();
            arrows[i].writeToNBT(arrTag);
            /** The NBT for arrows[i] is then stored as a sub tag of the compound tag arrowsTag. */
            arrowsTag.setTag("arrow" + i, arrTag);
            /** The entityString of arrows[i] is stored as a sub tag of the compound tag arrowsType. */
            arrowsType.setString("arrow" + i, EntityList.getEntityString(arrows[i]));
        }

        /** These tags are then saved. */
        tag.setTag("arrows", arrowsTag);
        tag.setTag("arrowsType", arrowsType);
    }

}
