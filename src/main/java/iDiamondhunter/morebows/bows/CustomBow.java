package iDiamondhunter.morebows.bows;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import iDiamondhunter.morebows.entities.CustomArrow;
import iDiamondhunter.morebows.entities.CustomArrow.ArrowType;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;

/** This entire class is a huge hack. I'm ashamed of myself. And yes, this is important to document.
 * TODO: Re-write all of this (or at least re-think most of it). Burn the original.
 * Also TODO: Create a workaround for having to override getIcon, getRarity, getMaxItemUseDuration etc.
 *
 **/
public class CustomBow extends ItemBow {

    /* Default values for bow construction */
    private static final EnumRarity defaultRarity = EnumRarity.common;
    private static final byte[] defaultIconTimes = {18, 13};
    private static final double defaultDamageMult = 1D;
    private static final int defaultFlameTime = 100;
    private static final int defaultMaxDamage = 384;
    private static final float defaultPowerDiv = 20.0F;
    private static final float defaultVelocityMult = 2.0F;
    private static final ArrowType defaultArrowType = ArrowType.NOT_CUSTOM;

    /* Bow instance variables */
    private final double damageMult;
    private final int flameTime;
    private final float powerDiv;
    private final EnumRarity rarity;
    private final ArrowType arrowType;
    private final float velocityMult;

    /* Arrows to shoot for a given release of the bow. TODO Replace this. */
    private EntityArrow[] arrows;

    /* Icon related variables */
    @SideOnly(Side.CLIENT)
    private IIcon[] icons;
    public final byte[] iconTimes; // TODO This is not a great solution, and could be considered a "magic number" in some ways.

    /* TODO Remove as many constructors as possible */

    /* Reference constructor with each default option. This bow should be roughly the same as the vanilla bow. */
    /*public CustomBow() {
        this(defaultMaxDamage, defaultRarity, defaultIconTimes, defaultVelocityMult, defaultPowerDiv, defaultFlameTime, defaultDamageMult, defaultArrowType);
    }*/

    /** A constructor that is useful for a number of bows, as they only use these parameters. */
    public CustomBow(int maxDamage, EnumRarity rarity, byte[] iconTimes, float powerDiv, double damageMult, ArrowType arrowType) {
        this(maxDamage, rarity, iconTimes, defaultVelocityMult, powerDiv, defaultFlameTime, damageMult, arrowType);
    }

    /** A constructor that can use every customization, minus a bow having a custom arrow. */
    public CustomBow(int maxDamage, EnumRarity rarity, byte[] iconTimes, float velocityMult, float powerDiv, int flameTime, double damageMult) {
        this(maxDamage, rarity, iconTimes, velocityMult, powerDiv, flameTime, damageMult, defaultArrowType);
    }

    /** A constructor that can use every customization. */
    public CustomBow(int maxDamage, EnumRarity rarity, byte[] iconTimes, float velocityMult, float powerDiv, int flameTime, double damageMult, ArrowType arrowType) {
        //super();
        maxStackSize = 1;
        setMaxDamage(maxDamage);
        bFull3D = true;
        setCreativeTab(CreativeTabs.tabCombat);

        if (iconTimes !=  null) {
            this.iconTimes = iconTimes;
        } else {
            this.iconTimes = defaultIconTimes;
        }

        setMaxDamage(maxDamage);
        this.rarity = rarity;
        this.powerDiv = powerDiv;
        this.velocityMult = velocityMult;
        this.flameTime = flameTime;
        this.damageMult = damageMult;
        this.arrowType = arrowType;
    }

    /** This method handles adding or changing the properties of the arrow, based on a variety of factors including enchantments the bow has etc.
     *  This is largely based on vanilla, with specific additions for this mod.
     *  TODO: possibly replace this */
    public EntityArrow[] addModifiers(EntityArrow[] arrs, final ItemStack stack, final boolean noPickup, final boolean isCrit) {
        final int powerEnchResult = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
        final boolean powerEnch = (powerEnchResult > 0);
        final int knockback = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);
        final boolean punchEnch = (knockback > 0);
        final boolean flameEnch = (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0);
        final boolean isFireArr = (arrowType == ArrowType.FIRE);
        final boolean shouldMulti = (damageMult != 1);

