package iDiamondhunter.morebows;

import iDiamondhunter.morebows.entities.CustomArrow;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EmptyEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

/** The client mod initializer + a custom EntityRenderer, to customize the rendering of frost arrows. */
public final class Client extends ProjectileEntityRenderer<CustomArrow> implements ClientModInitializer {
    private static final Identifier ARROWS = new Identifier("textures/entity/projectiles/arrow.png");

    private final FlyingItemEntityRenderer<CustomArrow> snow;

    @SuppressWarnings("NullAway")
    public Client() {
        super(new Context(null, null, null, null, null, null, null));
        snow = null;
    }

    private Client(Context context) {
        super(context);
        snow = new FlyingItemEntityRenderer<>(context);
    }

    @Override
    public Identifier getTexture(CustomArrow entity) {
        return (entity.getDataTracker().get(CustomArrow.trackedType) == MoreBows.ARROW_TYPE_FROST) && !MoreBows.configGeneralInst.oldFrostArrowRendering ? SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE : ARROWS;
    }

    @Override
    public void render(CustomArrow entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if ((entity.getDataTracker().get(CustomArrow.trackedType) == MoreBows.ARROW_TYPE_FROST) && !MoreBows.configGeneralInst.oldFrostArrowRendering) {
            snow.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        } else {
            // TODO Implement old cube rendering
            super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        }
    }

    /** Client specific mod initialization code. */
    @Override
    public void onInitializeClient() {
        final Identifier pull = new Identifier("pull");
        final Identifier pulling = new Identifier("pulling");
        final ClampedModelPredicateProvider pullProvider = (stack, world, entity, seed) -> ((entity == null) || (entity.getActiveItem() != stack) ? 0.0F : (stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / ((CustomBow) stack.getItem()).powerDiv);
        final ClampedModelPredicateProvider pullingProvider = (stack, world, entity, seed) -> ((entity != null) && entity.isUsingItem() && (entity.getActiveItem() == stack) ? 1.0F : 0.0F);
        EntityRendererRegistry.register(MoreBows.ARROW_SPAWNER, EmptyEntityRenderer::new);
        EntityRendererRegistry.register(MoreBows.CUSTOM_ARROW, Client::new);

        for (final Item bow : MoreBows.getAllItems()) {
            ModelPredicateProviderRegistry.register(bow, pull, pullProvider);
            ModelPredicateProviderRegistry.register(bow, pulling, pullingProvider);
        }
    }

}
