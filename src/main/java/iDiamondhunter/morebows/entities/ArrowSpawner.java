package iDiamondhunter.morebows.entities;

import iDiamondhunter.morebows.MoreBows;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/** This entity is responsible for storing and spawning the time delayed "bonus" arrows of the ender bow. */
public final class ArrowSpawner extends Entity {

    private static final EntityArrow[] NO_ARROWS = {};

    /** The arrows to spawn. */
    private EntityArrow[] arrows = NO_ARROWS;
    /** The stored shot velocity. */
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
                /* Check that the arrows exist before accessing them. */
                if (arrows.length == 0) {
                    MoreBows.modLog.error("No arrows in ArrowSpawner!");
                    setDead();
                    return;
                }

                // First arrow
                worldObj.spawnEntityInWorld(arrows[0]);
            }

            if (ticksExisted == 61) {
                final int arrLength = arrows.length;

                for (int i = 1; i < arrLength; ++i) {
                    final EntityArrow arrow = arrows[i];
                    worldObj.spawnEntityInWorld(arrow);
                    final double arrYDisp;
                    final double arrXDisp;
                    final double arrZDisp;
                    final float soundVolume;
                    final float soundPitch;

                    switch (i) {
                    case 2:
                        arrYDisp = 1.0;
                        arrXDisp = -1.25;
                        arrZDisp = 1.75;
                        soundVolume = 1.0F;
                        soundPitch = (1.0F / ((rand.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F);
                        break;

                    case 3:
                        arrYDisp = 1.45;
                        arrXDisp = -2.25;
                        arrZDisp = -0.75;
                        soundVolume = 0.25F;
                        soundPitch = (1.0F / ((rand.nextFloat() * 0.4F) + 1.0F)) + (shotVelocity * 0.3F);
                        break;

                    case 4:
                        arrYDisp = 2.0;
                        arrXDisp = 0.25;
                        arrZDisp = 2.5;
                        soundVolume = 1.0F;
                        soundPitch = (1.0F / ((rand.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F);
                        break;

                    case 5:
                        arrYDisp = 1.75;
                        arrXDisp = 1.75;
                        arrZDisp = 1.5;
                        soundVolume = 0.5F;
                        soundPitch = (1.0F / ((rand.nextFloat() * 0.4F) + 1.0F)) + (shotVelocity * 0.4F);
                        break;

                    default:
                        arrYDisp = 0.0;
                        arrXDisp = 0.0;
                        arrZDisp = 0.0;
                        soundVolume = 0.5F;
                        soundPitch = (1.0F / ((rand.nextFloat() * 0.4F) + 1.0F)) + (shotVelocity * 0.4F);
                        break;
                    }

                    arrow.posY += arrYDisp;
                    arrow.posX += arrXDisp;
                    arrow.posZ += arrZDisp;
                    worldObj.playSoundAtEntity(arrows[i], (i & 1) != 0 ? "mob.endermen.portal" : "random.bow", soundVolume, soundPitch);
                }
            }
        }
    }

    /** This method reads the entity specific data from saved NBT data, including the stored arrows. */
    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
        /* Restore the saved amount of ticks that this entity has existed for */
        ticksExisted = tag.getByte("ticksExisted");
        /* Restore the saved shot velocity */
        shotVelocity = tag.getFloat("shotVelocity");

        /* An over-engineered system to load an arbitrary amount of entities. */
        if (tag.hasKey("arrows", 10)) {
            final NBTTagCompound arrowsTag = tag.getCompoundTag("arrows");
            final int arrowsAmount = arrowsTag.func_150296_c().size();
            final EntityArrow[] readArrows = new EntityArrow[arrowsAmount];

            for (int i = 0; i < arrowsAmount; i++) {
                final String arrTagName = "arrow" + i;
                EntityArrow toAdd;

                if (arrowsTag.hasKey(arrTagName, 10)) {
                    final NBTTagCompound currentArrow = arrowsTag.getCompoundTag(arrTagName);

                    try {
                        /*
                         * Assuming the data from the written NBT tag is valid,
                         * arrows[i] is set to an arrow created by calling
                         * createEntityFromNBT with the saved NBT data.
                         */
                        final Entity savedEntity = EntityList.createEntityFromNBT(currentArrow, worldObj);

                        if (savedEntity instanceof EntityArrow) {
                            toAdd = (EntityArrow) savedEntity;
                        } else {
                            MoreBows.modLog.error("The saved NBT data for arrow {} for ArrowSpawner {} ({}) was not able to spawn an EntityArrow (spawned Entity was {}).", i, this, currentArrow, savedEntity);

                            if (savedEntity != null) {
                                savedEntity.setDead();
                            }

                            toAdd = null;
                        }
                    } catch (final Exception e) {
                        /* Catch any errors thrown when trying to load arrows from NBT. */
                        MoreBows.modLog.error("An error occurred when trying to spawn an arrow from saved NBT data ({}).", currentArrow, e);
                        toAdd = null;
                    }
                } else {
                    toAdd = null;
                }

                if (toAdd != null) {
                    readArrows[i] = toAdd;
                } else {
                    /* If the data isn't valid, a new EntityArrow is created to avoid null objects. */
                    readArrows[i] = new EntityArrow(worldObj);
                }
            }

            arrows = readArrows;
        } else {
            MoreBows.modLog.error("Could not find saved EntityArrows for ArrowSpawner {} when loading from NBT data ({}).", this, tag);
            arrows = NO_ARROWS;
        }
    }

    /** This method saves the entity specific data to NBT data, including the stored arrows. */
    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {
        /* Save the amount of ticks that this entity has existed for */
        tag.setByte("ticksExisted", (byte) ticksExisted);
        /* Save the shot velocity */
        tag.setFloat("shotVelocity", shotVelocity);
        /*
         * This compound tag will contain the saved NBT data
         * from each arrow in the "arrows" array.
         */
        final NBTTagCompound arrowsTag = new NBTTagCompound();
        /* An over-engineered system to save an arbitrary amount of entities. */
        final int arrLength = arrows.length;

        for (int i = 0; i < arrLength; i++) {
            /* This is a temporary NBT tag variable, to store the NBT data for arrows[i]. */
            NBTTagCompound arrTag;

            try {
                arrTag = new NBTTagCompound();
                arrows[i].writeToNBTOptional(arrTag);
            } catch (final Exception e) {
                /* Some mods don't properly register entities. */
                MoreBows.modLog.error("An error occurred when trying to serialize the NBT data of {}. This is likely due to an error made by the mod that added the type of arrow that was being shot ({}).", arrows[i], arrows[i].getClass(), e);
                arrTag = new NBTTagCompound();
                new EntityArrow(worldObj).writeToNBTOptional(arrTag);
            }

            /*
             * The NBT for arrows[i] is then stored as a sub tag
             * of the compound tag arrowsTag.
             */
            arrowsTag.setTag("arrow" + i, arrTag);
        }

        tag.setTag("arrows", arrowsTag);
    }

}
