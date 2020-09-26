package iDiamondhunter.morebowsmod.bows;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemStoneBow extends MoreAccessibleItemBow
{	
    public ItemStoneBow()
    {
        super(484);
        super.arrowPowerDivisor = 17F;
        super.damageMultiplier = 1.15D;
    }

    /** Create a better way to do this with {@code MoreAccessibleItemBow}! */
    @Override
    public void addModifiersToArrows(World world, ItemStack stack, Boolean noPickupFlag, Boolean alwaysCrit) {
    	super.addModifiersToArrows(world, stack, noPickupFlag, true);
    }
    
}
