package iDiamondhunter.morebows.entities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.world.World;

public class CustomArrow extends EntityArrow {

    //* TODO Attempt to merge FrostArrow with this if possible (need to create custom renderer for the "snowball") */
    public Boolean fire = false;

    // TODO I think I can't remove these constructors, but I'm not sure.
    public CustomArrow(World world) {
        super(world);
    }

    public CustomArrow(World world, double var1, double var2, double var3) {
        super(world, var1, var2, var3);
    }

    public CustomArrow(World world, EntityLivingBase living1, EntityLivingBase living2, float var1, float var2) {
        super(world, living1, living2, var1, var2);
    }

    public CustomArrow(World world, EntityLivingBase living, float var) {
        super(world, living, var);
    }

    public CustomArrow(World world, EntityLivingBase living, float var, Boolean fire) {
        this(world, living, var);
        this.fire = fire;
    }

}
