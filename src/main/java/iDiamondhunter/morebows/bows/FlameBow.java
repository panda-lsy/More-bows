package iDiamondhunter.morebows.bows;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import iDiamondhunter.morebows.entities.FireArrow;
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
        super.powerDiv = 15F;
        super.damageMult = 2.0D;
    }

    @Override
    public void setArrows(World world, EntityPlayer player) {
        arrows = new EntityArrow[] { new FireArrow(world, player, shotVelocity * 2.0F) };
    }

    @Override
    public void addModifiers(World world, ItemStack stack, Boolean noPickup, Boolean alwaysCrit) {
        super.addModifiers(world, stack, noPickup, alwaysCrit);
        /* TODO this is a bit sus, I think it's the right original behavior though */
        final boolean flameEnch = (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0);

        for (final EntityArrow arr : arrows) {
            if (flameEnch) {
                arr.setDamage(arr.getDamage() * 1.25D);
            }

            arr.setFire(50);
        }
    }

    @Override
    public final EnumRarity getRarity(ItemStack item) {
        return EnumRarity.uncommon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int u, EntityPlayer p, ItemStack item, int useRem) {
        if (item == null) {
            return itemIcon;
        }

        final int ticks = stack.getMaxItemUseDuration() - useRem;

        if (ticks >= 14) {
            return icons[2];
        } else if (ticks > 9) {
            return icons[1];
        } else if (ticks > 0) {
            return icons[0];
        } else {
            return itemIcon;
        }
    }

}
