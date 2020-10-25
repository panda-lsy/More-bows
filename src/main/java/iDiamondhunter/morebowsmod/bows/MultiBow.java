package iDiamondhunter.morebowsmod.bows;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class MultiBow extends CustomBow {
    private final Random rand = new Random();
    protected boolean thirdArrow = false;

    public MultiBow() {
        super(550);
        super.arrowPowerDivisor = 13;
    }

    /** TODO Decide if the player only gets one arrow back or consumes the amount that they shoot. */
    @Override
    public void setArrows(World world, EntityPlayer player) {
        bowShots = new EntityArrow[] {
            new EntityArrow(world, player, shotVelocity * 2.0F),
            new EntityArrow(world, player, shotVelocity * 1.65F),
            new EntityArrow(world, player, shotVelocity * 1.275F)
        };
        bowShots[1].canBePickedUp = 0;
        bowShots[2].canBePickedUp = 0;
    }

    /** TODO Fix weird angles on arrows */
    @Override
    public void spawnArrows(World world) {
        world.spawnEntityInWorld(bowShots[0]);
        world.spawnEntityInWorld(bowShots[1]);

        //TODO figure out which is supposed to be changed and to what
        if (bowShots[1].shootingEntity.rotationYaw > 180) {
            bowShots[1].posX = bowShots[1].posX + (bowShots[1].shootingEntity.rotationYaw / 180);
        } else {
            bowShots[1].posX = bowShots[1].posX + (bowShots[1].shootingEntity.rotationYaw / 180);
        }

        bowShots[0].setDamage(bowShots[0].getDamage() * 1.5D);
        bowShots[1].setDamage(bowShots[1].getDamage() * 1.3D);
        bowShots[2].setDamage(bowShots[2].getDamage() * 1.15D);
        thirdArrow = (rand.nextInt(4) == 0);

        if (thirdArrow) {
            world.spawnEntityInWorld(bowShots[2]);

            if (bowShots[2].shootingEntity.rotationYaw > 180) {
                bowShots[2].posX = bowShots[2].posX - (bowShots[2].shootingEntity.rotationYaw / 180);
            } else {
                bowShots[2].posX = bowShots[2].posX - (bowShots[2].shootingEntity.rotationYaw / 180);
            }
        }
    }

    @Override
    public void playBowNoise(World world, EntityPlayer player) {
        //TODO: Clean this up
        final double xpos = player.posX;
        final double ypos = player.posY;
        final double zpos = player.posZ;
        world.playSoundAtEntity(player, defaultShotSound, 1.0F, (1.0F / ((itemRand.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));
        world.playSoundEffect(xpos + (player.rotationYaw / 180), ypos, zpos, defaultShotSound, 1.0F, (1.0F / ((itemRand.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));

        if (thirdArrow) {
            world.playSoundEffect(xpos - (player.rotationYaw / 180), ypos, zpos, defaultShotSound, 1.0F, (1.0F / ((itemRand.nextFloat() * 0.4F) + 1.2F)) + (shotVelocity * 0.5F));
        }
    }

    @Override
    public final EnumRarity getRarity(ItemStack par1ItemStack) {
        return EnumRarity.rare;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
        if (usingItem == null) {
            return itemIcon;
        }

        final int ticksInUse = stack.getMaxItemUseDuration() - useRemaining;

        if (ticksInUse >= 12) {
            return iconArray[2];
        } else if (ticksInUse > 7) {
            return iconArray[1];
        } else if (ticksInUse > 0) {
            return iconArray[0];
        } else {
            return itemIcon;
        }
    }

}
