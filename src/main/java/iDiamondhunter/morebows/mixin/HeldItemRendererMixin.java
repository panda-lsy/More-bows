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

/**
 * A mixin to {@link net.minecraft.client.render.item.HeldItemRenderer},
 * to modify the drawback speed of a CustomBow.
 */
@Mixin(HeldItemRenderer.class)
public final class HeldItemRendererMixin {

    /**
     * Dummy constructor.
     *
     * @deprecated Why are you calling this? It's a mixin.
     */
    @SuppressWarnings("unused")
    private HeldItemRendererMixin() {
        // Empty private constructor to hide default constructor
    }

    /**
     * renderFirstPersonItem uses a hardcoded value when rendering
     * the bow draw back animation.
     * TODO This is not a good Mixin.
     *
     * @param div             the standard bow's drawback divisor
     * @param player          unused
     * @param tickDelta       unused
     * @param pitch           unused
     * @param hand            unused
     * @param swingProgress   unused
     * @param item            the item in use
     * @param equipProgress   unused
     * @param matrices        unused
     * @param vertexConsumers unused
     * @param light           unused
     * @return the CustomBow's drawback divisor
     * @deprecated Why are you calling this? It's a mixin.
     */
    @SuppressWarnings("unused")
    @ModifyConstant(method = "renderFirstPersonItem", constant = @Constant(floatValue = 20.0f))
    private float modifyBowDiv(float div, AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (item.getItem() instanceof final CustomBow bow) {
            return bow.powerDiv;
        }

        return div;
    }

}
