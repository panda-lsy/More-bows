package iDiamondhunter.morebows;

import java.util.Random;

import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import iDiamondhunter.morebows.bows.CustomBow;
import iDiamondhunter.morebows.bows.EnderBow;
import iDiamondhunter.morebows.bows.MultiBow;
import iDiamondhunter.morebows.entities.ArrowSpawner;
import iDiamondhunter.morebows.entities.CustomArrow;
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
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

/** If you're reading this, I'm very sorry you have to deal with my code. */
@Mod(modid = MoreBows.MOD_ID, guiFactory = "iDiamondhunter.morebows.Client", useMetadata = true /* Note: Forge likes to complain if there isn't something assigned to the "version" property when loading. It should get overwritten by the actual version in the mcmod.info file. */)
public class MoreBows {

    /** The mod ID of More Bows */
    public static final String MOD_ID = "MoreBows";

    /** Used for naming items. */
    private static final String modSeperator = "morebows:";

    /** Mod instance */
    @Instance(MOD_ID)
    private static MoreBows inst;

    /** Mod proxy. TODO This is super janky, see if it's possible to remove this */
    @SidedProxy(clientSide = "iDiamondhunter.morebows.Client", serverSide = "iDiamondhunter.morebows.MoreBows")
    private static MoreBows proxy;

    /** MoreBows config */
    public static Configuration config;
    /**
     * MoreBows config setting.
     * If true, render frost arrows as snow cubes. If false, render as snowballs.
     */
    public static boolean oldArrRender;

    /*
     * Hardcoded magic numbers, because Enums (as they're classes) require a large amount of file space, and I'm targeting 64kb as the compiled .jar size.
     * I'm really sorry for doing this.
     */
    /**
     * Arrow type:
     * An arrow that is not a custom arrow.
     */
    public static final byte ARROW_TYPE_NOT_CUSTOM = 0;
    /**
     * Arrow type:
     * A "particle arrow", used by the ender bow.
     */
    public static final byte ARROW_TYPE_BASE = 1;
    /**
     * Arrow type:
     * A fire arrow, used by the fire bow.
     */
    public static final byte ARROW_TYPE_FIRE = 2;
    /**
     * Arrow type:
     * A frost arrow, used by the frost bow.
     */
    public static final byte ARROW_TYPE_FROST = 3;

    /* Names of bows. TODO: Re-evaluate how & where stuff should be declared & initialized. */
    private static final String DiamondBowName = "DiamondBow";
    private static final String GoldBowName = "GoldBow";
    private static final String EnderBowName = "EnderBow";
    private static final String StoneBowName = "StoneBow";
    private static final String IronBowName = "IronBow";
    private static final String MultiBowName = "MultiBow";
    private static final String FlameBowName = "FlameBow";
    private static final String FrostBowName = "FrostBow";

    /* Default values for bow construction */
    /** Default values for bow construction: the default speed at which icons change. */
    public static final byte[] defaultIconTimes = { 18, 13 };
    /** Default values for bow construction: the default damage multiplier. */
    public static final double defaultDamageMult = 1D;
    /** Default values for bow construction: the default amount of time an arrow burns for. */
    public static final int defaultFlameTime = 100;
    // /** Default values for bow construction: the default power divisor (TODO document better) */
    // public static final float defaultPowerDiv = 20.0F;
    /** Default values for bow construction: the default velocity multiplier (TODO document better) */
    public static final float defaultVelocityMult = 2.0F;
    /** Default values for bow construction: the default type of arrow a bow shoots. */
    public static final byte defaultArrowType = ARROW_TYPE_NOT_CUSTOM;

