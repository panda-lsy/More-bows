package iDiamondhunter.morebowsmod.bows;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public class ItemDiamondBow extends MoreAccessibleItemBow
{
    public ItemDiamondBow()
    {
        super(1016);
        super.defaultShotVelocityMultiplier = 2.2F;
        super.arrowPowerDivisor = 6F;
        super.flameBurnTime = 140;
        super.damageMultiplier = 2.25;
    }

    /** TODO check if this makes sense, the original mod did this. */
  	@Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack)
    {
        return 36000;
    }

  	@Override
    public EnumRarity getRarity(ItemStack par1ItemStack)
    {
        return EnumRarity.rare;
    }
  	
}
