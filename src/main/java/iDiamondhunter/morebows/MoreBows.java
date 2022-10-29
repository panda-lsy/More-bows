package iDiamondhunter.morebows;

import static iDiamondhunter.morebows.config.ConfigGeneral.frostArrowsShouldBeCold;
import static iDiamondhunter.morebows.config.ConfigGeneral.oldFrostArrowMobSlowdown;

import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import iDiamondhunter.morebows.config.ConfigBows;
import iDiamondhunter.morebows.entities.ArrowSpawner;
import iDiamondhunter.morebows.entities.CustomArrow;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.oredict.OreDictionary;

/** If you're reading this, I'm very sorry you have to deal with my code. */
@Mod(modid = MoreBows.MOD_ID, version = "${version}", updateJSON = "https://nerdthened.github.io/More-bows/update.json")
public class MoreBows {

    /** The mod ID of More Bows. */
    public static final String MOD_ID = "morebows";

    /** Used for naming items. */
    private static final String modSeparator = "morebows:";

    /** Mod proxy. TODO This is super janky, see if it's possible to remove this */
    @SidedProxy(clientSide = "iDiamondhunter.morebows.Client", serverSide = "iDiamondhunter.morebows.MoreBows")
    @SuppressWarnings("NullAway.Init")
    private static MoreBows proxy;

    /** The mod log. */
    @SuppressWarnings("NullAway.Init")
    @SuppressFBWarnings(value = "MS_CANNOT_BE_FINAL")
    public static Logger modLog;

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
    private static final String DiamondBowName = "diamond_bow";
    private static final String GoldBowName = "gold_bow";
    private static final String EnderBowName = "ender_bow";
    private static final String StoneBowName = "stone_bow";
    private static final String IronBowName = "iron_bow";
    private static final String MultiBowName = "multi_bow";
    private static final String FlameBowName = "flame_bow";
    private static final String FrostBowName = "frost_bow";

    /* Translation keys for bows. */
    public static final String DiamondBowTransKey = MOD_ID + "." + DiamondBowName;
    public static final String GoldBowTransKey = MOD_ID + "." + GoldBowName;
    public static final String EnderBowTransKey = MOD_ID + "." + EnderBowName;
    public static final String StoneBowTransKey = MOD_ID + "." + StoneBowName;
    public static final String IronBowTransKey = MOD_ID + "." + IronBowName;
    public static final String MultiBowTransKey = MOD_ID + "." + MultiBowName;
    public static final String FlameBowTransKey = MOD_ID + "." + FlameBowName;
    public static final String FrostBowTransKey = MOD_ID + "." + FrostBowName;

    /* Default values for bow construction */

    /** Default values for bow construction: the default type of arrow a bow shoots. */
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

    /* EntityEntryBuilders. */
    private static final @NotNull EntityEntry customArrowEntry = EntityEntryBuilder.create().entity(CustomArrow.class).id("custom_arrow", 1).name("Custom arrow").tracker(64, 20, true).build();
    private static final @NotNull EntityEntry arrowSpawnerEntry = EntityEntryBuilder.create().entity(ArrowSpawner.class).id("arrow_spawner", 2).name("Arrow spawner").tracker(-1, Integer.MAX_VALUE, false).build();

