package iDiamondhunter.morebows;

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
import iDiamondhunter.morebows.entities.ArrowSpawner;
import iDiamondhunter.morebows.entities.CustomArrow;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

/** If you're reading this, I'm very sorry you have to deal with my code. */
@Mod(modid = MoreBows.MOD_ID, guiFactory = "iDiamondhunter.morebows.Client" /* Note: Forge likes to complain if there isn't something assigned to the "version" property when loading. It should get overwritten by the actual version in the mcmod.info file. */)
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
    /** MoreBows config setting: If true, frost arrows extinguish fire from Entities that are on fire. If false, frost arrows can be on fire. */
    public static boolean frostArrowsShouldBeCold;
    /** MoreBows config setting: If true, frost arrows slow Entities down by pretending to have set them in a web for one tick. If false, frost arrows apply the slowness potion effect on hit. */
    public static boolean oldFrostArrowMobSlowdown;
    /** MoreBows config setting: If true, render frost arrows as snow cubes. If false, render as snowballs. */
    public static boolean oldFrostArrowRendering;

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
    public static final byte ARROW_TYPE_ENDER = 1;
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

    /** The maximum amount of time (in ticks) that a bow can be used for. */
    public static final int bowMaxUseDuration = 72000;

    /* Names of bows. */
    private static final String DiamondBowName = "DiamondBow";
    private static final String EnderBowName = "EnderBow";
    private static final String FlameBowName = "FlameBow";
    private static final String FrostBowName = "FrostBow";
    private static final String GoldBowName = "GoldBow";
    private static final String IronBowName = "IronBow";
    private static final String MultiBowName = "MultiBow";
    private static final String StoneBowName = "StoneBow";

    /* Default values for bow construction */
    /// ** Default values for bow construction: the default speed at which icons change. */
    // private static final byte[] defaultIconTimes = { 18, 13 };
    /** Default values for bow construction: the default damage multiplier. */
    private static final double noDamageMult = 1D;
    /** Default values for bow construction: the default power divisor (TODO document better) */
    private static final float defaultPowerDiv = 20.0F;
    /** Default values for bow construction: the default type of arrow a bow shoots. */
    private static final byte defaultArrowType = ARROW_TYPE_NOT_CUSTOM;

    /* Bow items. */
    protected static final Item DiamondBow = new CustomBow(1016, defaultArrowType, 2.25D, new byte[] { 8, 4 }, false, 6.0F, EnumRarity.rare).setUnlocalizedName(DiamondBowName).setTextureName(modSeperator + DiamondBowName);
    protected static final Item EnderBow = new CustomBow(215, ARROW_TYPE_ENDER, noDamageMult, new byte[] { 19, 10 }, true, 22.0F, EnumRarity.epic).setUnlocalizedName(EnderBowName).setTextureName(modSeperator + EnderBowName);
    protected static final Item FlameBow = new CustomBow(576, ARROW_TYPE_FIRE, 2.0D, new byte[] { 14, 9 }, false, 15.0F, EnumRarity.uncommon).setUnlocalizedName(FlameBowName).setTextureName(modSeperator + FlameBowName);
    protected static final Item FrostBow = new CustomBow(550, ARROW_TYPE_FROST, noDamageMult, new byte[] { 26, 13 }, false, 26.0F, EnumRarity.common).setUnlocalizedName(FrostBowName).setTextureName(modSeperator + FrostBowName);
    protected static final Item GoldBow = new CustomBow(68, defaultArrowType, 2.5D, new byte[] { 8, 4 }, false, 6.0F, EnumRarity.uncommon).setUnlocalizedName(GoldBowName).setTextureName(modSeperator + GoldBowName);
    protected static final Item IronBow = new CustomBow(550, defaultArrowType, 1.5D, new byte[] { 16, 11 }, false, 17.0F, EnumRarity.common).setUnlocalizedName(IronBowName).setTextureName(modSeperator + IronBowName);
    protected static final Item MultiBow = new CustomBow(550, ARROW_TYPE_NOT_CUSTOM, noDamageMult, new byte[] { 12, 7 }, true, 13.0F, EnumRarity.rare).setUnlocalizedName(MultiBowName).setTextureName(modSeperator + MultiBowName);
    protected static final Item StoneBow = new CustomBow(484, defaultArrowType, 1.15D, new byte[] { 18, 13 }, false, defaultPowerDiv, EnumRarity.common).setUnlocalizedName(StoneBowName).setTextureName(modSeperator + StoneBowName);

    /** This method syncs the config file with the Configuration, as well as syncing any config related variables. */
    private static final void conf() {
        frostArrowsShouldBeCold = config.get(Configuration.CATEGORY_GENERAL, "frostArrowsShouldBeCold", true).getBoolean();
        oldFrostArrowMobSlowdown = config.get(Configuration.CATEGORY_GENERAL, "oldFrostArrowMobSlowdown", false).getBoolean();
        oldFrostArrowRendering = config.get(Configuration.CATEGORY_GENERAL, "oldFrostArrowRendering", false).getBoolean();
        config.save();
    }

    /**
     * This method attempts to spawn a particle on the server world.
     * It first checks if the provided world is a client or a server. If it is a client world, it doesn't do anything.
     * If it is a server world, it calls the server world specific method to spawn a particle on the server.
     * This particle will be sent to connected clients.
     * The parameter randDisp can be set, which sets the particles position to somewhere random close to the entity.
     *
     * @param world    The world to attempt to spawn a particle in.
     * @param entity   The entity to spawn the particle at.
     * @param part     The particle type to spawn.
     * @param randDisp If this is true, particles will be randomly distributed around the entity.
     * @param velocity The velocity of spawned particles.
     */
    public static final void tryPart(World world, Entity entity, String part, boolean randDisp, double velocity) {
        if (!world.isRemote) {
            // final int amount = 1;
            final double xDisp;
            final double yDisp;
            final double zDisp;

            if (randDisp) {
                xDisp = (world.rand.nextFloat() * entity.width * 2.0F) - entity.width;
                yDisp = 0.5D + (world.rand.nextFloat() * entity.height);
                zDisp = (world.rand.nextFloat() * entity.width * 2.0F) - entity.width;
            } else {
                xDisp = 0;
                yDisp = 0.5D;
                zDisp = 0;
            }

            ((WorldServer) world).func_147487_a(part, entity.posX, entity.posY, entity.posZ, 1, xDisp, yDisp, zDisp, velocity);
        }
    }

    /**
     * Creates the particle effects when a custom arrow hits an entity.
     *
     * @param event the event
     */
    @SubscribeEvent
    public final void arrHit(LivingAttackEvent event) {
        if ((!event.entity.worldObj.isRemote) && (event.source.getSourceOfDamage() instanceof CustomArrow)) {
            final CustomArrow arr = (CustomArrow) event.source.getSourceOfDamage();
            final String part;
            final int amount;
            final double velocity;
            final boolean randDisp;

            switch (arr.type) {
            case ARROW_TYPE_ENDER:
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
        }
    }

    /**
     * Handles custom effects from the frost arrow.
     *
     * @param event the event
     */
    @SubscribeEvent
    public final void arrHurt(LivingHurtEvent event) {
        if ((event.source.getSourceOfDamage() instanceof CustomArrow) && (((CustomArrow) event.source.getSourceOfDamage()).type == ARROW_TYPE_FROST)) {
            if (frostArrowsShouldBeCold) {
                if (event.entityLiving instanceof EntityBlaze) {
                    event.ammount *= 3;
                }

                event.entity.extinguish();
            }

            if (!oldFrostArrowMobSlowdown) {
                event.entityLiving.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), 300, 2));
            } else {
                event.entity.setInWeb();
            }
        }
    }

    /**
     * Calls conf() when the Configuration changes.
     *
     * @param event the event
     */
    @SubscribeEvent
    public final void confChange(OnConfigChangedEvent event) {
        if (event.modID.equals(MOD_ID)) {
            conf();
        }
    }

    /**
     * This method handles setting up the mod.
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

    /** This method registers all mod content for a given side. Server side, it registers items, recipes, and entities. Client side, it also registers custom renderers. */
    protected void register() {
        /* Item registry */
        GameRegistry.registerItem(DiamondBow, DiamondBowName);
        GameRegistry.registerItem(EnderBow, EnderBowName);
        GameRegistry.registerItem(FlameBow, FlameBowName);
        GameRegistry.registerItem(FrostBow, FrostBowName);
        GameRegistry.registerItem(GoldBow, GoldBowName);
        GameRegistry.registerItem(IronBow, IronBowName);
        GameRegistry.registerItem(MultiBow, MultiBowName);
        GameRegistry.registerItem(StoneBow, StoneBowName);
        /* Recipe registry */
        GameRegistry.addRecipe(new ShapedOreRecipe(DiamondBow, " DC", "ABC", " DC", 'C', "string", 'D', "gemDiamond", 'A', "ingotIron", 'B', "bow"));
        GameRegistry.addRecipe(new ShapedOreRecipe(EnderBow, "CD", "AB", "CD", 'C', "ingotGold", 'D', "pearlEnder", 'B', "bowIron", 'A', "pearlEnderEye"));
        GameRegistry.addRecipe(new ShapedOreRecipe(FlameBow, "CD", "AB", "CD", 'A', "ingotGold", 'D', "rodBlaze", 'B', "bowIron", 'C', "netherrack"));
        GameRegistry.addRecipe(new ShapedOreRecipe(FrostBow, " DC", "ABC", " DC", 'C', "string", 'D', "ice", 'A', "snowball", 'B', "bowIron"));
        GameRegistry.addRecipe(new ShapedOreRecipe(GoldBow, " AC", "ABC", " AC", 'C', "string", 'A', "ingotGold", 'B', "bow"));
        GameRegistry.addRecipe(new ShapedOreRecipe(IronBow, " AC", "ABC", " AC", 'C', "string", 'A', "ingotIron", 'B', "bow"));
        GameRegistry.addRecipe(new ShapedOreRecipe(MultiBow, " DC", "A C", " DC", 'C', "string", 'A', "ingotIron", 'D', "bowIron"));
        GameRegistry.addRecipe(new ShapedOreRecipe(StoneBow, " DC", "ABC", " DC", 'A', "stickWood", 'C', "string", 'D', "stone", 'B', "bow"));
        /* Hack-ish - Registers groups of items to the OreDictionary that Forge doesn't by default. I'm pretty sure that nothing bad happens if other mods duplicate entries to it. */
        OreDictionary.registerOre("bow", DiamondBow);
        OreDictionary.registerOre("bow", EnderBow);
        OreDictionary.registerOre("bow", FlameBow);
        OreDictionary.registerOre("bow", FrostBow);
        OreDictionary.registerOre("bow", GoldBow);
        OreDictionary.registerOre("bow", IronBow);
        OreDictionary.registerOre("bow", Items.bow);
        OreDictionary.registerOre("bow", MultiBow);
        OreDictionary.registerOre("bow", StoneBow);
        OreDictionary.registerOre("bowDiamond", DiamondBow);
        OreDictionary.registerOre("bowGold", GoldBow);
        OreDictionary.registerOre("bowIron", IronBow);
        OreDictionary.registerOre("ice", Blocks.ice);
        OreDictionary.registerOre("ice", Blocks.packed_ice);
        OreDictionary.registerOre("netherrack", Blocks.netherrack);
        OreDictionary.registerOre("pearlEnder", Items.ender_pearl);
        OreDictionary.registerOre("pearlEnderEye", Items.ender_eye);
        OreDictionary.registerOre("rodBlaze", Items.blaze_rod);
        OreDictionary.registerOre("snowball", Items.snowball);
        OreDictionary.registerOre("string", Items.string);
        /* Entities */
        EntityRegistry.registerModEntity(ArrowSpawner.class, "ArrowSpawner", 1, MoreBows.inst, /** As the player can never see an ArrowSpawner and all of the logic for it is handled server-side, there's no reason to send any tracking updates. */ -1, Integer.MAX_VALUE, false);
        EntityRegistry.registerModEntity(CustomArrow.class, "CustomArrow", 2, MoreBows.inst, 64, 20, true);
    }

}
