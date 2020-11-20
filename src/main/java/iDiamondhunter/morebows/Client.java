package iDiamondhunter.morebows;

import java.util.Set;

import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import iDiamondhunter.morebows.entities.ArrowSpawner;
import iDiamondhunter.morebows.entities.CustomArrow;
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
     * Hack used by ModRenderer. This value is set to the partialTicks of a RenderHandEvent.
     * This value is needed by ModRenderer to render the bow!
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
     * In ModRenderer, partialTicks is needed, but it is never passed to it.
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

            if (f > 1.0F) {
                f = 1.0F;
            } else {
                f *= f;
            }

            event.newfov *= 1.0F - (f * 0.15F);
        }
    }

    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement a) {
        return null;
    }

    public void initialize(Minecraft a) {
        // This space left intentionally blank
    }

    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return Config.class;
    }

    /** TODO Document */
    @Override
    protected void register() {
        super.register();
        /** Registration of custom renderers */
        RenderingRegistry.registerEntityRenderingHandler(ArrowSpawner.class, new ModRenderer());
        RenderingRegistry.registerEntityRenderingHandler(CustomArrow.class, new ModRenderer());
        MinecraftForgeClient.registerItemRenderer(MoreBows.DiamondBow, new ModRenderer());
        MinecraftForgeClient.registerItemRenderer(MoreBows.GoldBow, new ModRenderer());
        MinecraftForgeClient.registerItemRenderer(MoreBows.EnderBow, new ModRenderer());
        MinecraftForgeClient.registerItemRenderer(MoreBows.StoneBow, new ModRenderer());
        MinecraftForgeClient.registerItemRenderer(MoreBows.IronBow, new ModRenderer());
        MinecraftForgeClient.registerItemRenderer(MoreBows.MultiBow, new ModRenderer());
        MinecraftForgeClient.registerItemRenderer(MoreBows.FlameBow, new ModRenderer());
        MinecraftForgeClient.registerItemRenderer(MoreBows.FrostBow, new ModRenderer());
    }

    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    /**
     * TODO Possibly implement something like this but more compatible.
     *
     * <pre>
     * {@code
     * {@literal @}SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
     * public void guiChange(GuiOpenEvent event) {
     *     if (event.gui instanceof GuiIngameModOptions) {
     *         event.gui = new Config(null);
     *     }
     * }
     * }
     * </pre>
     */

}
