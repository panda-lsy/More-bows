package iDiamondhunter.morebows.bows;

import iDiamondhunter.morebows.Util;
import iDiamondhunter.morebows.entities.ArrowSpawner;
import iDiamondhunter.morebows.entities.CustomArrow;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;

public class EnderBow extends CustomBow {

    public EnderBow() {
        super(384, (byte) 0, null, 22F);
        MinecraftForge.EVENT_BUS.register(this);
    }

    /** TODO Document */
    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
        if  (!entityLiving.worldObj.isRemote) {
            Util.spawnParticle((WorldServer) entityLiving.worldObj, entityLiving, "portal");
        }

        return false;
    }

    @Override
    public void setArrows(World world, EntityPlayer player) {
        super.arrows = new EntityArrow[] {
            new CustomArrow(world, player, shotVelocity * 2.0F),
            new CustomArrow(world, player, shotVelocity * 1F),
            new CustomArrow(world, player, shotVelocity * 1.2F),
            new CustomArrow(world, player, shotVelocity * 1.5F),
            new CustomArrow(world, player, shotVelocity * 1.75F),
            new CustomArrow(world, player, shotVelocity * 1.825F)
        };
        arrows[1].canBePickedUp = 2;
        arrows[2].canBePickedUp = 2;
        arrows[3].canBePickedUp = 2;
        arrows[4].canBePickedUp = 2;
        arrows[5].canBePickedUp = 2;
    }

    /** TODO Decide if the player only gets one arrow back or consumes the amount that they shoot, replace TimerTask with tick counting (see {@code MoreArrowsTask}). */
    @Override
    public void spawnArrows(World world, EntityPlayer player) {
        world.spawnEntityInWorld(new ArrowSpawner(world, player.posX, player.posY, player.posZ, shotVelocity, arrows));
    }

}
