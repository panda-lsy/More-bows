package iDiamondhunter.morebows.bows;

import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_NOT_CUSTOM;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.EnumRarity;
import net.minecraft.world.World;

/** TODO Merge into CustomBow? */
public final class MultiBow extends CustomBow {

    /* Default values for bow construction */
    private static final byte defaultArrowType = ARROW_TYPE_NOT_CUSTOM;
    private static final int defaultFlameTime = 100;
    private static final float defaultVelocityMult = 2.0F;

    public MultiBow() {
        super(550, EnumRarity.rare, new byte[] {12, 7}, defaultVelocityMult, 13F, defaultFlameTime, 1D, defaultArrowType);
    }

    /** Plays the noises at each arrow */
    @Override
    protected void playNoise(World world, EntityPlayer player, EntityArrow[] arrs, float shotVelocity) {
        //TODO: Clean this up
        final double xpos = player.posX;
        final double ypos = player.posY;
        final double zpos = player.posZ;
        world.playSoundAtEntity(player, "random.bow", 1.0F, (1.0F / ((itemRand.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));
        world.playSoundEffect(xpos + (player.rotationYaw / 180), ypos, zpos, "random.bow", 1.0F, (1.0F / ((itemRand.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));

        if (arrs.length > 2) {
            world.playSoundEffect(xpos - (player.rotationYaw / 180), ypos, zpos, "random.bow", 1.0F, (1.0F / ((itemRand.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));
        }
    }

    /** Creates the multiple arrows of the bow. TODO Replace this */
    @Override
    protected EntityArrow[] setArrows(World world, EntityPlayer player, float shotVelocity) {
        final EntityArrow[] arrs;

        if (itemRand.nextInt(4) == 0) {
            arrs = new EntityArrow[] {
                new EntityArrow(world, player, shotVelocity * 2.0F),
                new EntityArrow(world, player, shotVelocity * 1.65F),
                new EntityArrow(world, player, shotVelocity * 1.275F)
            };
            arrs[2].canBePickedUp = 0;
        } else {
            arrs = new EntityArrow[] {
                new EntityArrow(world, player, shotVelocity * 2.0F),
                new EntityArrow(world, player, shotVelocity * 1.65F)
            };
        }

        arrs[1].canBePickedUp = 0;
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

        if (arrs.length > 2) {
            world.spawnEntityInWorld(arrs[2]);

            if (arrs[2].shootingEntity.rotationYaw > 180) {
                arrs[2].posX = arrs[2].posX - (arrs[2].shootingEntity.rotationYaw / 180);
            } else {
                arrs[2].posX = arrs[2].posX - (arrs[2].shootingEntity.rotationYaw / 180);
            }

            arrs[2].setDamage(arrs[2].getDamage() * 1.15D);
        }
    }

}
