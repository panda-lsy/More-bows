package iDiamondhunter.morebows;

import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_FROST;
import static iDiamondhunter.morebows.config.ConfigGeneral.oldFrostArrowRendering;

import org.jetbrains.annotations.Nullable;

import iDiamondhunter.morebows.entities.CustomArrow;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.util.ResourceLocation;

/** Handles rendering CustomArrows. */
final class ModRenderer extends RenderArrow<CustomArrow> {

    private static final ResourceLocation ARROWS = new ResourceLocation("textures/entity/projectiles/arrow.png");
    private static final ResourceLocation BLOCKS = TextureMap.LOCATION_BLOCKS_TEXTURE;

    private final Render<Entity> cube;
    private final Render<Entity> snow;

    ModRenderer(RenderManager renderManagerIn) {
        super(renderManagerIn);
        /*
         * Not sure if this is a super cursed hack,
         * or if it's actually the best way to do this...
         */
        cube = renderManagerIn.getEntityClassRenderObject(Entity.class);
        snow = renderManagerIn.getEntityClassRenderObject(EntitySnowball.class);
    }

    @Override
    public void doRender(CustomArrow entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (entity.type == ARROW_TYPE_FROST) {
            if (!oldFrostArrowRendering) {
                snow.doRender(entity, x, y, z, entityYaw, partialTicks);
            } else {
                cube.doRender(entity, x, y, z, entityYaw, partialTicks);
            }
        } else {
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        }
    }

    @Override
    @Nullable
    protected ResourceLocation getEntityTexture(CustomArrow entity) {
        return entity.type != ARROW_TYPE_FROST ? ARROWS : !oldFrostArrowRendering ? BLOCKS : null;
    }

}