    /**
     * Constructs the array of all bow items if it hasn't been constructed,
     * and returns it. This is a hack to construct the items
     * after the config values have been loaded.
     * TODO Make less hacky.
     *
     * @return an array of all bow items
     */
    static Item[] getAllItems() {
        if (allItems == null) {
            DiamondBow = new CustomBow(ConfigBows.DiamondBow.confBowDurability, defaultArrowType, ConfigBows.DiamondBow.confBowDamageMult, false, ConfigBows.DiamondBow.confBowDrawbackDiv, EnumRarity.RARE).setTranslationKey(DiamondBowTransKey).setRegistryName(modSeparator + DiamondBowName);
            EnderBow = new CustomBow(ConfigBows.EnderBow.confBowDurability, ARROW_TYPE_ENDER, ConfigBows.EnderBow.confBowDamageMult, true, ConfigBows.EnderBow.confBowDrawbackDiv, EnumRarity.EPIC).setTranslationKey(EnderBowTransKey).setRegistryName(modSeparator + EnderBowName);
            FlameBow = new CustomBow(ConfigBows.FlameBow.confBowDurability, ARROW_TYPE_FIRE, ConfigBows.FlameBow.confBowDamageMult, false, ConfigBows.FlameBow.confBowDrawbackDiv, EnumRarity.UNCOMMON).setTranslationKey(FlameBowTransKey).setRegistryName(modSeparator + FlameBowName);
            FrostBow = new CustomBow(ConfigBows.FrostBow.confBowDurability, ARROW_TYPE_FROST, ConfigBows.FrostBow.confBowDamageMult, false, ConfigBows.FrostBow.confBowDrawbackDiv, EnumRarity.COMMON).setTranslationKey(FrostBowTransKey).setRegistryName(modSeparator + FrostBowName);
            GoldBow = new CustomBow(ConfigBows.GoldBow.confBowDurability, defaultArrowType, ConfigBows.GoldBow.confBowDamageMult, false, ConfigBows.GoldBow.confBowDrawbackDiv, EnumRarity.UNCOMMON).setTranslationKey(GoldBowTransKey).setRegistryName(modSeparator + GoldBowName);
            IronBow = new CustomBow(ConfigBows.IronBow.confBowDurability, defaultArrowType, ConfigBows.IronBow.confBowDamageMult, false, ConfigBows.IronBow.confBowDrawbackDiv, EnumRarity.COMMON).setTranslationKey(IronBowTransKey).setRegistryName(modSeparator + IronBowName);
            MultiBow = new CustomBow(ConfigBows.MultiBow.confBowDurability, ARROW_TYPE_NOT_CUSTOM, ConfigBows.MultiBow.confBowDamageMult, true, ConfigBows.MultiBow.confBowDrawbackDiv, EnumRarity.RARE).setTranslationKey(MultiBowTransKey).setRegistryName(modSeparator + MultiBowName);
            StoneBow = new CustomBow(ConfigBows.StoneBow.confBowDurability, defaultArrowType, ConfigBows.StoneBow.confBowDamageMult, false, ConfigBows.StoneBow.confBowDrawbackDiv, EnumRarity.COMMON).setTranslationKey(StoneBowTransKey).setRegistryName(modSeparator + StoneBowName);
            allItems = new Item[] { DiamondBow, EnderBow, FlameBow, FrostBow, GoldBow, IronBow, MultiBow, StoneBow };
        }

        return allItems;
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
     * @param world    The world to attempt to spawn a particle in.
     * @param entity   The entity to spawn the particle at.
     * @param part     The particle type to spawn.
     * @param randDisp If this is true, particles will be randomly distributed
     *                 around the entity.
     * @param velocity The velocity of spawned particles.
     */
    public static void tryPart(World world, Entity entity, EnumParticleTypes part, boolean randDisp, double velocity) {
        if (!world.isRemote) {
            // final int amount = 1;
            final double xDisp;
            final double yDisp;
            final double zDisp;

            if (randDisp) {
                xDisp = (world.rand.nextFloat() * entity.width * 2.0F) - entity.width;
                yDisp = 0.5 + (world.rand.nextFloat() * entity.height);
                zDisp = (world.rand.nextFloat() * entity.width * 2.0F) - entity.width;
            } else {
                xDisp = 0.0;
                yDisp = 0.5;
                zDisp = 0.0;
            }

            ((WorldServer) world).spawnParticle(part, entity.posX, entity.posY, entity.posZ, 1, xDisp, yDisp, zDisp, velocity);
        }
    }

    /**
     * Handles custom effects from the frost arrow.
     * TODO see if this can be done inside CustomArrow
     *
     * @param event the event
     */
    @SubscribeEvent
    public final void arrHurt(LivingHurtEvent event) {
        final @Nullable Entity source = event.getSource().getImmediateSource();

        if ((source instanceof CustomArrow) && (((CustomArrow) source).type == ARROW_TYPE_FROST)) {
            final EntityLivingBase living = event.getEntityLiving();

            if (frostArrowsShouldBeCold) {
                if (living instanceof EntityBlaze) {
                    event.setAmount(event.getAmount() * 3.0F);
                }

                living.extinguish();
            }

            if (!oldFrostArrowMobSlowdown) {
                final @Nullable Potion slow = Potion.getPotionFromResourceLocation("slowness");

                if (slow != null) {
                    living.addPotionEffect(new PotionEffect(slow, 300, 2));
                }
            } else {
                living.setInWeb();
            }
        }
    }

    /**
     * This method handles setting up the mod.
     *
     * @param event the event
     */
    @EventHandler
    @SuppressFBWarnings(value = "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
    public final void init(FMLPreInitializationEvent event) {
        modLog = event.getModLog();
        proxy.register();
        MinecraftForge.EVENT_BUS.register(proxy);
    }

    /**
     * This was once used to register anything needed
     * both client side and server side,
     * but it looks like it's not needed for server-side registration anymore.
     * TODO probably remove
     */
    void register() {
        // This space left unintentionally blank?
    }

    /**
     * Register entities.
     * TODO review
     *
     * @param event the RegistryEvent
     */
    @SubscribeEvent
    public final void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        event.getRegistry().registerAll(customArrowEntry, arrowSpawnerEntry);
    }

    /**
     * Register items and OreDictionary entries.
     * TODO review
     *
     * @param event the RegistryEvent
     */
    @SubscribeEvent
    public final void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(getAllItems());

        /*
         * Apparently, you should register to the OreDictionary in this event.
         * I don't make the rules.
         * TODO create a config setting to use WILDCARD_VALUE or not
         */
        for (final Item item : getAllItems()) {
            OreDictionary.registerOre("bow", new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE));
        }

        // Register the Vanilla bow
        OreDictionary.registerOre("bow", new ItemStack(Items.BOW, 1, OreDictionary.WILDCARD_VALUE));
        // Register to hypothetically useful ore names
        OreDictionary.registerOre("bowDiamond", new ItemStack(DiamondBow, 1, OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre("bowGold", new ItemStack(GoldBow, 1, OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre("bowIron", new ItemStack(IronBow, 1, OreDictionary.WILDCARD_VALUE));
    }

}
