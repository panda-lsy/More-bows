package iDiamondhunter.morebowsmod.entities;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class FireArrow extends EntityArrow {

    public FireArrow(World world) {
        super(world);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public FireArrow(World world, double var1, double var2, double var3) {
        super(world, var1, var2, var3);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public FireArrow(World world, EntityLivingBase living1, EntityLivingBase living2, float var1, float var2) {
        super(world, living1, living2, var1, var2);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public FireArrow(World world, EntityLivingBase living, float var) {
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
        //if(!event.source.getSourceOfDamage().isEntityEqual(this) && !event.entity.isBurning() && !event.entity.handleWaterMovement()) { // TODO: Verify if correct behavior
        if ((this == event.source.getSourceOfDamage()) && event.entity.isBurning()) {
            event.entity.setFire(15);
            /** TODO Replace particle spawning with proxy methods. */
            event.entity.worldObj.spawnParticle("portal", (event.entity.posX + (rand.nextFloat() * event.entity.width * 2.0F)) - event.entity.width, event.entity.posY + 0.5D + (rand.nextFloat() * event.entity.height), (event.entity.posZ + (rand.nextFloat() * event.entity.width * 2.0F)) - event.entity.width, rand.nextGaussian() * 0.02D, rand.nextGaussian() * 0.02D, rand.nextGaussian() * 0.02D);
        }
    }

    /** TODO I'm not sure what this code does / did. Probably used for setting fire to things. Check original behavior. */
    /*@Override
    public void onUpdate()
    {
        super.onUpdate();

        if (flag = flag || this.onGround) {
            if (this.worldObj.canPlaceEntityOnSide(Blocks.fire, (int) this.posX, (int) this.posY, (int) this.posZ, true, 1, (Entity)null, (ItemStack)null))
            {
                this.setDead();
            }
            flag = false;
        }
    }*/

    /** TODO I'm not sure what this code does / did. Probably used for setting fire to things. Check original behavior. */
    /*public void hitGround(int var1, int var2, int var3, int var4, int var5)
    {
        if (var4 == 0)
        {
            --var2;
        }

        if (var4 == 1)
        {
            ++var2;
        }

        if (var4 == 2)
        {
            --var3;
        }

        if (var4 == 3)
        {
            ++var3;
        }

        if (var4 == 4)
        {
            --var1;
        }

        if (var4 == 5)
        {
            ++var1;
        }

        if (this.worldObj.canPlaceEntityOnSide(Block.fire.blockID, var1, var2, var3, true, 1, (Entity)null))
        {

            this.setDead();
        }
    }*/

}
