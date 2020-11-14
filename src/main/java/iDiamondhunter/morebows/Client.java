package iDiamondhunter.morebows;

import java.util.Set;

import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import iDiamondhunter.morebows.bows.CustomBow;
import iDiamondhunter.morebows.entities.ArrowSpawner;
import iDiamondhunter.morebows.entities.CustomArrow;
import iDiamondhunter.morebows.render.RenderBow;
import iDiamondhunter.morebows.render.RenderModEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent.Pre;

/**
 * Handles almost all general client side only code. Client is also the client proxy.
 * - Client event handling
 * - Client rendering registration
 * - Even implements IModGuiFactory
 */
public final class Client extends MoreBows implements IModGuiFactory {

    /**
     * Hack used by RenderBow. This value is set to the partialTicks of a RenderHandEvent.
     * This value is needed by RenderBow to render the bow!
     */
    public static float partialTicks = 0;

    /**
     * Poses the arms of a player to display the "bow aiming" action on drawing back a bow TODO finish documenting
     *
     * @param event the event
     */
    @SubscribeEvent
    public void bowPose(Pre event) {
        if ((event.entityPlayer.getItemInUse() != null) && (event.entityPlayer.getItemInUse().getItem() instanceof CustomBow)) {
            event.renderer.modelArmorChestplate.aimedBow = event.renderer.modelArmor.aimedBow = event.renderer.modelBipedMain.aimedBow = true;
        }
    }

    /**
     * Hack to store the amount of partial ticks to use in bow rendering.
     * In RenderBow, partialTicks is needed, but it is never passed to it.
     * partialTicks is roughly equivalent to (Minecraft.getMinecraft().entityRenderer.renderEndNanoTime + (long)(1000000000 / Minecraft.getMinecraft().gameSettings.limitFramerate))),
     * however renderEndNanoTime is a private field.
     * However, this paticular value is passed through a whole bunch of places.
     * RenderHandEvent happens to be the closest to rendering items, as it's posted just before any item rendering is done.
     * TODO try to replace this, better documentation
     *
     * @param event the event
     */
    @SubscribeEvent
    public void bowTicks(RenderHandEvent event) {
        partialTicks = event.partialTicks;
    }

    /**
     * Handles the FOV "zoom in" when drawing a custom bow.
     * Minecraft is hardcoded to only do this for items which are equal to the bow item, so we have to do it manually.
     *
     * @param event the event
     */
    @SubscribeEvent
    public void FOV(FOVUpdateEvent event) {
        if ((event.entity.getItemInUse() != null) && (event.entity.getItemInUse().getItem() instanceof CustomBow)) {
            /** See net.minecraft.client.entity.EntityPlayerSP.getFOVMultiplier() */
            float f = (float) event.entity.getItemInUseDuration() / (float) ((((CustomBow) event.entity.getItemInUse().getItem()).iconTimes[0] * 10) / 9);

            // float f1 = (float) event.entity.getItemInUseDuration() / bow.powerDiv;
            if (f > 1.0F) {
                f = 1.0F;
            } else {
                f *= f;
            }

            event.newfov *= 1.0F - (f * 0.15F);
        }
    }

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement a) {
        return null;
    }

    @Override
    public void initialize(Minecraft a) {
        // This space left intentionally blank
    }

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return Config.class;
    }

    /** TODO Document */
    @Override
    protected void register() {
        super.register();
        /** Registration of custom renderers */
        RenderingRegistry.registerEntityRenderingHandler(ArrowSpawner.class, new RenderModEntity());
        RenderingRegistry.registerEntityRenderingHandler(CustomArrow.class, new RenderModEntity());
        MinecraftForgeClient.registerItemRenderer(MoreBows.DiamondBow, new RenderBow());
        MinecraftForgeClient.registerItemRenderer(MoreBows.GoldBow, new RenderBow());
        MinecraftForgeClient.registerItemRenderer(MoreBows.EnderBow, new RenderBow());
        MinecraftForgeClient.registerItemRenderer(MoreBows.StoneBow, new RenderBow());
        MinecraftForgeClient.registerItemRenderer(MoreBows.IronBow, new RenderBow());
        MinecraftForgeClient.registerItemRenderer(MoreBows.MultiBow, new RenderBow());
        MinecraftForgeClient.registerItemRenderer(MoreBows.FlameBow, new RenderBow());
        MinecraftForgeClient.registerItemRenderer(MoreBows.FrostBow, new RenderBow());
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

}
