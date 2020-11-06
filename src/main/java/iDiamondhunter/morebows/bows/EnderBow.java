package iDiamondhunter.morebows.bows;

import iDiamondhunter.morebows.MoreBows;
import iDiamondhunter.morebows.entities.ArrowSpawner;
import iDiamondhunter.morebows.entities.CustomArrow;
import iDiamondhunter.morebows.entities.CustomArrow.ArrowType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EnderBow extends CustomBow {

    public EnderBow() {
        super(215, EnumRarity.epic, new byte[] {19, 10}, 22F, 1D, ArrowType.BASE);
    }

    /** This method creates particles when left clicking with an ender bow. */
    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
        MoreBows.trySpawnParticle(entityLiving.worldObj, entityLiving, "portal", true, 1);
        return false;
    }

    /** Creates the multiple ender arrows to use with the ArrowSpawner TODO Replace this */
    @Override
    public EntityArrow[] setArrows(World world, EntityPlayer player, float shotVelocity) {
        final EntityArrow[] arrs = new EntityArrow[] {
            new CustomArrow(world, player, shotVelocity * 2.0F),
            new CustomArrow(world, player, shotVelocity * 1F),
            new CustomArrow(world, player, shotVelocity * 1.2F),
            new CustomArrow(world, player, shotVelocity * 1.5F),
            new CustomArrow(world, player, shotVelocity * 1.75F),
            new CustomArrow(world, player, shotVelocity * 1.825F)
        };
        arrs[1].canBePickedUp = 2;
        arrs[2].canBePickedUp = 2;
        arrs[3].canBePickedUp = 2;
        arrs[4].canBePickedUp = 2;
        arrs[5].canBePickedUp = 2;
        return arrs;
    }

    /** Spawns the ArrowSpawner, which spawns the multiple arrows of the bow. TODO Replace this */
    @Override
    protected void spawnArrows(World world, EntityPlayer player, float shotVelocity, EntityArrow[] arrs) {
        world.spawnEntityInWorld(new ArrowSpawner(world, player.posX, player.posY, player.posZ, shotVelocity, arrs));
    }

}
