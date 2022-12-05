package iDiamondhunter.morebows;

import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_ENDER;
import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_FIRE;
import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_FROST;
import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_NOT_CUSTOM;

import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import iDiamondhunter.morebows.compat.NyfsQuiversCompat;
import iDiamondhunter.morebows.config.ConfigGeneral.CustomArrowMultiShotType;
import iDiamondhunter.morebows.entities.ArrowSpawner;
import iDiamondhunter.morebows.entities.CustomArrow;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

/**
 * This entire class is a huge hack. I'm ashamed of myself.
 * And yes, this is important to document.
 * TODO: Possibly come up with better ideas for various bits of this class.
 **/
public final class CustomBow extends BowItem {

    private static final ItemStack defaultAmmo = new ItemStack(Items.ARROW);
    private static final ArrowItem defaultArrow = (ArrowItem) Items.ARROW;

    /** TODO review */
    private static PersistentProjectileEntity arrowHelper(World world, PlayerEntity player, float velocity, ItemStack ammo, ArrowItem arrow) {
        final PersistentProjectileEntity entityarrow = arrow.createArrow(world, ammo, player);
        return arrowHelperHelper(player, velocity, entityarrow);
    }

    /** TODO review */
    private static PersistentProjectileEntity arrowHelperHelper(PlayerEntity player, float velocity, PersistentProjectileEntity entityarrow) {
        entityarrow.setVelocity(player, player.getPitch(), player.getYaw(), 0.0f, velocity, 1.0f);
        return entityarrow;
    }

    /** TODO review */
    private static PersistentProjectileEntity customArrowHelper(World world, PlayerEntity player, float velocity, byte arrType) {
        final PersistentProjectileEntity entityarrow = new CustomArrow(world, player, arrType);
        return arrowHelperHelper(player, velocity, entityarrow);
    }

    /** TODO review */
    private static PersistentProjectileEntity possiblyCustomArrowHelper(World world, PlayerEntity player, float velocity, ItemStack ammo, ArrowItem arrow, byte arrType) {
        if (arrow == Items.ARROW) {
            return customArrowHelper(world, player, velocity, arrType);
        }

        return arrowHelper(world, player, velocity, ammo, arrow);
    }

    /* Bow instance variables */
    /** The type of arrows this bow shoots. */
    @MagicConstant(intValues = { ARROW_TYPE_NOT_CUSTOM, ARROW_TYPE_ENDER, ARROW_TYPE_FIRE, ARROW_TYPE_FROST })
    private final byte bowType;

    /** The damage multiplier of the bow. */
    private final double damageMult;

    /** True if the bow is a multi-shot bow. */
    private final boolean multiShot;

    /** The drawback speed of the bow. */
    public final float powerDiv;

    /**
     * A more customizable bow than the vanilla one.
     *
     * @param settings   The settings TODO
     * @param bowType    The type of arrows this bow shoots.
     *                   This also influences some behaviors of the bow as well.
     * @param damageMult The multiplier to damage done by an arrow shot by this bow.
     * @param multiShot  True if this bow shoots multiple arrows.
     * @param powerDiv   The power divisor of this bow. Influences drawback speed.
     */
    CustomBow(Settings settings, @MagicConstant(intValues = {ARROW_TYPE_NOT_CUSTOM, ARROW_TYPE_ENDER, ARROW_TYPE_FIRE, ARROW_TYPE_FROST}) byte bowType, double damageMult, boolean multiShot, float powerDiv) {
        super(settings);
        this.bowType = bowType;
        this.damageMult = damageMult;
        this.multiShot = multiShot;
        this.powerDiv = powerDiv;
    }

