package iDiamondhunter.morebows.compat;

import java.util.Arrays;

import iDiamondhunter.morebows.MoreBows;
import iDiamondhunter.morebows.config.ConfigBows;
import iDiamondhunter.morebows.config.ConfigBows.BowConfig;
import iDiamondhunter.morebows.config.ConfigGeneral;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.DoubleListEntry;
import me.shedaniel.clothconfig2.gui.entries.FloatListEntry;
import me.shedaniel.clothconfig2.gui.entries.IntegerListEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** TODO This is horrible. */
public final class ConfigScreen {

    public static Screen moreBowsConfigScreen (Screen parent) {
        final ConfigBuilder moreBowsConfigBuilder = ConfigBuilder.create().setParentScreen(parent).setTitle(Component.translatable("morebows.confTitle"));
        // General settings
        final ConfigCategory general = moreBowsConfigBuilder.getOrCreateCategory(Component.translatable("morebows.confCatGen"));
        // frostArrowsShouldBeCold
        final ConfigEntryBuilder frostArrowsShouldBeCold = moreBowsConfigBuilder.entryBuilder();
        general.addEntry(frostArrowsShouldBeCold.startBooleanToggle(Component.translatable("morebows.confGenFrostCold"), MoreBows.configGeneralInst.frostArrowsShouldBeCold)
                         .setDefaultValue(true)
                         .setTooltip(Component.translatable("morebows.confGenFrostCold.tooltip"))
                         .setSaveConsumer(newValue -> MoreBows.configGeneralInst.frostArrowsShouldBeCold = newValue)
                         .build());
        // oldFrostArrowMobSlowdown
        final ConfigEntryBuilder oldFrostArrowMobSlowdown = moreBowsConfigBuilder.entryBuilder();
        general.addEntry(oldFrostArrowMobSlowdown.startBooleanToggle(Component.translatable("morebows.confGenOldSlowdown"), MoreBows.configGeneralInst.oldFrostArrowMobSlowdown)
                         .setDefaultValue(false)
                         .setTooltip(Component.translatable("morebows.confGenOldSlowdown.tooltip"))
                         .setSaveConsumer(newValue -> MoreBows.configGeneralInst.oldFrostArrowMobSlowdown = newValue)
                         .build());
        // oldFrostArrowRendering
        final ConfigEntryBuilder oldFrostArrowRendering = moreBowsConfigBuilder.entryBuilder();
        general.addEntry(oldFrostArrowRendering.startBooleanToggle(Component.translatable("morebows.confGenOldRendering"), MoreBows.configGeneralInst.oldFrostArrowRendering)
                         .setDefaultValue(false)
                         .setTooltip(Component.translatable("morebows.confGenOldRendering.tooltip"))
                         .setSaveConsumer(newValue -> MoreBows.configGeneralInst.oldFrostArrowRendering = newValue)
                         .build());
        // customArrowMultiShot
        final ConfigEntryBuilder customArrowMultiShot = moreBowsConfigBuilder.entryBuilder();
        general.addEntry(customArrowMultiShot.startEnumSelector(Component.translatable("morebows.confMultiShotAmmo"), ConfigGeneral.CustomArrowMultiShotType.class, MoreBows.configGeneralInst.customArrowMultiShot)
                         .setDefaultValue(ConfigGeneral.CustomArrowMultiShotType.AlwaysCustomArrows)
                         .setTooltip(Component.translatable("morebows.confMultiShotAmmo.tooltip"))
                         .setSaveConsumer(newValue -> MoreBows.configGeneralInst.customArrowMultiShot = newValue)
                         .build());
        // End general settings
        // Bow stat settings
        final ConfigCategory bowStats = moreBowsConfigBuilder.getOrCreateCategory(Component.translatable("morebows.confCatBow"));
        final BowConfig[] allBows = MoreBows.configBowsInst.getAllBowConfigs();
        final BowConfig[] defaultBows = ConfigBows.getDefaultConfig().getAllBowConfigs();
        final String[] allBowNames = ConfigBows.getBowNames();
        final int length = allBows.length;

        for (int i = 0; i < length; i++) {
            final BowConfig bowConfig = allBows[i];
            final BowConfig defaultBowConfig = defaultBows[i];
            final String bowName = allBowNames[i];
            final String transKey = "item." + MoreBows.MOD_ID + "." + bowName;
            final ConfigEntryBuilder confBowDurability = moreBowsConfigBuilder.entryBuilder();
            final IntegerListEntry confBowDurabilityEntry = confBowDurability.startIntField(Component.translatable("morebows.confBowDurability"), bowConfig.confBowDurability)
                    .setDefaultValue(defaultBowConfig.confBowDurability)
                    .setTooltip(Component.translatable("morebows.confBowDurability.tooltip"))
                    .setSaveConsumer(newValue -> bowConfig.confBowDurability = newValue)
                    .requireRestart()
                    .build();
            final ConfigEntryBuilder confBowDamageMult = moreBowsConfigBuilder.entryBuilder();
            final DoubleListEntry confBowDamageMultEntry = confBowDamageMult.startDoubleField(Component.translatable("morebows.confBowDamageMult"), bowConfig.confBowDamageMult)
                    .setDefaultValue(defaultBowConfig.confBowDamageMult)
                    .setTooltip(Component.translatable("morebows.confBowDamageMult.tooltip"))
                    .setSaveConsumer(newValue -> bowConfig.confBowDamageMult = newValue)
                    .requireRestart()
                    .build();
            final ConfigEntryBuilder confBowDrawbackDiv = moreBowsConfigBuilder.entryBuilder();
            final FloatListEntry confBowDrawbackDivEntry = confBowDrawbackDiv.startFloatField(Component.translatable("morebows.confBowDrawbackDiv"), bowConfig.confBowDrawbackDiv)
                    .setDefaultValue(defaultBowConfig.confBowDrawbackDiv)
                    .setTooltip(Component.translatable("morebows.confBowDrawbackDiv.tooltip"))
                    .setSaveConsumer(newValue -> bowConfig.confBowDrawbackDiv = newValue)
                    .requireRestart()
                    .build();
            final ConfigEntryBuilder bowConfigBuilder = moreBowsConfigBuilder.entryBuilder();
            bowStats.addEntry(bowConfigBuilder.startSubCategory(Component.translatable(transKey), Arrays.asList(confBowDurabilityEntry, confBowDamageMultEntry, confBowDrawbackDivEntry))
                              .build());
        }

        // End bow stat settings
        moreBowsConfigBuilder.setSavingRunnable(() -> {
            ConfigBows.writeConfig(MoreBows.configBowsInst);
            ConfigGeneral.writeConfig(MoreBows.configGeneralInst);
        });
        return moreBowsConfigBuilder.build();
    }

    private ConfigScreen() {
        // Empty private constructor to hide default constructor
    }

}
