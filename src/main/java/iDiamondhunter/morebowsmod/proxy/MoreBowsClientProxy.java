package iDiamondhunter.morebowsmod.proxy;

import cpw.mods.fml.client.registry.RenderingRegistry;
import iDiamondhunter.morebowsmod.client.NoRender;
import iDiamondhunter.morebowsmod.entities.ArrowSpawner;

/** TODO Remove if not needed */
@Deprecated
public class MoreBowsClientProxy extends MoreBowsProxy {

    @Override
    public void register() {
        RenderingRegistry.registerEntityRenderingHandler(ArrowSpawner.class, new NoRender());
    }

}
