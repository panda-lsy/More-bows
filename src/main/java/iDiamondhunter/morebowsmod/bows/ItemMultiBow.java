package iDiamondhunter.morebowsmod.bows;

import java.util.Random;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemMultiBow extends MoreAccessibleItemBow
{
	private Random rand = new Random();
	
    public ItemMultiBow()
    {
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
		
		//TODO where is the third shot supposed to come from??? go through the code again.
		world.spawnEntityInWorld(bowShots[0]);
		world.spawnEntityInWorld(bowShots[1]);
		
		//TODO figure out which is supposed to be changed and to what
    	if(bowShots[1].shootingEntity.rotationYaw > 180)
    	{
    		bowShots[1].posX = bowShots[1].posX + bowShots[1].shootingEntity.rotationYaw / 180; 
    	}
    	else
    	{
    		bowShots[1].posX = bowShots[1].posX + bowShots[1].shootingEntity.rotationYaw / 180;
    	}
    	bowShots[0].setDamage(bowShots[0].getDamage() * 1.5D);
    	bowShots[1].setDamage(bowShots[1].getDamage() * 1.3D);
    	bowShots[2].setDamage(bowShots[2].getDamage() * 1.15D);
    	
        if(rand.nextInt(4) == 0)
        {
        	world.spawnEntityInWorld(bowShots[2]);
        	if(bowShots[2].shootingEntity.rotationYaw > 180)
        	{
        		bowShots[2].posX = bowShots[2].posX - bowShots[2].shootingEntity.rotationYaw / 180;
        	}
        	else
        	{
        		bowShots[2].posX = bowShots[2].posX - bowShots[2].shootingEntity.rotationYaw / 180; 
        	}
        }
		
	}
    
    @Override
    public EnumRarity getRarity(ItemStack par1ItemStack)
    {
        return EnumRarity.rare;
    }

}
