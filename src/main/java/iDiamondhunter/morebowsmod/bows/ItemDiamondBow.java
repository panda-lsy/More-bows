package iDiamondhunter.morebowsmod.bows;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemDiamondBow extends MoreAccessibleItemBow {
    public ItemDiamondBow() {
        super(1016);
        super.defaultShotVelocityMultiplier = 2.2F;
        super.arrowPowerDivisor = 6F;
        super.flameBurnTime = 140;
        super.damageMultiplier = 2.25;
    }

    @Override
    public final int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return 36000;
    }

    @Override
    public final EnumRarity getRarity(ItemStack par1ItemStack) {
        return EnumRarity.rare;
    }

    /** TODO Replace this system! */
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
        if (usingItem == null) {
            return itemIcon;
        }

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
