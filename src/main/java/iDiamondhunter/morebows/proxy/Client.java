package iDiamondhunter.morebows.proxy;

import cpw.mods.fml.client.registry.RenderingRegistry;
import iDiamondhunter.morebows.client.NoRender;
import iDiamondhunter.morebows.entities.ArrowSpawner;
import iDiamondhunter.morebows.entities.FrostArrow;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Items;

/** TODO Remove if not needed */
public class Client extends Common {

    @Override
    public void register() {
        RenderingRegistry.registerEntityRenderingHandler(ArrowSpawner.class, new NoRender());
        RenderingRegistry.registerEntityRenderingHandler(FrostArrow.class, new RenderSnowball(Items.snowball));
    }

}
