package iDiamondhunter.morebows;

import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_ENDER;
import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_FIRE;
import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_FROST;
import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_NOT_CUSTOM;

import java.util.Random;
import java.util.function.Supplier;

import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import iDiamondhunter.morebows.config.ConfigGeneral.CustomArrowMultiShotType;
import iDiamondhunter.morebows.entities.ArrowSpawner;
import iDiamondhunter.morebows.entities.CustomArrow;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Lazy;

/**
 * This entire class is a huge hack. I'm ashamed of myself.
 * And yes, this is important to document.
 * TODO: Possibly come up with better ideas for various bits of this class.
 **/
public final class CustomBow extends BowItem {

    private static final ItemStack defaultAmmo = new ItemStack(Items.ARROW);
    private static final ArrowItem defaultArrow = (ArrowItem) Items.ARROW;

    /** TODO review */
    private AbstractArrowEntity arrowHelper(World world, PlayerEntity player, float velocity, ItemStack ammo, ArrowItem arrow) {
        final AbstractArrowEntity entityarrow = arrow.createArrow(world, ammo, player);
        return arrowHelperHelper(player, velocity, entityarrow);
    }

    /** TODO review */
    private AbstractArrowEntity arrowHelperHelper(PlayerEntity player, float velocity, AbstractArrowEntity entityarrow) {
        entityarrow = customArrow(entityarrow);
        entityarrow.shootFromRotation(player, player.xRot, player.yRot, 0.0f, velocity, 1.0f);
        return entityarrow;
    }

    /** TODO review */
    private AbstractArrowEntity customArrowHelper(World world, PlayerEntity player, float velocity, byte arrType) {
        final AbstractArrowEntity entityarrow = new CustomArrow(world, player, arrType);
        return arrowHelperHelper(player, velocity, entityarrow);
    }

    /** TODO review */
    private AbstractArrowEntity possiblyCustomArrowHelper(World world, PlayerEntity player, float velocity, ItemStack ammo, ArrowItem arrow, byte arrType) {
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

    /** The material that can be used to repair this bow with */
    private final Lazy<Ingredient> repairIngredient;

    /**
     * A more customizable bow than the vanilla one.
     *
     * @param settings         The settings TODO
     * @param repairIngredient The type of material that can be used to repair this bow with
     * @param bowType          The type of arrows this bow shoots.
     *                         This also influences some behaviors of the bow as well.
     * @param damageMult       The multiplier to damage done by an arrow shot by this bow.
     * @param multiShot        True if this bow shoots multiple arrows.
     * @param powerDiv         The power divisor of this bow. Influences drawback speed.
     */
    CustomBow(Item.Properties settings, Supplier<Ingredient> repairIngredient, @MagicConstant(intValues = {ARROW_TYPE_NOT_CUSTOM, ARROW_TYPE_ENDER, ARROW_TYPE_FIRE, ARROW_TYPE_FROST}) byte bowType, double damageMult, boolean multiShot, float powerDiv) {
        super(settings);
        this.repairIngredient = Lazy.concurrentOf(repairIngredient);
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
    public void releaseUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity)) {
            return;
        }

