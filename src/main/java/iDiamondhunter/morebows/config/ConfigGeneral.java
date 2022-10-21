package iDiamondhunter.morebows.config;

import iDiamondhunter.morebows.MoreBows;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.LangKey;

@LangKey("confGenCat")
@Config(modid = MoreBows.MOD_ID)
public class ConfigGeneral {
    /** MoreBows config setting: If true, frost arrows extinguish fire from Entities that are on fire. If false, frost arrows can be on fire. */
    @LangKey("frostArrowsShouldBeCold")
    public static boolean frostArrowsShouldBeCold = true;
    /** MoreBows config setting: If true, frost arrows slow Entities down by pretending to have set them in a web for one tick. If false, frost arrows apply the slowness potion effect on hit. */
    @LangKey("oldFrostArrowMobSlowdown")
    public static boolean oldFrostArrowMobSlowdown = false;
    /** MoreBows config setting: If true, render frost arrows as snow cubes. If false, render as snowballs. */
    @LangKey("oldFrostArrowRendering")
    public static boolean oldFrostArrowRendering = false;
}