    /* Bow items. TODO: This is super janky. */
    protected static final Item DiamondBow = new CustomBow(1016, EnumRarity.rare, new byte[] { 8, 4 }, 2.2F, 6F, 140, 2.25D, defaultArrowType).setUnlocalizedName(DiamondBowName).setTextureName(modSeperator + DiamondBowName);
    protected static final Item GoldBow = new CustomBow(68, EnumRarity.uncommon, new byte[] { 8, 4 }, 2.4F, 5F, 100, 1.5D, defaultArrowType).setUnlocalizedName(GoldBowName).setTextureName(modSeperator + GoldBowName);
    protected static final Item EnderBow = new EnderBow().setUnlocalizedName(EnderBowName).setTextureName(modSeperator + EnderBowName);
    protected static final Item StoneBow = new CustomBow(484, EnumRarity.common, defaultIconTimes, defaultVelocityMult, 17F, defaultFlameTime, 1.15D, defaultArrowType).setUnlocalizedName(StoneBowName).setTextureName(modSeperator + StoneBowName);
    protected static final Item IronBow = new CustomBow(550, EnumRarity.common, new byte[] { 16, 11 }, 2.1F, 17F, 105, 1.5D, defaultArrowType).setUnlocalizedName(IronBowName).setTextureName(modSeperator + IronBowName);
    protected static final Item MultiBow = new MultiBow().setUnlocalizedName(MultiBowName).setTextureName(modSeperator + MultiBowName);
    protected static final Item FlameBow = new CustomBow(576, EnumRarity.uncommon, new byte[] { 14, 9 }, defaultVelocityMult, 15F, defaultFlameTime, 2.0D, ARROW_TYPE_FIRE).setUnlocalizedName(FlameBowName).setTextureName(modSeperator + FlameBowName);
    protected static final Item FrostBow = new CustomBow(550, EnumRarity.common, new byte[] { 26, 13 }, defaultVelocityMult, 26.0F, defaultFlameTime, defaultDamageMult, ARROW_TYPE_FROST).setUnlocalizedName(FrostBowName).setTextureName(modSeperator + FrostBowName);

    /* TODO put this somewhere else? */
    private static final Random rand = new Random();

    /** Syncs the config file TODO documentation */
    public static void conf() {
        // config.load();
        Property prop;
        prop = config.get(Configuration.CATEGORY_GENERAL, "oldFrostArrowRendering", false);
        oldArrRender = prop.getBoolean();

        if (config.hasChanged()) {
            config.save();
        }
    }

    /**
     * Checks if the provided world is a client or a server. If it is a client world, it doesn't do anything.
     * If it is a server world, it calls the server world specific method to spawn a particle on the server.
     * This particle will be sent to connected clients.
     * The parameter randDisp can be set, which sets the particles position to somewhere random close to the entity.
     * TODO Cleanup and re-evaluate, document better.
     *
     * @param world    The world to attempt to spawn a particle in.
     * @param entity   The entity to spawn the particle at.
     * @param part     The particle type to spawn.
     * @param randDisp If this is true, particles will be randomly distributed around the entity.
     * @param velocity The velocity of spawned particles.
     */
    public static final void tryPart(World world, Entity entity, String part, boolean randDisp, double velocity) {
        if (!world.isRemote) {
            final WorldServer server = (WorldServer) world;
            // final int amount = 1;
            final double xDisp;
            final double yDisp;
            final double zDisp;

            if (randDisp) {
                xDisp = (rand.nextFloat() * entity.width * 2.0F) - entity.width;
                yDisp = 0.5D + (rand.nextFloat() * entity.height);
                zDisp = (rand.nextFloat() * entity.width * 2.0F) - entity.width;
            } else {
                xDisp = 0;
                yDisp = 0.5D;
                zDisp = 0;
            }

            server.func_147487_a(part, entity.posX, entity.posY, entity.posZ, 1, xDisp, yDisp, zDisp, velocity);
        }
    }

