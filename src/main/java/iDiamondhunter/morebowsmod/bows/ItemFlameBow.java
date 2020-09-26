package iDiamondhunter.morebowsmod.bows;

import iDiamondhunter.morebowsmod.entities.EntityiDiamondhunterFireArrow;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemFlameBow extends MoreAccessibleItemBow
{	
    public ItemFlameBow()
    {
        super(576);
        super.arrowPowerDivisor = 15F;
        super.damageMultiplier = 2.0D;
    }

    @Override
	public void setArrows(World world, EntityPlayer player) {
		bowShots = new EntityArrow[] { new EntityiDiamondhunterFireArrow(world, player, shotVelocity * 2.0F) };
	}
    
    @Override
    public void addModifiersToArrows(World world, ItemStack stack, Boolean noPickupFlag, Boolean alwaysCrit) {
    	super.addModifiersToArrows(world, stack, noPickupFlag, alwaysCrit);
    	
    	/* TODO this is a bit sus, I think it's the right original behavior though */
    	boolean flameEnchantmentFlag = (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0);
    	
    	for (EntityArrow arr : bowShots) {
    		
	        if (flameEnchantmentFlag)
	        {
	        	arr.setDamage(arr.getDamage() * 1.25D);
	        }
	        
	        arr.setFire(50);
    		
    	}
    }
    
    @Override
    public EnumRarity getRarity(ItemStack par1ItemStack)
    {
        return EnumRarity.uncommon;
    }
    
}
