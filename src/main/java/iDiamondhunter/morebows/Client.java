package iDiamondhunter.morebows;

import java.util.Set;

import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import iDiamondhunter.morebows.entities.ArrowSpawner;
import iDiamondhunter.morebows.entities.CustomArrow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.FOVUpdateEvent;
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
     * Hack to store the amount of partial rendering ticks at the start of each RenderTickEvent.
     * This is later used in ModRenderer when rendering a CustomBow.
     *
     * @param event the event
     */
    @SubscribeEvent
    public void bowTicks(RenderTickEvent event) {
        partialTicks = event.renderTickTime;
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
            /**
             * In net.minecraft.client.entity.EntityPlayerSP.getFOVMultiplier(), this operation is event.entity.getItemInUseDuration() / 20.0F.
             * In an attempt to roughly maintain the same ratio of this divisor (20) to the amount of ticks it takes for the vanilla ItemBow icons to finish changing (18),
             * the amount of ticks it takes for the CustomBow's icons to finish changing is multiplied by 1.1 (20 / 18) and is used instead.
             */
            float f = (72000 /** Maximum use duration for a bow */ - event.entity.getItemInUseCount()) / (((CustomBow) event.entity.getItemInUse().getItem()).iconTimes[0] * 1.1F);

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
        /* This space left intentionally blank */
    }

    /** If you know why mainConfigGuiClass wasn't designed to use an anonymous inner class, please let me know :( */
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
