package iDiamondhunter.morebows.bows;

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
        super.powerDiv = 13;
    }

    /** TODO Decide if the player only gets one arrow back or consumes the amount that they shoot. */
    @Override
    public void setArrows(World world, EntityPlayer player) {
        arrows = new EntityArrow[] {
            new EntityArrow(world, player, shotVelocity * 2.0F),
            new EntityArrow(world, player, shotVelocity * 1.65F),
            new EntityArrow(world, player, shotVelocity * 1.275F)
        };
        arrows[1].canBePickedUp = 0;
        arrows[2].canBePickedUp = 0;
    }

    /** TODO Fix weird angles on arrows */
    @Override
    public void spawnArrows(World world, EntityPlayer shooter) {
        world.spawnEntityInWorld(arrows[0]);
        world.spawnEntityInWorld(arrows[1]);

        //TODO figure out which is supposed to be changed and to what
        if (arrows[1].shootingEntity.rotationYaw > 180) {
            arrows[1].posX = arrows[1].posX + (arrows[1].shootingEntity.rotationYaw / 180);
        } else {
            arrows[1].posX = arrows[1].posX + (arrows[1].shootingEntity.rotationYaw / 180);
        }

        arrows[0].setDamage(arrows[0].getDamage() * 1.5D);
        arrows[1].setDamage(arrows[1].getDamage() * 1.3D);
        arrows[2].setDamage(arrows[2].getDamage() * 1.15D);
        thirdArrow = (rand.nextInt(4) == 0);

        if (thirdArrow) {
            world.spawnEntityInWorld(arrows[2]);

            if (arrows[2].shootingEntity.rotationYaw > 180) {
                arrows[2].posX = arrows[2].posX - (arrows[2].shootingEntity.rotationYaw / 180);
            } else {
                arrows[2].posX = arrows[2].posX - (arrows[2].shootingEntity.rotationYaw / 180);
            }
        }
    }

    @Override
    public void playNoise(World world, EntityPlayer player) {
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

    @Override
    public final EnumRarity getRarity(ItemStack par1ItemStack) {
        return EnumRarity.rare;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int u, EntityPlayer p, ItemStack item, int useRem) {
        if (item == null) {
            return itemIcon;
        }

        final int ticks = stack.getMaxItemUseDuration() - useRem;

        if (ticks >= 12) {
            return icons[2];
        } else if (ticks > 7) {
            return icons[1];
        } else if (ticks > 0) {
            return icons[0];
        } else {
            return itemIcon;
        }
    }

}