    /**
     * Handles particle effects on custom arrows hitting an entity, and adds critical damage to frost arrows.
     * Frost arrows always return false when normal methods to get if they're critical are called. This is to hide the vanilla particle trail, so it can create its own custom one.
     * TODO Cleanup, document better.
     *
     * @param event the event
     */
    @SubscribeEvent
    public final void arrHit(LivingAttackEvent event) {
        if (!event.entity.worldObj.isRemote && (event.source.getSourceOfDamage() instanceof CustomArrow)) {
            final CustomArrow arr = (CustomArrow) event.source.getSourceOfDamage();
            final byte type = arr.getType();
            final String part;
            final int amount;
            final double velocity;
            final boolean randDisp;

            switch (type) {
            case ARROW_TYPE_BASE:
                part = "portal";
                amount = 3;
                randDisp = true;
                velocity = 1;
                break;

            case ARROW_TYPE_FIRE:
                if (arr.isBurning()) {
                    part = "flame";
                } else {
                    part = "smoke";
                }

                amount = 5;
                randDisp = true;
                velocity = 0.05;
                break;

            case ARROW_TYPE_FROST:
                part = "splash";
                amount = 1;
                randDisp = false;
                velocity = 0.01;
                break;

            default:
                part = "depthsuspend";
                amount = 20;
                randDisp = true;
                velocity = 0;
                break;
            }

            for (int i = 0; i < amount; i++) {
                tryPart(event.entity.worldObj, event.entity, part, randDisp, velocity);
            }

            if ((type == ARROW_TYPE_FROST) && arr.getCrit()) {
                arr.setIsCritical(false);
                event.setCanceled(true);
                event.entity.attackEntityFrom(event.source, event.ammount + MoreBows.rand.nextInt((int) ((event.ammount / 2) + 2))); // Manually apply critical damage due to deliberately not exposing if an arrow is critical. See CustomArrow.
            }
        }
    }

    /**
     * Handles custom effects from the frost arrow.
     * TODO Cleanup?
     *
     * @param event the event
     */
    @SubscribeEvent
    public final void arrHurt(LivingHurtEvent event) {
        if (event.source.getSourceOfDamage() instanceof CustomArrow) {
            if (((CustomArrow) event.source.getSourceOfDamage()).getType() == ARROW_TYPE_FROST) {
                event.entity.setInWeb(); // TODO Replace with slowness effect? This is the original behavior...

                // event.entity.extinguish(); // Potentially would be nice to have? Not in the original mod, just though it seemed right.
                if (!(event.entity instanceof EntityEnderman)) { // Vanilla arrows don't get destroyed after they've hit an Enderman
                    event.source.getSourceOfDamage().setDead();
                }
            }
        }
    }

