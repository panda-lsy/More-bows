package iDiamondhunter.morebows;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import iDiamondhunter.morebows.bows.CustomBow;
import iDiamondhunter.morebows.entities.ArrowSpawner;
import iDiamondhunter.morebows.entities.CustomArrow;
import iDiamondhunter.morebows.render.NoRender;
import iDiamondhunter.morebows.render.RenderCustomArrow;
import iDiamondhunter.morebows.render.RenderCustomBow;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderHandEvent;

public class Client extends MoreBows {

    public static float hackForTicks = 0;

    /** Handles the FOV "zoom in" when drawing a custom bow.
     *  Minecraft is hardcoded to only do this for items which are equal to the bow item, so we have to do it manually. */
    @SubscribeEvent
    @SideOnly(value = Side.CLIENT)
    public void fov(FOVUpdateEvent event) {
        if ((event.entity.getItemInUse() != null) && (event.entity.getItemInUse().getItem() instanceof CustomBow)) {
            /** See net.minecraft.client.entity.EntityPlayerSP.getFOVMultiplier() */
            final CustomBow bow = (CustomBow) event.entity.getItemInUse().getItem();
            float f1 = (float) event.entity.getItemInUseDuration() / (float) (bow.iconTimes[0] * (10 / 9));

            //float f1 = (float) event.entity.getItemInUseDuration() / bow.powerDiv;
            if (f1 > 1.0F) {
                f1 = 1.0F;
            } else {
                f1 *= f1;
            }

            event.newfov *= 1.0F - (f1 * 0.15F);
        }
    }

    /** TODO Document */
    @Override
    protected void registerEntities() {
        super.registerEntities();
        /** Registration of custom renderers */
        RenderingRegistry.registerEntityRenderingHandler(ArrowSpawner.class, new NoRender());
        RenderingRegistry.registerEntityRenderingHandler(CustomArrow.class, new RenderCustomArrow());
        // TODO Move this to somewhere else or rename method
        MinecraftForgeClient.registerItemRenderer(MoreBows.DiamondBow, new RenderCustomBow());
        MinecraftForgeClient.registerItemRenderer(MoreBows.GoldBow, new RenderCustomBow());
        MinecraftForgeClient.registerItemRenderer(MoreBows.EnderBow, new RenderCustomBow());
        MinecraftForgeClient.registerItemRenderer(MoreBows.StoneBow, new RenderCustomBow());
        MinecraftForgeClient.registerItemRenderer(MoreBows.IronBow, new RenderCustomBow());
        MinecraftForgeClient.registerItemRenderer(MoreBows.MultiBow, new RenderCustomBow());
        MinecraftForgeClient.registerItemRenderer(MoreBows.FlameBow, new RenderCustomBow());
        MinecraftForgeClient.registerItemRenderer(MoreBows.FrostBow, new RenderCustomBow());
        //MinecraftForgeClient.registerItemRenderer(Items.bow, new RenderCustomBow()); // DEBUG
    }

    /** Huge hack, stores the amount of partialTicks to use in bow rendering. TODO try to replace this, Document */
    @SubscribeEvent
    @SideOnly(value = Side.CLIENT)
    public void renderFirstPersonCustomBow(RenderHandEvent event) { // Alight, I give up. This is incredibly dumb
        hackForTicks = event.partialTicks;
    }

}
