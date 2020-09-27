package iDiamondhunter.morebowsmod.bows;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemIronBow extends MoreAccessibleItemBow
{	
	/** M i n i m a l i s m */
    public ItemIronBow()
    {
        super(550);
        super.defaultShotVelocityMultiplier = 2.1F;
        super.arrowPowerDivisor = 17F;
        super.flameBurnTime = 105;
        super.damageMultiplier = 1.5D;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
        if (usingItem == null) { return itemIcon; }
        int ticksInUse = stack.getMaxItemUseDuration() - useRemaining;

        if (ticksInUse >= 16) {
              return iconArray[2];
        } else if (ticksInUse > 11) {
            return iconArray[1];
        } else if (ticksInUse > 0) {
            return iconArray[0];
        } else {
            return itemIcon;
        }
    }
}
