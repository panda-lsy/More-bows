package iDiamondhunter.morebows;

import java.util.Random;

import iDiamondhunter.morebows.bows.CustomBow;
import iDiamondhunter.morebows.bows.EnderBow;
import iDiamondhunter.morebows.bows.MultiBow;
import iDiamondhunter.morebows.entities.ArrowSpawner;
import iDiamondhunter.morebows.entities.CustomArrow;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.EndermanEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.impl.client.rendering.BuiltinItemRendererRegistryImpl;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/** If you're reading this, I'm very sorry you have to deal with my code. */
public class MoreBows implements ModInitializer {

    /** The mod ID of More Bows */
    public static final String MOD_ID = "morebows";

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
    private static final String DiamondBowName = "diamond_bow";
    private static final String GoldBowName = "gold_bow";
    private static final String EnderBowName = "ender_bow";
    private static final String StoneBowName = "stone_bow";
    private static final String IronBowName = "iron_bow";
    private static final String MultiBowName = "multi_bow";
    private static final String FlameBowName = "flame_bow";
    private static final String FrostBowName = "frost_bow";

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
    public static final CustomBow DiamondBow = new CustomBow(new FabricItemSettings().group(ItemGroup.COMBAT).maxDamage(1016).rarity(Rarity.RARE), 2.2F, 6F, 140, 2.25D, defaultArrowType);
    public static final CustomBow GoldBow = new CustomBow(new FabricItemSettings().group(ItemGroup.COMBAT).maxDamage(68).rarity(Rarity.UNCOMMON), 2.4F, 5F, 100, 1.5D, defaultArrowType);
    public static final CustomBow EnderBow = new EnderBow(new FabricItemSettings().group(ItemGroup.COMBAT).maxDamage(215).rarity(Rarity.EPIC));
    public static final CustomBow StoneBow = new CustomBow(new FabricItemSettings().group(ItemGroup.COMBAT).maxDamage(484).rarity(Rarity.COMMON), defaultVelocityMult, 17F, defaultFlameTime, 1.15D, defaultArrowType);
    public static final CustomBow IronBow = new CustomBow(new FabricItemSettings().group(ItemGroup.COMBAT).maxDamage(550).rarity(Rarity.COMMON), 2.1F, 17F, 105, 1.5D, defaultArrowType);
    public static final CustomBow MultiBow = new MultiBow(new FabricItemSettings().group(ItemGroup.COMBAT).maxDamage(550).rarity(Rarity.RARE));
    public static final CustomBow FlameBow = new CustomBow(new FabricItemSettings().group(ItemGroup.COMBAT).maxDamage(576).rarity(Rarity.UNCOMMON), defaultVelocityMult, 15F, defaultFlameTime, 2.0D, ARROW_TYPE_FIRE);
    public static final CustomBow FrostBow = new CustomBow(new FabricItemSettings().group(ItemGroup.COMBAT).maxDamage(550).rarity(Rarity.COMMON), defaultVelocityMult, 26.0F, defaultFlameTime, defaultDamageMult, ARROW_TYPE_FROST);

    /* TODO put this somewhere else? */
    private static final Random rand = new Random();

    public void onInitialize() {
        registerBow(DiamondBow, DiamondBowName);
        registerBow(GoldBow, GoldBowName);
        registerBow(EnderBow, EnderBowName);
        registerBow(StoneBow, StoneBowName);
        registerBow(IronBow, IronBowName);
        registerBow(MultiBow, MultiBowName);
        registerBow(FlameBow, FlameBowName);
        registerBow(FrostBow, FrostBowName);
    }

    public void registerBow (CustomBow bow, String name) {
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, name), bow);
        FabricModelPredicateProviderRegistry.register(bow, new Identifier("pull"), (itemStack, clientWorld, livingEntity) -> {
            if (livingEntity == null) {
                return 0.0F;
            } else {
                return livingEntity.getActiveItem() != itemStack ? 0.0F : (float)(itemStack.getMaxUseTime() - livingEntity.getItemUseTimeLeft()) / 20.0F;
            }
        });
        FabricModelPredicateProviderRegistry.register(bow, new Identifier("pulling"), (itemStack, clientWorld, livingEntity) -> {
            return livingEntity != null && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F;
        });
    }

}
