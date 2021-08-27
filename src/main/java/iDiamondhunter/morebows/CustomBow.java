package iDiamondhunter.morebows;

import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_ENDER;
import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_FIRE;
import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_NOT_CUSTOM;
import static iDiamondhunter.morebows.MoreBows.bowMaxUseDuration;

import javax.annotation.Nullable;

//import iDiamondhunter.morebows.entities.ArrowSpawner;
//import iDiamondhunter.morebows.entities.CustomArrow;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This entire class is a huge hack. I'm ashamed of myself. And yes, this is important to document.
 * TODO: Possibly come up with better ideas for various bits of this class.
 **/
public final class CustomBow extends ItemBow {

    /* Bow instance variables */
    private final byte bowType;
    private final double damageMult;
    /**
     * The amount of time it takes to switch bow icons when the bow is being drawn back.
     * TODO This is not a great solution.
     */
    //public final byte[] iconTimes;
    private final boolean multiShot;
    public final float powerDiv;
    private final EnumRarity rarity;

    /**
     * A more customizable bow than the vanilla one.
     *
     * @param maxDamage  The maximum damage a bow can do.
     * @param bowType    The type of arrows this bow shoots. This also influences some of the behaviors of the bow as well.
     * @param damageMult The multiplier to damage done by an arrow shot by this bow.
     * @param iconTimes  The amount of time it takes to switch bow icons when the bow is being drawn back. TODO This is not a great solution.
     * @param multiShot  A dirty, dirty hack, indicating if this bow shoots multiple arrows or not.
     * @param powerDiv   The power divisor of this bow. TODO document better.
     * @param rarity     The rarity of this bow.
     */
    public CustomBow(int maxDamage, byte bowType, double damageMult, byte[] iconTimes, boolean multiShot, float powerDiv, EnumRarity rarity) {
        setMaxDamage(maxDamage);
        this.bowType = bowType;
        this.damageMult = damageMult;
        // TODO unimplemented
        // either convert iconTimes into json predicate (powerDiv / iconTimes[n]) or possibly just speed up predicate?
        //this.iconTimes = iconTimes;
        this.multiShot = multiShot;
        this.powerDiv = powerDiv;
        this.rarity = rarity;
        maxStackSize = 1;
        setMaxDamage(384);
        setCreativeTab(CreativeTabs.COMBAT);
        addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter() {
            @Override
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack bow, @Nullable World world, @Nullable EntityLivingBase entity) {
                if (entity == null) {
                    return 0.0F;
                }

                return !(entity.getActiveItemStack().getItem() instanceof CustomBow) ? 0.0F : (bowMaxUseDuration - entity.getItemInUseCount()) / powerDiv;
            }
        });
        addPropertyOverride(new ResourceLocation("pulling"), new IItemPropertyGetter() {
            @Override
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
                return (entityIn != null) && entityIn.isHandActive() && (entityIn.getActiveItemStack() == stack) ? 1.0F : 0.0F;
            }
        });
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
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

                // Create the arrows to fire
                // TODO arrow velocity defaults have changed. I have not updated these yet.
                if (multiShot) { // Bows that shoot multiple arrows
                    if (bowType == ARROW_TYPE_ENDER) { // Ender bow
                        arrs = new EntityArrow[] {
                            arrowHelper(world, player, shotVelocity * 2.0F, ammo, arrow),
                            arrowHelper(world, player, shotVelocity * 1F, ammo, arrow),
                            arrowHelper(world, player, shotVelocity * 1.2F, ammo, arrow),
                            arrowHelper(world, player, shotVelocity * 1.5F, ammo, arrow),
                            arrowHelper(world, player, shotVelocity * 1.75F, ammo, arrow),
                            arrowHelper(world, player, shotVelocity * 1.825F, ammo, arrow)
                        };
                        arrs[1].pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
                        arrs[2].pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
                        arrs[3].pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
                        arrs[4].pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
                        arrs[5].pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
                    } else {
                        if (itemRand.nextInt(4) == 0) { // Multi bow
                            arrs = new EntityArrow[] {
                                arrowHelper(world, player, shotVelocity * 2.0F, ammo, arrow),
                                arrowHelper(world, player, shotVelocity * 1.65F, ammo, arrow),
                                arrowHelper(world, player, shotVelocity * 1.275F, ammo, arrow)
                            };
                            arrs[2].pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
                        } else {
                            arrs = new EntityArrow[] {
                                arrowHelper(world, player, shotVelocity * 2.0F, ammo, arrow),
                                arrowHelper(world, player, shotVelocity * 1.65F, ammo, arrow)
                            };
                        }

                        arrs[1].pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
                    }
                } else if (bowType == ARROW_TYPE_NOT_CUSTOM) { // "Standard" style bows that do not shoot multiple arrows or have a custom arrow type. Note to self: this is after the multi-arrow bows due to the multi bow having arrows of a normal type.
                    arrs = new EntityArrow[] { arrowHelper(world, player, shotVelocity * 2.0F, ammo, arrow) };
                } else { // Bows that shoot only one custom arrow, currently only frost / fire bows
                    // TODO unimplemented
                    arrs = new EntityArrow[] { arrowHelper(world, player, shotVelocity * 2.0F, ammo, arrow) };
                }

                if (alwaysShoots) {
                    arrs[0].pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
                }

                // Set up flags for adding enchantment effects / other modifiers
                final int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, bow);
                final int knockback = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, bow);
                final boolean flame = EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, bow) > 0;

                // Add enchantment effects / other modifiers to each arrow
                // TODO review
                for (int i = 0; i < arrs.length; ++i) {
                    if (isCrit) { /* setIsCritical calls dataWatcher methods, avoid calling it unless needed. */
                        arrs[i].setIsCritical(true);
                    }

                    if (power > 0) {
                        arrs[i].setDamage(arrs[i].getDamage() + (power * 0.5D) + 0.5D);
                    }

                    if (knockback > 0) {
                        arrs[i].setKnockbackStrength(knockback);
                    }

                    if (flame) {
                        arrs[i].setFire(100);

                        if (bowType == ARROW_TYPE_FIRE) {
                            arrs[i].setDamage(arrs[i].getDamage() * 1.25D);
                        }
                    } else if (bowType == ARROW_TYPE_FIRE) {
                        arrs[i].setFire(50);
                    }

                    arrs[i].setDamage(arrs[i].getDamage() * damageMult);
                }

                bow.damageItem(1, player);

                // Spawn the arrows

                if (multiShot) {
                    if (bowType == ARROW_TYPE_ENDER) { // Ender bow
                        // TODO unimplemented
                        //world.spawnEntity(new ArrowSpawner(world, player.posX, player.posY, player.posZ, shotVelocity, arrs));
                        // temporary
                        world.spawnEntity(arrs[0]);
                        world.spawnEntity(arrs[1]);
                        world.spawnEntity(arrs[2]);
                        world.spawnEntity(arrs[3]);
                        world.spawnEntity(arrs[4]);
                        world.spawnEntity(arrs[5]);
                    } else { // Multi bow
                        world.spawnEntity(arrs[0]);
                        world.spawnEntity(arrs[1]);
                        /**
                         * This was some code that checked the rotationYaw of the shooting player, but didn't change anything.
                         * TODO figure out which is supposed to be changed and to what
                         *
                         * <pre>
                         * if (arrs[1].shootingEntity.rotationYaw > 180) {
                         *     arrs[1].posX = arrs[1].posX + (arrs[1].shootingEntity.rotationYaw / 180);
                         * } else {
                         *     arrs[1].posX = arrs[1].posX + (arrs[1].shootingEntity.rotationYaw / 180);
                         * }
                         * </pre>
                         */
                        arrs[1].posX = arrs[1].posX + (arrs[1].shootingEntity.rotationYaw / 180);
                        arrs[0].setDamage(arrs[0].getDamage() * 1.5D);
                        arrs[1].setDamage(arrs[1].getDamage() * 1.3D);

                        if (arrs.length > 2) {
                            world.spawnEntity(arrs[2]);
                            /**
                             * This was some code that checked the rotationYaw of the shooting player, but didn't change anything.
                             * TODO figure out which is supposed to be changed and to what
                             *
                             * <pre>
                             * if (arrs[2].shootingEntity.rotationYaw > 180) {
                             *     arrs[2].posX = arrs[2].posX - (arrs[2].shootingEntity.rotationYaw / 180);
                             * } else {
                             *     arrs[2].posX = arrs[2].posX - (arrs[2].shootingEntity.rotationYaw / 180);
                             * }
                             * </pre>
                             */
                            arrs[2].posX = arrs[2].posX - (arrs[2].shootingEntity.rotationYaw / 180);
                            arrs[2].setDamage(arrs[2].getDamage() * 1.15D);
                        }
                    }
                } else { // Other bows
                    for (int i = 0; i < arrs.length; ++i) {
                        world.spawnEntity(arrs[i]);
                    }
                }
            }

            // Play the "bow shot" sound
            if (multiShot && (bowType != ARROW_TYPE_ENDER)) { // Multi bow
                world.playSound(player, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, (1.0F / ((itemRand.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));
                world.playSound(player, player.posX + (player.rotationYaw / 180), player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, (1.0F / ((itemRand.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));
                // TODO
                /*if (arrs.length > 2) {
                	world.playSound(player, player.posX - (player.rotationYaw / 180), player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + shotVelocity * 0.5F);
                }*/
            } else { // Other bows
                world.playSound(player, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, (1.0F / ((itemRand.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));
            }

            // TODO merge somehow
            final boolean infiniteAmmo = player.capabilities.isCreativeMode || ((ammo.getItem() instanceof ItemArrow) && ((ItemArrow) ammo.getItem()).isInfinite(ammo, bow, player));

            if (!infiniteAmmo) {
                // TODO review
                ammo.shrink(1);

                if (ammo.isEmpty()) {
                    player.inventory.deleteStack(ammo);
                }
            }

            player.addStat(StatList.getObjectUseStats(this));
        }
    }

    // TODO review
    private EntityArrow arrowHelper(World world, EntityPlayer player, float velocity, ItemStack ammo, ItemArrow arrow) {
        EntityArrow entityarrow = arrow.createArrow(world, ammo, player);
        entityarrow = customizeArrow(entityarrow);
        entityarrow.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, velocity, 1.0F);
        return entityarrow;
    }

    // TODO why was this a static method :(
    /*private float customArrowVelocity(int charge) {
        float shotVelocity = charge / powerDiv;
        shotVelocity = ((shotVelocity * shotVelocity) + (shotVelocity * 2.0F)) / 3.0F;

        if (shotVelocity > 1.0F) {
            shotVelocity = 1.0F;
        }

        return shotVelocity;
    }*/
}
