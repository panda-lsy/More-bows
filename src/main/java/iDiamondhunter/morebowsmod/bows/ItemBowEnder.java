package iDiamondhunter.morebowsmod.bows;

import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import java.util.Timer;
import java.util.TimerTask;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
//import cpw.mods.fml.relauncher.Side;
import iDiamondhunter.morebowsmod.MoreBowsMod;
import iDiamondhunter.morebowsmod.entities.EntityiDiamondhunterEnderArrow;

public class ItemBowEnder extends MoreAccessibleItemBow
{
	@Deprecated
	Timer timer = new Timer();
	
	public ItemBowEnder()
    {
		super(384);
		super.arrowPowerDivisor = 22F;
		MinecraftForge.EVENT_BUS.register(this);
    }
	
	@Override
	public void setArrows(World world, EntityPlayer player) {
		
		super.bowShots = new EntityArrow[] {
		new EntityiDiamondhunterEnderArrow(world, player, shotVelocity * 2.0F),
		new EntityiDiamondhunterEnderArrow(world, player, shotVelocity * 1F),
		new EntityiDiamondhunterEnderArrow(world, player, shotVelocity * 1.2F),
		new EntityiDiamondhunterEnderArrow(world, player, shotVelocity * 1.5F),
		new EntityiDiamondhunterEnderArrow(world, player, shotVelocity * 1.75F),
		new EntityiDiamondhunterEnderArrow(world, player, shotVelocity * 1.825F)
		};
		
		bowShots[1].canBePickedUp = 2;
		bowShots[2].canBePickedUp = 2;
		bowShots[3].canBePickedUp = 2;
		bowShots[4].canBePickedUp = 2;
		bowShots[5].canBePickedUp = 2;
		
	}
	
	/** TODO Decide if the player only gets one arrow back or consumes the amount that they shoot, replace TimerTask with tick counting (see {@code MoreArrowsTask}). */
	@Override
	public void spawnArrows(World world) { 
		
		world.spawnEntityInWorld(bowShots[0]);
		
        timer.schedule(new MoreArrowsTask(world, bowShots), 3000);
        //MoreBowsMod.modLog.info("REMOVE AFTER TEST: New timer!"); //debug
		
	}
	
	/** TODO: Remove this as soon as possible!
	 * In case it isn't obvious, this current behavior is NOT thread safe, and is wildly dangerous! 
	 * It causes ConcurrentModificationExceptions, so be warned!
	 * Also, the SubscribeEvent's aren't working, so there's not even *any* functional safeguards!
	 * The original mod technically beats me on being thread safe, but only because it froze the entire game for 3 seconds. 
	 * Yes, really, it was using thread.sleep. */
	@Deprecated
    class MoreArrowsTask extends TimerTask {
    	
    	EntityArrow[] arrows;
    	World world;
    	
    	@Deprecated
    	MoreArrowsTask (World world, EntityArrow[] arrows) {
    		
    		this.arrows = arrows;
    		this.world = world;
    		
    	}
    	
    	@SubscribeEvent
    	public void unloadingWorld (WorldEvent.Unload event)
    	{
    		if (event.world == world) {
    		MoreBowsMod.modLog.info("Ender arrows lost in transit! World " + event.world.getWorldInfo().getWorldName() + " was unloaded before arrows could be spawned.");
    		this.cancel();
    		}
    	}
    	
    	@SubscribeEvent
    	public void playerLoggedOutEvent (PlayerEvent.PlayerLoggedOutEvent event)
    	{
    		if (event.player == arrows[0].shootingEntity) {
    		MoreBowsMod.modLog.info("Ender arrows lost in transit! Player " + event.player.getDisplayName() + " logged out before arrows could be spawned.");
    		this.cancel();
    		}
    	}
    	
    	@Deprecated
        public void run() {
            spawnMoreArrows(world, arrows);
            //MoreBowsMod.modLog.info("REMOVE AFTER TEST: Cancel timer, spawn arrows!"); //debug
            this.cancel();
        }
    }
    
	/** TODO: Replace this as soon as possible! See {@code MoreArrowsTask}. */
	@Deprecated
    public void spawnMoreArrows(World world, EntityArrow[] arrows) {
    	
		/* TODO Replace these sounds as well */
		
    	world.spawnEntityInWorld(arrows[1]);
    	world.playSoundAtEntity(arrows[1], "mob.endermen.portal", 0.5F, 1F / (itemRand.nextFloat() * 0.4F + 1F) + shotVelocity * 0.4F);
    	
    	world.spawnEntityInWorld(arrows[2]);
    	arrows[2].posY++;
    	arrows[2].posX -= 1.25;
		arrows[2].posZ += 1.75;
		world.playSoundAtEntity(arrows[2], defaultShotSound, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + shotVelocity * 0.5F);
		
		world.spawnEntityInWorld(arrows[3]);
		arrows[3].posY += 1.45;
		arrows[3].posX -= 2.25;
		arrows[3].posZ -= 0.75;
		world.playSoundAtEntity(arrows[3], "mob.endermen.portal", 0.25F, 1F / (itemRand.nextFloat() * 0.4F + 1F) + shotVelocity * 0.3F);
		
		world.spawnEntityInWorld(arrows[4]);
        arrows[4].posY += 2;
        arrows[4].posX += 0.25;
        arrows[4].posZ += 2.5;
        world.playSoundAtEntity(arrows[4], defaultShotSound, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + shotVelocity * 0.5F);
        
        world.spawnEntityInWorld(arrows[5]);
        arrows[5].posY += 1.75;
        arrows[5].posX += 1.75;
        arrows[5].posZ += 1.5;
        world.playSoundAtEntity(arrows[5], "mob.endermen.portal", 0.5F, 1F / (itemRand.nextFloat() * 0.4F + 1F) + shotVelocity * 0.4F);
    	
        /* Portal 3 confirmed??? */
        
    }
	
    @Override
    public EnumRarity getRarity(ItemStack itemstack)
    {
    	return EnumRarity.epic;
    }

}
