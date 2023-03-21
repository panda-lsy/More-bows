package iDiamondhunter.morebows;

import org.apache.logging.log4j.Logger;

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
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

/** If you're reading this, I'm very sorry you have to deal with my code. */
@Mod(modid = MoreBows.MOD_ID, guiFactory = "iDiamondhunter.morebows.Client" /* Note: Forge likes to complain if there isn't something assigned to the "version" property when loading. It should get overwritten by the actual version in the mcmod.info file. */)
public class MoreBows {
    /** Data class for bow stats. */
    public static final class BowConfig {

        /** The configured bow durability. */
        final int confBowDurability;

        /** The configured bow damage multiplier. */
        final double confBowDamageMult;

        /** The configured bow drawback divisor. */
        final float confBowDrawbackDiv;

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

    /** The mod ID of More Bows */
    public static final String MOD_ID = "MoreBows";

    /** Used for naming items. */
    private static final String modSeparator = "morebows:";

    /** Mod instance */
    @Instance(MOD_ID)
    private static MoreBows inst;

    /** Mod proxy. TODO This is super janky, see if it's possible to remove this */
    @SidedProxy(clientSide = "iDiamondhunter.morebows.Client", serverSide = "iDiamondhunter.morebows.MoreBows")
    private static MoreBows proxy;

    /** The mod log. */
    public static Logger modLog;

    /** MoreBows config */
    private static Configuration config;
    /** MoreBows config setting: If true, frost arrows extinguish fire from Entities that are on fire. If false, frost arrows can be on fire. */
    public static boolean frostArrowsShouldBeCold;
    /** MoreBows config setting: If true, frost arrows slow Entities down by pretending to have set them in a web for one tick. If false, frost arrows apply the slowness potion effect on hit. */
    public static boolean oldFrostArrowMobSlowdown;
    /** MoreBows config setting: If true, render frost arrows as snow cubes. If false, render as snowballs. */
    public static boolean oldFrostArrowRendering;
    /** MoreBows config setting: If true, use as many arrows as possible, and shoot used arrows. If false, use 1 arrow, and shoot multiple arrows. */
    public static boolean useAmmoForShotArrows;

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

    static final String[] BowNames = {
        DiamondBowName,
        EnderBowName,
        FlameBowName,
        FrostBowName,
        GoldBowName,
        IronBowName,
        MultiBowName,
        StoneBowName
    };

    /* Default values for bow construction */
    /** Default values for bow construction: the default damage multiplier. */
    private static final double noDamageMult = 1.0D;
    /** Default values for bow construction: the default power divisor (TODO document better) */
    private static final float defaultPowerDiv = 20.0F;
    /** Default values for bow construction: the default type of arrow a bow shoots. */
    private static final byte defaultArrowType = ARROW_TYPE_NOT_CUSTOM;

    /* Bow stats. */

    private static final BowConfig[] DefaultBowConfigs = {
        /** Diamond bow stats. */
        new BowConfig(1016, 2.25, 6.0F),

        /** Ender bow stats. */
        new BowConfig(215, noDamageMult, 22.0F),

        /** Flame bow stats. */
        new BowConfig(576, 2.0, 15.0F),

        /** Frost bow stats. */
        new BowConfig(550, noDamageMult, 26.0F),

        /** Gold bow stats. */
        new BowConfig(68, 2.5, 6.0F),

        /** Iron bow stats. */
        new BowConfig(550, 1.5, 17.0F),

        /** Multi bow stats. */
        new BowConfig(550, noDamageMult, 13.0F),

        /** Stone bow stats. */
        new BowConfig(484, 1.15, defaultPowerDiv)
    };

    private static final BowConfig[] BowConfigs = new BowConfig[DefaultBowConfigs.length];

    /* Bow items. */
    protected static Item DiamondBow;
    protected static Item EnderBow;
    protected static Item FlameBow;
    protected static Item FrostBow;
    protected static Item GoldBow;
    protected static Item IronBow;
    protected static Item MultiBow;
    protected static Item StoneBow;

    private static Item[] allItems;

