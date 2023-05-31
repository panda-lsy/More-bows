package iDiamondhunter.morebows;

import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import com.google.errorprone.annotations.CompileTimeConstant;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import iDiamondhunter.morebows.config.ConfigBows;
import iDiamondhunter.morebows.config.ConfigGeneral;
import iDiamondhunter.morebows.entities.ArrowSpawner;
import iDiamondhunter.morebows.entities.CustomArrow;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Rarity;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.particles.IParticleData;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/** If you're reading this, I'm very sorry you have to deal with my code. */
@Mod("morebows")
public final class MoreBows {

    /** The mod ID of More Bows. */
    @CompileTimeConstant
    public static final String MOD_ID = "morebows";

    /** The mod log. */
    public static final Logger modLog = LogManager.getLogger(MOD_ID);

    /** The loaded bow stats config settings. */
    @SuppressWarnings("NullAway.Init")
    @SuppressFBWarnings("MS_CANNOT_BE_FINAL")
    static ConfigBows configBowsInst;

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

    /*
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
            */

    private static final DeferredRegister<EntityType<?>> ENTITY = DeferredRegister.create(ForgeRegistries.ENTITIES, MOD_ID);

    public static final RegistryObject<EntityType<ArrowSpawner>> ARROW_SPAWNER = ENTITY.register("arrow_spawner", () ->
            EntityType.Builder.<ArrowSpawner>of(ArrowSpawner::new, EntityClassification.MISC)
            .sized(0.0F, 0.0F)
            .fireImmune()
            .clientTrackingRange(1)
            .setTrackingRange(1)
            .updateInterval(1)
            .build(new ResourceLocation(MOD_ID, "arrow_spawner").toString())
                                                                                                );

