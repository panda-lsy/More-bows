package iDiamondhunter.morebows.entities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import iDiamondhunter.morebows.MoreBows;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

/**
 * This entity is responsible for storing and spawning
 * the time delayed "bonus" arrows of the ender bow.
 */
public final class ArrowSpawner extends Entity {

    private static final PersistentProjectileEntity[] NO_ARROWS = {};

    /** The arrows to spawn. */
    private @NotNull PersistentProjectileEntity @NotNull [] arrows = NO_ARROWS;

    /** The stored shot velocity. */
    private float shotVelocity;

    /**
     * Don't use this.
     * TODO I think I can't remove these constructors, but I'm not sure.
     *
     * @deprecated Don't use this
     * @param type    the type
     * @param worldIn the world to spawn in
     */
    public ArrowSpawner(EntityType<?> type, World worldIn) {
        super(type, worldIn);
        /*noClip = true;
        preventEntitySpawning = false;
        isImmuneToFire = true;*/
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
    public ArrowSpawner(World worldIn, double posX, double posY, double posZ, float shotVelocity, @NotNull PersistentProjectileEntity @NotNull [] arrows) {
        this(MoreBows.ARROW_SPAWNER, worldIn);
        setPosition(posX, posY, posZ);
        this.shotVelocity = shotVelocity;
        this.arrows = arrows;
    }

    @Override
    public void tick() {
        // Executed first, to prevent weird edge cases
        if (age > 61) {
            discard();
            return;
        }

        if (!world.isClient) {
            if (age == 1) {
                /* Check that the arrows exist before accessing them. */
                if (arrows.length == 0) {
                    MoreBows.modLog.error("No arrows in ArrowSpawner!");
                    discard();
                    return;
                }

                // First arrow
                world.spawnEntity(arrows[0]);
            }

            if (age == 61) {
                final int arrLength = arrows.length;

                for (int i = 1; i < arrLength; ++i) {
                    final @NotNull PersistentProjectileEntity arrow = arrows[i];
                    world.spawnEntity(arrow);
                    final double arrYDisp;
                    final double arrXDisp;
                    final double arrZDisp;
                    final float soundVolume;
                    final float soundPitch;

                    switch (i) {
                        case 2 -> {
                            arrYDisp = 1.0;
                            arrXDisp = -1.25;
                            arrZDisp = 1.75;
                            soundVolume = 1.0F;
                            soundPitch = (1.0F / ((random.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F);
                        }
                        case 3 -> {
                            arrYDisp = 1.45;
                            arrXDisp = -2.25;
                            arrZDisp = -0.75;
                            soundVolume = 0.25F;
                            soundPitch = (1.0F / ((random.nextFloat() * 0.4F) + 1.0F)) + (shotVelocity * 0.3F);
                        }
                        case 4 -> {
                            arrYDisp = 2.0;
                            arrXDisp = 0.25;
                            arrZDisp = 2.5;
                            soundVolume = 1.0F;
                            soundPitch = (1.0F / ((random.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F);
                        }
                        case 5 -> {
                            arrYDisp = 1.75;
                            arrXDisp = 1.75;
                            arrZDisp = 1.5;
                            soundVolume = 0.5F;
                            soundPitch = (1.0F / ((random.nextFloat() * 0.4F) + 1.0F)) + (shotVelocity * 0.4F);
                        }
                        default -> {
                            arrYDisp = 0.0;
                            arrXDisp = 0.0;
                            arrZDisp = 0.0;
                            soundVolume = 0.5F;
                            soundPitch = (1.0F / ((random.nextFloat() * 0.4F) + 1.0F)) + (shotVelocity * 0.4F);
                        }
                    }

                    arrow.refreshPositionAndAngles(arrow.getX() + arrXDisp, arrow.getY() + arrYDisp, arrow.getZ() + arrZDisp, arrow.getYaw(), arrow.getPitch());
                    world.playSound(null, arrow.getX(), arrow.getY(), arrow.getZ(), (i & 1) != 0 ? SoundEvents.ENTITY_ENDERMAN_TELEPORT : SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, soundVolume, soundPitch);
                }
            }
        }
    }

    /**
     * Initializes the data tracker, not that ArrowSpawner uses it.
     * TODO review
     */
    @Override
    protected void initDataTracker() {
        // This method left intentionally blank
    }

    /**
     * Creates the spawn packet.
     * TODO review
     *
     * @return the packet
     */
    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    /**
     * This method reads the entity specific data from saved NBT data,
     * including the stored arrows.
     * TODO Clean-up code
     *
     * @param compound the NBT compound tag to read from
     */
    @Override
    protected void readCustomDataFromNbt(NbtCompound compound) {
        /* Restore the saved amount of ticks that this entity has existed for */
        age = compound.getByte("age");
        /* Restore the saved shot velocity */
        shotVelocity = compound.getFloat("shotVelocity");

        /* An over-engineered system to load an arbitrary amount of entities. */
        if (compound.contains("arrows", 10)) {
            final NbtCompound arrowsTag = compound.getCompound("arrows");
            final int arrowsAmount = arrowsTag.getSize();
            final @NotNull PersistentProjectileEntity @NotNull [] readArrows = new PersistentProjectileEntity[arrowsAmount];

            for (int i = 0; i < arrowsAmount; i++) {
                final String arrTagName = "arrow" + i;
                @Nullable PersistentProjectileEntity toAdd;

                if (arrowsTag.contains(arrTagName, 10)) {
                    final NbtCompound currentArrow = arrowsTag.getCompound(arrTagName);

                    try {
                        /*
                         * Assuming the data from the written NBT tag is valid,
                         * arrows[i] is set to an arrow created by calling
                         * createEntityFromNBT with the saved NBT data.
                         */
                        final @Nullable Entity savedEntity = EntityType.getEntityFromNbt(currentArrow, world).orElse(null);

                        if (savedEntity instanceof final PersistentProjectileEntity savedEntityProjectile) {
                            toAdd = savedEntityProjectile;
                        } else {
                            MoreBows.modLog.error("The saved NBT data for arrow {} for ArrowSpawner {} ({}) was not able to spawn an PersistentProjectileEntity (spawned Entity was {}).", i, this, currentArrow, savedEntity);

                            if (savedEntity != null) {
                                savedEntity.discard();
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
                 * If the data isn't valid, a new PersistentProjectileEntity is created
                 * to avoid null objects.
                 */
                readArrows[i] = toAdd != null ? toAdd : new ArrowEntity(world, getX(), getY(), getZ());
            }

            arrows = readArrows;
        } else {
            MoreBows.modLog.error("Could not find saved PersistentProjectileEntitys for ArrowSpawner {} when loading from NBT data ({}).", this, compound);
            arrows = NO_ARROWS;
        }
    }

    /**
     * This method saves the entity specific data to NBT data,
     * including the stored arrows.
     *
     * @param compound the NBT compound tag to write to
     */
    @Override
    protected void writeCustomDataToNbt(NbtCompound compound) {
        /* Save the amount of ticks that this entity has existed for */
        compound.putByte("age", (byte) age);
        /* Save the shot velocity */
        compound.putFloat("shotVelocity", shotVelocity);
        /*
         * This compound tag will contain the saved NBT data
         * from each arrow in the "arrows" array.
         */
        final NbtCompound arrowsTag = new NbtCompound();
        /* An over-engineered system to save an arbitrary amount of entities. */
        final int arrLength = arrows.length;

        for (int i = 0; i < arrLength; i++) {
            /* This is a temporary NBT tag variable, to store the NBT data for arrows[i]. */
            NbtCompound arrTag;

            try {
                final NbtCompound toSave = new NbtCompound();
                arrows[i].saveNbt(toSave);
                arrTag = toSave;
            } catch (final Exception e) {
                /* Some mods don't properly register entities. */
                MoreBows.modLog.error("An error occurred when trying to serialize the NBT data of {}. This is likely due to an error made by the mod that added the type of arrow that was being shot ({}).", arrows[i], arrows[i].getClass(), e);
                final NbtCompound toSave = new NbtCompound();
                new ArrowEntity(world, getX(), getY(), getZ()).saveNbt(toSave);
                arrTag = toSave;
            }

            /*
             * The NBT for arrows[i] is then stored as a sub tag
             * of the compound tag arrowsTag.
             */
            arrowsTag.put("arrow" + i, arrTag);
        }

        compound.put("arrows", arrowsTag);
    }

}
