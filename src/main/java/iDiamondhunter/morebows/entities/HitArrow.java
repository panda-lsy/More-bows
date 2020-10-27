package iDiamondhunter.morebows.entities;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class HitArrow extends EntityArrow {

    /* TODO remove if not needed */
    @Deprecated
    protected String particle = "portal";

    public HitArrow(World world) {
        super(world);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public HitArrow(World world, double var1, double var2, double var3) {
        super(world, var1, var2, var3);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public HitArrow(World world, EntityLivingBase living1, EntityLivingBase living2, float var1, float var2) {
        super(world, living1, living2, var1, var2);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public HitArrow(World world, EntityLivingBase living, float var) {
        super(world, living, var);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void setDead() {
        MinecraftForge.EVENT_BUS.unregister(this);
        super.setDead();
    }

    protected EntityArrow checkThis() {
        return this;
    }

    /* TODO Tidy up and merge more logic if possible */
    @SubscribeEvent
    public void hitListen(LivingAttackEvent event) {
        //iDiamondhunter.morebows.MoreBows.modLog.error("Test ParticleArrow " + event.entity.worldObj.isRemote + " test " +  this.getClass().getName() + " " + this.toString()); //debug
        if  (!event.entity.worldObj.isRemote && (checkThis() == event.source.getSourceOfDamage())) {
            //iDiamondhunter.morebows.MoreBows.modLog.error("Test ParticleArrow " + event.entity.worldObj.isRemote); //debug
            // TODO Document
            //event.entity.worldObj.spawn
            final WorldServer server = (WorldServer) event.entity.worldObj;
            onHit(server, event);
        }
    }

    /* TODO Tidy up and merge more logic if possible */
    protected void onHit(WorldServer server, LivingAttackEvent event) {
        // Create particle
        // onLivingHurtEvent is only ever the server world
        // func_147487_a seems to be a helper method to send a packet to all clients, which then spawns a particle.
        // someone on the forums defined it as WorldServer#func_147487_a(String particleName, double x, double y, double z, int numParticles, double displacementX, double displacementY, double displacementZ, double velocity)
        // https://forums.minecraftforge.net/topic/36526-1710-spawning-particles-the-world/
        // http://jabelarminecraft.blogspot.com/p/minecraft-forge-1721710-modding-tips.html
        // https://forums.minecraftforge.net/topic/21292-1710-spawn-particles/
        // https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/minecraft-mods/modification-development/2729635-particles-not-spawning?page=2
        // (String particleType, double xCoord, double yCoord, double zCoord, int numberOfParticles, double xOffset, double yOffset, double zOffset, double particleSpeed)
        final int numPart = 1;
        final double vel = 1;
        // original event.entity.worldObj.spawnParticle(particle, (event.entity.posX + (rand.nextFloat() * event.entity.width * 2.0F)) - event.entity.width, event.entity.posY + 0.5D + (rand.nextFloat() * event.entity.height), (event.entity.posZ + (rand.nextFloat() * event.entity.width * 2.0F)) - event.entity.width, rand.nextGaussian() * 0.02D, rand.nextGaussian() * 0.02D, rand.nextGaussian() * 0.02D);
        //server.func_147487_a(particle, (event.entity.posX + (rand.nextFloat() * event.entity.width * 2.0F)) - event.entity.width, event.entity.posY + 0.5D + (rand.nextFloat() * event.entity.height), (event.entity.posZ + (rand.nextFloat() * event.entity.width * 2.0F)) - event.entity.width, numPart /* Number of particles? */, rand.nextGaussian() * 0.02D, rand.nextGaussian() * 0.02D, rand.nextGaussian() * 0.02D, vel /* Velocity? Not sure... */);
        server.func_147487_a(particle, (event.entity.posX + (rand.nextFloat() * event.entity.width * 2.0F)) - event.entity.width, event.entity.posY + 0.5D + (rand.nextFloat() * event.entity.height), (event.entity.posZ + (rand.nextFloat() * event.entity.width * 2.0F)) - event.entity.width, numPart /* Number of particles? */, 0, 0, 0, vel /* Velocity? Not sure... */);
    }

}