    /**
     * This handles the process of shooting an arrow from this bow.
     * TODO Cleanup, document more
     *
     * @param stack             the ItemStack of the shot bow
     * @param world             the world the bow was shot in
     * @param user              the shooting entity
     * @param remainingUseTicks how long the bow has been in use for
     */
    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof final PlayerEntity player)) {
            return;
        }

        final boolean alwaysShoots = player.getAbilities().creativeMode || (EnchantmentHelper.getLevel(Enchantments.INFINITY, stack) > 0);
        ItemStack ammo = player.getArrowType(stack);

        if (!ammo.isEmpty() || alwaysShoots) {
            if (ammo.isEmpty()) {
                ammo = new ItemStack(Items.ARROW);
            }

            final int charge = getMaxUseTime(stack) - remainingUseTicks;
            float shotVelocity = charge / powerDiv;
            shotVelocity = ((shotVelocity * shotVelocity) + (shotVelocity * 2.0F)) / 3.0F;

            if (shotVelocity < 0.1F) {
                return;
            }

            // TODO merge with alwaysShoots somehow
            final boolean infiniteAmmo = alwaysShoots && ammo.isOf(Items.ARROW);
            final int ammoCount = infiniteAmmo ? 64 : ammo.getCount();
            final int usedAmmo;
            final int shotArrows;
            final int maxAmmo;
            /* TODO Review */
            final Random playerRandom = player.getRandom();

            if (multiShot) {
                if (bowType == ARROW_TYPE_ENDER) {
                    maxAmmo = 6;
                } else {
                    maxAmmo = playerRandom.nextInt(4) == 0 ? 3 : 2;
                }
            } else {
                maxAmmo = 1;
            }

            if (MoreBows.configGeneralInst.customArrowMultiShot == CustomArrowMultiShotType.UseAmountShot) {
                usedAmmo = ammoCount > maxAmmo ? maxAmmo : ammoCount;
                shotArrows = usedAmmo;
            } else {
                usedAmmo = 1;
                shotArrows = maxAmmo;
            }

            if (!world.isClient) {
                final boolean isCrit;

                if (shotVelocity >= 1.0F) {
                    shotVelocity = 1.0F;
                    isCrit = true;
                } else {
                    isCrit = false;
                }

                final ArrowItem arrow = (ArrowItem) (ammo.getItem() instanceof ArrowItem ? ammo.getItem() : Items.ARROW);
                final @NotNull PersistentProjectileEntity @NotNull [] arrs;

                /*
                 * Create the arrows to fire.
                 * The velocity multiplier for each arrow
                 * is written as a value multiplied by 1.5F.
                 * This is because this mod was originally for older versions of Minecraft,
                 * where the vanilla bow had a base velocity multiplier of 2.0F.
                 * As I still want to maintain and backport changes
                 * for the 1.7.10 version of this mod,
                 * (and because I can't figure out how the velocity multipliers
                 * for each CustomBow were chosen),
                 * I have simply multiplied each velocity multiplier
                 * to be the equivalent value for a base multiplier of 3.0F instead
                 * (2.0F * 1.5F == 3.0F).
                 */
                if (multiShot) { // Bows that shoot multiple arrows
                    final ItemStack useAmmo;
                    final ArrowItem useArrow;
                    final PersistentProjectileEntity.PickupPermission pickStatus = (MoreBows.configGeneralInst.customArrowMultiShot == CustomArrowMultiShotType.UseAmountShot) && !infiniteAmmo ? PersistentProjectileEntity.PickupPermission.ALLOWED : PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;

                    if (MoreBows.configGeneralInst.customArrowMultiShot == CustomArrowMultiShotType.AlwaysStandardArrows) {
                        useAmmo = defaultAmmo;
                        useArrow = defaultArrow;
                    } else {
                        useAmmo = ammo;
                        useArrow = arrow;
                    }

                    if (bowType == ARROW_TYPE_ENDER) { // Ender bow
                        arrs = new PersistentProjectileEntity[shotArrows];

                        for (int i = 0; i < shotArrows; ++i) {

                            final float velocityChoice = switch (i) {
                                case 1 -> shotVelocity * (1.0F * 1.5F);
                                case 2 -> shotVelocity * (1.2F * 1.5F);
                                case 3 -> shotVelocity * (1.5F * 1.5F);
                                case 4 -> shotVelocity * (1.75F * 1.5F);
                                case 5 -> shotVelocity * (1.825F * 1.5F);
                                default -> shotVelocity * (2.0F * 1.5F);
                            };

                            if (i > 0) {
                                arrs[i] = possiblyCustomArrowHelper(world, player, velocityChoice, useAmmo, useArrow, bowType);
                                arrs[i].pickupType = pickStatus;
                            } else {
                                arrs[i] = possiblyCustomArrowHelper(world, player, velocityChoice, ammo, arrow, bowType);
                            }
                        }
                    } else {
                        arrs = new PersistentProjectileEntity[shotArrows];

                        for (int i = 0; i < shotArrows; ++i) {

                            final float velocityChoice = switch (i) {
                                case 1 -> shotVelocity * (1.65F * 1.5F);
                                case 2 -> shotVelocity * (1.275F * 1.5F);
                                default -> shotVelocity * (2.0F * 1.5F);
                            };

                            if (i > 0) {
                                arrs[i] = arrowHelper(world, player, velocityChoice, useAmmo, useArrow);
                                arrs[i].pickupType = pickStatus;
                            } else {
                                arrs[i] = arrowHelper(world, player, velocityChoice, ammo, arrow);
                            }
                        }
                    }
                } else if (bowType == ARROW_TYPE_NOT_CUSTOM) {
                    /*
                     * "Standard" style bows that do not shoot multiple arrows,
                     * or have a custom arrow type.
                     * Note to self: this is after the multi-arrow bows
                     * due to the multi bow having arrows of a normal type.
                     */
                    arrs = new PersistentProjectileEntity[] { arrowHelper(world, player, shotVelocity * (2.0F * 1.5F), ammo, arrow) };
                } else { // Bows that shoot only one custom arrow, currently only frost / fire bows
                    arrs = new PersistentProjectileEntity[] { possiblyCustomArrowHelper(world, player, shotVelocity * (2.0F * 1.5F), ammo, arrow, bowType) };
                }

                if (infiniteAmmo || (player.getAbilities().creativeMode && (ammo.isOf(Items.SPECTRAL_ARROW) || ammo.isOf(Items.TIPPED_ARROW)))) {
                    arrs[0].pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                }

                // Set up flags for adding enchantment effects / other modifiers
                final int power = EnchantmentHelper.getLevel(Enchantments.POWER, stack);
                final int knockback = EnchantmentHelper.getLevel(Enchantments.PUNCH, stack);
                final boolean flame = EnchantmentHelper.getLevel(Enchantments.FLAME, stack) > 0;

                // Add enchantment effects / other modifiers to each arrow
                // TODO review
                for (final PersistentProjectileEntity arr : arrs) {
                    if (isCrit) { /* setIsCritical calls dataWatcher methods, avoid calling it unless needed. */
                        arr.setCritical(true);
                    }

                    if (power > 0) {
                        arr.setDamage(arr.getDamage() + (power * 0.5) + 0.5);
                    }

                    if (knockback > 0) {
                        arr.setPunch(knockback);
                    }

                    if (flame) {
                        arr.setOnFireFor(100);

                        if (bowType == ARROW_TYPE_FIRE) {
                            arr.setDamage(arr.getDamage() * 1.25);
                        }
                    } else if (bowType == ARROW_TYPE_FIRE) {
                        arr.setOnFireFor(50);
                    }

                    arr.setDamage(arr.getDamage() * damageMult);
                }

                stack.damage(1, player, p -> p.sendToolBreakStatus(player.getActiveHand()));

                // Spawn the arrows

                if (multiShot) {
                    if (bowType == ARROW_TYPE_ENDER) { // Ender bow
                        world.spawnEntity(new ArrowSpawner(world, player.getX(), player.getY(), player.getZ(), shotVelocity, arrs));
                    } else { // Multi bow
                        for (int i = 0; i < shotArrows; ++i) {
                            final @NotNull PersistentProjectileEntity arr = arrs[i];
                            world.spawnEntity(arr);

                            final double damageMultiChoice = switch (i) {
                                case 1 -> 1.3;
                                case 2 -> 1.15;
                                default -> 1.5;
                            };

                            final @Nullable Entity arrOwner = arr.getOwner();

                            if ((i > 0) && (arrOwner != null)) {
                                final double negate = ((i & 1) << 1) - 1;
                                final double newPosX = arr.getX() + ((arrOwner.getYaw() / 180.0) * negate);
                                arr.refreshPositionAndAngles(newPosX, arr.getY(), arr.getZ(), arr.getYaw(), arr.getPitch());
                            }

                            arr.setDamage(arr.getDamage() * damageMultiChoice);
                        }
                    }
                } else { // Other bows
                    for (final PersistentProjectileEntity arr : arrs) {
                        world.spawnEntity(arr);
                    }
                }
            }

            // Play the "bow shot" sound
            if (multiShot && (bowType != ARROW_TYPE_ENDER)) { // Multi bow
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, (1.0F / ((world.getRandom().nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));

                if (shotArrows > 1) {
                    world.playSound(null, player.getX() + (player.getYaw() / 180.0F), player.getY(), player.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, (1.0F / ((world.getRandom().nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));
                }

                if (shotArrows > 2) {
                    world.playSound(null, player.getX() - (player.getYaw() / 180.0F), player.getY(), player.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, (1.0F / ((world.getRandom().nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));
                }
            } else { // Other bows
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, (1.0F / ((world.getRandom().nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));
            }

            if (!infiniteAmmo && !player.getAbilities().creativeMode) {
                ammo.decrement(usedAmmo);

                // Nyf's Quivers compatibility
                if (!alwaysShoots && MoreBows.configGeneralInst.nyfsQuiversCompatEnabled && FabricLoader.getInstance().isModLoaded("nyfsquiver")) {
                    NyfsQuiversCompat.drawFromQuiver(player, usedAmmo);
                }

                if (ammo.isEmpty()) {
                    player.getInventory().removeOne(ammo);
                }
            }

            player.incrementStat(Stats.USED.getOrCreateStat(this));
        }
    }

    /**
     * This method creates particles when left-clicking with an ender bow.
     * TODO probably replace with client-side function
     */
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (bowType == ARROW_TYPE_ENDER) {
            MoreBows.tryPart(user.world, user, ParticleTypes.PORTAL, true, 1.0);
        }

        return super.use(world, user, hand);
    }
}
