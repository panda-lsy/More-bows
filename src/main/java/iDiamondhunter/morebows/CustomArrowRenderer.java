package iDiamondhunter.morebows;


import com.mojang.blaze3d.vertex.PoseStack;

import iDiamondhunter.morebows.entities.CustomArrow;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

/** A custom EntityRenderer, to customize the rendering of frost arrows. */
final class CustomArrowRenderer extends ArrowRenderer<CustomArrow> {

    private static final ResourceLocation ARROWS = new ResourceLocation("textures/entity/projectiles/arrow.png");

    private final ThrownItemRenderer<CustomArrow> snow;

    CustomArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
        snow = new ThrownItemRenderer<>(context);
    }

    @Override
    public ResourceLocation getTextureLocation(CustomArrow entity) {
        return (entity.getEntityData().get(CustomArrow.trackedType) == MoreBows.ARROW_TYPE_FROST) && !MoreBows.configGeneralInst.oldFrostArrowRendering ? TextureAtlas.LOCATION_BLOCKS : ARROWS;
    }

    @Override
    public void render(CustomArrow entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        if ((entity.getEntityData().get(CustomArrow.trackedType) == MoreBows.ARROW_TYPE_FROST) && !MoreBows.configGeneralInst.oldFrostArrowRendering) {
            snow.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        } else {
            // TODO Implement old cube rendering
            super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        }
    }

}
