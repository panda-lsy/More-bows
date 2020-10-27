package iDiamondhunter.morebows.bows;

import iDiamondhunter.morebows.entities.ArrowSpawner;
import iDiamondhunter.morebows.entities.HitArrow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class EnderBow extends CustomBow {

    public EnderBow() {
        super(384);
        super.powerDiv = 22F;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void setArrows(World world, EntityPlayer player) {
        super.arrows = new EntityArrow[] {
            new HitArrow(world, player, shotVelocity * 2.0F),
            new HitArrow(world, player, shotVelocity * 1F),
            new HitArrow(world, player, shotVelocity * 1.2F),
            new HitArrow(world, player, shotVelocity * 1.5F),
            new HitArrow(world, player, shotVelocity * 1.75F),
            new HitArrow(world, player, shotVelocity * 1.825F)
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
        world.spawnEntityInWorld(new ArrowSpawner(world, player, shotVelocity, arrows));
    }

    @Override
    public final EnumRarity getRarity(ItemStack i) {
        return EnumRarity.epic;
    }

}
