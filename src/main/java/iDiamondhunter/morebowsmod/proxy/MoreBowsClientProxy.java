package iDiamondhunter.morebowsmod.proxy;

import cpw.mods.fml.client.registry.RenderingRegistry;
import iDiamondhunter.morebowsmod.client.NoRender;
import iDiamondhunter.morebowsmod.entities.ArrowSpawner;

public class MoreBowsClientProxy extends MoreBowsProxy {

    @Override
    public void Proxy() {
        RenderingRegistry.registerEntityRenderingHandler(ArrowSpawner.class, new NoRender());
    }

}
