package iDiamondhunter.morebows;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.errorprone.annotations.CompileTimeConstant;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import iDiamondhunter.morebows.config.ConfigBows;
import iDiamondhunter.morebows.config.ConfigGeneral;
import iDiamondhunter.morebows.entities.ArrowSpawner;
import iDiamondhunter.morebows.entities.CustomArrow;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

/** If you're reading this, I'm very sorry you have to deal with my code. */
public final class MoreBows implements ModInitializer {

    /** The mod ID of More Bows. */
    @CompileTimeConstant
    public static final String MOD_ID = "morebows";

    /** The mod log. */
    public static final Logger modLog = LoggerFactory.getLogger(MOD_ID);

    /** The loaded bow stats config settings. */
    @SuppressWarnings("NullAway.Init")
    @SuppressFBWarnings("MS_CANNOT_BE_FINAL")
    public static ConfigBows configBowsInst;

    /** The loaded general config settings. */
    @SuppressWarnings("NullAway.Init")
    @SuppressFBWarnings("MS_CANNOT_BE_FINAL")
    public static ConfigGeneral configGeneralInst;

    /*
     * Hardcoded magic numbers, because Enums (as they are classes)
     * require a large amount of file space,
     * and I'm targeting 64kb as the compiled .jar size.
     * I'm really sorry for doing this.
     */
    /**
     * Arrow type:
     * An arrow that is not a custom arrow.
     */
    @CompileTimeConstant
    public static final byte ARROW_TYPE_NOT_CUSTOM = 0;
    /**
     * Arrow type:
     * A "particle arrow", used by the ender bow.
     */
    @CompileTimeConstant
    public static final byte ARROW_TYPE_ENDER = 1;
    /**
     * Arrow type:
     * A fire arrow, used by the fire bow.
     */
    @CompileTimeConstant
    public static final byte ARROW_TYPE_FIRE = 2;
    /**
     * Arrow type:
     * A frost arrow, used by the frost bow.
     */
    @CompileTimeConstant
    public static final byte ARROW_TYPE_FROST = 3;

    /* Names of bows. */
    @CompileTimeConstant
    public static final String DiamondBowName = "diamond_bow";
    @CompileTimeConstant
    public static final String GoldBowName = "gold_bow";
    @CompileTimeConstant
    public static final String EnderBowName = "ender_bow";
    @CompileTimeConstant
    public static final String StoneBowName = "stone_bow";
    @CompileTimeConstant
    public static final String IronBowName = "iron_bow";
    @CompileTimeConstant
    public static final String MultiBowName = "multi_bow";
    @CompileTimeConstant
    public static final String FlameBowName = "flame_bow";
    @CompileTimeConstant
    public static final String FrostBowName = "frost_bow";

    /* Default values for bow construction */

    /** Default values for bow construction: the default type of arrow a bow shoots. */
    @CompileTimeConstant
    private static final byte defaultArrowType = ARROW_TYPE_NOT_CUSTOM;

    /*
     * Bow items.
     * Avoid using directly, try to use getAllItems() instead.
     */
    @SuppressWarnings("NullAway.Init")
    private static Item DiamondBow;
    @SuppressWarnings("NullAway.Init")
    private static Item EnderBow;
    @SuppressWarnings("NullAway.Init")
    private static Item FlameBow;
    @SuppressWarnings("NullAway.Init")
    private static Item FrostBow;
    @SuppressWarnings("NullAway.Init")
    private static Item GoldBow;
    @SuppressWarnings("NullAway.Init")
    private static Item IronBow;
    @SuppressWarnings("NullAway.Init")
    private static Item MultiBow;
    @SuppressWarnings("NullAway.Init")
    private static Item StoneBow;

    /**
     * An array of all bow items.
     * Avoid using directly, try to use getAllItems() instead.
     */
    @SuppressWarnings("NullAway.Init")
    private static Item[] allItems;

    public static final EntityType<ArrowSpawner> ARROW_SPAWNER = Registry.register(
                Registry.ENTITY_TYPE,
                new Identifier(MOD_ID, "arrow_spawner"),
                FabricEntityTypeBuilder.<ArrowSpawner>create(SpawnGroup.MISC, ArrowSpawner::new).dimensions(EntityDimensions.fixed(0.0F, 0.0F)).fireImmune().trackRangeChunks(1).trackedUpdateRate(1).build()
            );

    public static final EntityType<CustomArrow> CUSTOM_ARROW = Registry.register(
                Registry.ENTITY_TYPE,
                new Identifier(MOD_ID, "custom_arrow"),
                FabricEntityTypeBuilder.<CustomArrow>create(SpawnGroup.MISC, CustomArrow::new).dimensions(EntityDimensions.fixed(0.5F, 0.5F)).trackRangeChunks(4).trackedUpdateRate(20).build()
            );

