package iDiamondhunter.morebows;

import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_ENDER;
import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_FIRE;
import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_NOT_CUSTOM;
import static iDiamondhunter.morebows.MoreBows.bowMaxUseDuration;

import iDiamondhunter.morebows.config.ConfigGeneral;
import iDiamondhunter.morebows.config.ConfigGeneral.CustomArrowMultiShotType;
import iDiamondhunter.morebows.entities.ArrowSpawner;
import iDiamondhunter.morebows.entities.CustomArrow;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;

/**
 * This entire class is a huge hack. I'm ashamed of myself. And yes, this is important to document.
 * TODO: Possibly come up with better ideas for various bits of this class.
 **/
final class CustomBow extends ItemBow {

    private static final ItemStack defaultAmmo = new ItemStack(Items.ARROW);
    private static final ItemArrow defaultArrow = (ItemArrow) Items.ARROW;

    /* Bow instance variables */
    private final byte bowType;
    private final double damageMult;
    private final boolean multiShot;
    final float powerDiv;
    private final EnumRarity rarity;

    /**
     * A more customizable bow than the vanilla one.
     *
     * @param maxDamage  The durability of the bow.
     * @param bowType    The type of arrows this bow shoots. This also influences some behaviors of the bow as well.
     * @param damageMult The multiplier to damage done by an arrow shot by this bow.
     * @param multiShot  A dirty, dirty hack, indicating if this bow shoots multiple arrows or not.
     * @param powerDiv   The power divisor of this bow. TODO document better.
     * @param rarity     The rarity of this bow.
     */
    CustomBow(int maxDamage, byte bowType, double damageMult, boolean multiShot, float powerDiv, EnumRarity rarity) {
        setMaxDamage(maxDamage);
        this.bowType = bowType;
        this.damageMult = damageMult;
        this.multiShot = multiShot;
        this.powerDiv = powerDiv;
        this.rarity = rarity;
        addPropertyOverride(new ResourceLocation("pull"), (ItemStack bow, World world, EntityLivingBase entity) -> {
            if (entity == null) {
                return 0.0F;
            }

            return (bowMaxUseDuration - entity.getItemInUseCount()) / powerDiv;
        });
    }

    // TODO review
    private EntityArrow arrowHelper(World world, EntityPlayer player, float velocity, ItemStack ammo, ItemArrow arrow) {
        final EntityArrow entityarrow = arrow.createArrow(world, ammo, player);
        return arrowHelperHelper(player, velocity, entityarrow);
    }