    private static Item[] getAllItems() {
        if (allItems == null) {
            DiamondBow = new CustomBow(BowConfigs[0].confBowDurability, defaultArrowType, BowConfigs[0].confBowDamageMult, false, BowConfigs[0].confBowDrawbackDiv, EnumRarity.rare).setUnlocalizedName(DiamondBowName).setTextureName(modSeparator + DiamondBowName);
            EnderBow = new CustomBow(BowConfigs[1].confBowDurability, ARROW_TYPE_ENDER, BowConfigs[1].confBowDamageMult, true, BowConfigs[1].confBowDrawbackDiv, EnumRarity.epic).setUnlocalizedName(EnderBowName).setTextureName(modSeparator + EnderBowName);
            FlameBow = new CustomBow(BowConfigs[2].confBowDurability, ARROW_TYPE_FIRE, BowConfigs[2].confBowDamageMult, false, BowConfigs[2].confBowDrawbackDiv, EnumRarity.uncommon).setUnlocalizedName(FlameBowName).setTextureName(modSeparator + FlameBowName);
            FrostBow = new CustomBow(BowConfigs[3].confBowDurability, ARROW_TYPE_FROST, BowConfigs[3].confBowDamageMult, false, BowConfigs[3].confBowDrawbackDiv, EnumRarity.common).setUnlocalizedName(FrostBowName).setTextureName(modSeparator + FrostBowName);
            GoldBow = new CustomBow(BowConfigs[4].confBowDurability, defaultArrowType, BowConfigs[4].confBowDamageMult, false, BowConfigs[4].confBowDrawbackDiv, EnumRarity.uncommon).setUnlocalizedName(GoldBowName).setTextureName(modSeparator + GoldBowName);
            IronBow = new CustomBow(BowConfigs[5].confBowDurability, defaultArrowType, BowConfigs[5].confBowDamageMult, false, BowConfigs[5].confBowDrawbackDiv, EnumRarity.common).setUnlocalizedName(IronBowName).setTextureName(modSeparator + IronBowName);
            MultiBow = new CustomBow(BowConfigs[6].confBowDurability, ARROW_TYPE_NOT_CUSTOM, BowConfigs[6].confBowDamageMult, true, BowConfigs[6].confBowDrawbackDiv, EnumRarity.rare).setUnlocalizedName(MultiBowName).setTextureName(modSeparator + MultiBowName);
            StoneBow = new CustomBow(BowConfigs[7].confBowDurability, defaultArrowType, BowConfigs[7].confBowDamageMult, false, BowConfigs[7].confBowDrawbackDiv, EnumRarity.common).setUnlocalizedName(StoneBowName).setTextureName(modSeparator + StoneBowName);
            allItems = new Item[] { DiamondBow, EnderBow, FlameBow, FrostBow, GoldBow, IronBow, MultiBow, StoneBow };
        }

        return allItems;
    }

    private static final String confCatBows = "bows";

    static Property getFrostArrowsShouldBeColdProp() {
        return config.get(Configuration.CATEGORY_GENERAL, "frostArrowsShouldBeCold", true);
    }

    static Property getOldFrostArrowMobSlowdownProp() {
        return config.get(Configuration.CATEGORY_GENERAL, "oldFrostArrowMobSlowdown", false);
    }

    static Property getOldFrostArrowRenderingProp() {
        return config.get(Configuration.CATEGORY_GENERAL, "oldFrostArrowRendering", false);
    }

    static Property getUseAmmoForShotArrowsProp() {
        return config.get(Configuration.CATEGORY_GENERAL, "useAmmoForShotArrows", false);
    }

    static Property getConfBowDamageMultProp(String bowName, int i) {
        return config.get(confCatBows + "." + bowName, "confBowDamageMult", DefaultBowConfigs[i].confBowDamageMult);
    }

    static Property getConfBowDurabilityProp(String bowName, int i) {
        return config.get(confCatBows + "." + bowName, "confBowDurability", DefaultBowConfigs[i].confBowDurability);
    }

    static Property getConfBowDrawbackDivProp(String bowName, int i) {
        return config.get(confCatBows + "." + bowName, "confBowDrawbackDiv", DefaultBowConfigs[i].confBowDrawbackDiv);
    }

