package iDiamondhunter.morebows.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import net.fabricmc.loader.api.FabricLoader;

/** Mod menu config screen compatibility class. */
public final class ModMenuCompat implements ModMenuApi {

    /**
     * Creates the config screen if Cloth config is loaded.
     *
     * @return the mod config screen if Cloth config is loaded
     */
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return FabricLoader.getInstance().isModLoaded("cloth-config2") ? ConfigScreen::moreBowsConfigScreen : ModMenuApi.super.getModConfigScreenFactory();
    }

}
