package iDiamondhunter.morebowsmod;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
//import cpw.mods.fml.common.SidedProxy;
//import iDiamondhunter.morebowsmod.proxy.*;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent.MissingMapping;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import iDiamondhunter.morebowsmod.bows.DiamondBow;
import iDiamondhunter.morebowsmod.bows.EnderBow;
import iDiamondhunter.morebowsmod.bows.FlameBow;
import iDiamondhunter.morebowsmod.bows.FrostBow;
import iDiamondhunter.morebowsmod.bows.GoldBow;
import iDiamondhunter.morebowsmod.bows.IronBow;
import iDiamondhunter.morebowsmod.bows.MultiBow;
import iDiamondhunter.morebowsmod.bows.StoneBow;
import iDiamondhunter.morebowsmod.entities.EnderArrow;
import iDiamondhunter.morebowsmod.entities.FireArrow;
import iDiamondhunter.morebowsmod.entities.FrostArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Mod(modid = MoreBows.MOD_ID, useMetadata = true)
public class MoreBows {
    public static final String MOD_ID = "MoreBows";

    @Instance(MOD_ID)
    public static MoreBows instance;

    public static Logger modLog;

    /* TODO: Make proxies, re-evaluate how & where stuff should be declared & initialized. */

    public final static String DiamondBowName = "DiamondBow";
    public final static String GoldBowName = "GoldBow";
    public final static String EnderBowName = "EnderBow";
    public final static String StoneBowName = "StoneBow";
    public final static String IronBowName = "IronBow";
    public final static String MultiBowName = "MultiBow";
    public final static String FlameBowName = "FlameBow";
    public final static String FrostBowName = "FrostBow";

    /* This is super janky, rethink. */
    private final static String modSeperator = "morebows:";

