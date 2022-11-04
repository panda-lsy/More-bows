package iDiamondhunter.morebows.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import iDiamondhunter.morebows.CustomBow;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {

    /**
     * renderFirstPersonItem uses a hardcoded value when rendering the bow draw back animation.
     * TODO This is not a good Mixin.
     */
    @ModifyConstant(method = "renderFirstPersonItem", constant = @Constant(floatValue = 20.0f))
    private float modifyBowDiv(float div, AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (item.getItem() instanceof final CustomBow bow) {
            return bow.powerDiv;
        }

        return div;
    }

}