    public static final RegistryObject<EntityType<CustomArrow>> CUSTOM_ARROW = ENTITY.register("custom_arrow", () ->
            EntityType.Builder.<CustomArrow>of(CustomArrow::new, EntityClassification.MISC)
            .sized(0.5F, 0.5F)
            .setTrackingRange(4)
            .updateInterval(20)
            .build(new ResourceLocation(MOD_ID, "custom_arrow").toString())
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
            final Supplier<Ingredient> diamonds = () -> Ingredient.of(ItemTags.getAllTags().getTagOrEmpty(new ResourceLocation(MOD_ID, "diamonds")));
            final Supplier<Ingredient> gold = () -> Ingredient.of(ItemTags.getAllTags().getTagOrEmpty(new ResourceLocation(MOD_ID, "gold_ingots")));
            final Supplier<Ingredient> enderPearls = () -> Ingredient.of(ItemTags.getAllTags().getTagOrEmpty(new ResourceLocation("c", "ender_pearls")));
            final Supplier<Ingredient> stone = () -> Ingredient.of(ItemTags.getAllTags().getTagOrEmpty(new ResourceLocation(MOD_ID, "stone")));
            final Supplier<Ingredient> iron = () -> Ingredient.of(ItemTags.getAllTags().getTagOrEmpty(new ResourceLocation(MOD_ID, "iron_ingots")));
            final Supplier<Ingredient> ice = () -> Ingredient.of(ItemTags.getAllTags().getTagOrEmpty(new ResourceLocation(MOD_ID, "ice")));
            DiamondBow = new CustomBow(new Item.Properties().tab(ItemGroup.TAB_COMBAT).durability(configBowsInst.DiamondBow.confBowDurability).rarity(Rarity.RARE), diamonds, defaultArrowType, configBowsInst.DiamondBow.confBowDamageMult, false, configBowsInst.DiamondBow.confBowDrawbackDiv).setRegistryName(new ResourceLocation(MOD_ID, DiamondBowName));
            GoldBow = new CustomBow(new Item.Properties().tab(ItemGroup.TAB_COMBAT).durability(configBowsInst.GoldBow.confBowDurability).rarity(Rarity.UNCOMMON), gold, defaultArrowType, configBowsInst.GoldBow.confBowDamageMult, false, configBowsInst.GoldBow.confBowDrawbackDiv).setRegistryName(new ResourceLocation(MOD_ID, GoldBowName));
            EnderBow = new CustomBow(new Item.Properties().tab(ItemGroup.TAB_COMBAT).durability(configBowsInst.EnderBow.confBowDurability).rarity(Rarity.EPIC), enderPearls, ARROW_TYPE_ENDER, configBowsInst.EnderBow.confBowDamageMult, true, configBowsInst.EnderBow.confBowDrawbackDiv).setRegistryName(new ResourceLocation(MOD_ID, EnderBowName));
            StoneBow = new CustomBow(new Item.Properties().tab(ItemGroup.TAB_COMBAT).durability(configBowsInst.StoneBow.confBowDurability).rarity(Rarity.COMMON), stone, defaultArrowType, configBowsInst.StoneBow.confBowDamageMult, false, configBowsInst.StoneBow.confBowDrawbackDiv).setRegistryName(new ResourceLocation(MOD_ID, StoneBowName));
            IronBow = new CustomBow(new Item.Properties().tab(ItemGroup.TAB_COMBAT).durability(configBowsInst.IronBow.confBowDurability).rarity(Rarity.COMMON), iron, defaultArrowType, configBowsInst.IronBow.confBowDamageMult, false, configBowsInst.IronBow.confBowDrawbackDiv).setRegistryName(new ResourceLocation(MOD_ID, IronBowName));
            MultiBow = new CustomBow(new Item.Properties().tab(ItemGroup.TAB_COMBAT).durability(configBowsInst.MultiBow.confBowDurability).rarity(Rarity.RARE), iron, ARROW_TYPE_NOT_CUSTOM, configBowsInst.MultiBow.confBowDamageMult, true, configBowsInst.MultiBow.confBowDrawbackDiv).setRegistryName(new ResourceLocation(MOD_ID, MultiBowName));
            FlameBow = new CustomBow(new Item.Properties().tab(ItemGroup.TAB_COMBAT).durability(configBowsInst.FlameBow.confBowDurability).rarity(Rarity.UNCOMMON).fireResistant(), gold, ARROW_TYPE_FIRE, configBowsInst.FlameBow.confBowDamageMult, false, configBowsInst.FlameBow.confBowDrawbackDiv).setRegistryName(new ResourceLocation(MOD_ID, FlameBowName));
            FrostBow = new CustomBow(new Item.Properties().tab(ItemGroup.TAB_COMBAT).durability(configBowsInst.FrostBow.confBowDurability).rarity(Rarity.COMMON), ice, ARROW_TYPE_FROST, configBowsInst.FrostBow.confBowDamageMult, false, configBowsInst.FrostBow.confBowDrawbackDiv).setRegistryName(new ResourceLocation(MOD_ID, FrostBowName));
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
     * Common mod initialization code.
     */
    public MoreBows() {
        readConfigs();
        //MinecraftForge.EVENT_BUS.register(this);
        //FMLJavaModLoadingContext.get().getModEventBus().register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, this::registerItems);
        ENTITY.register(FMLJavaModLoadingContext.get().getModEventBus());

        if (!configGeneralInst.nyfsQuiversCompatEnabled) {
            modLog.warn("You have disabled Nyf's Quivers compatibility features. Please don't do this unless More Bows is incompatible with the current version of Nyf's Quivers, as issues with arrows being duplicated happen otherwise.");
        }
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        modLog.info("item register");
        event.getRegistry().registerAll(getAllItems());
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
    public static <T extends IParticleData> void tryPart(World world, Entity entity, T part, boolean randDisp, double velocity) {
        if (!world.isClientSide) {
            // final int amount = 1;
            final double xDisp;
            final double yDisp;
            final double zDisp;

            if (randDisp) {
                xDisp = (world.random.nextFloat() * entity.getBbWidth() * 2.0F) - entity.getBbWidth();
                yDisp = 0.5 + (world.random.nextFloat() * entity.getBbHeight());
                zDisp = (world.random.nextFloat() * entity.getBbWidth() * 2.0F) - entity.getBbWidth();
            } else {
                xDisp = 0.0;
                yDisp = 0.5;
                zDisp = 0.0;
            }

            ((ServerWorld) world).sendParticles(part, entity.getX(), entity.getY(), entity.getZ(), 1, xDisp, yDisp, zDisp, velocity);
        }
    }

}
