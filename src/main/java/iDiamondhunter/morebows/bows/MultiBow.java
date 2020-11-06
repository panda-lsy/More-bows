package iDiamondhunter.morebows.bows;

import java.util.Random;

import iDiamondhunter.morebows.entities.CustomArrow.ArrowType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.EnumRarity;
import net.minecraft.world.World;

public class MultiBow extends CustomBow {
    private static final Random rand = new Random();
    private boolean thirdArrow = false;

    public MultiBow() {
        super(550, EnumRarity.rare, new byte[] {12, 7}, 13F, 1D, ArrowType.BASE);
    }

    /** Plays the noises at each arrow */
    @Override
    protected void playNoise(World world, EntityPlayer player, float shotVelocity) {
        //TODO: Clean this up
        final double xpos = player.posX;
        final double ypos = player.posY;
        final double zpos = player.posZ;
        world.playSoundAtEntity(player, "random.bow", 1.0F, (1.0F / ((itemRand.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));
        world.playSoundEffect(xpos + (player.rotationYaw / 180), ypos, zpos, "random.bow", 1.0F, (1.0F / ((itemRand.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));

        if (thirdArrow) {
            world.playSoundEffect(xpos - (player.rotationYaw / 180), ypos, zpos, "random.bow", 1.0F, (1.0F / ((itemRand.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));
        }
    }

    /** Creates the multiple arrows of the bow. TODO Replace this */
    @Override
    public EntityArrow[] setArrows(World world, EntityPlayer player, float shotVelocity) {
        final EntityArrow[] arrs = new EntityArrow[] {
            new EntityArrow(world, player, shotVelocity * 2.0F),
            new EntityArrow(world, player, shotVelocity * 1.65F),
            new EntityArrow(world, player, shotVelocity * 1.275F)
        };
        arrs[1].canBePickedUp = 0;
        arrs[2].canBePickedUp = 0;
        return arrs;
    }

    /** Spawns the multiple arrows of the bow. TODO Replace this, fix weird angles on arrows */
    @Override
    protected void spawnArrows(World world, EntityPlayer player, float shotVelocity, EntityArrow[] arrs) {
        world.spawnEntityInWorld(arrs[0]);
        world.spawnEntityInWorld(arrs[1]);

        //TODO figure out which is supposed to be changed and to what
        if (arrs[1].shootingEntity.rotationYaw > 180) {
            arrs[1].posX = arrs[1].posX + (arrs[1].shootingEntity.rotationYaw / 180);
        } else {
            arrs[1].posX = arrs[1].posX + (arrs[1].shootingEntity.rotationYaw / 180);
        }

        arrs[0].setDamage(arrs[0].getDamage() * 1.5D);
        arrs[1].setDamage(arrs[1].getDamage() * 1.3D);
        arrs[2].setDamage(arrs[2].getDamage() * 1.15D);
        thirdArrow = (rand.nextInt(4) == 0);

        if (thirdArrow) {
            world.spawnEntityInWorld(arrs[2]);

            if (arrs[2].shootingEntity.rotationYaw > 180) {
                arrs[2].posX = arrs[2].posX - (arrs[2].shootingEntity.rotationYaw / 180);
            } else {
                arrs[2].posX = arrs[2].posX - (arrs[2].shootingEntity.rotationYaw / 180);
            }
        }
    }

}
