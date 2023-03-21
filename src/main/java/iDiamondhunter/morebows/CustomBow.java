package iDiamondhunter.morebows;

import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_ENDER;
import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_FIRE;
import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_NOT_CUSTOM;
import static iDiamondhunter.morebows.MoreBows.bowMaxUseDuration;

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
    private final boolean multiShot;
    final float powerDiv;
    private final EnumRarity rarity;

    /* Icon related variables */
    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    /**
     * A more customizable bow than the vanilla one.
     *
     * @param maxDamage  The maximum damage a bow can do.
     * @param bowType    The type of arrows this bow shoots. This also influences behaviors of the bow as well.
     * @param damageMult The multiplier to damage done by an arrow shot by this bow.
     * @param multiShot  A dirty, dirty hack, indicating if this bow shoots multiple arrows or not.
     * @param powerDiv   The power divisor of this bow. TODO document better.
     * @param rarity     The rarity of this bow.
     */
    public CustomBow(int maxDamage, byte bowType, double damageMult, boolean multiShot, float powerDiv, EnumRarity rarity) {
        setMaxDamage(maxDamage);
        this.bowType = bowType;
        this.damageMult = damageMult;
        this.multiShot = multiShot;
        this.powerDiv = powerDiv;
        this.rarity = rarity;
    }

    /** This returns the bow sprite for a given duration of drawing the bow back. */
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int i, EntityPlayer player, ItemStack useItem, int useRem) {
        if (useRem == 0) {
            return itemIcon;
        }

        final float pull = (bowMaxUseDuration - useRem) / powerDiv;

        if (pull >= 0.9) {
            return icons[2];
        }

        if (pull > 0.65) {
            return icons[1];
        }

        return icons[0];
    }

    /** EnumAction.none is returned, as the bow is rendered by a custom IItemRenderer which effectively applies a tweaked version of EnumAction.bow. See ModRenderer. */
    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.none;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return rarity;
    }

    /** This method creates particles when left-clicking with an ender bow. */
    @Override
    public boolean onEntitySwing(EntityLivingBase player, ItemStack stack) {
        if (bowType == ARROW_TYPE_ENDER) {
            MoreBows.tryPart(player.worldObj, player, "portal", true, 1.0);
        }

        return false;
    }

    /** This handles the process of shooting an arrow from this bow. TODO Cleanup, document more */
    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int useRem) {
        final ArrowLooseEvent event = new ArrowLooseEvent(player, stack, bowMaxUseDuration - useRem);
        MinecraftForge.EVENT_BUS.post(event);

        if (event.isCanceled()) {
            return;
        }

        float shotVelocity = event.charge / powerDiv;
        shotVelocity = ((shotVelocity * shotVelocity) + (shotVelocity * 2.0F)) / 3.0F;

        if (shotVelocity < 0.1F) {
            return;
        }

        final boolean isCrit;

        if (shotVelocity >= 1.0F) {
            shotVelocity = 1.0F;
            isCrit = true;
        } else {
            isCrit = false;
        }

        /** Flag to indicate that the player is in Creative mode or has infinity on this bow */
        final boolean infiniteArrows = player.capabilities.isCreativeMode || (EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0);
        final EntityArrow[] arrs;

        // Create the arrows to fire
        if (multiShot) { // Bows that shoot multiple arrows
            if (bowType == ARROW_TYPE_ENDER) { // Ender bow
                arrs = new EntityArrow[] {
                    new CustomArrow(world, player, shotVelocity * 2.0F, ARROW_TYPE_ENDER),
                    new CustomArrow(world, player, shotVelocity * 1.0F, ARROW_TYPE_ENDER),
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
        } else if (bowType == ARROW_TYPE_NOT_CUSTOM) { // "Standard" style bows that do not shoot multiple arrows or have a custom arrow type. Note to self: this is after the multi-arrow bows due to the multi bow having arrows of a normal type.
            arrs = new EntityArrow[] { new EntityArrow(world, player, shotVelocity * 2.0F) };
        } else { // Bows that shoot only one custom arrow, currently only frost / fire bows
            arrs = new EntityArrow[] { new CustomArrow(world, player, shotVelocity * 2.0F, bowType) };
        }

        if (infiniteArrows) {
            arrs[0].canBePickedUp = 2;
        }

        // Set up flags for adding enchantment effects / other modifiers
        final int power = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
        final int knockback = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);
        final boolean flame = EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0;
        // Add enchantment effects / other modifiers to each arrow
        final int arrsLength = arrs.length;

        for (int i = 0; i < arrsLength; ++i) {
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

        if (!infiniteArrows) {
            player.inventory.consumeInventoryItem(Items.arrow);
        }

        stack.damageItem(1, player);

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
                    arrs[1].posX = arrs[1].posX + (arrs[1].shootingEntity.rotationYaw / 180.0F);
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
                        arrs[2].posX = arrs[2].posX - (arrs[2].shootingEntity.rotationYaw / 180.0F);
                        arrs[2].setDamage(arrs[2].getDamage() * 1.15D);
                    }
                }
            } else { // Other bows
                for (int i = 0; i < arrsLength; ++i) {
                    world.spawnEntityInWorld(arrs[i]);
                }
            }
        }

        // Play the "bow shot" sound
        if (multiShot && (bowType != ARROW_TYPE_ENDER)) { // Multi bow
            world.playSoundAtEntity(player, "random.bow", 1.0F, (1.0F / ((itemRand.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));
            world.playSoundEffect(player.posX + (player.rotationYaw / 180.0F), player.posY, player.posZ, "random.bow", 1.0F, (1.0F / ((itemRand.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));

            if (arrs.length > 2) {
                world.playSoundEffect(player.posX - (player.rotationYaw / 180.0F), player.posY, player.posZ, "random.bow", 1.0F, (1.0F / ((itemRand.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));
            }
        } else { // Other bows
            world.playSoundAtEntity(player, "random.bow", 1.0F, (1.0F / ((itemRand.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));
        }
    }

    /** This method registers the icons of a given bow. */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconReg) {
        itemIcon = iconReg.registerIcon(getIconString() + "1"); // redo with off-by-one error fixed
        // this.iconArray = new IIcon[this.bowPullIconNameArray.length];
        icons = new IIcon[3];
        final int length = icons.length;

        for (int i = 0; i < length; ++i) {
            // this.iconArray[i] = iconRegistry.registerIcon(this.getIconString() + "_" + this.bowPullIconNameArray[i]);
            icons[i] = iconReg.registerIcon(getIconString() + (i + 2)); // awful hack, icons start from 2 here
            // this.iconArray[i] = iconRegistry.registerIcon(this.bowPullIconNameArray[i]);
        }
    }
}
