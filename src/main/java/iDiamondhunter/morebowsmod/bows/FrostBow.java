package iDiamondhunter.morebowsmod.bows;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import iDiamondhunter.morebowsmod.entities.FrostArrow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class FrostBow extends CustomBow {
    public FrostBow() {
        super(550);
        super.arrowPowerDivisor = 26.0F;
    }

    @Override
    public void setArrows(World world, EntityPlayer player) {
        bowShots = new EntityArrow[] { new FrostArrow(world, player, shotVelocity * 2.4F) };
    }

    /** TODO THIS ISN'T RIGHT, FIND THE RIGHT VALUE */
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
        if (usingItem == null) {
            return itemIcon;
        }

        final int ticksInUse = stack.getMaxItemUseDuration() - useRemaining;

        if (ticksInUse >= 26) {
            return iconArray[2];
        } else if (ticksInUse >= 13) {
            return iconArray[1];
        } else if (ticksInUse > 0) {
            return iconArray[0];
        } else {
            return itemIcon;
        }
    }

}
