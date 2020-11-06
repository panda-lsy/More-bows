package iDiamondhunter.morebows.render;

import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.entity.Entity;

/** Handles not rendering the ArrowSpawner. TODO Remove if possible */
public class NoRender extends RenderEntity {

    @Override
    public void doRender(Entity n, double e, double r, double d, float y, float o) {
        //don'tRender
    }

}
