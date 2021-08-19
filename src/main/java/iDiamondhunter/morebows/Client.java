package iDiamondhunter.morebows;

import static net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL;

import java.util.Set;

//import iDiamondhunter.morebows.entities.CustomArrow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

/**
 * Handles almost all general client side only code. Client is also the client proxy.
 * - Client event handling
 * - Client rendering registration
 * - Even implements IModGuiFactory
 */
public final class Client extends MoreBows implements IModGuiFactory {

    /**
     * A class that extends GuiConfig, because the Forge team likes to complicate things.
     * Why this wasn't an anonymous inner class is beyond me.
     * TODO see if this can be removed.
     */
    public static final class Config extends GuiConfig {
        /**
         * Returns a new Config with all child elements of whatever's in the default config category.
         * This might break in the future, at which point something like this should be implemented:
         *
         * <pre>
         * {@code
         * private static String[] wantedProperties = new String[] { "oldFrostArrowRendering" };
         *
         * private static List<IConfigElement> getConfigElements() {
         *     final List<IConfigElement> list = new ArrayList<IConfigElement>();
         *     for (final String propName : wantedProperties) {
         *         list.add(new ConfigElement<Boolean>(MoreBows.config.get(Configuration.CATEGORY_GENERAL, propName, false)));
         *     }
         *     return list;
         * }
         * }
         * </pre>
         *
         * @param g the previous screen.
         */
        public Config(GuiScreen g) {
            super(g, new ConfigElement(MoreBows.config.getCategory(CATEGORY_GENERAL)).getChildElements(), MOD_ID, false, false, /* GuiConfig.getAbridgedConfigPath(MoreBows.config.toString()) */ MOD_NAME);
        }
    }

    /**
     * Hack used by ModRenderer. This value is set to the partialTicks of a RenderHandEvent.
     * This value is needed by ModRenderer to render the bow!
     */
    public static float partialTicks = 0;

    /**
     * Poses the arms of a player when drawing back a CustomBow, displaying the "bow aiming" animation.
     *
     * @param event the event
     */
    // TODO unimplemented
    /*@SubscribeEvent
    public void bowPose(Pre event) {
        if ((event.entityPlayer.getItemInUse() != null) && (event.entityPlayer.getItemInUse().getItem() instanceof CustomBow)) {
            event.renderer.modelArmorChestplate.aimedBow = event.renderer.modelArmor.aimedBow = event.renderer.modelBipedMain.aimedBow = true;
        }
    }*/

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
    // TODO unimplemented
    /*@SubscribeEvent
    public void FOV(FOVUpdateEvent event) {
        if ((event.entity.getItemInUse() != null) && (event.entity.getItemInUse().getItem() instanceof CustomBow)) {
            /**
             * In net.minecraft.client.entity.EntityPlayerSP.getFOVMultiplier(), this operation is event.entity.getItemInUseDuration() / 20.0F.
             * In an attempt to roughly maintain the same ratio of this divisor (20) to the amount of ticks it takes for the vanilla ItemBow icons to finish changing (18),
             * the amount of ticks it takes for the CustomBow's icons to finish changing is multiplied by 1.1 (20 / 18) and is used instead.
             *
            float f = (bowMaxUseDuration - event.entity.getItemInUseCount()) / (((CustomBow) event.entity.getItemInUse().getItem()).iconTimes[0] * 1.1F);

            if (f > 1.0F) {
                f = 1.0F;
            } else {
                f *= f;
            }

            event.newfov *= 1.0F - (f * 0.15F);
        }
    }*/

    @Override
    public void initialize(Minecraft a) {
        /* This space left intentionally blank */
    }

    /** If you know why mainConfigGuiClass wasn't designed to use an anonymous inner class, please let me know :( */
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return Config.class;
    }

    // TODO unimplemented
    /*@Override
    protected void register() {
        super.register();
        /** Registration of custom renderers
        RenderingRegistry.registerEntityRenderingHandler(CustomArrow.class, new ModRenderer());
        MinecraftForgeClient.registerItemRenderer(MoreBows.DiamondBow, new ModRenderer());
        MinecraftForgeClient.registerItemRenderer(MoreBows.GoldBow, new ModRenderer());
        MinecraftForgeClient.registerItemRenderer(MoreBows.EnderBow, new ModRenderer());
        MinecraftForgeClient.registerItemRenderer(MoreBows.StoneBow, new ModRenderer());
        MinecraftForgeClient.registerItemRenderer(MoreBows.IronBow, new ModRenderer());
        MinecraftForgeClient.registerItemRenderer(MoreBows.MultiBow, new ModRenderer());
        MinecraftForgeClient.registerItemRenderer(MoreBows.FlameBow, new ModRenderer());
        MinecraftForgeClient.registerItemRenderer(MoreBows.FrostBow, new ModRenderer());
    }*/

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    @Override
    public boolean hasConfigGui() {
        return true;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parent) {
        return new Config(parent);
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
