package iDiamondhunter.morebows;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import iDiamondhunter.morebows.entities.CustomArrow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderPlayerEvent.Pre;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Property;

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
        public Config(GuiScreen g) {
            super(g, getConfigElements(), MOD_ID, false, false, I18n.format("morebows.confTitle"));
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        private static List<IConfigElement> getConfigElements() {
            final List<IConfigElement> list = new ArrayList<IConfigElement>();
            final List<IConfigElement<DummyCategoryElement>> bowConfs = new ArrayList<IConfigElement<DummyCategoryElement>>();
            final int length = BowNames.length;

            for (int i = 0; i < length; i++) {
                final List<IConfigElement> bowConf = new ArrayList<IConfigElement>();
                final String bowName = BowNames[i];
                final String transKey = "item." + bowName + ".name";
                final Property confBowDamageMultProp = getConfBowDamageMultProp(bowName, i);
                final Property confBowDurabilityProp = getConfBowDurabilityProp(bowName, i);
                final Property confBowDrawbackDivProp = getConfBowDrawbackDivProp(bowName, i);
                confBowDamageMultProp.setRequiresMcRestart(true);
                confBowDurabilityProp.setRequiresMcRestart(true);
                confBowDrawbackDivProp.setRequiresMcRestart(true);
                bowConf.add(new ConfigElement(confBowDamageMultProp));
                bowConf.add(new ConfigElement(confBowDurabilityProp));
                bowConf.add(new ConfigElement(confBowDrawbackDivProp));
                bowConfs.add(new DummyCategoryElement<DummyCategoryElement>(bowName, transKey, bowConf));
            }

            list.add(new DummyCategoryElement("confCatBowList", "confCatBow", bowConfs));
            final List<IConfigElement> generalSettings = new ArrayList<IConfigElement>();
            generalSettings.add(new ConfigElement<Boolean>(getFrostArrowsShouldBeColdProp()));
            generalSettings.add(new ConfigElement<Boolean>(getOldFrostArrowMobSlowdownProp()));
            generalSettings.add(new ConfigElement<Boolean>(getOldFrostArrowRenderingProp()));
            generalSettings.add(new ConfigElement<Boolean>(getUseAmmoForShotArrowsProp()));
            list.add(new DummyCategoryElement("confCatGenList", "confCatGen", generalSettings));
            return list;
        }
    }

    /**
     * Hack used by ModRenderer. This value is set to the partialTicks of a RenderHandEvent.
     * This value is needed by ModRenderer to render the bow!
     */
    public static float partialTicks;

    /**
     * Poses the arms of a player when drawing back a CustomBow, displaying the "bow aiming" animation.
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
            float f = (bowMaxUseDuration - event.entity.getItemInUseCount()) / ((CustomBow) event.entity.getItemInUse().getItem()).powerDiv;

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

    @Override
    protected void register() {
        super.register();
        /** Registration of custom renderers */
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

}
