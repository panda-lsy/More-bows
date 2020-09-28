package iDiamondhunter.morebowsmod.entities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EntityiDiamondhunterEnderArrow extends EntityArrow
{
	
	@Deprecated
	private static final String particle = "portal";
	
    public EntityiDiamondhunterEnderArrow(World world)
    {
        super(world);
        MinecraftForge.EVENT_BUS.register(this);
    }
	
    public EntityiDiamondhunterEnderArrow(World world, double var1, double var2, double var3)
    {
    	super(world, var1, var2, var3);
    	MinecraftForge.EVENT_BUS.register(this);
    }
    
    public EntityiDiamondhunterEnderArrow(World world, EntityLivingBase living1, EntityLivingBase living2, float var1, float var2)
    {
    	super(world, living1, living2, var1, var2);
    	MinecraftForge.EVENT_BUS.register(this);
    }
    
    public EntityiDiamondhunterEnderArrow(World world, EntityLivingBase living, float var)
    {
    	super(world, living, var);
    	MinecraftForge.EVENT_BUS.register(this);
    }
    
    @Override
    public void setDead()
    {
        super.setDead();
        MinecraftForge.EVENT_BUS.unregister(this);
    }
    
    @SubscribeEvent
    public void onLivingHurtEvent(LivingAttackEvent event)
    {
    	if(this == event.source.getSourceOfDamage()) {
            event.entity.worldObj.spawnParticle(particle, event.entity.posX + (double)(this.rand.nextFloat() * event.entity.width * 2.0F) - (double)event.entity.width, event.entity.posY + 0.5D + (double)(this.rand.nextFloat() * event.entity.height), event.entity.posZ + (double)(this.rand.nextFloat() * event.entity.width * 2.0F) - (double)event.entity.width, this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D);
        }
    }
    
}
