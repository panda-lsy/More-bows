package iDiamondhunter.morebows.proxy;

import java.util.Random;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import iDiamondhunter.morebows.entities.CustomArrow;
import net.minecraft.entity.Entity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

/** TODO Remove if not needed */
public class Common {

    static Random rand = new Random();

    public static void spawnParticle(WorldServer server, Entity entity, String particle) {
        final int numPart = 1;
        final double vel = 1;
        // Create particle
        // onLivingHurtEvent is only ever the server world
        // func_147487_a seems to be a helper method to send a packet to all clients, which then spawns a particle.
        // someone on the forums defined it as WorldServer#func_147487_a(String particleName, double x, double y, double z, int numParticles, double displacementX, double displacementY, double displacementZ, double velocity)
        // https://forums.minecraftforge.net/topic/36526-1710-spawning-particles-the-world/
        // http://jabelarminecraft.blogspot.com/p/minecraft-forge-1721710-modding-tips.html
        // https://forums.minecraftforge.net/topic/21292-1710-spawn-particles/
        // https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/minecraft-mods/modification-development/2729635-particles-not-spawning?page=2
        // (String particleType, double xCoord, double yCoord, double zCoord, int numberOfParticles, double xOffset, double yOffset, double zOffset, double particleSpeed)
        // original event.entity.worldObj.spawnParticle(particle, (event.entity.posX + (rand.nextFloat() * event.entity.width * 2.0F)) - event.entity.width, event.entity.posY + 0.5D + (rand.nextFloat() * event.entity.height), (event.entity.posZ + (rand.nextFloat() * event.entity.width * 2.0F)) - event.entity.width, rand.nextGaussian() * 0.02D, rand.nextGaussian() * 0.02D, rand.nextGaussian() * 0.02D);
        //server.func_147487_a(particle, (event.entity.posX + (rand.nextFloat() * event.entity.width * 2.0F)) - event.entity.width, event.entity.posY + 0.5D + (rand.nextFloat() * event.entity.height), (event.entity.posZ + (rand.nextFloat() * event.entity.width * 2.0F)) - event.entity.width, numPart /* Number of particles? */, rand.nextGaussian() * 0.02D, rand.nextGaussian() * 0.02D, rand.nextGaussian() * 0.02D, vel /* Velocity? Not sure... */);
        server.func_147487_a(particle, (entity.posX + (rand.nextFloat() * entity.width * 2.0F)) - entity.width, entity.posY + 0.5D + (rand.nextFloat() * entity.height), (entity.posZ + (rand.nextFloat() * entity.width * 2.0F)) - entity.width, numPart /* Number of particles? */, 0, 0, 0, vel /* Velocity? Not sure... */);
        //iDiamondhunter.morebows.MoreBows.modLog.error("Particle spawned! entity " + entity.getClass().getName() + " particle " + particle); //debug
    }

    @SubscribeEvent
    public void arrHit(LivingAttackEvent event) {
        //iDiamondhunter.morebows.MoreBows.modLog.error("Test ParticleArrow " + event.entity.worldObj.isRemote + " test " +  this.getClass().getName() + " " + this.toString()); //debug
        //iDiamondhunter.morebows.MoreBows.modLog.error("An attack was detected."); //debug

        /*if (event.source.getSourceOfDamage() instanceof CustomArrow) {
            iDiamondhunter.morebows.MoreBows.modLog.error("Event was a custom arrow: " + event.source.getSourceOfDamage().getClass().getName()); //debug

            if (!event.entity.worldObj.isRemote) {
                iDiamondhunter.morebows.MoreBows.modLog.error("World was server."); //debug
            } else {
                iDiamondhunter.morebows.MoreBows.modLog.error("World was client."); //debug
            }
        } else {
            iDiamondhunter.morebows.MoreBows.modLog.error("Event was not custom arrow"); //debug
        }*/
        if  (!event.entity.worldObj.isRemote && (event.source.getSourceOfDamage() instanceof CustomArrow)) {
            //iDiamondhunter.morebows.MoreBows.modLog.error("Test arr " + event.entity.worldObj.isRemote); //debug
            // TODO Document
            //event.entity.worldObj.spawn
            final CustomArrow arr = (CustomArrow) event.source.getSourceOfDamage();
            final WorldServer server = (WorldServer) event.entity.worldObj;
            spawnParticle(server, event.entity, "portal");

            // TODO Figure out that weird code from the fire arrow.
            if (arr.fire) {
                event.entity.setFire(15);
            } //else if (arr.type == 2) {}
        }
    }

    public void register() {
        // It's nothing!
    }

}
