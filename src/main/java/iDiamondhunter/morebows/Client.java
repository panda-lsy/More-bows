package iDiamondhunter.morebows;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import iDiamondhunter.morebows.bows.CustomBow;
import iDiamondhunter.morebows.entities.ArrowSpawner;
import iDiamondhunter.morebows.entities.CustomArrow;
import iDiamondhunter.morebows.render.RenderBow;
import iDiamondhunter.morebows.render.RenderModEntity;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderHandEvent;

public class Client extends MoreBows {

    public static float partialTicks = 0;

    /** Handles the FOV "zoom in" when drawing a custom bow.
     *  Minecraft is hardcoded to only do this for items which are equal to the bow item, so we have to do it manually. */
    @SubscribeEvent
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
        RenderingRegistry.registerEntityRenderingHandler(ArrowSpawner.class, new RenderModEntity());
        RenderingRegistry.registerEntityRenderingHandler(CustomArrow.class, new RenderModEntity());
        // TODO Move this to somewhere else or rename method
        MinecraftForgeClient.registerItemRenderer(MoreBows.DiamondBow, new RenderBow());
        MinecraftForgeClient.registerItemRenderer(MoreBows.GoldBow, new RenderBow());
        MinecraftForgeClient.registerItemRenderer(MoreBows.EnderBow, new RenderBow());
        MinecraftForgeClient.registerItemRenderer(MoreBows.StoneBow, new RenderBow());
        MinecraftForgeClient.registerItemRenderer(MoreBows.IronBow, new RenderBow());
        MinecraftForgeClient.registerItemRenderer(MoreBows.MultiBow, new RenderBow());
        MinecraftForgeClient.registerItemRenderer(MoreBows.FlameBow, new RenderBow());
        MinecraftForgeClient.registerItemRenderer(MoreBows.FrostBow, new RenderBow());
    }

    /** Hack to store the amount of partialTicks to use in bow rendering.
     * In RenderBow, partialTicks ticks is needed, but it is never passed to it.
     * partialTicks is roughly equivalent to (Minecraft.getMinecraft().entityRenderer.renderEndNanoTime + (long)(1000000000 / Minecraft.getMinecraft().gameSettings.limitFramerate))),
     * however renderEndNanoTime is a private field.
     * However, this paticular value is passed through a whole bunch of places.
     * RenderHandEvent happens to be the closest to rendering items, as it's posted just before any item rendering is done.
     * TODO try to replace this, better documentation */
    @SubscribeEvent
    public void renderFirstPersonCustomBow(RenderHandEvent event) {
        partialTicks = event.partialTicks;
    }

    /* does the arms only needed if no EnumAction TODO finish documenting */
    @SubscribeEvent
    public void renderThirdPersonCustomBowArms(net.minecraftforge.client.event.RenderPlayerEvent.Pre event) {
        //final EntityPlayer player = event.entityPlayer;

        //partialTicks = event.partialRenderTick; //doesn't work :(
        if ((event.entityPlayer.getItemInUse() != null) && (event.entityPlayer.getItemInUse().getItem() instanceof CustomBow)) {
            event.renderer.modelArmorChestplate.aimedBow = event.renderer.modelArmor.aimedBow = event.renderer.modelBipedMain.aimedBow = true;
        }
    }

}
