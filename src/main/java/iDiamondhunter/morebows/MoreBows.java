package iDiamondhunter.morebows;

import static iDiamondhunter.morebows.MoreBowsConfig.frostArrowsShouldBeCold;
import static iDiamondhunter.morebows.MoreBowsConfig.oldFrostArrowMobSlowdown;

import org.apache.logging.log4j.Logger;

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

    /** The full name of More Bows */
    static final String MOD_NAME = "More Bows Restrung";

    /** The mod ID of More Bows */
    static final String MOD_ID = "morebows";

    /** Used for naming items. */
    private static final String modSeparator = "morebows:";

    /** Mod proxy. TODO This is super janky, see if it's possible to remove this */
    @SidedProxy(clientSide = "iDiamondhunter.morebows.Client", serverSide = "iDiamondhunter.morebows.MoreBows")
    public static MoreBows proxy;

    public static Logger modLog;

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
    private static final String DiamondBowName = "diamond_bow";
    private static final String GoldBowName = "gold_bow";
    private static final String EnderBowName = "ender_bow";
    private static final String StoneBowName = "stone_bow";
    private static final String IronBowName = "iron_bow";
    private static final String MultiBowName = "multi_bow";
    private static final String FlameBowName = "flame_bow";
    private static final String FrostBowName = "frost_bow";

    /* Default values for bow construction */
    /** Default values for bow construction: the default damage multiplier. */
    private static final double noDamageMult = 1.0D;
    /** Default values for bow construction: the default power divisor (TODO document better) */
    private static final float defaultPowerDiv = 20.0F;
    /** Default values for bow construction: the default type of arrow a bow shoots. */
    private static final byte defaultArrowType = ARROW_TYPE_NOT_CUSTOM;

    /* Bow items. */
    private static final Item DiamondBow = new CustomBow(1016, defaultArrowType, 2.25D, false, 6.0F, EnumRarity.RARE).setTranslationKey(DiamondBowName).setRegistryName(modSeparator + DiamondBowName);
    private static final Item EnderBow = new CustomBow(215, ARROW_TYPE_ENDER, noDamageMult, true, 22.0F, EnumRarity.EPIC).setTranslationKey(EnderBowName).setRegistryName(modSeparator + EnderBowName);
    private static final Item FlameBow = new CustomBow(576, ARROW_TYPE_FIRE, 2.0D, false, 15.0F, EnumRarity.UNCOMMON).setTranslationKey(FlameBowName).setRegistryName(modSeparator + FlameBowName);
    private static final Item FrostBow = new CustomBow(550, ARROW_TYPE_FROST, noDamageMult, false, 26.0F, EnumRarity.COMMON).setTranslationKey(FrostBowName).setRegistryName(modSeparator + FrostBowName);
    private static final Item GoldBow = new CustomBow(68, defaultArrowType, 2.5D, false, 6.0F, EnumRarity.UNCOMMON).setTranslationKey(GoldBowName).setRegistryName(modSeparator + GoldBowName);
    private static final Item IronBow = new CustomBow(550, defaultArrowType, 1.5D, false, 17.0F, EnumRarity.COMMON).setTranslationKey(IronBowName).setRegistryName(modSeparator + IronBowName);
    private static final Item MultiBow = new CustomBow(550, ARROW_TYPE_NOT_CUSTOM, noDamageMult, true, 13.0F, EnumRarity.RARE).setTranslationKey(MultiBowName).setRegistryName(modSeparator + MultiBowName);
    private static final Item StoneBow = new CustomBow(484, defaultArrowType, 1.15D, false, defaultPowerDiv, EnumRarity.COMMON).setTranslationKey(StoneBowName).setRegistryName(modSeparator + StoneBowName);

    static final Item[] allItems = { DiamondBow, EnderBow, FlameBow, FrostBow, GoldBow, IronBow, MultiBow, StoneBow } ;

    /* EntityEntryBuilders. */
    private static final EntityEntry customArrowEntry = EntityEntryBuilder.create().entity(CustomArrow.class).id("custom_arrow", 1).name("Custom arrow").tracker(64, 20, true).build();
    private static final EntityEntry arrowSpawnerEntry = EntityEntryBuilder.create().entity(ArrowSpawner.class).id("arrow_spawner", 2).name("Arrow spawner").tracker(-1, Integer.MAX_VALUE, false).build();

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
    public static final void tryPart(World world, Entity entity, EnumParticleTypes part, boolean randDisp, double velocity) {
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
        final Entity source = event.getSource().getImmediateSource();

        if ((source instanceof CustomArrow) && (((CustomArrow) source).type == ARROW_TYPE_FROST)) {
            final EntityLivingBase living = event.getEntityLiving();

            if (frostArrowsShouldBeCold) {
                if (living instanceof EntityBlaze) {
                    event.setAmount(event.getAmount() * 3);
                }

                living.extinguish();
            }

            if (!oldFrostArrowMobSlowdown) {
                final Potion slow = Potion.getPotionFromResourceLocation("slowness");

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
    public final void init(FMLPreInitializationEvent event) {
        modLog = event.getModLog();
        proxy.register();
        MinecraftForge.EVENT_BUS.register(proxy);
    }

    /** This was once used to register anything needed both client side and server side, but it looks like it's not needed for server-side registration anymore. TODO probably remove */
    protected void register() {
        // This space left unintentionally blank?
    }

    // TODO review
    @SubscribeEvent
    public final void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        event.getRegistry().registerAll(customArrowEntry, arrowSpawnerEntry);
    }

    // TODO modify recipes involving bows?
    /*@SubscribeEvent
    public final void registerRecipes(RegistryEvent.Register<IRecipe> event) {
    }*/

    // TODO review
    @SubscribeEvent
    public final void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(allItems);

        // Apparently, you should register to the OreDictionary in this event. I don't make the rules.
        // TODO create a config setting to use WILDCARD_VALUE or not
        for (final Item item : allItems) {
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
