package iDiamondhunter.morebows;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.entity.EmptyEntityRenderer;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

/** The client mod initializer. */
public final class Client implements ClientModInitializer {
    /** Client specific mod initialization code. */
    @Override
    public void onInitializeClient() {
        final Identifier pull = new Identifier("pull");
        final Identifier pulling = new Identifier("pulling");
        final ClampedModelPredicateProvider pullProvider = (stack, world, entity, seed) -> ((entity == null) || (entity.getActiveItem() != stack) ? 0.0F : (stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / ((CustomBow) stack.getItem()).powerDiv);
        final ClampedModelPredicateProvider pullingProvider = (stack, world, entity, seed) -> ((entity != null) && entity.isUsingItem() && (entity.getActiveItem() == stack) ? 1.0F : 0.0F);
        EntityRendererRegistry.register(MoreBows.ARROW_SPAWNER, EmptyEntityRenderer::new);
        EntityRendererRegistry.register(MoreBows.CUSTOM_ARROW, CustomArrowRenderer::new);

        for (final Item bow : MoreBows.getAllItems()) {
            ModelPredicateProviderRegistry.register(bow, pull, pullProvider);
            ModelPredicateProviderRegistry.register(bow, pulling, pullingProvider);
        }
    }

}
