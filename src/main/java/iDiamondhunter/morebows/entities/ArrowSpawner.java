package iDiamondhunter.morebows.entities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import iDiamondhunter.morebows.MoreBows;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

/**
 * This entity is responsible for storing and spawning
 * the time delayed "bonus" arrows of the ender bow.
 */
public final class ArrowSpawner extends Entity {

    private static final AbstractArrowEntity[] NO_ARROWS = {};

    /** The arrows to spawn. */
    private @NotNull AbstractArrowEntity @NotNull [] arrows = NO_ARROWS;

    /** The stored shot velocity. */
    private float shotVelocity;

    /**
     * Don't use this.
     * TODO I think I can't remove these constructors, but I'm not sure.
     *
     * @deprecated Don't use this
     * @param type  the type
     * @param world the world to spawn in
     */
    public ArrowSpawner(EntityType<?> type, World world) {
        super(type, world);
        noPhysics = true;
        setInvisible(true);
    }

    /**
     * This entity is responsible for storing and spawning
     * the time delayed "bonus" arrows of the ender bow.
     *
     * @param worldIn      the world to spawn in
     * @param posX         the x position.
     * @param posY         the y position.
     * @param posZ         the z position.
     * @param shotVelocity the velocity of the stored arrows.
     * @param arrows       the stored arrows.
     */
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ArrowSpawner(World worldIn, double posX, double posY, double posZ, float shotVelocity, @NotNull AbstractArrowEntity @NotNull [] arrows) {
        this(MoreBows.ARROW_SPAWNER.get(), worldIn);
        setPos(posX, posY, posZ);
        this.shotVelocity = shotVelocity;
        this.arrows = arrows;
    }

    /**
     * Creates the spawn packet.
     * TODO review
     *
     * @return the packet
     */
    @Override
    public IPacket<?> getAddEntityPacket() {
        return new SSpawnObjectPacket(this);
    }

    /**
     * Initializes the data tracker, not that ArrowSpawner uses it.
     * TODO review
     */
    @Override
    protected void defineSynchedData() {
        // This method left intentionally blank
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return false;
    }

