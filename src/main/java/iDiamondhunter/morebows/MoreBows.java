package iDiamondhunter.morebows;

import java.util.Random;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import iDiamondhunter.morebows.bows.CustomBow;
import iDiamondhunter.morebows.bows.EnderBow;
import iDiamondhunter.morebows.bows.MultiBow;
import iDiamondhunter.morebows.entities.ArrowSpawner;
import iDiamondhunter.morebows.entities.CustomArrow;
import iDiamondhunter.morebows.entities.CustomArrow.EnumArrowType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

/* TODO: Document. */
@Mod(modid = MoreBows.MOD_ID, useMetadata = true)
public class MoreBows {
    /* TODO: Document. */
    public static final String MOD_ID = "MoreBows";
    private final static String modSeperator = "morebows:";

    private static Random rand = new Random();

    /* TODO: Document. */
    @Instance(MOD_ID)
    public static MoreBows inst;

    /* TODO: Document. This is super janky. */
    @SidedProxy(clientSide = "iDiamondhunter.morebows.Client",
                serverSide = "iDiamondhunter.morebows.MoreBows")
    public static MoreBows proxy;

    //private final static float defaultPowerDiv = 20.0F;

    /* TODO: Re-evaluate how & where stuff should be declared & initialized. */
    public final static String DiamondBowName = "DiamondBow";
    public final static String GoldBowName = "GoldBow";
    public final static String EnderBowName = "EnderBow";
    public final static String StoneBowName = "StoneBow";
    public final static String IronBowName = "IronBow";
    public final static String MultiBowName = "MultiBow";
    public final static String FlameBowName = "FlameBow";
    public final static String FrostBowName = "FrostBow";

    /* This is super janky. */
    public final static Item DiamondBow = new CustomBow(1016, EnumRarity.rare, new byte[] {8, 4}, 2.2F, 6F, 140, 2.25D, 36000).setUnlocalizedName(DiamondBowName).setTextureName(modSeperator + DiamondBowName);
    public final static Item GoldBow = new CustomBow(550, EnumRarity.uncommon, new byte[] {8, 4}, 2.4F, 5F, 100, 1.5D).setUnlocalizedName(GoldBowName).setTextureName(modSeperator + GoldBowName);
    public final static Item EnderBow = new EnderBow().setUnlocalizedName(EnderBowName).setTextureName(modSeperator + EnderBowName);
    public final static Item StoneBow = new CustomBow(484, EnumRarity.common, (byte[]) null, 17F, 1.15D, true).setUnlocalizedName(StoneBowName).setTextureName(modSeperator + StoneBowName);
    public final static Item IronBow = new CustomBow(550, EnumRarity.common, new byte[] {16, 11}, 2.1F, 17F, 105, 1.5D).setUnlocalizedName(IronBowName).setTextureName(modSeperator + IronBowName);
    public final static Item MultiBow = new MultiBow().setUnlocalizedName(MultiBowName).setTextureName(modSeperator + MultiBowName);
    public final static Item FlameBow = new CustomBow(576, EnumRarity.uncommon, new byte[] {14, 9}, 15F, 2.0D, false, EnumArrowType.fire).setUnlocalizedName(FlameBowName).setTextureName(modSeperator + FlameBowName);
    public final static Item FrostBow = new CustomBow(550, EnumRarity.common, new byte[] {26, 13}, 26.0F, 1D, false, EnumArrowType.frost).setUnlocalizedName(FrostBowName).setTextureName(modSeperator + FrostBowName);


    /* TODO: Document. */
    private static void doSpawnParticle(WorldServer server, Entity entity, String particle) {
        final int numPart = 1;
        final double vel = 0;
        server.func_147487_a(particle, (entity.posX + (rand.nextFloat() * entity.width * 2.0F)) - entity.width, entity.posY + 0.5D + (rand.nextFloat() * entity.height), (entity.posZ + (rand.nextFloat() * entity.width * 2.0F)) - entity.width, numPart /* Number of particles? */, 0, 0, 0, vel /* Velocity? Not sure... */);
    }

    /* TODO: Document. */
    public static boolean spawnParticle(World world, Entity entity, String particle) {
        if (!world.isRemote) {
            final WorldServer server = (WorldServer) world;
            doSpawnParticle(server, entity, particle);
            return true;
        } else {
            return false;
        }
    }

