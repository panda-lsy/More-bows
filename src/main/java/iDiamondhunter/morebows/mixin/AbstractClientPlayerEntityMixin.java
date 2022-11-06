package iDiamondhunter.morebows.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.mojang.authlib.GameProfile;

import iDiamondhunter.morebows.CustomBow;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;

/**
 * A mixin to {@link net.minecraft.client.network.AbstractClientPlayerEntity},
 * to modify the player's FOV when drawing back a CustomBow.
 */
@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity {

    /**
     * Dummy constructor.
     *
     * @param world       the world
     * @param gameProfile the profile
     * @param publicKey   the public key
     * @deprecated Why are you calling this? It's a mixin.
     */
    @SuppressWarnings("unused")
    private AbstractClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, world.getSpawnPos(), world.getSpawnAngle(), profile);
    }

    /**
     * Zoom in the FOV when a CustomBow is in use.
     *
     * @param finalFov the unadjusted FOV
     * @return the adjusted FOV
     * @deprecated Why are you calling this? It's a mixin.
     */
    @SuppressWarnings("unused")
    @ModifyVariable(method = "getSpeed()F", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F", shift = At.Shift.BEFORE), ordinal = 0)
    private float getSpeedMixin(float finalFov) {
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
