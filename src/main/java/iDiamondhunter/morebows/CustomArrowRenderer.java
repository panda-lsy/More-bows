package iDiamondhunter.morebows;

import iDiamondhunter.morebows.entities.CustomArrow;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

/** A custom EntityRenderer, to customize the rendering of frost arrows. */
final class CustomArrowRenderer extends ProjectileEntityRenderer<CustomArrow> {

    private static final Identifier ARROWS = new Identifier("textures/entity/projectiles/arrow.png");

    private final FlyingItemEntityRenderer<CustomArrow> snow;

    CustomArrowRenderer(Context ctx) {
        super(ctx);
        snow = new FlyingItemEntityRenderer<>(ctx);
    }

    @Override
    public void render(CustomArrow persistentProjectileEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        if ((persistentProjectileEntity.getDataTracker().get(CustomArrow.trackedType) == MoreBows.ARROW_TYPE_FROST) && !MoreBows.configGeneralInst.oldFrostArrowRendering) {
            snow.render(persistentProjectileEntity, f, g, matrixStack, vertexConsumerProvider, i);
        } else {
            // TODO Implement old cube rendering
            super.render(persistentProjectileEntity, f, g, matrixStack, vertexConsumerProvider, i);
        }
    }

    @Override
    public Identifier getTexture(CustomArrow persistentProjectileEntity) {
        return ((persistentProjectileEntity.getDataTracker().get(CustomArrow.trackedType) == MoreBows.ARROW_TYPE_FROST) && !MoreBows.configGeneralInst.oldFrostArrowRendering) ? SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE : ARROWS;
    }

}
