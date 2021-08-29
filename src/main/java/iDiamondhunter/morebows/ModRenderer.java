package iDiamondhunter.morebows;


import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_FROST;

import iDiamondhunter.morebows.entities.CustomArrow;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.util.ResourceLocation;

public final class ModRenderer extends RenderArrow<CustomArrow> {

    private final Render<Entity> cube;
    private final Render<Entity> snow;

    private static final ResourceLocation BLOCKS = TextureMap.LOCATION_BLOCKS_TEXTURE;
    private static final ResourceLocation ARROWS = new ResourceLocation("textures/entity/projectiles/arrow.png");

    protected ModRenderer(RenderManager renderManager) {
        super(renderManager);
        /** Not sure if this is a super cursed hack, of if it's actually the best way to do this... */
        cube = renderManager.getEntityClassRenderObject(Entity.class);
        snow = renderManager.getEntityClassRenderObject(EntitySnowball.class);
    }

    @Override
    public void doRender(CustomArrow e, double a, double b, double c, float d, float f) {
        if (e.type == ARROW_TYPE_FROST) {
            if (!MoreBows.oldFrostArrowRendering) {
                snow.doRender(e, a, b, c, d, f);
            } else {
                cube.doRender(e, a, b, c, d, f);
            }
        } else {
            super.doRender(e, a, b, c, d, f);
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(CustomArrow e) {
        if (e.type != ARROW_TYPE_FROST) {
            return ARROWS;
        }

        if (!MoreBows.oldFrostArrowRendering) {
            return BLOCKS;
        }

        return null;
    }

}
