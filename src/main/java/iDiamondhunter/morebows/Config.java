package iDiamondhunter.morebows;

import static iDiamondhunter.morebows.MoreBows.MOD_ID;
import static net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL;

import cpw.mods.fml.client.config.GuiConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;

public final class Config extends GuiConfig {

    /*private static String[] wantedProperties = new String[] {
    		"oldArrRender"
    };

    private static List<IConfigElement> getConfigElements() {
        final List<IConfigElement> list = new ArrayList<IConfigElement>();
        for (final String propName : wantedProperties) {
        	list.add(new ConfigElement<Boolean>(MoreBows.config.get(Configuration.CATEGORY_GENERAL, propName, false)));
        }
        return list;
    }*/

    public Config(GuiScreen g) {
        super(g, new ConfigElement<ConfigCategory>(MoreBows.config.getCategory(CATEGORY_GENERAL)).getChildElements(), MOD_ID, false, false, /*GuiConfig.getAbridgedConfigPath(MoreBows.config.toString())*/ MOD_ID);
    }
}
