package iDiamondhunter.morebows.bows;

import iDiamondhunter.morebows.entities.FrostArrow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.world.World;

public class FrostBow extends CustomBow {
    public FrostBow() {
        super(550, (byte) 0, new byte[] {26, 13}, 26.0F);
    }

    @Override
    public void setArrows(World world, EntityPlayer player) {
        arrows = new EntityArrow[] { new FrostArrow(world, player, shotVelocity * 2.4F) };
    }

}
