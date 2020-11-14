package iDiamondhunter.morebows.bows;

import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_FIRE;
import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_NOT_CUSTOM;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import iDiamondhunter.morebows.entities.CustomArrow;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
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
public class CustomBow extends ItemBow {

    /* Bow instance variables */
    private final double damageMult;
    private final int flameTime;
    private final float powerDiv;
    private final EnumRarity rarity;
    private final byte arrowType;
    private final float velocityMult;

    /* Icon related variables */
    @SideOnly(Side.CLIENT)
    private IIcon[] icons;
    /**
     * The amount of time it takes to switch bow icons when the bow is being drawn back.
     * TODO This is not a great solution.
     */
    public final byte[] iconTimes;

    /**
     * A more customizable bow than the vanilla one.
     *
     * @param maxDamage    The maximum damage a bow can do.
     * @param rarity       The rarity of this bow.
     * @param iconTimes    The amount of time it takes to switch bow icons when the bow is being drawn back. TODO This is not a great solution.
     * @param velocityMult The velocity multiplier of this bow. TODO document better.
     * @param powerDiv     The power divisor of this bow. TODO document better.
     * @param flameTime    The amount of time an arrow burns for when shot on fire with this bow (usually through the flame enchantment).
     * @param damageMult   The multiplier to damage done by an arrow shot by this bow.
     * @param arrowType    The type of arrows this bow shoots.
     */
    public CustomBow(int maxDamage, EnumRarity rarity, byte[] iconTimes, float velocityMult, float powerDiv, int flameTime, double damageMult, byte arrowType) {
        maxStackSize = 1;
        setMaxDamage(maxDamage);
        setCreativeTab(CreativeTabs.tabCombat);
        setMaxDamage(maxDamage);
        this.rarity = rarity;
        this.powerDiv = powerDiv;
        this.velocityMult = velocityMult;
        this.flameTime = flameTime;
        this.damageMult = damageMult;
        this.arrowType = arrowType;
        this.iconTimes = iconTimes;
    }

    /** This returns the bow sprite for a given duration of drawing the bow back. TODO This is still a bit janky. Remove this message when you're certain this is a good way to do it. */
    @Override
    @SideOnly(Side.CLIENT)
    public final IIcon getIcon(ItemStack stack, int u, EntityPlayer p, ItemStack i, int useRem) {
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

    /** EnumAction.none is returned, as the bow is rendered by a custom IItemRenderer which effectively applies a tweaked version of EnumAction.bow. See RenderBow. */
    @Override
    public final EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.none;
    }

    @Override
    public final EnumRarity getRarity(ItemStack item) {
        return rarity;
    }

    /** This handles the process of shooting an arrow from this bow. TODO Cleanup, document more */
    @Override
    public final void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int remaining) {
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

            // Get the arrows to fire
            final EntityArrow[] arrs = setArrows(world, player, shotVelocity);
            // Set up flags for adding enchantment effects / other modifiers
            final int power = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
            final int knockback = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);
            final boolean flame = (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0);

            // Add enchantment effects / other modifiers to each arrow
            for (final EntityArrow arr : arrs) {
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
                    arr.setFire(flameTime);

                    if (arrowType == ARROW_TYPE_FIRE) {
                        arr.setDamage(arr.getDamage() * 1.25D); // TODO: Verify
                    }
                }

                if (arrowType == ARROW_TYPE_FIRE) {
                    arr.setFire(50); // TODO: Verify. I'm pretty sure the original mod did this.
                }

                if (allwaysShoots) {
                    arr.canBePickedUp = 2;
                }

                arr.setDamage(arr.getDamage() * damageMult);
            }

            stack.damageItem(1, player);
            playNoise(world, player, arrs, shotVelocity);

            if (!allwaysShoots) {
                player.inventory.consumeInventoryItem(Items.arrow);
            }

            // Spawn the arrows
            if (!world.isRemote) {
                spawnArrows(world, player, shotVelocity, arrs);
            }
        }
    }

    /**
     * This method plays the bow releasing noise for a given release of the bow. TODO Remove this?
     *
     * @param world        The world that the arrows are in.
     * @param player       The player shooting the arrows.
     * @param arrs         The arrows that a bow is shooting.
     * @param shotVelocity The velocity of the arrows.
     */
    protected void playNoise(World world, EntityPlayer player, EntityArrow[] arrs, float shotVelocity) {
        world.playSoundAtEntity(player, "random.bow", 1.0F, (1.0F / ((itemRand.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));
    }

    /** This method registers the icons of a given bow. Overrides are used as ItemBow's icon related variables are not visible :( */
    @Override
    @SideOnly(Side.CLIENT)
    public final void registerIcons(IIconRegister iconReg) {
        itemIcon = iconReg.registerIcon(getIconString() + "1"); // redo with off-by-one error fixed
        // this.iconArray = new IIcon[this.bowPullIconNameArray.length];
        icons = new IIcon[3];

        for (int i = 0; i < icons.length; ++i) {
            // this.iconArray[i] = iconRegistry.registerIcon(this.getIconString() + "_" + this.bowPullIconNameArray[i]);
            icons[i] = iconReg.registerIcon(getIconString() + (i + 2)); // awful hack, icons start from 2 here
            // this.iconArray[i] = iconRegistry.registerIcon(this.bowPullIconNameArray[i]);
        }
    }

    /**
     * This method creates the arrows for a given release of the bow. TODO Remove this.
     *
     * @param world        The world that the arrows are in.
     * @param player       The player shooting the arrows.
     * @param shotVelocity The velocity of the arrows.
     *
     * @return The arrows to shoot.
     */
    protected EntityArrow[] setArrows(World world, EntityPlayer player, float shotVelocity) { // TODO rename later
        if (arrowType == ARROW_TYPE_NOT_CUSTOM) {
            return new EntityArrow[] { new EntityArrow(world, player, shotVelocity * velocityMult) };
        }

        return new EntityArrow[] { new CustomArrow(world, player, shotVelocity * velocityMult, arrowType) };
    }

    /**
     * This method spawns the arrows for a given release of the bow. TODO Remove this.
     *
     * @param world        The world that the arrows are in.
     * @param player       The player shooting the arrows.
     * @param shotVelocity The velocity of the arrows.
     * @param arrs         The arrows to shoot.
     */
    protected void spawnArrows(World world, EntityPlayer player, float shotVelocity, EntityArrow[] arrs) {
        for (final EntityArrow arr : arrs) {
            world.spawnEntityInWorld(arr);
        }
    }

}
