package iDiamondhunter.morebows.config;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import iDiamondhunter.morebows.MoreBows;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.RequiresMcRestart;

/** Bow stats config settings. */
@LangKey(MoreBows.MOD_ID + "." + "confCatBow")
@Config(modid = MoreBows.MOD_ID, category = "bows", name = MoreBows.MOD_ID + "_bowstats")
public final class ConfigBows {

    /** Data class for bow stats. Forge uses each field as config settings. */
    public static class BowConfig {

        /** The configured bow durability. */
        @LangKey(MoreBows.MOD_ID + "." + "confBowDurability")
        public int confBowDurability;

        /** The configured bow damage multiplier. */
        @LangKey(MoreBows.MOD_ID + "." + "confBowDamageMult")
        public double confBowDamageMult;

        /** The configured bow drawback divisor. */
        @LangKey(MoreBows.MOD_ID + "." + "confBowDrawbackDiv")
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
    private static final double noDamageMult = 1.0;

    /** Default values for bow construction: the default power divisor. */
    private static final float defaultPowerDiv = 20.0F;

    /* Bow stats. */
    /** Diamond bow stats. */
    @LangKey("item." + MoreBows.DiamondBowTransKey + ".name")
    @RequiresMcRestart
    @SuppressFBWarnings(value = "MS_SHOULD_BE_FINAL")
    public static BowConfig DiamondBow = new BowConfig(1016, 2.25, 6.0F);

    /** Ender bow stats. */
    @LangKey("item." + MoreBows.EnderBowTransKey + ".name")
    @RequiresMcRestart
    @SuppressFBWarnings(value = "MS_SHOULD_BE_FINAL")
    public static BowConfig EnderBow = new BowConfig(215, noDamageMult, 22.0F);

    /** Flame bow stats. */
    @LangKey("item." + MoreBows.FlameBowTransKey + ".name")
    @RequiresMcRestart
    @SuppressFBWarnings(value = "MS_SHOULD_BE_FINAL")
    public static BowConfig FlameBow = new BowConfig(576, 2.0, 15.0F);

    /** Frost bow stats. */
    @LangKey("item." + MoreBows.FrostBowTransKey + ".name")
    @RequiresMcRestart
    @SuppressFBWarnings(value = "MS_SHOULD_BE_FINAL")
    public static BowConfig FrostBow = new BowConfig(550, noDamageMult, 26.0F);

    /** Gold bow stats. */
    @LangKey("item." + MoreBows.GoldBowTransKey + ".name")
    @RequiresMcRestart
    @SuppressFBWarnings(value = "MS_SHOULD_BE_FINAL")
    public static BowConfig GoldBow = new BowConfig(68, 2.5, 6.0F);

    /** Iron bow stats. */
    @LangKey("item." + MoreBows.IronBowTransKey + ".name")
    @RequiresMcRestart
    @SuppressFBWarnings(value = "MS_SHOULD_BE_FINAL")
    public static BowConfig IronBow = new BowConfig(550, 1.5, 17.0F);

    /** Multi bow stats. */
    @LangKey("item." + MoreBows.MultiBowTransKey + ".name")
    @RequiresMcRestart
    @SuppressFBWarnings(value = "MS_SHOULD_BE_FINAL")
    public static BowConfig MultiBow = new BowConfig(550, noDamageMult, 13.0F);

    /** Stone bow stats. */
    @LangKey("item." + MoreBows.StoneBowTransKey + ".name")
    @RequiresMcRestart
    @SuppressFBWarnings(value = "MS_SHOULD_BE_FINAL")
    public static BowConfig StoneBow = new BowConfig(484, 1.15, defaultPowerDiv);

    private ConfigBows() {
        // Empty private constructor to hide default constructor
    }

}
