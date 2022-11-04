package iDiamondhunter.morebows.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.mojang.authlib.GameProfile;

import iDiamondhunter.morebows.CustomBow;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.encryption.PlayerPublicKey;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity {

    /** Dummy constructor. */
    public AbstractClientPlayerEntityMixin(ClientWorld world, GameProfile profile, @Nullable PlayerPublicKey publicKey) {
        super(world, world.getSpawnPos(), world.getSpawnAngle(), profile, publicKey);
    }

    /** Zoom in the FOV when a CustomBow is in use. */
    @ModifyVariable(method = "getFovMultiplier()F", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F", shift = At.Shift.BEFORE), ordinal = 0)
    private float getFovMultiplierMixin(float finalFov) {
        if (isUsingItem() && getActiveItem().getItem() instanceof final CustomBow bow) {
            final int itemUseCount = getItemUseTime();
            float customBow = itemUseCount / bow.powerDiv;

            if (customBow > 1.0F) {
                customBow = 1.0F;
            } else {
                customBow *= customBow;
            }

            finalFov *= 1.0F - (customBow * 0.15F);
        }

        return finalFov;
    }

}
