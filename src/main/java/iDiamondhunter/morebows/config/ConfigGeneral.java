package iDiamondhunter.morebows.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jetbrains.annotations.NotNull;

import com.google.errorprone.annotations.CompileTimeConstant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import iDiamondhunter.morebows.MoreBows;
import net.fabricmc.loader.api.FabricLoader;

/** General config settings. */
public final class ConfigGeneral {

    /** Changes how multi-shot bows handle shooting additional arrows. */
    public enum CustomArrowMultiShotType {

        /**
         * MoreBows multi-shot config setting:
         * Use 1 arrow, shoot multiple custom arrows.
         */
        AlwaysCustomArrows(MoreBows.MOD_ID + "." + confMultiShotAmmo + ".AlwaysCustomArrows"),

        /**
         * MoreBows multi-shot config setting:
         * Use 1 arrow, shoot multiple standard arrows.
         */
        AlwaysStandardArrows(MoreBows.MOD_ID + "." + confMultiShotAmmo + ".AlwaysStandardArrows"),

        /**
         * MoreBows multi-shot config setting:
         * Use as many arrows as possible, shoot used arrows.
         */
        UseAmountShot(MoreBows.MOD_ID + "." + confMultiShotAmmo + ".UseAmountShot");

        /** The language key for this setting. */
        private final @NotNull String langKey;

        /**
         * Create a new multi-shot config setting
         * with the given language key.
         *
         * @param langKey the language key
         */
        CustomArrowMultiShotType(@NotNull String langKey) {
            this.langKey = langKey;
        }

        /**
         * When Enums are used in Forge config annotations,
         * the value of toString() is used as a language key.
         *
         * @return the language key
         */
        @Override
        public @NotNull String toString() {
            return langKey;
        }

    }

    private static final Gson OUTPUT_GSON = new GsonBuilder().setPrettyPrinting().create();

    @CompileTimeConstant
    private static final String confMultiShotAmmo = "confMultiShotAmmo";

    /**
     * MoreBows config setting:
     * If true, frost arrows extinguish fire from Entities that are on fire.
     * If false, frost arrows can be on fire.
     */
    @SuppressFBWarnings("MS_SHOULD_BE_FINAL")
    public boolean frostArrowsShouldBeCold = true;

    /**
     * MoreBows config setting:
     * If true, frost arrows slow Entities down by
     * pretending to have set them in a web for one tick.
     * If false, frost arrows apply the slowness potion effect on hit.
     * TODO This never made much sense.
     */
    @SuppressFBWarnings("MS_SHOULD_BE_FINAL")
    public boolean oldFrostArrowMobSlowdown = false;

    /**
     * MoreBows config setting:
     * If true, render frost arrows as "snow cubes".
     * If false, render as snowballs.
     */
    @SuppressFBWarnings("MS_SHOULD_BE_FINAL")
    public boolean oldFrostArrowRendering = false;

    /**
     * MoreBows config setting:
     * Changes how multi-shot bows handle shooting additional arrows.
     * See each enum constant for more information.
     */
    @SuppressFBWarnings(value = { "NP_NONNULL_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR", "MS_SHOULD_BE_FINAL" }, justification = "SpotBugs seems to be having trouble detecting the initialisation of the field here")
    public @NotNull CustomArrowMultiShotType customArrowMultiShot = CustomArrowMultiShotType.AlwaysCustomArrows;

    private ConfigGeneral() {
        // Empty private constructor to hide default constructor
    }

    public static void writeConfig(ConfigGeneral config) {
        final Path configPath = FabricLoader.getInstance().getConfigDir().resolve(MoreBows.MOD_ID + "_general.json");

        try {
            Files.writeString(configPath, OUTPUT_GSON.toJson(config));
        } catch (final IOException e) {
            MoreBows.modLog.error("Error while writing config to file", e);
        }
    }

    public static @NotNull ConfigGeneral readConfig() {
        final Path configPath = FabricLoader.getInstance().getConfigDir().resolve(MoreBows.MOD_ID + "_general.json");
        @NotNull ConfigGeneral loadedConfig = new ConfigGeneral();

        if (configPath.toFile().exists()) {
            try {
                loadedConfig = OUTPUT_GSON.fromJson(Files.readString(configPath), ConfigGeneral.class);
            } catch (JsonSyntaxException | IOException e) {
                MoreBows.modLog.error("Error while reading config from file", e);
                loadedConfig = new ConfigGeneral();
            }
        }

        return loadedConfig;
    }

}