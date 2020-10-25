package iDiamondhunter.morebowsmod.entities;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class EnderArrow extends EntityArrow {

    @Deprecated
    private static final String particle = "portal";

    public EnderArrow(World world) {
        super(world);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public EnderArrow(World world, double var1, double var2, double var3) {
        super(world, var1, var2, var3);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public EnderArrow(World world, EntityLivingBase living1, EntityLivingBase living2, float var1, float var2) {
        super(world, living1, living2, var1, var2);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public EnderArrow(World world, EntityLivingBase living, float var) {
        super(world, living, var);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void setDead() {
        MinecraftForge.EVENT_BUS.unregister(this);
        super.setDead();
    }

    @SubscribeEvent
    public void onLivingHurtEvent(LivingAttackEvent event) {
        if (this == event.source.getSourceOfDamage()) {
            event.entity.worldObj.spawnParticle(particle, (event.entity.posX + (rand.nextFloat() * event.entity.width * 2.0F)) - event.entity.width, event.entity.posY + 0.5D + (rand.nextFloat() * event.entity.height), (event.entity.posZ + (rand.nextFloat() * event.entity.width * 2.0F)) - event.entity.width, rand.nextGaussian() * 0.02D, rand.nextGaussian() * 0.02D, rand.nextGaussian() * 0.02D);
        }
    }

}
