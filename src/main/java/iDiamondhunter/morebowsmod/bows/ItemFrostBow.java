package iDiamondhunter.morebowsmod.bows;

import iDiamondhunter.morebowsmod.entities.EntityiDiamondhunterFrostArrow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.world.World;

public class ItemFrostBow extends MoreAccessibleItemBow
{
    public ItemFrostBow()
    {
        super(550);
        super.arrowPowerDivisor = 26.0F;
    }
    
    @Override
	public void setArrows(World world, EntityPlayer player) {
		bowShots = new EntityArrow[] { new EntityiDiamondhunterFrostArrow(world, player, shotVelocity * 2.4F) };
	}
    
}
