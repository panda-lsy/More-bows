package iDiamondhunter.morebows.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jetbrains.annotations.NotNull;

import com.google.errorprone.annotations.CompileTimeConstant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import iDiamondhunter.morebows.MoreBows;
import net.fabricmc.loader.api.FabricLoader;

/** Bow stats config settings. */
public final class ConfigBows {

    private static final Gson OUTPUT_GSON = new GsonBuilder().setPrettyPrinting().create();

    /** Data class for bow stats. */
    public static class BowConfig {

        /** The configured bow durability. */
        public int confBowDurability;

        /** The configured bow damage multiplier. */
        public double confBowDamageMult;

        /** The configured bow drawback divisor. */
        public float confBowDrawbackDiv;

        /**
         * Creates configuration settings for a bow.
         *
         * @param confBowDurability  The default bow durability.
         * @param confBowDamageMult  The default bow damage multiplier.
         * @param confBowDrawbackDiv The default bow drawback divisor.
         */
        BowConfig(int confBowDurability, double confBowDamageMult, float confBowDrawbackDiv) {
            this.confBowDurability = confBowDurability;
            this.confBowDamageMult = confBowDamageMult;
            this.confBowDrawbackDiv = confBowDrawbackDiv;
        }

    }

    /* Default values for bow construction. */
    /** Default values for bow construction: the default damage multiplier. */
    @CompileTimeConstant
    private static final double noDamageMult = 1.0;

    /** Default values for bow construction: the default power divisor. */
    @CompileTimeConstant
    private static final float defaultPowerDiv = 20.0F;

    /* Bow stats. */
    /** Diamond bow stats. */
    public final BowConfig DiamondBow = new BowConfig(1016, 2.25, 6.0F);

    /** Ender bow stats. */
    public final BowConfig EnderBow = new BowConfig(215, noDamageMult, 22.0F);

    /** Flame bow stats. */
    public final BowConfig FlameBow = new BowConfig(576, 2.0, 15.0F);

    /** Frost bow stats. */
    public final BowConfig FrostBow = new BowConfig(550, noDamageMult, 26.0F);

    /** Gold bow stats. */
    public final BowConfig GoldBow = new BowConfig(68, 2.5, 6.0F);

    /** Iron bow stats. */
    public final BowConfig IronBow = new BowConfig(550, 1.5, 17.0F);

    /** Multi bow stats. */
    public final BowConfig MultiBow = new BowConfig(550, noDamageMult, 13.0F);

    /** Stone bow stats. */
    public final BowConfig StoneBow = new BowConfig(484, 1.15, defaultPowerDiv);

    private ConfigBows() {
        // Empty private constructor to hide default constructor
    }

    /**
     * TODO bad code
     *
     * @deprecated bad code
     * @return all bow configs
     */
    public BowConfig[] getAllBowConfigs() {
        return new BowConfig[] {
                   DiamondBow,
                   EnderBow,
                   FlameBow,
                   FrostBow,
                   GoldBow,
                   IronBow,
                   MultiBow,
                   StoneBow
               };
    }

    /**
     * TODO bad code
     *
     * @deprecated bad code
     * @return all bow names
     */
    public static String[] getBowNames() {
        return new String[] {
                   MoreBows.DiamondBowName,
                   MoreBows.EnderBowName,
                   MoreBows.FlameBowName,
                   MoreBows.FrostBowName,
                   MoreBows.GoldBowName,
                   MoreBows.IronBowName,
                   MoreBows.MultiBowName,
                   MoreBows.StoneBowName
               };
    }

    /**
     * Gets the default config settings.
     *
     * @return the default config settings
     */
    public static ConfigBows getDefaultConfig() {
        return new ConfigBows();
    }

    /**
     * Writes the config settings to the config folder.
     *
     * @param config the config to write
     */
    public static void writeConfig(ConfigBows config) {
        final Path configPath = FabricLoader.getInstance().getConfigDir().resolve(MoreBows.MOD_ID + "_bowstats.json");

        try {
            Files.writeString(configPath, OUTPUT_GSON.toJson(config));
        } catch (final IOException e) {
            MoreBows.modLog.error("Error while writing config to file", e);
        }
    }

    /**
     * Reads the config settings from the config folder.
     *
     * @return the read config settings
     */
    public static @NotNull ConfigBows readConfig() {
        final Path configPath = FabricLoader.getInstance().getConfigDir().resolve(MoreBows.MOD_ID + "_bowstats.json");
        @NotNull ConfigBows loadedConfig = new ConfigBows();

        if (configPath.toFile().exists()) {
            try {
                loadedConfig = OUTPUT_GSON.fromJson(Files.readString(configPath), ConfigBows.class);
            } catch (JsonSyntaxException | IOException e) {
                MoreBows.modLog.error("Error while reading config from file", e);
                loadedConfig = new ConfigBows();
            }
        }

        return loadedConfig;
    }

}
