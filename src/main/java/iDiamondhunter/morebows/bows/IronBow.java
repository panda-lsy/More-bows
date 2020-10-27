package iDiamondhunter.morebows.bows;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class IronBow extends CustomBow {
    /** M i n i m a l i s m */
    public IronBow() {
        super(550);
        super.velocityMult = 2.1F;
        super.powerDiv = 17F;
        super.flameTime = 105;
        super.damageMult = 1.5D;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int u, EntityPlayer p, ItemStack item, int useRem) {
        if (item == null) {
            return itemIcon;
        }

        final int ticks = stack.getMaxItemUseDuration() - useRem;

        if (ticks >= 16) {
            return icons[2];
        } else if (ticks > 11) {
            return icons[1];
        } else if (ticks > 0) {
            return icons[0];
        } else {
            return itemIcon;
        }
    }
}
