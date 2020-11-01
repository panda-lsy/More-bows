package iDiamondhunter.morebows.bows;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import iDiamondhunter.morebows.entities.CustomArrow;
import iDiamondhunter.morebows.entities.CustomArrow.EnumArrowType;
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

    private final boolean alwaysCrit;
    private final double damageMult;
    private final int flameTime;
    private final int maxUse;
    private final float powerDiv;
    private final EnumRarity rarity;
    private final EnumArrowType arrowType;
    private final float velocityMult;

    private EntityArrow[] arrows;

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;
    public final byte[] iconTimes;

    private final static int defaultFlameTime = 100;
    private final static int defaultMaxUse = 72000;
    private final static float defaultPowerDiv = 20.0F;
    private final static float defaultVelocityMult = 2.0F;
    private final static EnumArrowType defaultArrowType = EnumArrowType.notmb;

    public CustomBow(int maxDamage, EnumRarity rarity, byte[] iconTimes, float velocityMult, float powerDiv, int flameTime, double damageMult, int maxUse, boolean alwaysCrit, EnumArrowType arrowType) {
        //super();
        maxStackSize = 1;
        setMaxDamage(maxDamage);
        bFull3D = true;
        setCreativeTab(CreativeTabs.tabCombat);
        this.alwaysCrit = alwaysCrit;

        if (iconTimes !=  null) {
            this.iconTimes = iconTimes;
        } else {
            this.iconTimes = new byte[] {18, 13};
        }

        setMaxDamage(maxDamage);
        this.rarity = rarity;
        this.powerDiv = powerDiv;
        this.velocityMult = velocityMult;
        this.flameTime = flameTime;
        this.damageMult = damageMult;
        this.maxUse = maxUse;
        this.arrowType = arrowType;
    }

    /* TODO Remove as many of these as possible */
    public CustomBow(int maxDamage, EnumRarity rarity, byte[] iconTimes) {
        this(maxDamage, rarity, iconTimes, defaultVelocityMult, defaultPowerDiv, defaultFlameTime, 1, defaultMaxUse, false, defaultArrowType);
    }

    public CustomBow(int maxDamage, EnumRarity rarity, byte[] iconTimes, float velocityMult, float powerDiv) {
        this(maxDamage, rarity, iconTimes, velocityMult, powerDiv, defaultFlameTime, 1, defaultMaxUse, false, defaultArrowType);
    }

    public CustomBow(int maxDamage, EnumRarity rarity, byte[] iconTimes, float powerDiv, double damageMult, final boolean isCrit) {
        this(maxDamage, rarity, iconTimes, defaultVelocityMult, powerDiv, defaultFlameTime, damageMult, defaultMaxUse, isCrit, defaultArrowType);
    }

    public CustomBow(int maxDamage, EnumRarity rarity, byte[] iconTimes, float powerDiv, double damageMult, final boolean isCrit, EnumArrowType arrowType) {
        this(maxDamage, rarity, iconTimes, defaultVelocityMult, powerDiv, defaultFlameTime, damageMult, defaultMaxUse, isCrit, arrowType);
    }

    public CustomBow(int maxDamage, EnumRarity rarity, byte[] iconTimes, float velocityMult, float powerDiv, int flameTime) {
        this(maxDamage, rarity, iconTimes, velocityMult, powerDiv, flameTime, 1, defaultMaxUse, false, defaultArrowType);
    }

    public CustomBow(int maxDamage, EnumRarity rarity, byte[] iconTimes, float velocityMult, float powerDiv, int flameTime, double damageMult) {
        this(maxDamage, rarity, iconTimes, velocityMult, powerDiv, flameTime, damageMult, defaultMaxUse, false, defaultArrowType);
    }

    public CustomBow(int maxDamage, EnumRarity rarity, byte[] iconTimes, float velocityMult, float powerDiv, int flameTime, double damageMult, int maxUse) {
        this(maxDamage, rarity, iconTimes, velocityMult, powerDiv, flameTime, damageMult, maxUse, false, defaultArrowType);
    }

    public CustomBow(int maxDamage, EnumRarity rarity, byte[] iconTimes, float velocityMult, float powerDiv, int flameTime, double damageMult, int maxUse, EnumArrowType arrowType) {
        this(maxDamage, rarity, iconTimes, velocityMult, powerDiv, flameTime, damageMult, maxUse, false, arrowType);
    }

    /** TODO: replace this */
    public EntityArrow[] addModifiers(EntityArrow[] arrs, final ItemStack stack, final boolean noPickup, final boolean isCrit) { //TODO rename later
        //TODO THIS CODE IS AWFULL FIX IT
        //not just the readability, but also the fact that this is literal garbage to work with
        //when you need to insert an effect into this chain
        //see: ItemFlameBow
        final boolean crit = isCrit || alwaysCrit;
        final int powerEnchResult = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
        final boolean powerEnch = (powerEnchResult > 0);
        final int knockback = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);
        final boolean punchEnch = (knockback > 0);
        final boolean flameEnch = (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0);
        final boolean shouldMulti = (damageMult != 1);

        //default behavior
        for (final EntityArrow arr : arrs) {
            if (crit) {
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

    /** TODO This is still a bit janky. Remove this message when you're certain this is a good way to do it. */
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
    public final int getMaxItemUseDuration(ItemStack item) {
        return maxUse;
    }

    @Override
    public final EnumRarity getRarity(ItemStack item) {
        return rarity;
    }

    /** TODO find a cleaner way to implement this, change like all of this. also make better names. */
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

    protected void playNoise(World world, EntityPlayer player, float shotVelocity) {
        world.playSoundAtEntity(player, "random.bow", 1.0F, (1.0F / ((itemRand.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));
    }

    /* Overrides are used as ItemBow's icon related variables are not visible :( */
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

    public EntityArrow[] setArrows(World world, EntityPlayer player, float shotVelocity) { //TODO rename later
        if (arrowType == EnumArrowType.notmb) {
            return new EntityArrow[] { new EntityArrow(world, player, shotVelocity * velocityMult) };
        } else {
            return new EntityArrow[] { new CustomArrow(world, player, shotVelocity * velocityMult, arrowType) };
        }
    }

    protected void spawnArrows(World world, EntityPlayer player, float shotVelocity, EntityArrow[] arrs) {
        for (final EntityArrow arr : arrs) {
            world.spawnEntityInWorld(arr);
        }
    }


}
