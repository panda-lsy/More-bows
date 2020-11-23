package iDiamondhunter.morebows;

import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_ENDER;
import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_FIRE;
import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_NOT_CUSTOM;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import iDiamondhunter.morebows.entities.ArrowSpawner;
import iDiamondhunter.morebows.entities.CustomArrow;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;

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
    public final byte[] iconTimes;
    private final boolean multiShot;
    private final float powerDiv;
    private final EnumRarity rarity;

    /* Icon related variables */
    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    /**
     * A more customizable bow than the vanilla one.
     *
     * @param maxDamage    The maximum damage a bow can do.
     * @param bowType      The type of arrows this bow shoots. This also influences some of the behaviors of the bow as well.
     * @param damageMult   The multiplier to damage done by an arrow shot by this bow.
     * @param iconTimes    The amount of time it takes to switch bow icons when the bow is being drawn back. TODO This is not a great solution.
     * @param multiShot    A dirty, dirty hack, indicating if this bow shoots multiple arrows or not.
     * @param powerDiv     The power divisor of this bow. TODO document better.
     * @param rarity       The rarity of this bow.
     */
    public CustomBow(int maxDamage, byte bowType, double damageMult, byte[] iconTimes, boolean multiShot, float powerDiv, EnumRarity rarity) {
        super();
        setMaxDamage(maxDamage);
        this.bowType = bowType;
        this.damageMult = damageMult;
        this.iconTimes = iconTimes;
        this.multiShot = multiShot;
        this.powerDiv = powerDiv;
        this.rarity = rarity;
    }

    /** This returns the bow sprite for a given duration of drawing the bow back. TODO This is still a bit janky. Remove this message when you're certain this is a good way to do it. */
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int u, EntityPlayer p, ItemStack i, int useRem) {
        if (i == null) {
            return itemIcon;
        }

        final int ticks = stack.getMaxItemUseDuration() - useRem;

        if (ticks >= iconTimes[0]) {
            return icons[2];
        } else if (ticks > iconTimes[1]) {
            return icons[1];
        } else if (ticks > 0) {
            return icons[0];
        } else {
            return itemIcon;
        }
    }

    /** EnumAction.none is returned, as the bow is rendered by a custom IItemRenderer which effectively applies a tweaked version of EnumAction.bow. See ModRenderer. */
    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.none;
    }

    @Override
    public EnumRarity getRarity(ItemStack item) {
        return rarity;
    }

    /** This method creates particles when left clicking with an ender bow. */
    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
        if (bowType == ARROW_TYPE_ENDER) {
            MoreBows.tryPart(entityLiving.worldObj, entityLiving, "portal", true, 1);
        }

        return false;
    }

    /** This handles the process of shooting an arrow from this bow. TODO Cleanup, document more */
    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int remaining) {
        final ArrowLooseEvent event = new ArrowLooseEvent(player, stack, (getMaxItemUseDuration(stack) - remaining));
        MinecraftForge.EVENT_BUS.post(event);

        if (event.isCanceled()) {
            return;
        }

        final boolean allwaysShoots = player.capabilities.isCreativeMode || (EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0);

        if (allwaysShoots || player.inventory.hasItem(Items.arrow)) {
            float shotVelocity = event.charge / powerDiv;
            shotVelocity = ((shotVelocity * shotVelocity) + (shotVelocity * 2.0F)) / 3.0F;

            if (shotVelocity < 0.1D) {
                return;
            }

            if (shotVelocity > 1.0F) {
                shotVelocity = 1.0F;
            }

            final EntityArrow[] arrs;

            // Create the arrows to fire
            if (multiShot) {
                if (bowType == ARROW_TYPE_ENDER) { // Ender bow
                    arrs = new EntityArrow[] {
                        new CustomArrow(world, player, shotVelocity * 2.0F, ARROW_TYPE_ENDER),
                        new CustomArrow(world, player, shotVelocity * 1F, ARROW_TYPE_ENDER),
                        new CustomArrow(world, player, shotVelocity * 1.2F, ARROW_TYPE_ENDER),
                        new CustomArrow(world, player, shotVelocity * 1.5F, ARROW_TYPE_ENDER),
                        new CustomArrow(world, player, shotVelocity * 1.75F, ARROW_TYPE_ENDER),
                        new CustomArrow(world, player, shotVelocity * 1.825F, ARROW_TYPE_ENDER)
                    };
                    arrs[1].canBePickedUp = 2;
                    arrs[2].canBePickedUp = 2;
                    arrs[3].canBePickedUp = 2;
                    arrs[4].canBePickedUp = 2;
                    arrs[5].canBePickedUp = 2;
                } else {
                    if (itemRand.nextInt(4) == 0) { // Multi bow
                        arrs = new EntityArrow[] {
                            new EntityArrow(world, player, shotVelocity * 2.0F),
                            new EntityArrow(world, player, shotVelocity * 1.65F),
                            new EntityArrow(world, player, shotVelocity * 1.275F)
                        };
                        arrs[2].canBePickedUp = 2;
                    } else {
                        arrs = new EntityArrow[] {
                            new EntityArrow(world, player, shotVelocity * 2.0F),
                            new EntityArrow(world, player, shotVelocity * 1.65F)
                        };
                    }

                    arrs[1].canBePickedUp = 2;
                }
            } else if (bowType == ARROW_TYPE_NOT_CUSTOM) { // Other bows
                arrs = new EntityArrow[] { new EntityArrow(world, player, shotVelocity * 2.0F) };
            } else { // Frost / fire bows
                arrs = new EntityArrow[] { new CustomArrow(world, player, shotVelocity * 2.0F, bowType) };
            }

            // Set up flags for adding enchantment effects / other modifiers
            final int power = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
            final int knockback = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);
            final boolean flame = (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0);

            // Add enchantment effects / other modifiers to each arrow
            for (int i = 0; i < arrs.length; ++i) {
                final EntityArrow arr = arrs[i];

                if (shotVelocity == 1.0F) { /* setIsCritical calls dataWatcher methods, avoid this unless needed with the check. */
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
                        arr.setDamage(arr.getDamage() * 1.25D); // TODO: Verify
                    }
                }

                if (bowType == ARROW_TYPE_FIRE) {
                    arr.setFire(50); // TODO: Verify. I'm pretty sure the original mod did this.
                }

                if (allwaysShoots) {
                    arr.canBePickedUp = 2;
                }

                arr.setDamage(arr.getDamage() * damageMult);
            }

            stack.damageItem(1, player);

            // Play the "bow shot" sound
            if (multiShot && (bowType != ARROW_TYPE_ENDER)) { // Multi bow
                world.playSoundAtEntity(player, "random.bow", 1.0F, (1.0F / ((itemRand.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));
                world.playSoundEffect(player.posX + (player.rotationYaw / 180), player.posY, player.posZ, "random.bow", 1.0F, (1.0F / ((itemRand.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));

                if (arrs.length > 2) {
                    world.playSoundEffect(player.posX - (player.rotationYaw / 180), player.posY, player.posZ, "random.bow", 1.0F, (1.0F / ((itemRand.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));
                }
            } else { // Other bows
                world.playSoundAtEntity(player, "random.bow", 1.0F, (1.0F / ((itemRand.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));
            }

            if (!allwaysShoots) {
                player.inventory.consumeInventoryItem(Items.arrow);
            }

            // Spawn the arrows
            if (!world.isRemote) {
                if (multiShot) {
                    if (bowType == ARROW_TYPE_ENDER) { // Ender bow
                        world.spawnEntityInWorld(new ArrowSpawner(world, player.posX, player.posY, player.posZ, shotVelocity, arrs));
                    } else { // Multi bow
                        world.spawnEntityInWorld(arrs[0]);
                        world.spawnEntityInWorld(arrs[1]);
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
                            world.spawnEntityInWorld(arrs[2]);
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
                        world.spawnEntityInWorld(arrs[i]);
                    }
                }
            }
        }
    }

    /** This method registers the icons of a given bow. */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconReg) {
        itemIcon = iconReg.registerIcon(getIconString() + "1"); // redo with off-by-one error fixed
        // this.iconArray = new IIcon[this.bowPullIconNameArray.length];
        icons = new IIcon[3];

        for (int i = 0; i < icons.length; ++i) {
            // this.iconArray[i] = iconRegistry.registerIcon(this.getIconString() + "_" + this.bowPullIconNameArray[i]);
            icons[i] = iconReg.registerIcon(getIconString() + (i + 2)); // awful hack, icons start from 2 here
            // this.iconArray[i] = iconRegistry.registerIcon(this.bowPullIconNameArray[i]);
        }
    }
}
