package iDiamondhunter.morebowsmod.bows;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

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
    public EnumRarity getRarity(ItemStack itemstack)
    {
    	return EnumRarity.uncommon;
    }
	
}
