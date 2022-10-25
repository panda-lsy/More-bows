package iDiamondhunter.morebows.config;

import iDiamondhunter.morebows.MoreBows;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.LangKey;

/** General config settings. */
@LangKey(MoreBows.MOD_ID + "." + "confCatGen")
@Config(modid = MoreBows.MOD_ID)
public final class ConfigGeneral {

    private ConfigGeneral() {
        // Empty private constructor to hide default constructor
    }

    private static final String confMultiShotAmmo = "confMultiShotAmmo";

    /**
     * MoreBows config setting:
     * If true, frost arrows extinguish fire from Entities that are on fire.
     * If false, frost arrows can be on fire.
     */
    @LangKey(MoreBows.MOD_ID + "." + "confGenFrostCold")
    public static boolean frostArrowsShouldBeCold = true;

    /**
     * MoreBows config setting:
     * If true, frost arrows slow Entities down by
     * pretending to have set them in a web for one tick.
     * If false, frost arrows apply the slowness potion effect on hit.
     * TODO This never made much sense.
     */
    @LangKey(MoreBows.MOD_ID + "." + "confGenOldSlowdown")
    public static boolean oldFrostArrowMobSlowdown = false;

    /**
     * MoreBows config setting:
     * If true, render frost arrows as "snow cubes".
     * If false, render as snowballs.
     */
    @LangKey(MoreBows.MOD_ID + "." + "confGenOldRendering")
    public static boolean oldFrostArrowRendering = false;

    /**
     * MoreBows config setting:
     * Changes how multi-shot bows handle shooting additional arrows.
     * See each enum constant for more information.
     */
    @LangKey(MoreBows.MOD_ID + "." + confMultiShotAmmo)
    public static CustomArrowMultiShotType customArrowMultiShot = CustomArrowMultiShotType.AlwaysCustomArrows;

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
        private final String langKey;

        /**
         * Create a new multi-shot config setting
         * with the given language key.
         *
         * @param langKey the language key
         */
        CustomArrowMultiShotType(String langKey) {
            this.langKey = langKey;
        }

        /**
         * When Enums are used in Forge config annotations,
         * the value of toString() is used as a language key.
         *
         * @return the language key
         */
        @Override
        public String toString() {
            return langKey;
        }
    }
}