    /* TODO: Document. */
    @SubscribeEvent
    public void arrAttack(LivingAttackEvent event) {
        if  (!event.entity.worldObj.isRemote && (event.source.getSourceOfDamage() instanceof CustomArrow)) {
            final CustomArrow arr = (CustomArrow) event.source.getSourceOfDamage();
            final EnumArrowType type = arr.getType();
            final WorldServer server = (WorldServer) event.entity.worldObj;
            final String particle;
            final int numPart;

            switch (type) {
            case base:
                particle = "portal";
                numPart = 3;
                break;

            case fire:
                particle = "flame";
                numPart = 5;
                break;

            case frost:
                particle = "splash";
                numPart = 1;
                break;

            default:
                particle = "depthsuspend";
                numPart = 1;
                break;
            }

            for (int i = 0; i < numPart; i++ ) {
                doSpawnParticle(server, event.entity, particle);
            }

            // TODO Figure out that weird code from the fire arrow.
            if (type == EnumArrowType.fire) {
                event.entity.setFire(15);
            } else if ((type == EnumArrowType.frost) && arr.getCrit()) {
                arr.setIsCritical(false);
                event.setCanceled(true);
                event.entity.attackEntityFrom(event.source, event.ammount + MoreBows.rand.nextInt((int) ((event.ammount / 2) + 2)));
            }
        }
    }

