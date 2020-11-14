package iDiamondhunter.morebows;

import static iDiamondhunter.morebows.MoreBows.MOD_ID;
import static net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL;

import cpw.mods.fml.client.config.GuiConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;

/**
 * A class that extends GuiConfig, because the Forge team likes to complicate things.
 * TODO see if this can be removed.
 */
public final class Config extends GuiConfig {

    /**
     * Returns a new Config with all child elements of whatever's in the default config category.
     * This might break in the future, at which point something like this should be implemented:
     *
     * <pre>
     * private static String[] wantedProperties = new String[] { "oldArrRender" };
     *
     * private static List<IConfigElement> getConfigElements() {
     *     final List<IConfigElement> list = new ArrayList<IConfigElement>();
     *     for (final String propName : wantedProperties) {
     *         list.add(new ConfigElement<Boolean>(MoreBows.config.get(Configuration.CATEGORY_GENERAL, propName, false)));
     *     }
     *     return list;
     * }
     * </pre>
     *
     * @param g the previous screen.
     */
    public Config(GuiScreen g) {
        super(g, new ConfigElement<ConfigCategory>(MoreBows.config.getCategory(CATEGORY_GENERAL)).getChildElements(), MOD_ID, false, false, /* GuiConfig.getAbridgedConfigPath(MoreBows.config.toString()) */ MOD_ID);
    }

}
