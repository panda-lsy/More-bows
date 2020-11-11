package iDiamondhunter.morebows;

import static iDiamondhunter.morebows.MoreBows.MOD_ID;

import java.util.Set;

import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.client.config.GuiConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

public final class Config implements IModGuiFactory  {

    public static final class ConfigGUI extends GuiConfig {

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

        public ConfigGUI(GuiScreen g) {
            super(g, new ConfigElement<ConfigCategory>(MoreBows.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), MOD_ID, false, false, /*GuiConfig.getAbridgedConfigPath(MoreBows.config.toString())*/ MOD_ID);
        }
    }

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
        return null;
    }

    @Override
    public void initialize(Minecraft minecraftInstance) { }

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return ConfigGUI.class;
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

}