    /**
     * Constructs the array of all bow items if it hasn't been constructed,
     * and returns it. This is a hack to construct the items
     * after the config values have been loaded.
     * TODO Make less hacky.
     *
     * @return an array of all bow items
     */
    static @NotNull Item @NotNull [] getAllItems() {
        if (allItems == null) {
            DiamondBow = new CustomBow(new FabricItemSettings().group(ItemGroup.COMBAT).maxDamage(configBowsInst.DiamondBow.confBowDurability).rarity(Rarity.RARE), defaultArrowType, configBowsInst.DiamondBow.confBowDamageMult, false, configBowsInst.DiamondBow.confBowDrawbackDiv);
            GoldBow = new CustomBow(new FabricItemSettings().group(ItemGroup.COMBAT).maxDamage(configBowsInst.GoldBow.confBowDurability).rarity(Rarity.UNCOMMON), defaultArrowType, configBowsInst.GoldBow.confBowDamageMult, false, configBowsInst.GoldBow.confBowDrawbackDiv);
            EnderBow = new CustomBow(new FabricItemSettings().group(ItemGroup.COMBAT).maxDamage(configBowsInst.EnderBow.confBowDurability).rarity(Rarity.EPIC), ARROW_TYPE_ENDER, configBowsInst.EnderBow.confBowDamageMult, true, configBowsInst.EnderBow.confBowDrawbackDiv);
            StoneBow = new CustomBow(new FabricItemSettings().group(ItemGroup.COMBAT).maxDamage(configBowsInst.StoneBow.confBowDurability).rarity(Rarity.COMMON), defaultArrowType, configBowsInst.StoneBow.confBowDamageMult, false, configBowsInst.StoneBow.confBowDrawbackDiv);
            IronBow = new CustomBow(new FabricItemSettings().group(ItemGroup.COMBAT).maxDamage(configBowsInst.IronBow.confBowDurability).rarity(Rarity.COMMON), defaultArrowType, configBowsInst.IronBow.confBowDamageMult, false, configBowsInst.IronBow.confBowDrawbackDiv);
            MultiBow = new CustomBow(new FabricItemSettings().group(ItemGroup.COMBAT).maxDamage(configBowsInst.MultiBow.confBowDurability).rarity(Rarity.RARE), ARROW_TYPE_NOT_CUSTOM, configBowsInst.MultiBow.confBowDamageMult, true, configBowsInst.MultiBow.confBowDrawbackDiv);
            FlameBow = new CustomBow(new FabricItemSettings().group(ItemGroup.COMBAT).maxDamage(configBowsInst.FlameBow.confBowDurability).rarity(Rarity.UNCOMMON).fireproof(), ARROW_TYPE_FIRE, configBowsInst.FlameBow.confBowDamageMult, false, configBowsInst.FlameBow.confBowDrawbackDiv);
            FrostBow = new CustomBow(new FabricItemSettings().group(ItemGroup.COMBAT).maxDamage(configBowsInst.FrostBow.confBowDurability).rarity(Rarity.COMMON), ARROW_TYPE_FROST, configBowsInst.FrostBow.confBowDamageMult, false, configBowsInst.FrostBow.confBowDrawbackDiv);
            allItems = new Item[] { DiamondBow, EnderBow, FlameBow, FrostBow, GoldBow, IronBow, MultiBow, StoneBow };
        }

        return allItems;
    }

    /**
     * Reads the mod configs.
     */
    private static void readConfigs() {
        configBowsInst = ConfigBows.readConfig();
        configGeneralInst = ConfigGeneral.readConfig();
    }

    /**
     * Registers a bow.
     *
     * @param bow  the bow
     * @param name the name of the bow
     */
    private static void registerBow(Item bow, String name) {
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, name), bow);
    }

    /**
     * This method attempts to spawn a particle on the server world.
     * It first checks if the provided world is a client or a server.
     * If it is a client world, it doesn't do anything.
     * If it is a server world, it calls the server world specific method
     * to spawn a particle on the server.
     * This particle will be sent to connected clients.
     * The parameter randDisp can be set, which sets the particles position
     * to somewhere random close to the entity.
     *
     * @param <T>      Particle effect generic type.
     * @param world    The world to attempt to spawn a particle in.
     * @param entity   The entity to spawn the particle at.
     * @param part     The particle type to spawn.
     * @param randDisp If this is true, particles will be randomly distributed
     *                 around the entity.
     * @param velocity The velocity of spawned particles.
     */
    public static <T extends ParticleEffect> void tryPart(World world, Entity entity, T part, boolean randDisp, double velocity) {
        if (!world.isClient) {
            // final int amount = 1;
            final double xDisp;
            final double yDisp;
            final double zDisp;

            if (randDisp) {
                xDisp = (world.random.nextFloat() * entity.getWidth() * 2.0F) - entity.getWidth();
                yDisp = 0.5 + (world.random.nextFloat() * entity.getHeight());
                zDisp = (world.random.nextFloat() * entity.getWidth() * 2.0F) - entity.getWidth();
            } else {
                xDisp = 0.0;
                yDisp = 0.5;
                zDisp = 0.0;
            }

            ((ServerWorld) world).spawnParticles(part, entity.getX(), entity.getY(), entity.getZ(), 1, xDisp, yDisp, zDisp, velocity);
        }
    }

    /**
     * Common mod initialization code.
     */
    @Override
    public void onInitialize() {
        // TODO better code
        readConfigs();
        getAllItems();
        registerBow(DiamondBow, DiamondBowName);
        registerBow(GoldBow, GoldBowName);
        registerBow(EnderBow, EnderBowName);
        registerBow(StoneBow, StoneBowName);
        registerBow(IronBow, IronBowName);
        registerBow(MultiBow, MultiBowName);
        registerBow(FlameBow, FlameBowName);
        registerBow(FrostBow, FrostBowName);

        if (!MoreBows.configGeneralInst.nyfsQuiversCompatEnabled) {
            modLog.warn("You have disabled Nyf's Quivers compatibility features. Please don't do this unless More Bows is incompatible with the current version of Nyf's Quivers, as issues with arrows being duplicated happen otherwise.");
        }

        // TODO fuel burn times?
    }

}