    /**
     * This method reads the entity specific data from saved NBT data,
     * including the stored arrows.
     * TODO Clean-up code
     *
     * @param nbt the NBT compound tag to read from
     */
    @Override
    protected void readAdditionalSaveData(CompoundNBT nbt) {
        /* Restore the saved amount of ticks that this entity has existed for */
        tickCount = nbt.getByte("age");
        /* Restore the saved shot velocity */
        shotVelocity = nbt.getFloat("shotVelocity");

        /* An over-engineered system to load an arbitrary amount of entities. */
        if (nbt.contains("arrows", 10)) {
            final CompoundNBT arrowsTag = nbt.getCompound("arrows");
            final int arrowsAmount = arrowsTag.size();
            final @NotNull AbstractArrowEntity @NotNull [] readArrows = new AbstractArrowEntity[arrowsAmount];

            for (int i = 0; i < arrowsAmount; i++) {
                final String arrTagName = "arrow" + i;
                @Nullable AbstractArrowEntity toAdd;

                if (arrowsTag.contains(arrTagName, 10)) {
                    final CompoundNBT currentArrow = arrowsTag.getCompound(arrTagName);

                    try {
                        /*
                         * Assuming the data from the written NBT tag is valid,
                         * arrows[i] is set to an arrow created by calling
                         * createEntityFromNBT with the saved NBT data.
                         */
                        final @Nullable Entity savedEntity = EntityType.create(currentArrow, level).orElse(null);

                        if (savedEntity instanceof AbstractArrowEntity) {
                            toAdd = (AbstractArrowEntity) savedEntity;
                        } else {
                            MoreBows.modLog.error("The saved NBT data for arrow {} for ArrowSpawner {} ({}) was not able to spawn an AbstractArrowEntity (spawned Entity was {}).", i, this, currentArrow, savedEntity);

                            if (savedEntity != null) {
                                savedEntity.remove();
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

                /*
                 * If the data isn't valid, a new AbstractArrowEntity is created
                 * to avoid null objects.
                 */
                readArrows[i] = toAdd != null ? toAdd : new ArrowEntity(level, getX(), getY(), getZ());
            }

            arrows = readArrows;
        } else {
            MoreBows.modLog.error("Could not find saved arrows for ArrowSpawner {} when loading from NBT data ({}).", this, nbt);
            arrows = NO_ARROWS;
        }
    }

    @Override
    public void tick() {
        // Executed first, to prevent weird edge cases
        if (tickCount > 61) {
            remove();
            return;
        }

        if (!level.isClientSide) {
            if (tickCount == 1) {
                /* Check that the arrows exist before accessing them. */
                if (arrows.length == 0) {
                    MoreBows.modLog.error("No arrows in ArrowSpawner!");
                    remove();
                    return;
                }

                // First arrow
                level.addFreshEntity(arrows[0]);
            }

            if (tickCount == 61) {
                final int arrLength = arrows.length;

                for (int i = 1; i < arrLength; ++i) {
                    final @NotNull AbstractArrowEntity arrow = arrows[i];
                    level.addFreshEntity(arrow);
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
                        soundPitch = (1.0F / ((random.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F);
                        break;

                    case 3:
                        arrYDisp = 1.45;
                        arrXDisp = -2.25;
                        arrZDisp = -0.75;
                        soundVolume = 0.25F;
                        soundPitch = (1.0F / ((random.nextFloat() * 0.4F) + 1.0F)) + (shotVelocity * 0.3F);
                        break;

                    case 4:
                        arrYDisp = 2.0;
                        arrXDisp = 0.25;
                        arrZDisp = 2.5;
                        soundVolume = 1.0F;
                        soundPitch = (1.0F / ((random.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F);
                        break;

                    case 5:
                        arrYDisp = 1.75;
                        arrXDisp = 1.75;
                        arrZDisp = 1.5;
                        soundVolume = 0.5F;
                        soundPitch = (1.0F / ((random.nextFloat() * 0.4F) + 1.0F)) + (shotVelocity * 0.4F);
                        break;

                    default:
                        arrYDisp = 0.0;
                        arrXDisp = 0.0;
                        arrZDisp = 0.0;
                        soundVolume = 0.5F;
                        soundPitch = (1.0F / ((random.nextFloat() * 0.4F) + 1.0F)) + (shotVelocity * 0.4F);
                        break;
                    }

                    arrow.setPos(arrow.getX() + arrXDisp, arrow.getY() + arrYDisp, arrow.getZ() + arrZDisp);
                    level.playSound(null, arrow.getX(), arrow.getY(), arrow.getZ(), (i & 1) != 0 ? SoundEvents.ENDERMAN_TELEPORT : SoundEvents.ARROW_SHOOT, SoundCategory.PLAYERS, soundVolume, soundPitch);
                }
            }
        }
    }

    /**
     * This method saves the entity specific data to NBT data,
     * including the stored arrows.
     *
     * @param nbt the NBT compound tag to write to
     */
    @Override
    protected void addAdditionalSaveData(CompoundNBT nbt) {
        /* Save the amount of ticks that this entity has existed for */
        nbt.putByte("age", (byte) tickCount);
        /* Save the shot velocity */
        nbt.putFloat("shotVelocity", shotVelocity);
        /*
         * This compound tag will contain the saved NBT data
         * from each arrow in the "arrows" array.
         */
        final CompoundNBT arrowsTag = new CompoundNBT();
        /* An over-engineered system to save an arbitrary amount of entities. */
        final int arrLength = arrows.length;

        for (int i = 0; i < arrLength; i++) {
            /* This is a temporary NBT tag variable, to store the NBT data for arrows[i]. */
            CompoundNBT arrTag;

            try {
                final CompoundNBT toSave = arrows[i].serializeNBT();
                arrTag = toSave;
            } catch (final Exception e) {
                /* Some mods don't properly register entities. */
                MoreBows.modLog.error("An error occurred when trying to serialize the NBT data of {}. This is likely due to an error made by the mod that added the type of arrow that was being shot ({}).", arrows[i], arrows[i].getClass(), e);
                final CompoundNBT toSave = new ArrowEntity(level, getX(), getY(), getZ()).serializeNBT();
                arrTag = toSave;
            }

            /*
             * The NBT for arrows[i] is then stored as a sub tag
             * of the compound tag arrowsTag.
             */
            arrowsTag.put("arrow" + i, arrTag);
        }

        nbt.put("arrows", arrowsTag);
    }

}
