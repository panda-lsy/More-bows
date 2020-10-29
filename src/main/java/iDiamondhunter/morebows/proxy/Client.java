package iDiamondhunter.morebows.proxy;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import iDiamondhunter.morebows.bows.CustomBow;
import iDiamondhunter.morebows.client.NoRender;
import iDiamondhunter.morebows.entities.ArrowSpawner;
import iDiamondhunter.morebows.entities.FrostArrow;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Items;
import net.minecraftforge.client.event.FOVUpdateEvent;

/** TODO Remove if not needed */
public class Client extends Common {

    @SubscribeEvent
    public void FOVUpdate(FOVUpdateEvent event) {
        if ((event.entity.getItemInUse() != null) && (event.entity.getItemInUse().getItem() instanceof CustomBow)) {
            // See net.minecraft.client.entity.EntityPlayerSP.getFOVMultiplier()
            final CustomBow bow = (CustomBow) event.entity.getItemInUse().getItem();
            float f1 = (float) event.entity.getItemInUseDuration() / (float) (bow.iconTimes[0] * (10 / 9));

            if (f1 > 1.0F) {
                f1 = 1.0F;
            } else {
                f1 *= f1;
            }

            event.newfov *= 1.0F - (f1 * 0.15F);
        }
    }

    @Override
    public void register() {
        RenderingRegistry.registerEntityRenderingHandler(ArrowSpawner.class, new NoRender());
        RenderingRegistry.registerEntityRenderingHandler(FrostArrow.class, new RenderSnowball(Items.snowball));
    }

}
