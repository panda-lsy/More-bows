package iDiamondhunter.morebowsmod.bows;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemGoldBow extends MoreAccessibleItemBow
{	
    public ItemGoldBow()
    {
        super(550);
        super.defaultShotVelocityMultiplier = 2.4F;
        super.arrowPowerDivisor = 5F;
        super.damageMultiplier = 1.5D;
    }
    
    @Override
    public final EnumRarity getRarity(ItemStack itemstack)
    {
    	return EnumRarity.uncommon;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
        if (usingItem == null) { return itemIcon; }
        int ticksInUse = stack.getMaxItemUseDuration() - useRemaining;

        if (ticksInUse >= 8) {
              return iconArray[2];
        } else if (ticksInUse > 4) {
            return iconArray[1];
        } else if (ticksInUse > 0) {
            return iconArray[0];
        } else {
            return itemIcon;
        }
    }
	
}
