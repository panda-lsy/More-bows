package iDiamondhunter.morebows;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.item.UnclampedModelPredicateProvider;
import net.minecraft.client.render.entity.EmptyEntityRenderer;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

/** The client mod initializer. */
public final class Client implements ClientModInitializer {

    private static final Identifier PULL = new Identifier("pull");
    private static final Identifier PULLING = new Identifier("pulling");

    private static final UnclampedModelPredicateProvider PULL_PROVIDER = (stack, world, entity, seed) -> (entity == null ? 0.0F : entity.getActiveItem() != stack ? 0.0F : (stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / ((CustomBow) stack.getItem()).powerDiv);
    private static final UnclampedModelPredicateProvider PULLING_PROVIDER = (stack, world, entity, seed) -> ((entity != null) && entity.isUsingItem() && (entity.getActiveItem() == stack) ? 1.0F : 0.0F);

    /** Client specific mod initialization code. */
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(MoreBows.ARROW_SPAWNER, EmptyEntityRenderer::new);
        EntityRendererRegistry.register(MoreBows.CUSTOM_ARROW, CustomArrowRenderer::new);

        for (final Item bow : MoreBows.getAllItems()) {
            FabricModelPredicateProviderRegistry.register(bow, PULL, PULL_PROVIDER);
            FabricModelPredicateProviderRegistry.register(bow, PULLING, PULLING_PROVIDER);
        }
    }

}
