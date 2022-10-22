package iDiamondhunter.morebows.config;

import iDiamondhunter.morebows.MoreBows;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.RequiresMcRestart;

@LangKey(MoreBows.MOD_ID + "." + "confCatBow")
@Config(modid = MoreBows.MOD_ID, category = "bows", name = MoreBows.MOD_ID + "_bowstats")
public class ConfigBows {

    /* Default values for bow construction */
    /** Default values for bow construction: the default damage multiplier. */
    private static final double noDamageMult = 1.0D;
    /** Default values for bow construction: the default power divisor */
    private static final float defaultPowerDiv = 20.0F;

    @LangKey("item." + MoreBows.DiamondBowTransKey + ".name")
    @RequiresMcRestart
    public static BowConfig DiamondBow = new BowConfig(1016, 2.25D, 6.0F);

    @LangKey("item." + MoreBows.EnderBowTransKey + ".name")
    @RequiresMcRestart
    public static BowConfig EnderBow = new BowConfig(215, noDamageMult, 22.0F);

    @LangKey("item." + MoreBows.FlameBowTransKey + ".name")
    @RequiresMcRestart
    public static BowConfig FlameBow = new BowConfig(576, 2.0D, 15.0F);

    @LangKey("item." + MoreBows.FrostBowTransKey + ".name")
    @RequiresMcRestart
    public static BowConfig FrostBow = new BowConfig(550, noDamageMult, 26.0F);

    @LangKey("item." + MoreBows.GoldBowTransKey + ".name")
    @RequiresMcRestart
    public static BowConfig GoldBow = new BowConfig(68, 2.5D, 6.0F);

    @LangKey("item." + MoreBows.IronBowTransKey + ".name")
    @RequiresMcRestart
    public static BowConfig IronBow = new BowConfig(550, 1.5D, 17.0F);

    @LangKey("item." + MoreBows.MultiBowTransKey + ".name")
    @RequiresMcRestart
    public static BowConfig MultiBow = new BowConfig(550, noDamageMult, 13.0F);

    @LangKey("item." + MoreBows.StoneBowTransKey + ".name")
    @RequiresMcRestart
    public static BowConfig StoneBow = new BowConfig(484, 1.15D, defaultPowerDiv);

    public static class BowConfig {

        public BowConfig (int confBowDurability, double confBowDamageMult, float confBowDrawbackDiv) {
            this.confBowDurability = confBowDurability;
            this.confBowDamageMult = confBowDamageMult;
            this.confBowDrawbackDiv = confBowDrawbackDiv;
        }

        @LangKey(MoreBows.MOD_ID + "." + "confBowDurability")
        public int confBowDurability;
        @LangKey(MoreBows.MOD_ID + "." + "confBowDamageMult")
        public double confBowDamageMult;
        @LangKey(MoreBows.MOD_ID + "." + "confBowDrawbackDiv")
        public float confBowDrawbackDiv;
    }

}
