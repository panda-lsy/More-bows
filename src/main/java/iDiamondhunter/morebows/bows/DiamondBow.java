package iDiamondhunter.morebows.bows;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class DiamondBow extends CustomBow {
    public DiamondBow() {
        super(1016);
        super.velocityMult = 2.2F;
        super.powerDiv = 6F;
        super.flameTime = 140;
        super.damageMult = 2.25;
    }

    @Override
    public final int getMaxItemUseDuration(ItemStack item) {
        return 36000; // TODO Determine why this is 36000
    }

    @Override
    public final EnumRarity getRarity(ItemStack item) {
        return EnumRarity.rare;
    }

    /** TODO Replace this system! */
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int u, EntityPlayer p, ItemStack item, int useRem) {
        if (item == null) {
            return itemIcon;
        }

        final int ticks = stack.getMaxItemUseDuration() - useRem;

        if (ticks >= 8) {
            return icons[2];
        } else if (ticks > 4) {
            return icons[1];
        } else if (ticks > 0) {
            return icons[0];
        } else {
            return itemIcon;
        }
    }

}
