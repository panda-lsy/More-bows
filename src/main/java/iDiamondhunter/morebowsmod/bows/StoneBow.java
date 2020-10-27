package iDiamondhunter.morebowsmod.bows;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class StoneBow extends CustomBow {
    public StoneBow() {
        super(484);
        super.arrowPowerDivisor = 17F;
        super.damageMultiplier = 1.15D;
    }

    /** Create a better way to do this with {@code MoreAccessibleItemBow}! */
    @Override
    public void addModifiers(World world, ItemStack stack, Boolean noPickupFlag, Boolean alwaysCrit) {
        super.addModifiers(world, stack, noPickupFlag, true);
    }

}
