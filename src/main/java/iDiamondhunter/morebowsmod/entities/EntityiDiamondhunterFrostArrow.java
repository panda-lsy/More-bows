package iDiamondhunter.morebowsmod.entities;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.IThrowableEntity;

public class EntityiDiamondhunterFrostArrow extends EntityArrow implements IProjectile, IThrowableEntity { //TODO re-implement rendering of snowball instead of arrow

    //final static int snowID = GameData.getBlockRegistry().getId(Blocks.snow);
    //final static int iceID = GameData.getBlockRegistry().getId(Blocks.ice);

    private boolean isThisActuallyCriticalThough = false;

    public EntityiDiamondhunterFrostArrow(World world) {
        super(world);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public EntityiDiamondhunterFrostArrow(World world, double var1, double var2, double var3) {
        super(world, var1, var2, var3);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public EntityiDiamondhunterFrostArrow(World world, EntityLivingBase living1, EntityLivingBase living2, float var1, float var2) {
        super(world, living1, living2, var1, var2);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public EntityiDiamondhunterFrostArrow(World world, EntityLivingBase living, float var) {
        super(world, living, var);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void setDead() {
        super.setDead();
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    /** See {@code getIsCritical()} for an explanation of why this method manually adds crit damage to the attack. */
    @SubscribeEvent
    public void onLivingAttackEvent(LivingAttackEvent event) {
        if (this.isThisActuallyCriticalThough && this == event.source.getSourceOfDamage()) {
            //MoreBowsMod.modLog.info("thisIsActuallyCriticalThough: attack edition"); //debug
            isThisActuallyCriticalThough = false;
            event.setCanceled(true);
            event.entity.attackEntityFrom(event.source, event.ammount);
        }
    }

    /** See {@code getIsCritical()} for why this kills the arrow after the attack. */
    @SubscribeEvent
    public void onLivingHurtEvent(LivingHurtEvent event) {
        if (this == event.source.getSourceOfDamage()) {
            event.entity.setInWeb(); //TODO Replace with slowness effect? This is the original behavior...
        }

        if (!(event.entity instanceof EntityEnderman)) { //TODO Verify that this is the right behavior
            this.setDead();
        }
    }

    /*@SubscribeEvent
    public void onParticleSpawn(EntityJoinWorldEvent event) //I don't think that this is possible to do. TODO remove code for release.
    {
        if (event.entity instanceof EntityCritFX) {
            MoreBowsMod.modLog.info("ENTITY ENTERED WORLD WHEN ARROW WAS CRITICAL: " + this.getDistanceToEntity(event.entity));
            MoreBowsMod.modLog.info("ALSO " + (this.posX + this.posY + this.posZ) + " " + (event.entity.posX + event.entity.posY + event.entity.posZ));
            }

        if (event.entity instanceof EntityCritFX && (this.posX + this.posY + this.posZ) == (event.entity.posX + event.entity.posY + event.entity.posZ)) {
            //event.setCanceled(true);
            this.worldObj.spawnParticle("splash", event.entity.posX, event.entity.posY, event.entity.posZ, event.entity.motionX, event.entity.motionY, event.entity.motionZ);
            //event.entity.setDead(); //I don't think this does anything. Another method already seems to have grabbed the particle.
        }
    }*/

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.inGround) { //TODO: Redo this by counting ticks & remove access widener.
            if (this.ticksInGround <= 2) {
                this.worldObj.spawnParticle("snowballpoof", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
            }

            /*
             * if (Block.isEqualTo(test, Blocks.water)) {
             * this.worldObj.setBlockMetadataWithNotify(tempThingX, tempThingY, tempThingZ,
             * Block.getIdFromBlock(Blocks.ice), 3); }
             */

            if (this.ticksInGround <= 30) {
                this.worldObj.spawnParticle("splash", this.posX, this.posY - 0.3D, this.posZ, 0.0D, 0.0D, 0.0D);
            }

            //if (this.ticksInGround >= 64 && this.ticksInGround <= 65) //Why was this the original logic?
            /** Responsible for adding snow layers on top the block the arrow hits, or "freezing" the water it's in by setting the block to ice. */
            if (this.ticksInGround == 64) {
                //test = this.worldObj.getBlock((int) this.posX, (int) this.posY, (int) this.posZ);
                final int tempThingX = MathHelper.floor_double(this.posX);
                final int tempThingY = MathHelper.floor_double(this.posY);
                final int tempThingZ = MathHelper.floor_double(this.posZ);
                /* TODO Verify that this is the right block!
                 * Also, why does this sometimes set multiple blocks? It's the correct behavior of the original mod, but it's concerning... */
                Block testo2 = this.worldObj.getBlock(tempThingX, tempThingY, tempThingZ);

                //if (Block.isEqualTo(this.field_145790_g, Blocks.snow)) {
                /* TODO Possibly implement incrementing snow layers. */
                if (Block.isEqualTo(testo2, Blocks.air)) {
                    //this.worldObj.setBlockMetadataWithNotify(tempThingX, tempThingY, tempThingZ, snowID, 3);
                    this.worldObj.setBlock(tempThingX, tempThingY, tempThingZ, Blocks.snow_layer);
                }

                if (Block.isEqualTo(testo2, Blocks.water)) {
                    /* TODO Check if the earlier event or this one is the correct one.
                     * Also: bouncy arrow on ice, a bit like stone skimming? Could be cool. */
                    //this.worldObj.setBlockMetadataWithNotify(tempThingX, tempThingY, tempThingZ, iceID, 3); //help I don't understand
                    this.worldObj.setBlock(tempThingX, tempThingY, tempThingZ, Blocks.ice); //good enough for science
                    //this.worldObj.setBlock(tempThingX, tempThingY, tempThingZ, Blocks.water, -1, 2); //TODO figure out if it's possible to freeze water by setting metadata. the original mod suggests that it is, but I'm probably reading the code wrong.
                }
            }
        } else if (this.isThisActuallyCriticalThough && !this.worldObj.isRemote) { //TODO: Replace with sided proxy, make sure you're actually just supposed to spawn particles on server

            //MoreBowsMod.modLog.info("thisIsActuallyCriticalThough: particles edition"); //debug
            //TODO this isn't spawning particles help
            for (int i = 0; i < 4; ++i) {
                //this.worldObj.spawnParticle("crit", this.posX + this.motionX * (double)i / 4.0D, this.posY + this.motionY * (double)i / 4.0D, this.posZ + this.motionZ * (double)i / 4.0D, -this.motionX, -this.motionY + 0.2D, -this.motionZ);
                this.worldObj.spawnParticle("crit", this.posX + this.motionX * (double)i / 4.0D, this.posY + this.motionY * (double)i / 4.0D, this.posZ + this.motionZ * (double)i / 4.0D, -this.motionX, -this.motionY + 0.2D, -this.motionZ);
            }
        }
    }

    /** TODO: Is this ever used? Might be legacy ModLoader code... */
    @Deprecated
    public Entity getThrower() {
        return this.shootingEntity;
    }

    /** TODO: Is this ever used? Might be legacy ModLoader code... */
    @Deprecated
    public void setThrower(Entity var1) {
        this.shootingEntity = var1;
    }

    /** This is only used to take into account whether the arrow adds crit damage or not! See {@code getIsCritical()} for the explanation. */
    public void setIsCritical(boolean crit) {
        super.setIsCritical(crit);
        isThisActuallyCriticalThough = true;
    }

    /** This doesn't actually return whether the arrow is critical, it will always return false! See the comments in the code for why this awful hack was made. */
    @Deprecated
    public boolean getIsCritical() {
        return false;
        /* Obviously, you're just a bad shot :D
         *
         * This is an awful hack to prevent the vanilla crit particles from displaying.
         * The vanilla code to display the arrow particle trail is buried deep inside onUpdate,
         * and the only other options I have are to:
         * - intercept the particles with packets,
         * - intercept the particles with events (not feasible from what I can tell),
         * - ASM it out,
         * - or perform some ridiculous wrapping around the World to intercept the method to spawn particles.
         *
         * Instead of doing that, I just prevent anything from ever knowing that it's crited,
         * and instead I wrap around the event when the arrow attacks something. See onLivingAttackEvent() for the details,
         * but the TLDR is that I cancel the attack and start a new one with the crit taken into account.
         * This allows the entity to take the crit into account when deciding if it's damaged or not.
         *
         * This is probably the lesser of these evils.
         */
    }
}
