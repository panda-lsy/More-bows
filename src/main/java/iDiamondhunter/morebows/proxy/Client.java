package iDiamondhunter.morebows.proxy;

import cpw.mods.fml.client.registry.RenderingRegistry;
import iDiamondhunter.morebows.client.NoRender;
import iDiamondhunter.morebows.entities.ArrowSpawner;

/** TODO Remove if not needed */
@Deprecated
public class Client extends Common {

    @Override
    public void register() {
        RenderingRegistry.registerEntityRenderingHandler(ArrowSpawner.class, new NoRender());
    }

}
