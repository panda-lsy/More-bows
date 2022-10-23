package iDiamondhunter.morebows.entities;

import iDiamondhunter.morebows.MoreBows;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

/** This entity is responsible for storing and spawning the time delayed "bonus" arrows of the ender bow. */
public final class ArrowSpawner extends Entity {

    private static final int EXPECTED_ARR_AMOUNT = 6;
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
        setEntityInvulnerable(true);
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

    private static boolean invalidArrAmount(EntityArrow[] checkedArrows) {
        return (checkedArrows == null) || (checkedArrows.length != EXPECTED_ARR_AMOUNT);
    }

    @Override
    public void onUpdate() {
        // Executed first, to prevent weird edge cases
        if (ticksExisted > 61) {
            setDead();
            return;
        }

        if (!world.isRemote) {
            if (ticksExisted == 1) {
                /** Check that the arrows exist before accessing them. */
                if (invalidArrAmount(arrows)) {
                    MoreBows.modLog.error("Invalid arrow amount when trying to spawn ender arrows!");
                    setDead();
                    return;
                }

                // First arrow
                world.spawnEntity(arrows[0]);
            }

            if (ticksExisted == 61) {
                /** Check that the arrows exist before accessing them. */
                if (invalidArrAmount(arrows)) {
                    MoreBows.modLog.error("Invalid arrow amount when trying to spawn ender arrows!");
                    setDead();
                    return;
                }

                // Second batch of arrows TODO Check if accurate to older versions of the mod
                world.spawnEntity(arrows[1]);
                world.playSound(null, arrows[1].posX, arrows[1].posY, arrows[1].posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 0.5F, (1.0F / ((rand.nextFloat() * 0.4F) + 1.0F)) + (shotVelocity * 0.4F));
                world.spawnEntity(arrows[2]);
                arrows[2].posY++;
                arrows[2].posX -= 1.25;
                arrows[2].posZ += 1.75;
                world.playSound(null, arrows[2].posX, arrows[2].posY, arrows[2].posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, (1.0F / ((rand.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));
                world.spawnEntity(arrows[3]);
                arrows[3].posY += 1.45;
                arrows[3].posX -= 2.25;
                arrows[3].posZ -= 0.75;
                world.playSound(null, arrows[3].posX, arrows[3].posY, arrows[3].posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 0.25F, (1.0F / ((rand.nextFloat() * 0.4F) + 1.0F)) + (shotVelocity * 0.3F));
                world.spawnEntity(arrows[4]);
                arrows[4].posY += 2;
                arrows[4].posX += 0.25;
                arrows[4].posZ += 2.5;
                world.playSound(null, arrows[4].posX, arrows[4].posY, arrows[4].posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, (1.0F / ((rand.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));
                world.spawnEntity(arrows[5]);
                arrows[5].posY += 1.75;
                arrows[5].posX += 1.75;
                arrows[5].posZ += 1.5;
                world.playSound(null, arrows[5].posX, arrows[5].posY, arrows[5].posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 0.5F, (1.0F / ((rand.nextFloat() * 0.4F) + 1.0F)) + (shotVelocity * 0.4F));
            }
        }
    }

    /** This method reads the entity specific data from saved NBT data, including the stored arrows. TODO Clean-up code */
    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
        /** Restore the saved amount of ticks that this entity has existed for */
        ticksExisted = tag.getByte("ticksExisted");
        /** Restore the saved shot velocity */
        shotVelocity = tag.getFloat("shotVelocity");

        /** An over-engineered system to load an arbitrary amount of entities. */
        if (tag.hasKey("arrows", 10)) {
            final NBTTagCompound arrowsTag = tag.getCompoundTag("arrows");
            final int arrowsAmount = arrowsTag.getSize();
            arrows = new EntityArrow[arrowsAmount];

            for (int i = 0; i < arrowsAmount; i++) {
                final String arrTagName = "arrow" + i;
                EntityArrow toAdd;

                if (arrowsTag.hasKey(arrTagName, 10)) {
                    final NBTTagCompound currentArrow = arrowsTag.getCompoundTag(arrTagName);

                    try {
                        /** Assuming the data from the written NBT tag is valid, arrows[i] is set to an arrow created by calling createEntityFromNBT with the saved NBT data. */
                        final Entity savedEntity = EntityList.createEntityFromNBT(currentArrow, world);

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
                        /** Catch any errors thrown when trying to load arrows from NBT. */
                        MoreBows.modLog.error("An error occurred when trying to spawn an arrow from saved NBT data ({}).", currentArrow, e);
                        toAdd = null;
                    }
                } else {
                    toAdd = null;
                }

                if (toAdd != null) {
                    arrows[i] = toAdd;
                } else {
                    /** If the data isn't valid, a new EntityArrow is created to avoid null objects. */
                    arrows[i] = new EntityTippedArrow(world);
                }
            }
        } else {
            MoreBows.modLog.error("Could not find saved EntityArrows for ArrowSpawner {} when loading from NBT data ({}).", this, tag);
            arrows = null;
        }
    }

    /** This method saves the entity specific data to NBT data, including the stored arrows. */
    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {
        /** Save the amount of ticks that this entity has existed for */
        tag.setByte("ticksExisted", (byte) ticksExisted);
        /** Save the shot velocity */
        tag.setFloat("shotVelocity", shotVelocity);

        if (!invalidArrAmount(arrows)) {
            /** This compound tag will contain the saved NBT data from each arrow in the "arrows" array. */
            final NBTTagCompound arrowsTag = new NBTTagCompound();

            /** An over-engineered system to save an arbitrary amount of entities. */
            for (int i = 0; i < arrows.length; i++) {
                /** This is a temporary NBT tag variable, to store the NBT data for arrows[i]. */
                NBTTagCompound arrTag;

                try {
                    final EntityArrow currentArrow = arrows[i];

                    if (currentArrow != null) {
                        arrTag = currentArrow.serializeNBT();
                    } else {
                        MoreBows.modLog.error("An arrow was null when trying to serialize the NBT data of an ArrowSpawner.");
                        arrTag = new EntityTippedArrow(world).serializeNBT();
                    }
                } catch (final Exception e) {
                    /** Some mods don't properly register entities. */
                    MoreBows.modLog.error("An error occurred when trying to serialize the NBT data of {}. This is likely due to an error made by the mod that added the type of arrow that was being shot ({}).", arrows[i], arrows[i].getClass(), e);
                    arrTag = new EntityTippedArrow(world).serializeNBT();
                }

                /** The NBT for arrows[i] is then stored as a sub tag of the compound tag arrowsTag. */
                arrowsTag.setTag("arrow" + i, arrTag);
            }

            tag.setTag("arrows", arrowsTag);
        }
    }

}