        final PlayerEntity player = (PlayerEntity) user;
        final boolean alwaysShoots = player.abilities.instabuild || (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack) > 0);
        ItemStack ammo = player.getProjectile(stack);
        final int charge = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, world, player, getUseDuration(stack) - remainingUseTicks, !stack.isEmpty() || alwaysShoots);

        if (charge < 0) {
            return;
        }

        if (!ammo.isEmpty() || alwaysShoots) {
            if (ammo.isEmpty()) {
                ammo = new ItemStack(Items.ARROW);
            }

            float shotVelocity = charge / powerDiv;
            shotVelocity = ((shotVelocity * shotVelocity) + (shotVelocity * 2.0F)) / 3.0F;

            if (shotVelocity < 0.1F) {
                return;
            }

            // TODO merge with alwaysShoots somehow
            //final boolean infiniteAmmo = alwaysShoots && ammo.isOf(Items.ARROW);
            final boolean infiniteAmmo = player.abilities.instabuild || ((ammo.getItem() instanceof ArrowItem) && ((ArrowItem)ammo.getItem()).isInfinite(ammo, stack, player));
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

            if (!world.isClientSide) {
                final boolean isCrit;

                if (shotVelocity >= 1.0F) {
                    shotVelocity = 1.0F;
                    isCrit = true;
                } else {
                    isCrit = false;
                }

                final ArrowItem arrow = (ArrowItem) (ammo.getItem() instanceof ArrowItem ? ammo.getItem() : Items.ARROW);
                final @NotNull AbstractArrowEntity @NotNull [] arrs;

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
                    final AbstractArrowEntity.PickupStatus pickStatus = (MoreBows.configGeneralInst.customArrowMultiShot == CustomArrowMultiShotType.UseAmountShot) && !infiniteAmmo ? AbstractArrowEntity.PickupStatus.ALLOWED : AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;

                    if (MoreBows.configGeneralInst.customArrowMultiShot == CustomArrowMultiShotType.AlwaysStandardArrows) {
                        useAmmo = defaultAmmo;
                        useArrow = defaultArrow;
                    } else {
                        useAmmo = ammo;
                        useArrow = arrow;
                    }

                    if (bowType == ARROW_TYPE_ENDER) { // Ender bow
                        arrs = new AbstractArrowEntity[shotArrows];

                        for (int i = 0; i < shotArrows; ++i) {
                            final float velocityChoice;

                            switch (i) {
                            case 1:
                                velocityChoice = shotVelocity * (1.0F * 1.5F);
                                break;

                            case 2:
                                velocityChoice = shotVelocity * (1.2F * 1.5F);
                                break;

                            case 3:
                                velocityChoice = shotVelocity * (1.5F * 1.5F);
                                break;

                            case 4:
                                velocityChoice = shotVelocity * (1.75F * 1.5F);
                                break;

                            case 5:
                                velocityChoice = shotVelocity * (1.825F * 1.5F);
                                break;

                            default:
                                velocityChoice = shotVelocity * (2.0F * 1.5F);
                                break;
                            }

                            if (i > 0) {
                                arrs[i] = possiblyCustomArrowHelper(world, player, velocityChoice, useAmmo, useArrow, bowType);
                                arrs[i].pickup = pickStatus;
                            } else {
                                arrs[i] = possiblyCustomArrowHelper(world, player, velocityChoice, ammo, arrow, bowType);
                            }
                        }
                    } else {
                        arrs = new AbstractArrowEntity[shotArrows];

                        for (int i = 0; i < shotArrows; ++i) {
                            final float velocityChoice;

                            switch (i) {
                            case 1:
                                velocityChoice = shotVelocity * (1.65F * 1.5F);
                                break;

                            case 2:
                                velocityChoice = shotVelocity * (1.275F * 1.5F);
                                break;

                            default:
                                velocityChoice = shotVelocity * (2.0F * 1.5F);
                                break;
                            }

                            if (i > 0) {
                                arrs[i] = arrowHelper(world, player, velocityChoice, useAmmo, useArrow);
                                arrs[i].pickup = pickStatus;
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
                    arrs = new AbstractArrowEntity[] { arrowHelper(world, player, shotVelocity * (2.0F * 1.5F), ammo, arrow) };
                } else { // Bows that shoot only one custom arrow, currently only frost / fire bows
                    arrs = new AbstractArrowEntity[] { possiblyCustomArrowHelper(world, player, shotVelocity * (2.0F * 1.5F), ammo, arrow, bowType) };
                }

                //if (infiniteAmmo || (player.getAbilities().creativeMode && (ammo.isOf(Items.SPECTRAL_ARROW) || ammo.isOf(Items.TIPPED_ARROW)))) {
                if (infiniteAmmo || (player.abilities.instabuild && ((ammo.getItem() == Items.SPECTRAL_ARROW) || (ammo.getItem() == Items.TIPPED_ARROW)))) {
                    arrs[0].pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                }

                // Set up flags for adding enchantment effects / other modifiers
                final int power = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
                final int knockback = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);
                final boolean flame = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) > 0;

                // Add enchantment effects / other modifiers to each arrow
                // TODO review
                for (final AbstractArrowEntity arr : arrs) {
                    if (isCrit) { /* setIsCritical calls dataWatcher methods, avoid calling it unless needed. */
                        arr.setCritArrow(true);
                    }

                    if (power > 0) {
                        arr.setBaseDamage(arr.getBaseDamage() + (power * 0.5) + 0.5);
                    }

                    if (knockback > 0) {
                        arr.setKnockback(knockback);
                    }

                    if (flame) {
                        arr.setSecondsOnFire(100);

                        if (bowType == ARROW_TYPE_FIRE) {
                            arr.setBaseDamage(arr.getBaseDamage() * 1.25);
                        }
                    } else if (bowType == ARROW_TYPE_FIRE) {
                        arr.setSecondsOnFire(50);
                    }

                    arr.setBaseDamage(arr.getBaseDamage() * damageMult);
                }

                stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));

                // Spawn the arrows

                if (multiShot) {
                    if (bowType == ARROW_TYPE_ENDER) { // Ender bow
                        world.addFreshEntity(new ArrowSpawner(world, player.getX(), player.getY(), player.getZ(), shotVelocity, arrs));
                    } else { // Multi bow
                        for (int i = 0; i < shotArrows; ++i) {
                            final @NotNull AbstractArrowEntity arr = arrs[i];
                            world.addFreshEntity(arr);
                            final double damageMultiChoice;

                            switch (i) {
                            case 1:
                                damageMultiChoice = 1.3;
                                break;

                            case 2:
                                damageMultiChoice = 1.15;
                                break;

                            default:
                                damageMultiChoice = 1.5;
                                break;
                            }

                            final @Nullable Entity arrOwner = arr.getOwner();

                            if ((i > 0) && (arrOwner != null)) {
                                final double negate = ((i & 1) << 1) - 1;
                                final double newPosX = arr.getX() + ((arrOwner.yRot / 180.0) * negate);
                                arr.setPos(newPosX, arr.getY(), arr.getZ());
                            }

                            arr.setBaseDamage(arr.getBaseDamage() * damageMultiChoice);
                        }
                    }
                } else { // Other bows
                    for (final AbstractArrowEntity arr : arrs) {
                        world.addFreshEntity(arr);
                    }
                }
            }

            // Play the "bow shot" sound
            if (multiShot && (bowType != ARROW_TYPE_ENDER)) { // Multi bow
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, (1.0F / ((world.getRandom().nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));

                if (shotArrows > 1) {
                    world.playSound(null, player.getX() + (player.yRot / 180.0F), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, (1.0F / ((world.getRandom().nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));
                }

                if (shotArrows > 2) {
                    world.playSound(null, player.getX() - (player.yRot / 180.0F), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, (1.0F / ((world.getRandom().nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));
                }
            } else { // Other bows
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, (1.0F / ((world.getRandom().nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));
            }

            if (!infiniteAmmo && !player.abilities.instabuild) {
                ammo.shrink(usedAmmo);

                if (ammo.isEmpty()) {
                    player.inventory.removeItem(ammo);
                }
            }

            player.awardStat(Stats.ITEM_USED.get(this));
        }
    }

    /**
     * This method creates particles when left-clicking with an ender bow.
     * TODO probably replace with client-side function
     */
    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (bowType == ARROW_TYPE_ENDER) {
            MoreBows.tryPart(user.level, user, ParticleTypes.PORTAL, true, 1.0);
        }

        return super.use(world, user, hand);
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack ingredient) {
        return repairIngredient.get().test(ingredient) || super.isValidRepairItem(stack, ingredient);
    }

    @Override
    public boolean isRepairable(ItemStack stack) {
        return true;
    }

}
