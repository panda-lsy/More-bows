package iDiamondhunter.morebows.bows;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
    /* TODO: replace some of this, potentially move assignments to constructors, better names */
    private boolean alwaysCrit = false;
    protected EntityArrow[] arrows;
    private double damageMult = 1D;
    private int flameTime = 100;
    @SideOnly(Side.CLIENT)
    private IIcon[] icons;
    public final byte[] iconTimes;
    private int maxUse = 72000;
    private float powerDiv = 20.0F;
    private final EnumRarity rarity;
    /** TODO Remove this */
    protected float shotVelocity;
    private float velocityMult = 2.0F;

    /** The "base" CustomBow initualiser. TODO better parameter order, decide which variables should be set in the constructor (possibly provide a better default one) */
    public CustomBow(int maxDamage, byte rarity, byte[] iconTimes) {
        maxStackSize = 1;
        setMaxDamage(maxDamage);
        bFull3D = true;
        setCreativeTab(CreativeTabs.tabCombat);

        if (iconTimes !=  null) {
            this.iconTimes = iconTimes;
        } else {
            this.iconTimes = new byte[] {18, 13};
        }

        EnumRarity detRare;

        switch (rarity) {
        case 0:
            detRare = EnumRarity.uncommon;

        case 1:
            detRare = EnumRarity.rare;

        case 2:
            detRare = EnumRarity.epic;

        default:
            detRare = EnumRarity.common;
        }

        this.rarity = detRare;
    }


    // TODO Probably remove some of these
    /* The most common contractor */
    public CustomBow(int maxDamage, byte rarity, byte[] iconTimes, float powerDiv) {
        this(maxDamage, rarity, iconTimes);
        this.powerDiv = powerDiv;
    }

    /* Specialty method for use in constructing the fire bow */
    public CustomBow(int maxDamage, byte rarity, byte[] iconTimes, float powerDiv, double damageMult) {
        this(maxDamage, rarity, iconTimes);
        this.powerDiv = powerDiv;
        this.damageMult = damageMult;
    }

    /* Specialty method for use in constructing the stone bow */
    public CustomBow(int maxDamage, byte rarity, byte[] iconTimes, float powerDiv, double damageMult, boolean alwaysCrit) {
        this(maxDamage, rarity, iconTimes, powerDiv, damageMult);
        this.alwaysCrit = alwaysCrit;
    }

    /* Specialty method for use in constructing the gold and iron bows */
    public CustomBow(int maxDamage, byte rarity, byte[] iconTimes, float velocityMult, float powerDiv, int flameTime, double damageMult) {
        this(maxDamage, rarity, iconTimes);
        this.velocityMult = velocityMult;
        this.powerDiv = powerDiv;
        this.flameTime = flameTime;
        this.damageMult = damageMult;
    }

    /* Specialty method for use in constructing the diamond bow */
    public CustomBow(int maxDamage, byte rarity, byte[] iconTimes, float velocityMult, float powerDiv, int flameTime, double damageMult, int maxUse) {
        this(maxDamage, rarity, iconTimes, velocityMult, powerDiv, flameTime, damageMult);
        this.maxUse = maxUse;
    }

    /** TODO: replace this */
    public void addModifiers(World world, ItemStack stack, Boolean noPickup) { //TODO rename later
        //TODO THIS CODE IS AWFULL FIX IT
        //not just the readability, but also the fact that this is literal garbage to work with
        //when you need to insert an effect into this chain
        //see: ItemFlameBow
        final boolean crit = (shotVelocity == 1.0F) || alwaysCrit;
        final int k = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
        final boolean powerEnch = (k > 0);
        final int l = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);
        final boolean punchEnch = (l > 0);
        final boolean flameEnch = (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0);

        //default behavior
        for (final EntityArrow arr : arrows) {
            if (crit) {
                arr.setIsCritical(true);
            }

            if (powerEnch) {
                arr.setDamage(arr.getDamage() + (k * 0.5D) + 0.5D);
            }

            if (punchEnch) {
                arr.setKnockbackStrength(l);
            }

            if (flameEnch) {
                arr.setFire(flameTime);
            }

            if (noPickup) {
                arr.canBePickedUp = 2;
            }

            arr.setDamage(arr.getDamage() * damageMult);
        }
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
        int charge = getMaxItemUseDuration(stack) - remaining;
        final ArrowLooseEvent event = new ArrowLooseEvent(player, stack, charge);
        MinecraftForge.EVENT_BUS.post(event);

        if (event.isCanceled()) {
            return;
        }

        charge = event.charge;
        // TODO Remove "flag", it isn't used when calling method externally.
        final boolean flag = player.capabilities.isCreativeMode || (EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0);

        if (flag || player.inventory.hasItem(Items.arrow)) {
            shotVelocity = charge / powerDiv;
            shotVelocity = ((shotVelocity * shotVelocity) + (shotVelocity * 2.0F)) / 3.0F;

            if (shotVelocity < 0.1D) {
                return;
            }

            if (shotVelocity > 1.0F) {
                shotVelocity = 1.0F;
            }

            setArrows(world, player);
            addModifiers(world, stack, flag);
            stack.damageItem(1, player);
            playNoise(world, player);

            if (!flag) {
                player.inventory.consumeInventoryItem(Items.arrow);
            }

            if (!world.isRemote) {
                spawnArrows(world, player);
            }
        }
    }

    /** TODO: Go through each bow and check if they have custom noises. Also make this better. */
    public void playNoise(World world, EntityPlayer player) {
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

    /** TODO: replace this */
    public void setArrows(World world, EntityPlayer player) { //TODO rename later
        //default behavior
        arrows = new EntityArrow[] { new EntityArrow(world, player, shotVelocity * velocityMult) };
    }

    /** TODO: replace this */
    public void spawnArrows(World world, EntityPlayer player) { //TODO rename later

        //TODO add logic to spawn arrows over time
        for (final EntityArrow arr : arrows) {
            world.spawnEntityInWorld(arr);
        }
    }


}