        for (final EntityArrow arr : arrs) {
            if (isCrit) { /* setIsCritical calls dataWatcher methods, avoid this unless needed with the check. */
                arr.setIsCritical(true);
            }

            if (powerEnch) {
                arr.setDamage(arr.getDamage() + (powerEnchResult * 0.5D) + 0.5D);
            }

            if (punchEnch) {
                arr.setKnockbackStrength(knockback);
            }

            if (flameEnch) {
                arr.setFire(flameTime);

                if (isFireArr) {
                    arr.setDamage(arr.getDamage() * 1.25D); // TODO: Verify
                }
            }

            if (isFireArr) {
                arr.setFire(50); // TODO: Verify. I'm pretty sure the original mod did this.
            }

            if (noPickup) {
                arr.canBePickedUp = 2;
            }

            if (shouldMulti) {
                arr.setDamage(arr.getDamage() * damageMult);
            }
        }

        return arrs;
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

    @Override
    public final EnumRarity getRarity(ItemStack item) {
        return rarity;
    }

    /** This handles the process of shooting an arrow, with methods for specific parts of this process. These were intended to be overridden when needed, but this has been changed a bit since. TODO Cleanup. */
    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int remaining) {
        final int initCharge = getMaxItemUseDuration(stack) - remaining;
        final ArrowLooseEvent event = new ArrowLooseEvent(player, stack, initCharge);
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

            final boolean shouldCrit = (shotVelocity == 1.0F);
            arrows = addModifiers(setArrows(world, player, shotVelocity), stack, allwaysShoots, shouldCrit);
            stack.damageItem(1, player);
            playNoise(world, player, shotVelocity);

            if (!allwaysShoots) {
                player.inventory.consumeInventoryItem(Items.arrow);
            }

            if (!world.isRemote) {
                spawnArrows(world, player, shotVelocity, arrows);
            }
        }
    }

    /** This method plays the bow releasing noise for a given release of the bow. TODO Remove this. */
    protected void playNoise(World world, EntityPlayer player, float shotVelocity) {
        world.playSoundAtEntity(player, "random.bow", 1.0F, (1.0F / ((itemRand.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));
    }

    /** This method registers the icons of a given bow. Overrides are used as ItemBow's icon related variables are not visible :( */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconReg) {
        itemIcon = iconReg.registerIcon(getIconString() + "1"); //redo with off-by-one error fixed
        //this.iconArray = new IIcon[this.bowPullIconNameArray.length];
        icons = new IIcon[3];

        for (int i = 0; i < icons.length; ++i) {
            //this.iconArray[i] = iconRegistry.registerIcon(this.getIconString() + "_" + this.bowPullIconNameArray[i]);
            icons[i] = iconReg.registerIcon(getIconString() + (i + 2)); //awful hack, icons start from 2 here
            //this.iconArray[i] = iconRegistry.registerIcon(this.bowPullIconNameArray[i]);
        }
    }

    /** This method creates the arrows for a given release of the bow. TODO Remove this. */
    public EntityArrow[] setArrows(World world, EntityPlayer player, float shotVelocity) { //TODO rename later
        if (arrowType == ArrowType.NOT_CUSTOM) {
            return new EntityArrow[] { new EntityArrow(world, player, shotVelocity * velocityMult) };
        } else {
            return new EntityArrow[] { new CustomArrow(world, player, shotVelocity * velocityMult, arrowType) };
        }
    }

    /** This method spawns the arrows for a given release of the bow. TODO Remove this. */
    protected void spawnArrows(World world, EntityPlayer player, float shotVelocity, EntityArrow[] arrs) {
        for (final EntityArrow arr : arrs) {
            world.spawnEntityInWorld(arr);
        }
    }


}
