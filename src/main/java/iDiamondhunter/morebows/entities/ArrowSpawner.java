package iDiamondhunter.morebows.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.world.World;

/** This entity is responsible for storing and spawning the time delayed "bonus" arrows of the ender bow. TODO Cleanup, more documentation */
public final class ArrowSpawner extends Entity {

    //private EntityArrow[] arrows;
    //private float shotVelocity;

    /**
     * @param world the world to spawn in
     */
    public ArrowSpawner(EntityType<?> type, World world) {
        super(type, world);
        /*noClip = true;
        preventEntitySpawning = false;
        isImmuneToFire = true;*/
    }

    /**
     * @param world        the world to spawn in
     * @param posX         the x position.
     * @param posY         the y position.
     * @param posZ         the z position.
     * @param shotVelocity the velocity of the stored arrows.
     * @param arrows       the stored arrows.
     */
    /*public ArrowSpawner(World world, double posX, double posY, double posZ, float shotVelocity, EntityArrow[] arrows) {
        this(world);
        this.shotVelocity = shotVelocity;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        /**
         * // TODO Fix
         *
         * <pre>
         * if (arrows.length != 6) {
         *     // System.out.println("ArrowSpawner expects 6 arrows, got " + arrows.length + " instead! It will not spawn any arrows until I implement better methods."); // debug
         *     setDead();
         * }
         * </pre>
         *
        this.arrows = arrows;
    }*/

    /*@Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    protected void entityInit() {
        /**
         * // TODO Possibly do this
         *
         * <pre>
         * noClip = true;
         * preventEntitySpawning = false;
         * isImmuneToFire = true;
         * </pre>
         *
    }

    @Override
    public boolean isEntityInvulnerable() {
        return true;
    }

    @Override
    public boolean isInvisible() {
        return true;
    }*/

    /*@Override
    public void onUpdate() {
        // Executed first, to prevent weird edge cases
        if (ticksExisted > 61) {
            setDead();
            return;
        }

        if (!worldObj.isRemote) {
            if (arrows == null) {
                // System.out.println("Bonus ender arrows lost! Will fix this soon..."); // debug
                setDead();
                return;
            }

            if (ticksExisted == 1) {
                // First arrow
                worldObj.spawnEntityInWorld(arrows[0]);
            }

            if (ticksExisted == 61) {
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
    /*@Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
        // Variables related to this entity
        shotVelocity = tag.getFloat("shotVelocity");
        // Variables related to the arrows stored by this entity
        final int arrowsArmount = tag.getInteger("arrowsAmount");
        arrows = new EntityArrow[arrowsArmount];

        /** An over engineered system to load an arbitrary amount of entities
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
    /*@Override
    protected void writeEntityToNBT(NBTTagCompound tag) {
        // Variables related to this entity
        tag.setFloat("shotVelocity", shotVelocity);
        // Variables related to the arrows stored by this entity
        tag.setInteger("arrowsAmount", arrows.length);
        final NBTTagCompound arrowsTag = new NBTTagCompound();
        final NBTTagCompound arrowsType = new NBTTagCompound();

        /** An over engineered system to save an arbitrary amount of entities
        for (int i = 0; i < arrows.length; i++) {
            final NBTTagCompound arrTag = new NBTTagCompound();
            arrows[i].writeToNBT(arrTag);
            arrowsTag.setTag("arrow" + i, arrTag);
            arrowsType.setString("arrow" + i, EntityList.getEntityString(arrows[i]));
        }

        // These values are then saved with these tags
        tag.setTag("arrows", arrowsTag);
        tag.setTag("arrowsType", arrowsType);
    }*/

    @Override
    protected void initDataTracker() {
        // TODO Auto-generated method stub
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag tag) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag tag) {
        // TODO Auto-generated method stub
    }

    @Override
    public Packet<?> createSpawnPacket() {
        // TODO Auto-generated method stub
        return null;
    }

}
