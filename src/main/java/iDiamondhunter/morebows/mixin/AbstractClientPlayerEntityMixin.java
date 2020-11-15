package iDiamondhunter.morebows.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import com.mojang.authlib.GameProfile;

import iDiamondhunter.morebows.bows.CustomBow;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity {

    //Dummy constructor, I'm not sure if this is how it's supposed to be done or if I'm bad at Mixins.
    public AbstractClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @ModifyVariable(method = "getSpeed()F", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F", shift = At.Shift.BEFORE), ordinal = 0)
    //@ModifyVariable(method = "getSpeed()F", at = @At(value = "TAIL", shift = At.Shift.BEFORE), ordinal = 0) // this doesn't work? probably due to some bytecode differences...
    private float fovModify(float f) {
        if (this.isUsingItem() && this.getActiveItem().getItem() instanceof CustomBow) {
            int i = this.getItemUseTime();
            float g = (float)i / 20.0F;

            if (g > 1.0F) {
                g = 1.0F;
            } else {
                g *= g;
            }

            f *= 1.0F - g * 0.15F;
        }

        return f;
    }

}
