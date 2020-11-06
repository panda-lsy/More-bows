package iDiamondhunter.morebows.render;

import iDiamondhunter.morebows.entities.CustomArrow;
import iDiamondhunter.morebows.entities.CustomArrow.ArrowType;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntitySnowball;

/** Handles rendering a CustomArrow. If the CustomArrow is of type FROST, it renders as a snowball. If it's not, it renders as an arrow. */
public class RenderCustomArrow extends RenderEntity {

    // Not sure if this is a super cused hack, of if it's actually the best way to do this...
    private final static Render arrow = RenderManager.instance.getEntityClassRenderObject(EntityArrow.class);
    private final static Render snowball = RenderManager.instance.getEntityClassRenderObject(EntitySnowball.class);

    @Override
    public void doRender(Entity entity, double a, double b, double c, float d, float e) {
        final CustomArrow arr = (CustomArrow) entity;

        if (arr.getType() == ArrowType.FROST) {
            snowball.doRender(entity, a, b, c, d, e);
        } else {
            arrow.doRender(entity, a, b, c, d, e);
        }
    }

}
