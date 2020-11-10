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

/* If you're reading this, I'm very sorry you have to deal with my code. */
@Mod(modid = MoreBows.MOD_ID, useMetadata = true /* Forge likes to complain if there isn't something assigned to the "version" property when loading. It should get overwritten by the actual version in the mcmod.info file. */)
public class MoreBows {
    /* Mod specific reusable values */
    protected static final String MOD_ID = "MoreBows";
    private static final String modSeperator = "morebows:";

    private static final Random rand = new Random();

    /* Mod instance */
    @Instance(MOD_ID)
    private static MoreBows inst;

    /* Mod proxy. TODO This is super janky, see if it's possible to remove this */
    @SidedProxy(clientSide = "iDiamondhunter.morebows.Client", serverSide = "iDiamondhunter.morebows.MoreBows")
    private static MoreBows proxy;

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
    private static final byte[] defaultIconTimes = {18, 13};
    private static final double defaultDamageMult = 1D;
    private static final int defaultFlameTime = 100;
    //private static final float defaultPowerDiv = 20.0F;
    private static final float defaultVelocityMult = 2.0F;
    private static final ArrowType defaultArrowType = ArrowType.NOT_CUSTOM;

    /* Bow items. TODO: This is super janky. */
    protected static final Item DiamondBow = new CustomBow(1016, EnumRarity.rare, new byte[] {8, 4}, 2.2F, 6F, 140, 2.25D, defaultArrowType).setUnlocalizedName(DiamondBowName).setTextureName(modSeperator + DiamondBowName);
    protected static final Item GoldBow = new CustomBow(68, EnumRarity.uncommon, new byte[] {8, 4}, 2.4F, 5F, 100, 1.5D, defaultArrowType).setUnlocalizedName(GoldBowName).setTextureName(modSeperator + GoldBowName);
    protected static final Item EnderBow = new EnderBow().setUnlocalizedName(EnderBowName).setTextureName(modSeperator + EnderBowName);
    protected static final Item StoneBow = new CustomBow(484, EnumRarity.common, defaultIconTimes, defaultVelocityMult, 17F, defaultFlameTime, 1.15D, defaultArrowType).setUnlocalizedName(StoneBowName).setTextureName(modSeperator + StoneBowName);
    protected static final Item IronBow = new CustomBow(550, EnumRarity.common, new byte[] {16, 11}, 2.1F, 17F, 105, 1.5D, defaultArrowType).setUnlocalizedName(IronBowName).setTextureName(modSeperator + IronBowName);
    protected static final Item MultiBow = new MultiBow().setUnlocalizedName(MultiBowName).setTextureName(modSeperator + MultiBowName);
    protected static final Item FlameBow = new CustomBow(576, EnumRarity.uncommon, new byte[] {14, 9}, defaultVelocityMult, 15F, defaultFlameTime, 2.0D, ArrowType.FIRE).setUnlocalizedName(FlameBowName).setTextureName(modSeperator + FlameBowName);
    protected static final Item FrostBow = new CustomBow(550, EnumRarity.common, new byte[] {26, 13}, defaultVelocityMult, 26.0F, defaultFlameTime, defaultDamageMult, ArrowType.FROST).setUnlocalizedName(FrostBowName).setTextureName(modSeperator + FrostBowName);

    /** Calls the server world specific method to spawn a particle on the server. This particle will be sent to connected clients.
     *  The parameter randDisp can be set, which sets the particles position to somewhere random close to the entity.
     *  TODO Cleanup and re-evaluate, document better.
     */
    public static final void tryParticle(World world, Entity entity, String part, boolean randDisp, double velocity) {
        if (!world.isRemote) {
            final WorldServer server = (WorldServer) world;
            final int numPart = 1;
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

            server.func_147487_a(part, entity.posX, entity.posY, entity.posZ, numPart, xDisp, yDisp, zDisp, velocity);
        }
    }

    /** Handles particle effects on custom arrows hitting an entity, and adds critical damage to frost arrows.
     *  Frost arrows always return false when normal methods to get if they're critical are called. This is to hide the vanilla particle trail, so it can create its own custom one.
     *  TODO Cleanup, document better.
     */
    @SubscribeEvent
    public final void arrHit(LivingAttackEvent event) {
        if  (!event.entity.worldObj.isRemote && (event.source.getSourceOfDamage() instanceof CustomArrow)) {
            final CustomArrow arr = (CustomArrow) event.source.getSourceOfDamage();
            final ArrowType type = arr.getType();
            final String part;
            final int numPart;
            final double velocity;
            final boolean randDisp;

            switch (type) {
            case BASE:
                part = "portal";
                numPart = 3;
                randDisp = true;
                velocity = 1;
                break;

            case FIRE:
                if (arr.isBurning()) {
                    part = "flame";
                } else {
                    part = "smoke";
                }

                numPart = 5;
                randDisp = true;
                velocity = 0.05;
                break;

            case FROST:
                part = "splash";
                numPart = 1;
                randDisp = false;
                velocity = 0.01;
                break;

            default:
                part = "depthsuspend";
                numPart = 1;
                randDisp = false;
                velocity = 1;
                break;
            }

            for (int i = 0; i < numPart; i++ ) {
                tryParticle(event.entity.worldObj, event.entity, part, randDisp, velocity);
            }

            if ((type == ArrowType.FROST) && arr.getCrit()) {
                arr.setIsCritical(false);
                event.setCanceled(true);
                event.entity.attackEntityFrom(event.source, event.ammount + MoreBows.rand.nextInt((int) ((event.ammount / 2) + 2))); // Manually apply critical damage due to deliberately not exposing if an arrow is critical. See CustomArrow.
            }
        }
    }

    /** Handles custom effects from the frost arrow.
     *  TODO Cleanup?
     */
    @SubscribeEvent
    public final void arrHurt(LivingHurtEvent event) {
        if (event.source.getSourceOfDamage() instanceof CustomArrow) {
            final CustomArrow arr = (CustomArrow) event.source.getSourceOfDamage();

            if (arr.getType() == ArrowType.FROST) {
                event.entity.setInWeb(); // TODO Replace with slowness effect? This is the original behavior...
                //event.entity.extinguish(); // Potentially would be nice to have? Not in the original mod, just though it seemed right.

                if (!(event.entity instanceof EntityEnderman)) { // Vanilla arrows don't get destroyed after they've hit an Enderman
                    event.source.getSourceOfDamage().setDead();
                }
            }
        }
    }

    @EventHandler
    public final void init(FMLInitializationEvent event) {
        proxy.register();
        MinecraftForge.EVENT_BUS.register(proxy);
    }

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
        /* Entities TODO I'm not sure how this works. */
        EntityRegistry.registerModEntity(ArrowSpawner.class, "ArrowSpawner", 1, MoreBows.inst, 64, 20, true);
        EntityRegistry.registerModEntity(CustomArrow.class, "CustomArrow", 2, MoreBows.inst, 64, 20, true);
    }

}