    // TODO review
    private EntityArrow arrowHelperHelper(EntityPlayer player, float velocity, EntityArrow entityarrow) {
        entityarrow = customizeArrow(entityarrow);
        entityarrow.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, velocity, 1.0F);
        return entityarrow;
    }

    // TODO review
    private EntityArrow customArrowHelper(World world, EntityPlayer player, float velocity) {
        final EntityArrow entityarrow = new CustomArrow(world, player, bowType);
        return arrowHelperHelper(player, velocity, entityarrow);
    }

    @Override
    public IRarity getForgeRarity(ItemStack stack) {
        return rarity;
    }

    /** This method creates particles when left clicking with an ender bow. TODO probably replace with client-side function */
    @Override
    public boolean onEntitySwing(EntityLivingBase player, ItemStack stack) {
        if (bowType == ARROW_TYPE_ENDER) {
            MoreBows.tryPart(player.world, player, EnumParticleTypes.PORTAL, true, 1);
        }

        return false;
    }

    /** This handles the process of shooting an arrow from this bow. TODO Cleanup, document more */
    @Override
    public void onPlayerStoppedUsing(ItemStack bow, World world, EntityLivingBase living, int useRem) {
        if (!(living instanceof EntityPlayer)) {
            return;
        }

        final EntityPlayer player = (EntityPlayer)living;
        final boolean alwaysShoots = player.capabilities.isCreativeMode || (EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, bow) > 0);
        ItemStack ammo = findAmmo(player);
        final int charge = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(bow, world, player, bowMaxUseDuration - useRem, !ammo.isEmpty() || alwaysShoots);

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
            final boolean infiniteAmmo = player.capabilities.isCreativeMode || ((ammo.getItem() instanceof ItemArrow) && ((ItemArrow) ammo.getItem()).isInfinite(ammo, bow, player));
            final boolean bonusArrow = itemRand.nextInt(4) == 0;
            final int ammoCount = ammo.getCount();
            final int usedAmmo;
            final int shotArrows;
            
            // TODO Redo all of this.

            if ((ConfigGeneral.customArrowMultiShot == CustomArrowMultiShotType.UseAmountShot) && multiShot) {
                if (bowType == ARROW_TYPE_ENDER) { // Ender bow
                    usedAmmo = ammoCount > 6 ? 6 : ammoCount;
                } else {
                    final int maxAmmo = bonusArrow ? 3 : 2;
                    usedAmmo = ammoCount > maxAmmo ? maxAmmo : ammoCount;
                }
            } else {
                usedAmmo = 1;
            }

            if (ConfigGeneral.customArrowMultiShot == CustomArrowMultiShotType.UseAmountShot) {
                shotArrows = usedAmmo;
            } else {
                if (bowType == ARROW_TYPE_ENDER) { // Ender bow
                    shotArrows = 6;
                } else {
                    shotArrows = bonusArrow ? 3 : 2;
                }
            }

            if (!world.isRemote) {
                final boolean isCrit;

                if (shotVelocity >= 1.0F) {
                    shotVelocity = 1.0F;
                    isCrit = true;
                } else {
                    isCrit = false;
                }

                final ItemArrow arrow = (ItemArrow)(ammo.getItem() instanceof ItemArrow ? ammo.getItem() : Items.ARROW);
                final EntityArrow[] arrs;

                /**
                 * Create the arrows to fire.
                 * The velocity multiplier for each arrow is written as a value multiplied by 1.5F.
                 * This is because this mod was originally for older versions of Minecraft,
                 * where the vanilla bow had a base velocity multiplier of 2.0F.
                 * As I still want to maintain and backport changes for the 1.7.10 version of this mod,
                 * (and because I can't figure out how the velocity multipliers for each CustomBow were chosen),
                 * I have simply multiplied each velocity multiplier
                 * to be the equivalent value for a base multiplier of 3.0F instead
                 * (2.0F * 1.5F == 3.0F).
                 */
                if (multiShot) { // Bows that shoot multiple arrows
                    final ItemStack useAmmo;
                    final ItemArrow useArrow;
                    final EntityArrow.PickupStatus pickStatus = (ConfigGeneral.customArrowMultiShot == CustomArrowMultiShotType.UseAmountShot) && !infiniteAmmo ? EntityArrow.PickupStatus.ALLOWED : EntityArrow.PickupStatus.CREATIVE_ONLY;

                    if (ConfigGeneral.customArrowMultiShot == CustomArrowMultiShotType.FreeExtraNormalArrows) {
                        useAmmo = defaultAmmo;
                        useArrow = defaultArrow;
                    } else {
                        useAmmo = ammo;
                        useArrow = arrow;
                    }

                    if (bowType == ARROW_TYPE_ENDER) { // Ender bow
                        arrs = new EntityArrow[shotArrows];

                        for (int i = 0; i < arrs.length; ++i) {
                            switch (i) {
                            case 0:
                                arrs[i] = possiblyCustomArrowHelper(world, player, shotVelocity * (2.0F * 1.5F), ammo, arrow);
                                break;

                            case 1:
                                arrs[i] = possiblyCustomArrowHelper(world, player, shotVelocity * (1.0F * 1.5F), useAmmo, useArrow);
                                arrs[i].pickupStatus = pickStatus;
                                break;

                            case 2:
                                arrs[i] = possiblyCustomArrowHelper(world, player, shotVelocity * (1.2F * 1.5F), useAmmo, useArrow);
                                arrs[i].pickupStatus = pickStatus;
                                break;

                            case 3:
                                arrs[i] =  possiblyCustomArrowHelper(world, player, shotVelocity * (1.5F * 1.5F), useAmmo, useArrow);
                                arrs[i].pickupStatus = pickStatus;
                                break;

                            case 4:
                                arrs[i] = possiblyCustomArrowHelper(world, player, shotVelocity * (1.75F * 1.5F), useAmmo, useArrow);
                                arrs[i].pickupStatus = pickStatus;
                                break;

                            case 5:
                                arrs[i] = possiblyCustomArrowHelper(world, player, shotVelocity * (1.825F * 1.5F), useAmmo, useArrow);
                                arrs[i].pickupStatus = pickStatus;
                                break;

                            default:
                                arrs[i] = possiblyCustomArrowHelper(world, player, shotVelocity * (2.0F * 1.5F), useAmmo, useArrow);
                                arrs[i].pickupStatus = pickStatus;
                                break;
                            }
                        }
                    } else {
                        arrs = new EntityArrow[shotArrows];

                        for (int i = 0; i < arrs.length; ++i) {
                            switch (i) {
                            case 0:
                                arrs[i] = arrowHelper(world, player, shotVelocity * (2.0F * 1.5F), ammo, arrow);
                                break;

                            case 1:
                                arrs[i] = arrowHelper(world, player, shotVelocity * (1.65F * 1.5F), useAmmo, useArrow);
                                arrs[i].pickupStatus = pickStatus;
                                break;

                            case 2:
                                arrs[i] = arrowHelper(world, player, shotVelocity * (1.275F * 1.5F), useAmmo, useArrow);
                                arrs[i].pickupStatus = pickStatus;
                                break;

                            default:
                                arrs[i] = arrowHelper(world, player, shotVelocity * (2.0F * 1.5F), ammo, arrow);
                                arrs[i].pickupStatus = pickStatus;
                                break;
                            }
                        }
                    }
                } else if (bowType == ARROW_TYPE_NOT_CUSTOM) { // "Standard" style bows that do not shoot multiple arrows or have a custom arrow type. Note to self: this is after the multi-arrow bows due to the multi bow having arrows of a normal type.
                    arrs = new EntityArrow[] { arrowHelper(world, player, shotVelocity * (2.0F * 1.5F), ammo, arrow) };
                } else { // Bows that shoot only one custom arrow, currently only frost / fire bows
                    arrs = new EntityArrow[] { possiblyCustomArrowHelper(world, player, shotVelocity * (2.0F * 1.5F), ammo, arrow) };
                }

                if (infiniteAmmo) {
                    arrs[0].pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
                }

                // Set up flags for adding enchantment effects / other modifiers
                final int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, bow);
                final int knockback = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, bow);
                final boolean flame = EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, bow) > 0;

                // Add enchantment effects / other modifiers to each arrow
                // TODO review
                for (final EntityArrow arr : arrs) {
                    if (isCrit) { /* setIsCritical calls dataWatcher methods, avoid calling it unless needed. */
                        arr.setIsCritical(true);
                    }

                    if (power > 0) {
                        arr.setDamage(arr.getDamage() + (power * 0.5D) + 0.5D);
                    }

                    if (knockback > 0) {
                        arr.setKnockbackStrength(knockback);
                    }

                    if (flame) {
                        arr.setFire(100);

                        if (bowType == ARROW_TYPE_FIRE) {
                            arr.setDamage(arr.getDamage() * 1.25D);
                        }
                    } else if (bowType == ARROW_TYPE_FIRE) {
                        arr.setFire(50);
                    }

                    arr.setDamage(arr.getDamage() * damageMult);
                }

                bow.damageItem(1, player);

                // Spawn the arrows

                if (multiShot) {
                    if (bowType == ARROW_TYPE_ENDER) { // Ender bow
                        world.spawnEntity(new ArrowSpawner(world, player.posX, player.posY, player.posZ, shotVelocity, arrs));
                    } else { // Multi bow
                        for (int i = 0; i < arrs.length; ++i) {
                            switch (i) {
                            case 0:
                                world.spawnEntity(arrs[i]);
                                arrs[i].setDamage(arrs[i].getDamage() * 1.5D);
                                break;

                            case 1:
                                world.spawnEntity(arrs[i]);

                                if (arrs[i].shootingEntity != null) {
                                    arrs[i].posX = arrs[i].posX + (arrs[i].shootingEntity.rotationYaw / 180);
                                }

                                arrs[i].setDamage(arrs[i].getDamage() * 1.3D);
                                break;

                            case 2:
                                world.spawnEntity(arrs[i]);

                                if (arrs[i].shootingEntity != null) {
                                    arrs[i].posX = arrs[i].posX - (arrs[i].shootingEntity.rotationYaw / 180);
                                }

                                arrs[i].setDamage(arrs[i].getDamage() * 1.15D);
                                break;

                            default:
                                world.spawnEntity(arrs[i]);
                                break;
                            }
                        }
                    }
                } else { // Other bows
                    for (final EntityArrow arr : arrs) {
                        world.spawnEntity(arr);
                    }
                }
            }

            // Play the "bow shot" sound
            if (multiShot && (bowType != ARROW_TYPE_ENDER)) { // Multi bow
                world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, (1.0F / ((itemRand.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));
                world.playSound(null, player.posX + (player.rotationYaw / 180), player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, (1.0F / ((itemRand.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));

                if (bonusArrow) {
                    world.playSound(null, player.posX - (player.rotationYaw / 180), player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, (1.0F / ((itemRand.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));
                }
            } else { // Other bows
                world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, (1.0F / ((itemRand.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));
            }

            if (!infiniteAmmo) {
                // TODO review
                ammo.shrink(usedAmmo);

                if (ammo.isEmpty()) {
                    player.inventory.deleteStack(ammo);
                }
            }

            final StatBase stat = StatList.getObjectUseStats(this);

            if (stat != null) {
                player.addStat(stat);
            }
        }
    }

    // TODO review
    private EntityArrow possiblyCustomArrowHelper(World world, EntityPlayer player, float velocity, ItemStack ammo, ItemArrow arrow) {
        if (arrow == Items.ARROW) {
            return customArrowHelper(world, player, velocity);
        }

        return arrowHelper(world, player, velocity, ammo, arrow);
    }

}
