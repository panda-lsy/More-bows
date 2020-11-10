package iDiamondhunter.morebows.render;

import iDiamondhunter.morebows.ArrowType;
import iDiamondhunter.morebows.entities.CustomArrow;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntitySnowball;

/** Handles rendering the entities added by this mod.
 *  If the entity is a CustomArrow, and the CustomArrow is of type FROST, it renders as a snowball.
 *  If it's not of type FROST, it renders as an arrow.
 *  If it's not a CustomArrow, it doesn't render anything! This is deliberately used to not render any ArrowSpawners.
 */
public final class RenderModEntity extends RenderEntity {

    // Not sure if this is a super cused hack, of if it's actually the best way to do this...
    private final static Render arrow = RenderManager.instance.getEntityClassRenderObject(EntityArrow.class);
    private final static Render snowball = RenderManager.instance.getEntityClassRenderObject(EntitySnowball.class);

    @Override
    public void doRender(Entity e, double a, double b, double c, float d, float f) {
        if (e.getClass() == CustomArrow.class) {
            final CustomArrow arr = (CustomArrow) e;

            if (arr.getType() == ArrowType.FROST) {
                snowball.doRender(e, a, b, c, d, f);
            } else {
                arrow.doRender(e, a, b, c, d, f);
            }
        } // else do nothing
    }

}