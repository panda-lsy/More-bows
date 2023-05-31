package iDiamondhunter.morebows;

import com.mojang.blaze3d.matrix.MatrixStack;

import iDiamondhunter.morebows.entities.CustomArrow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;

/** A custom EntityRenderer, to customize the rendering of frost arrows. */
final class CustomArrowRenderer extends ArrowRenderer<CustomArrow> {

    private static final ResourceLocation ARROWS = new ResourceLocation("textures/entity/projectiles/arrow.png");

    private final SpriteRenderer<CustomArrow> snow;

    CustomArrowRenderer(EntityRendererManager context) {
        super(context);
        snow = new SpriteRenderer<>(context, Minecraft.getInstance().getItemRenderer());
    }

    @Override
    public ResourceLocation getTextureLocation(CustomArrow entity) {
        return (entity.getEntityData().get(CustomArrow.trackedType) == MoreBows.ARROW_TYPE_FROST) && !MoreBows.configGeneralInst.oldFrostArrowRendering ? AtlasTexture.LOCATION_BLOCKS : ARROWS;
    }

    @Override
    public void render(CustomArrow entity, float yaw, float tickDelta, MatrixStack matrices, IRenderTypeBuffer vertexConsumers, int light) {
        if ((entity.getEntityData().get(CustomArrow.trackedType) == MoreBows.ARROW_TYPE_FROST) && !MoreBows.configGeneralInst.oldFrostArrowRendering) {
            snow.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        } else {
            // TODO Implement old cube rendering
            super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        }
    }

}
