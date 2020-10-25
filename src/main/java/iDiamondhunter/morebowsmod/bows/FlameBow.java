package iDiamondhunter.morebowsmod.bows;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import iDiamondhunter.morebowsmod.entities.FireArrow;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class FlameBow extends CustomBow {
    public FlameBow() {
        super(576);
        super.arrowPowerDivisor = 15F;
        super.damageMultiplier = 2.0D;
    }

    @Override
    public void setArrows(World world, EntityPlayer player) {
        bowShots = new EntityArrow[] { new FireArrow(world, player, shotVelocity * 2.0F) };
    }

    @Override
    public void addModifiersToArrows(World world, ItemStack stack, Boolean noPickupFlag, Boolean alwaysCrit) {
        super.addModifiersToArrows(world, stack, noPickupFlag, alwaysCrit);
        /* TODO this is a bit sus, I think it's the right original behavior though */
        final boolean flameEnchantmentFlag = (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0);

        for (final EntityArrow arr : bowShots) {
            if (flameEnchantmentFlag) {
                arr.setDamage(arr.getDamage() * 1.25D);
            }

            arr.setFire(50);
        }
    }

    @Override
    public final EnumRarity getRarity(ItemStack par1ItemStack) {
        return EnumRarity.uncommon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
        if (usingItem == null) {
            return itemIcon;
        }

        final int ticksInUse = stack.getMaxItemUseDuration() - useRemaining;

        if (ticksInUse >= 14) {
            return iconArray[2];
        } else if (ticksInUse > 9) {
            return iconArray[1];
        } else if (ticksInUse > 0) {
            return iconArray[0];
        } else {
            return itemIcon;
        }
    }

}
