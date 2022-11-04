package iDiamondhunter.morebows.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import net.fabricmc.loader.api.FabricLoader;

public class ModMenuCompat implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return FabricLoader.getInstance().isModLoaded("cloth-config") ? ConfigScreen::moreBowsConfigScreen : ModMenuApi.super.getModConfigScreenFactory();
    }

}
