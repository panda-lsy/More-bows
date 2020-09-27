package iDiamondhunter.morebowsmod;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
//import cpw.mods.fml.common.SidedProxy;
//import iDiamondhunter.morebowsmod.proxy.*;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import iDiamondhunter.morebowsmod.bows.*;
import iDiamondhunter.morebowsmod.entities.*;

import org.apache.logging.log4j.Logger;

@Mod(modid = MoreBowsMod.MOD_ID, name = MoreBowsMod.MOD_NAME, version = MoreBowsMod.MOD_VERSION)
public class MoreBowsMod
{
	public static final String MOD_ID = "iDiamondhunterMoreBows";
	public static final String MOD_NAME	= "More Bows mod";
	public static final String MOD_VERSION	= "1.4.5";
	
	@Instance("iDiamondhunterMoreBows")
	public static MoreBowsMod instance;
	
    public static Logger modLog;
    
    /* TODO: Make proxies. */
    //at SidedProxy(clientSide = "iDiamondhunter.morebowsmod.client.ClientProxyiDiamondhunter", serverSide= "iDiamondhunter.morebowsmod.common.CommonProxyiDiamondhunter")
    //public static CommonProxyiDiamondhunter commonProxy;
    //public static ClientProxyiDiamondhunter clientProxy;
    
    /* TODO Re-evaluate where stuff should be declared and initialized. */
    public static Item DiamondBow;
    public final static String DiamondBowName = "DiamondBow";
    
	public static Item GoldBow;
	public final static String GoldBowName = "GoldBow";

	public static Item EnderBow;
	public final static String EnderBowName = "EnderBow";

	public static Item StoneBow;
	public final static String StoneBowName = "StoneBow";

	public static Item IronBow;
	public final static String IronBowName = "IronBow";

	public static Item MultiBow;
	public final static String MultiBowName = "MultiBow";

	public static Item FlameBow;
	public final static String FlameBowName = "FlameBow";

	public static Item FrostBow;
	public final static String FrostBowName = "FrostBow";
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		
		modLog = event.getModLog();
		modLog.info("Too many bows, or not enough?");
		initItems();
		
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		
		registerItems();
		
	}
	
	private void initItems() {
		
		DiamondBow = new ItemDiamondBow().setUnlocalizedName(DiamondBowName).setTextureName("morebowsmod:" + DiamondBowName);
		GoldBow = new ItemGoldBow().setUnlocalizedName(GoldBowName).setTextureName("morebowsmod:" + GoldBowName);
		EnderBow = new ItemBowEnder().setUnlocalizedName(EnderBowName).setTextureName("morebowsmod:" + EnderBowName);
		StoneBow = new ItemStoneBow().setUnlocalizedName(StoneBowName).setTextureName("morebowsmod:" + StoneBowName);
		IronBow = new ItemIronBow().setUnlocalizedName(IronBowName).setTextureName("morebowsmod:" + IronBowName);
		MultiBow = new ItemMultiBow().setUnlocalizedName(MultiBowName).setTextureName("morebowsmod:" + MultiBowName);
		FlameBow = new ItemFlameBow().setUnlocalizedName(FlameBowName).setTextureName("morebowsmod:" + FlameBowName);
		FrostBow = new ItemFrostBow().setUnlocalizedName(FrostBowName).setTextureName("morebowsmod:" + FrostBowName);
		
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
		
		GameRegistry.addRecipe(new ItemStack(StoneBow, 1), new Object[]
                {
                    " $*", "#(*", " $*", '#', Items.stick, '*', Items.string, '$', Blocks.stone, '(', Items.bow
                });
        GameRegistry.addRecipe(new ItemStack(StoneBow, 1), new Object[]
                {
                    "*$ ", "*(#", "*$ ", '#', Items.stick, '*', Items.string, '$', Blocks.stone, '(', Items.bow
                });
        GameRegistry.addRecipe(new ItemStack(IronBow, 1), new Object[]
                {
                    " $*", "$(*", " $*", '*', Items.string, '$', Items.iron_ingot, '(', Items.bow
                });
        GameRegistry.addRecipe(new ItemStack(IronBow, 1), new Object[]
                {
                    "*$ ", "*($", "*$ ", '*', Items.string, '$', Items.iron_ingot, '(', Items.bow
                });
        GameRegistry.addRecipe(new ItemStack(GoldBow, 1), new Object[]
                {
                    " $*", "$(*", " $*", '*', Items.string, '$', Items.gold_ingot, '(', Items.bow
                });
        GameRegistry.addRecipe(new ItemStack(GoldBow, 1), new Object[]
                {
                    "*$ ", "*($", "*$ ", '*', Items.string, '$', Items.gold_ingot, '(', Items.bow
                });
        GameRegistry.addRecipe(new ItemStack(DiamondBow, 1), new Object[]
                {
                    " $*", "I(*", " $*", '*', Items.string, '$', Items.diamond, 'I', Items.iron_ingot, '(', Items.bow
                });
        GameRegistry.addRecipe(new ItemStack(DiamondBow, 1), new Object[]
                {
                    "*$ ", "*(I", "*$ ", '*', Items.string, '$', Items.diamond, 'I', Items.iron_ingot, '(', Items.bow
                });
        GameRegistry.addRecipe(new ItemStack(MultiBow, 1), new Object[]
                {
                    " $*", "#(*", " $*", '*', Items.string, '#', Items.iron_ingot, '$', IronBow
                });
        GameRegistry.addRecipe(new ItemStack(MultiBow, 1), new Object[]
                {
                    "*$ ", "* #", "*$ ", '*', Items.string, '#', Items.iron_ingot, '$', IronBow
                });
        GameRegistry.addRecipe(new ItemStack(FlameBow, 1), new Object[]
                {"NB ", "GI ", "NB ",   'G', Items.gold_ingot, 'B', Items.blaze_rod,  'I', IronBow, 'N',Blocks.netherrack
        	    });
        GameRegistry.addRecipe(new ItemStack(FlameBow, 1), new Object[]
                {" NB", " GI", " NB",   'G', Items.gold_ingot, 'B', Items.blaze_rod, 'I', IronBow, 'N', Blocks.netherrack
        	    });
        GameRegistry.addRecipe(new ItemStack(EnderBow, 1), new Object[]
                {
                    "GP ", "EI ", "GP ",   'G', Items.gold_ingot, 'P', Items.ender_pearl,  'I', IronBow, 'E',Items.ender_eye
                });
        GameRegistry.addRecipe(new ItemStack(EnderBow, 1), new Object[]
                {
                    " GP", " EI", " GP",  'G', Items.gold_ingot, 'P', Items.ender_pearl,  'I', IronBow, 'E', Items.ender_eye
                });
        GameRegistry.addRecipe(new ItemStack(FrostBow, 1), new Object[]
        		{
        			" IR", "SER", " IR", 'R', Items.string, 'I', Blocks.ice, 'S', Items.snowball, 'E', IronBow
        		});
		
		EntityRegistry.registerModEntity(EntityiDiamondhunterFireArrow.class, "iDiamondhunterFireArrow", 1, this, 64, 20, true);
		EntityRegistry.registerModEntity(EntityiDiamondhunterFrostArrow.class, "iDiamondhunterFrostArrow", 2, this, 64, 20, true);
		EntityRegistry.registerModEntity(EntityiDiamondhunterParticleArrow.class, "iDiamondhunterFrostArrow", 3, this, 64, 20, true);
		
	}
}