    /** This method syncs the config file with the Configuration, as well as syncing any config related variables. */
    private static void conf() {
        frostArrowsShouldBeCold = getFrostArrowsShouldBeColdProp().getBoolean();
        oldFrostArrowMobSlowdown = getOldFrostArrowMobSlowdownProp().getBoolean();
        oldFrostArrowRendering = getOldFrostArrowRenderingProp().getBoolean();
        useAmmoForShotArrows = getUseAmmoForShotArrowsProp().getBoolean();
        final int length = BowNames.length;

        for (int i = 0; i < length; i++) {
            final String bowName = BowNames[i];
            final Property confBowDurabilityProp = getConfBowDurabilityProp(bowName, i);
            final Property confBowDamageMultProp = getConfBowDamageMultProp(bowName, i);
            final Property confBowDrawbackDivProp = getConfBowDrawbackDivProp(bowName, i);

            if (confBowDurabilityProp.isDefault() && confBowDamageMultProp.isDefault() && confBowDrawbackDivProp.isDefault()) {
                BowConfigs[i] = DefaultBowConfigs[i];
            } else {
                BowConfigs[i] = new BowConfig(confBowDurabilityProp.getInt(), confBowDamageMultProp.getDouble(), (float) confBowDrawbackDivProp.getDouble());
            }
        }

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
                xDisp = 0.0;
                yDisp = 0.5D;
                zDisp = 0.0;
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
        if (!event.entity.worldObj.isRemote && (event.source.getSourceOfDamage() instanceof CustomArrow)) {
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
                velocity = 1.0;
                break;

            case ARROW_TYPE_FIRE:
                part = arr.isBurning() ? "flame" : "smoke";
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
                velocity = 0.0;
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
                    event.ammount *= 3.0F;
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
        if (MOD_ID.equals(event.modID)) {
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
        modLog = event.getModLog();
        config = new Configuration(event.getSuggestedConfigurationFile());
        conf();
        proxy.register();
        // TODO see if it's possible to only use one of these
        MinecraftForge.EVENT_BUS.register(proxy);
        FMLCommonHandler.instance().bus().register(proxy);
    }

    /** This method registers all mod content for a given side. Server side, it registers items, recipes, and entities. Client side, it also registers custom renderers. */
    protected void register() {
        getAllItems();
        /* Item registration */
        GameRegistry.registerItem(DiamondBow, DiamondBowName);
        GameRegistry.registerItem(EnderBow, EnderBowName);
        GameRegistry.registerItem(FlameBow, FlameBowName);
        GameRegistry.registerItem(FrostBow, FrostBowName);
        GameRegistry.registerItem(GoldBow, GoldBowName);
        GameRegistry.registerItem(IronBow, IronBowName);
        GameRegistry.registerItem(MultiBow, MultiBowName);
        GameRegistry.registerItem(StoneBow, StoneBowName);
        /* Hack-ish - Register an OreDictionary'd version of the dispenser recipe, so MoreBows's bows work with it. */
        GameRegistry.addRecipe(new ShapedOreRecipe(Blocks.dispenser, "AAA", "ABA", "ACA", 'A', "cobblestone", 'B', "bow", 'C', "dustRedstone"));
        /* Bow recipe registration */
        GameRegistry.addRecipe(new ShapedOreRecipe(DiamondBow, " DC", "ABC", " DC", 'C', "string", 'D', "gemDiamond", 'A', "ingotIron", 'B', "bow"));
        GameRegistry.addRecipe(new ShapedOreRecipe(EnderBow, "CD", "AB", "CD", 'C', "ingotGold", 'D', "pearlEnder", 'B', "bowIron", 'A', "pearlEnderEye"));
        GameRegistry.addRecipe(new ShapedOreRecipe(FlameBow, "CD", "AB", "CD", 'A', "ingotGold", 'D', "rodBlaze", 'B', "bowIron", 'C', "netherrack"));
        GameRegistry.addRecipe(new ShapedOreRecipe(FrostBow, " DC", "ABC", " DC", 'C', "string", 'D', "ice", 'A', "snowball", 'B', "bowIron"));
        GameRegistry.addRecipe(new ShapedOreRecipe(GoldBow, " AC", "ABC", " AC", 'C', "string", 'A', "ingotGold", 'B', "bow"));
        GameRegistry.addRecipe(new ShapedOreRecipe(IronBow, " AC", "ABC", " AC", 'C', "string", 'A', "ingotIron", 'B', "bow"));
        GameRegistry.addRecipe(new ShapedOreRecipe(MultiBow, " DC", "A C", " DC", 'C', "string", 'A', "ingotIron", 'D', "bowIron"));
        GameRegistry.addRecipe(new ShapedOreRecipe(StoneBow, " DC", "ABC", " DC", 'A', "stickWood", 'C', "string", 'D', "stone", 'B', "bow"));
        /* Hack-ish - Registers groups of items to the OreDictionary that Forge doesn't by default. I'm pretty sure that nothing bad happens if other mods duplicate entries to it. */
        OreDictionary.registerOre("bow", new ItemStack(DiamondBow, 1, OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre("bow", new ItemStack(EnderBow, 1, OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre("bow", new ItemStack(FlameBow, 1, OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre("bow", new ItemStack(FrostBow, 1, OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre("bow", new ItemStack(GoldBow, 1, OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre("bow", new ItemStack(IronBow, 1, OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre("bow", new ItemStack(Items.bow, 1, OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre("bow", new ItemStack(MultiBow, 1, OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre("bow", new ItemStack(StoneBow, 1, OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre("bowDiamond", new ItemStack(DiamondBow, 1, OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre("bowGold", new ItemStack(GoldBow, 1, OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre("bowIron", new ItemStack(IronBow, 1, OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre("ice", Blocks.ice);
        OreDictionary.registerOre("ice", Blocks.packed_ice);
        OreDictionary.registerOre("netherrack", Blocks.netherrack);
        OreDictionary.registerOre("pearlEnder", Items.ender_pearl);
        OreDictionary.registerOre("pearlEnderEye", Items.ender_eye);
        OreDictionary.registerOre("rodBlaze", Items.blaze_rod);
        OreDictionary.registerOre("snowball", Items.snowball);
        OreDictionary.registerOre("string", Items.string);
        /* Entity registration */
        EntityRegistry.registerModEntity(ArrowSpawner.class, "ArrowSpawner", 1, inst, /** As the player can never see an ArrowSpawner and all the logic for it is handled server-side, there's no reason to send any tracking updates. */ -1, Integer.MAX_VALUE, false);
        EntityRegistry.registerModEntity(CustomArrow.class, "CustomArrow", 2, inst, 64, 20, true);
    }

}
