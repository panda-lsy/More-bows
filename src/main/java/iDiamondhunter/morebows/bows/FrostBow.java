package iDiamondhunter.morebows.bows;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import iDiamondhunter.morebows.entities.FrostArrow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class FrostBow extends CustomBow {
    public FrostBow() {
        super(550);
        super.powerDiv = 26.0F;
    }

    @Override
    public void setArrows(World world, EntityPlayer player) {
        arrows = new EntityArrow[] { new FrostArrow(world, player, shotVelocity * 2.4F) };
    }

    /** TODO THIS ISN'T RIGHT, FIND THE RIGHT VALUE */
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int u, EntityPlayer p, ItemStack item, int useRem) {
        if (item == null) {
            return itemIcon;
        }

        final int ticks = stack.getMaxItemUseDuration() - useRem;

        if (ticks >= 26) {
            return icons[2];
        } else if (ticks >= 13) {
            return icons[1];
        } else if (ticks > 0) {
            return icons[0];
        } else {
            return itemIcon;
        }
    }

}