    /**
     * Initualises mod TODO documentation
     *
     * @param event the event
     */
    @EventHandler
    public final void init(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());
        conf();
        proxy.register();
        // TODO see if it's possible to only use one of these
        MinecraftForge.EVENT_BUS.register(proxy);
        FMLCommonHandler.instance().bus().register(proxy);
    }

    /**
     * Calls conf() to sync the config file when the config file changes TODO documentation
     *
     * @param event the event
     */
    @SubscribeEvent
    public void onConfigurationChangedEvent(OnConfigChangedEvent event) {
        if (event.modID.equals(MOD_ID)) {
            conf();
        }
    }

    /** TODO documentation */
    protected void register() {
        /* Item registry */
        GameRegistry.registerItem(DiamondBow, DiamondBowName);
        GameRegistry.registerItem(GoldBow, GoldBowName);
        GameRegistry.registerItem(EnderBow, EnderBowName);
        GameRegistry.registerItem(StoneBow, StoneBowName);
        GameRegistry.registerItem(IronBow, IronBowName);
        GameRegistry.registerItem(MultiBow, MultiBowName);
        GameRegistry.registerItem(FlameBow, FlameBowName);
        GameRegistry.registerItem(FrostBow, FrostBowName);
        /* Recipes */
        GameRegistry.addRecipe(new ItemStack(StoneBow, 1), " DC", "ABC", " DC", 'A', Items.stick, 'C', Items.string, 'D', Blocks.stone, 'B', Items.bow);
        GameRegistry.addRecipe(new ItemStack(StoneBow, 1), "CD ", "CBA", "CD ", 'A', Items.stick, 'C', Items.string, 'D', Blocks.stone, 'B', Items.bow);
        GameRegistry.addRecipe(new ItemStack(IronBow, 1), " AC", "ABC", " AC", 'C', Items.string, 'A', Items.iron_ingot, 'B', Items.bow);
        GameRegistry.addRecipe(new ItemStack(IronBow, 1), "CA ", "CBA", "CA ", 'C', Items.string, 'A', Items.iron_ingot, 'B', Items.bow);
        GameRegistry.addRecipe(new ItemStack(GoldBow, 1), " AC", "ABC", " AC", 'C', Items.string, 'A', Items.gold_ingot, 'B', Items.bow);
        GameRegistry.addRecipe(new ItemStack(GoldBow, 1), "CA ", "CBA", "CA ", 'C', Items.string, 'A', Items.gold_ingot, 'B', Items.bow);
        GameRegistry.addRecipe(new ItemStack(DiamondBow, 1), " DC", "ABC", " DC", 'C', Items.string, 'D', Items.diamond, 'A', Items.iron_ingot, 'B', Items.bow);
        GameRegistry.addRecipe(new ItemStack(DiamondBow, 1), "CD ", "CBA", "CD ", 'C', Items.string, 'D', Items.diamond, 'A', Items.iron_ingot, 'B', Items.bow);
        GameRegistry.addRecipe(new ItemStack(MultiBow, 1), " BC", "A C", " BC", 'C', Items.string, 'A', Items.iron_ingot, 'B', IronBow);
        GameRegistry.addRecipe(new ItemStack(MultiBow, 1), "CB ", "C A", "CB ", 'C', Items.string, 'A', Items.iron_ingot, 'B', IronBow);
        GameRegistry.addRecipe(new ItemStack(FlameBow, 1), "CD ", "AB ", "CD ", 'A', Items.gold_ingot, 'D', Items.blaze_rod, 'B', IronBow, 'C', Blocks.netherrack);
        GameRegistry.addRecipe(new ItemStack(FlameBow, 1), " CD", " AB", " CD", 'A', Items.gold_ingot, 'D', Items.blaze_rod, 'B', IronBow, 'C', Blocks.netherrack);
        GameRegistry.addRecipe(new ItemStack(FlameBow, 1), "DC ", "BA ", "DC ", 'A', Items.gold_ingot, 'D', Items.blaze_rod, 'B', IronBow, 'C', Blocks.netherrack);
        GameRegistry.addRecipe(new ItemStack(FlameBow, 1), " DC", " BA", " DC", 'A', Items.gold_ingot, 'D', Items.blaze_rod, 'B', IronBow, 'C', Blocks.netherrack);
        GameRegistry.addRecipe(new ItemStack(EnderBow, 1), "CD ", "AB ", "CD ", 'C', Items.gold_ingot, 'D', Items.ender_pearl, 'B', IronBow, 'A', Items.ender_eye);
        GameRegistry.addRecipe(new ItemStack(EnderBow, 1), " CD", " AB", " CD", 'C', Items.gold_ingot, 'D', Items.ender_pearl, 'B', IronBow, 'A', Items.ender_eye);
        GameRegistry.addRecipe(new ItemStack(EnderBow, 1), "DC ", "BA ", "DC ", 'C', Items.gold_ingot, 'D', Items.ender_pearl, 'B', IronBow, 'A', Items.ender_eye);
        GameRegistry.addRecipe(new ItemStack(EnderBow, 1), " DC", " BA", " DC", 'C', Items.gold_ingot, 'D', Items.ender_pearl, 'B', IronBow, 'A', Items.ender_eye);
        GameRegistry.addRecipe(new ItemStack(FrostBow, 1), " DC", "ABC", " DC", 'C', Items.string, 'D', Blocks.ice, 'A', Items.snowball, 'B', IronBow);
        GameRegistry.addRecipe(new ItemStack(FrostBow, 1), "CD ", "CBA", "CD ", 'C', Items.string, 'D', Blocks.ice, 'A', Items.snowball, 'B', IronBow);
        /* Entities TODO I'm not sure how this works. */
        EntityRegistry.registerModEntity(ArrowSpawner.class, "ArrowSpawner", 1, MoreBows.inst, 64, 20, true);
        EntityRegistry.registerModEntity(CustomArrow.class, "CustomArrow", 2, MoreBows.inst, 64, 20, true);
    }

}