    public final static Item DiamondBow = new DiamondBow().setUnlocalizedName(DiamondBowName).setTextureName(modSeperator + DiamondBowName);
    public final static Item GoldBow = new GoldBow().setUnlocalizedName(GoldBowName).setTextureName(modSeperator + GoldBowName);
    public final static Item EnderBow = new EnderBow().setUnlocalizedName(EnderBowName).setTextureName(modSeperator + EnderBowName);
    public final static Item StoneBow = new StoneBow().setUnlocalizedName(StoneBowName).setTextureName(modSeperator + StoneBowName);
    public final static Item IronBow = new IronBow().setUnlocalizedName(IronBowName).setTextureName(modSeperator + IronBowName);
    public final static Item MultiBow = new MultiBow().setUnlocalizedName(MultiBowName).setTextureName(modSeperator + MultiBowName);
    public final static Item FlameBow = new FlameBow().setUnlocalizedName(FlameBowName).setTextureName(modSeperator + FlameBowName);
    public final static Item FrostBow = new FrostBow().setUnlocalizedName(FrostBowName).setTextureName(modSeperator + FrostBowName);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        modLog = event.getModLog();
        modLog.info("Too many bows, or not enough?");
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        registerItems();
    }

    @EventHandler
    public void fmlMissingMappingsEvent(FMLMissingMappingsEvent event) {
        for (final MissingMapping mapping : event.getAll()) {
            // Get the old name & mod ID of the item
            final String oldID = mapping.name.substring(0, mapping.name.indexOf(':'));
            final String oldName = mapping.name.substring(mapping.name.indexOf(':') + 1);

            // Attempt to migrate items from old IDs
            if (oldID.equals("iDiamondhunterMoreBows") /* Earlier builds of this 1.7.10 port. */ || oldID.equals("More Bows mod by iDiamondhunter") /* ID of iDiamondhunter's ports. */ || oldID.equals("${archivesBaseName}") /* Mistakes happen sometimes! */) {
                if (mapping.type == GameRegistry.Type.ITEM) {
                    final String remappedName = MOD_ID + ":" + oldName;
                    final Item remappedItem = GameData.getItemRegistry().getObject(remappedName);

                    if (remappedItem != null) {
                        modLog.info("ID remap: " + mapping.name + " > " + remappedName);
                        mapping.remap(remappedItem);
                    } else {
                        modLog.error("ID remap failed: no match for " + mapping.name);
                    }
                }
            }
        }
    }

    private void registerItems() {
        /* TODO check if this is the right way of doing things. */
        GameRegistry.registerItem(DiamondBow, DiamondBowName);
        GameRegistry.registerItem(GoldBow, GoldBowName);
        GameRegistry.registerItem(EnderBow, EnderBowName);
        GameRegistry.registerItem(StoneBow, StoneBowName);
        GameRegistry.registerItem(IronBow, IronBowName);
        GameRegistry.registerItem(MultiBow, MultiBowName);
        GameRegistry.registerItem(FlameBow, FlameBowName);
        GameRegistry.registerItem(FrostBow, FrostBowName);
        GameRegistry.addRecipe(new ItemStack(StoneBow, 1), " Ss", "TBs", " Ss", 'T', Items.stick, 's', Items.string, 'S',
                               Blocks.stone, 'B', Items.bow);
        GameRegistry.addRecipe(new ItemStack(StoneBow, 1), "sS ", "sBT", "sS ", 'T', Items.stick, 's', Items.string, 'S',
                               Blocks.stone, 'B', Items.bow);
        GameRegistry.addRecipe(new ItemStack(IronBow, 1), " Is", "IBs", " Is", 's', Items.string, 'I', Items.iron_ingot, 'B',
                               Items.bow);
        GameRegistry.addRecipe(new ItemStack(IronBow, 1), "sI ", "sBI", "sI ", 's', Items.string, 'I', Items.iron_ingot, 'B',
                               Items.bow);
        GameRegistry.addRecipe(new ItemStack(GoldBow, 1), " Gs", "GBs", " Gs", 's', Items.string, 'G', Items.gold_ingot, 'B',
                               Items.bow);
        GameRegistry.addRecipe(new ItemStack(GoldBow, 1), "sG ", "sBG", "sG ", 's', Items.string, 'G', Items.gold_ingot, 'B',
                               Items.bow);
        GameRegistry.addRecipe(new ItemStack(DiamondBow, 1), " Ds", "IBs", " Ds", 's', Items.string, 'D', Items.diamond, 'I',
                               Items.iron_ingot, 'B', Items.bow);
        GameRegistry.addRecipe(new ItemStack(DiamondBow, 1), "sD ", "sBI", "sD ", 's', Items.string, 'D', Items.diamond, 'I',
                               Items.iron_ingot, 'B', Items.bow);
        GameRegistry.addRecipe(new ItemStack(MultiBow, 1), " Bs", "I s", " Bs", 's', Items.string, 'I', Items.iron_ingot, 'B', IronBow);
        GameRegistry.addRecipe(new ItemStack(MultiBow, 1), "sB ", "s I", "sB ", 's', Items.string, 'I', Items.iron_ingot, 'B', IronBow);
        GameRegistry.addRecipe(new ItemStack(FlameBow, 1), "NB ", "GI ", "NB ", 'G', Items.gold_ingot, 'B', Items.blaze_rod, 'I', IronBow, 'N',
                               Blocks.netherrack);
        GameRegistry.addRecipe(new ItemStack(FlameBow, 1), " NB", " GI", " NB", 'G', Items.gold_ingot, 'B', Items.blaze_rod, 'I', IronBow, 'N',
                               Blocks.netherrack);
        GameRegistry.addRecipe(new ItemStack(EnderBow, 1), "GP ", "EI ", "GP ", 'G', Items.gold_ingot, 'P', Items.ender_pearl, 'I', IronBow, 'E',
                               Items.ender_eye);
        GameRegistry.addRecipe(new ItemStack(EnderBow, 1), " GP", " EI", " GP", 'G', Items.gold_ingot, 'P', Items.ender_pearl, 'I', IronBow, 'E',
                               Items.ender_eye);
        GameRegistry.addRecipe(new ItemStack(FrostBow, 1), " IR", "SER", " IR", 'R', Items.string, 'I', Blocks.ice, 'S',
                               Items.snowball, 'E', IronBow);
        /* I'm not sure how this works. */
        EntityRegistry.registerModEntity(FireArrow.class, "MoreBowsFireArrow", 1, this, 64, 20, true);
        EntityRegistry.registerModEntity(FrostArrow.class, "MoreBowsFrostArrow", 2, this, 64, 20, true);
        EntityRegistry.registerModEntity(EnderArrow.class, "MoreBowsEnderArrow", 3, this, 64, 20, true);
    }
}