    /* TODO: Document. */
    @SubscribeEvent
    public void arrHit(LivingHurtEvent event) {
        if (event.source.getSourceOfDamage() instanceof CustomArrow) {
            final CustomArrow arr = (CustomArrow) event.source.getSourceOfDamage();

            if ((arr.getType() == EnumArrowType.frost) && arr.getCrit()) {
                event.entity.setInWeb(); //TODO Replace with slowness effect? This is the original behavior...

                if (!(event.entity instanceof EntityEnderman)) { //TODO Verify that this is the right behavior
                    event.source.getSourceOfDamage().setDead();
                }
            }
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        registerItems();
        proxy.registerEntities();
        MinecraftForge.EVENT_BUS.register(proxy);
    }

    protected void registerEntities() {
        /* I'm not sure how this works. */
        EntityRegistry.registerModEntity(ArrowSpawner.class, "ArrowSpawner", 1, MoreBows.inst, 64, 20, true);
        EntityRegistry.registerModEntity(CustomArrow.class, "CustomArrow", 2, MoreBows.inst, 64, 20, true);
    }

    protected void registerItems() {
        /* TODO check if this is the right way of doing things. */
        /** Item registry */
        GameRegistry.registerItem(DiamondBow, DiamondBowName);
        GameRegistry.registerItem(GoldBow, GoldBowName);
        GameRegistry.registerItem(EnderBow, EnderBowName);
        GameRegistry.registerItem(StoneBow, StoneBowName);
        GameRegistry.registerItem(IronBow, IronBowName);
        GameRegistry.registerItem(MultiBow, MultiBowName);
        GameRegistry.registerItem(FlameBow, FlameBowName);
        GameRegistry.registerItem(FrostBow, FrostBowName);
        /** Recipes */
        GameRegistry.addRecipe(new ItemStack(StoneBow, 1), " Ss", "TBs", " Ss", 'T', Items.stick, 's', Items.string, 'S', Blocks.stone, 'B', Items.bow);
        GameRegistry.addRecipe(new ItemStack(StoneBow, 1), "sS ", "sBT", "sS ", 'T', Items.stick, 's', Items.string, 'S', Blocks.stone, 'B', Items.bow);
        GameRegistry.addRecipe(new ItemStack(IronBow, 1), " Is", "IBs", " Is", 's', Items.string, 'I', Items.iron_ingot, 'B', Items.bow);
        GameRegistry.addRecipe(new ItemStack(IronBow, 1), "sI ", "sBI", "sI ", 's', Items.string, 'I', Items.iron_ingot, 'B', Items.bow);
        GameRegistry.addRecipe(new ItemStack(GoldBow, 1), " Gs", "GBs", " Gs", 's', Items.string, 'G', Items.gold_ingot, 'B', Items.bow);
        GameRegistry.addRecipe(new ItemStack(GoldBow, 1), "sG ", "sBG", "sG ", 's', Items.string, 'G', Items.gold_ingot, 'B', Items.bow);
        GameRegistry.addRecipe(new ItemStack(DiamondBow, 1), " Ds", "IBs", " Ds", 's', Items.string, 'D', Items.diamond, 'I', Items.iron_ingot, 'B', Items.bow);
        GameRegistry.addRecipe(new ItemStack(DiamondBow, 1), "sD ", "sBI", "sD ", 's', Items.string, 'D', Items.diamond, 'I', Items.iron_ingot, 'B', Items.bow);
        GameRegistry.addRecipe(new ItemStack(MultiBow, 1), " Bs", "I s", " Bs", 's', Items.string, 'I', Items.iron_ingot, 'B', IronBow);
        GameRegistry.addRecipe(new ItemStack(MultiBow, 1), "sB ", "s I", "sB ", 's', Items.string, 'I', Items.iron_ingot, 'B', IronBow);
        GameRegistry.addRecipe(new ItemStack(FlameBow, 1), "NB ", "GI ", "NB ", 'G', Items.gold_ingot, 'B', Items.blaze_rod, 'I', IronBow, 'N', Blocks.netherrack);
        GameRegistry.addRecipe(new ItemStack(FlameBow, 1), " NB", " GI", " NB", 'G', Items.gold_ingot, 'B', Items.blaze_rod, 'I', IronBow, 'N', Blocks.netherrack);
        GameRegistry.addRecipe(new ItemStack(FlameBow, 1), "BN ", "IG ", "BN ", 'G', Items.gold_ingot, 'B', Items.blaze_rod, 'I', IronBow, 'N', Blocks.netherrack);
        GameRegistry.addRecipe(new ItemStack(FlameBow, 1), " BN", " IG", " BN", 'G', Items.gold_ingot, 'B', Items.blaze_rod, 'I', IronBow, 'N', Blocks.netherrack);
        GameRegistry.addRecipe(new ItemStack(EnderBow, 1), "GP ", "EI ", "GP ", 'G', Items.gold_ingot, 'P', Items.ender_pearl, 'I', IronBow, 'E', Items.ender_eye);
        GameRegistry.addRecipe(new ItemStack(EnderBow, 1), " GP", " EI", " GP", 'G', Items.gold_ingot, 'P', Items.ender_pearl, 'I', IronBow, 'E', Items.ender_eye);
        GameRegistry.addRecipe(new ItemStack(EnderBow, 1), "PG ", "IE ", "PG ", 'G', Items.gold_ingot, 'P', Items.ender_pearl, 'I', IronBow, 'E', Items.ender_eye);
        GameRegistry.addRecipe(new ItemStack(EnderBow, 1), " PG", " IE", " PG", 'G', Items.gold_ingot, 'P', Items.ender_pearl, 'I', IronBow, 'E', Items.ender_eye);
        GameRegistry.addRecipe(new ItemStack(FrostBow, 1), " IR", "SER", " IR", 'R', Items.string, 'I', Blocks.ice, 'S', Items.snowball, 'E', IronBow);
        GameRegistry.addRecipe(new ItemStack(FrostBow, 1), "RI ", "RES", "RI ", 'R', Items.string, 'I', Blocks.ice, 'S', Items.snowball, 'E', IronBow);
    }

    // This code is completely unneeded, but it's nice enough to keep around for reference purposes.
    // @EventHandler
    // public void fmlMissingMappingsEvent(FMLMissingMappingsEvent event) {
    //     for (final MissingMapping mapping : event.getAll()) {
    //         // Get the old name & mod ID of the item
    //         final String oldID = mapping.name.substring(0, mapping.name.indexOf(':'));
    //         final String oldName = mapping.name.substring(mapping.name.indexOf(':') + 1);
    //         // Attempt to migrate items from old IDs
    //         if (oldID.equals("iDiamondhunterMoreBows") /* Earlier builds of this 1.7.10 port. */ || oldID.equals("More Bows mod by iDiamondhunter") /* ID of iDiamondhunter's ports. */ || oldID.equals("${archivesBaseName}") /* Mistakes happen sometimes! */) {
    //             if (mapping.type == GameRegistry.Type.ITEM) {
    //                 final String remappedName = MOD_ID + ":" + oldName;
    //                 final Item remappedItem = GameData.getItemRegistry().getObject(remappedName);
    //                 if (remappedItem != null) {
    //                     modLog.info("ID remap: " + mapping.name + " > " + remappedName);
    //                     mapping.remap(remappedItem);
    //                 } else {
    //                     modLog.error("ID remap failed: no match for " + mapping.name);
    //                 }
    //             }
    //         }
    //     }
    // }

}
