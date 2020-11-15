package iDiamondhunter.morebows.bows;

import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_BASE;
import static iDiamondhunter.morebows.MoreBows.defaultFlameTime;
import static iDiamondhunter.morebows.MoreBows.defaultVelocityMult;

import iDiamondhunter.morebows.MoreBows;
import iDiamondhunter.morebows.entities.ArrowSpawner;
import iDiamondhunter.morebows.entities.CustomArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Settings;
import net.minecraft.world.World;

/** TODO Merge into CustomBow? */
public final class EnderBow extends CustomBow {

    /**
     * The constructor for the EnderBow calls the super constructor.
     */
    public EnderBow(Settings settings) {
        super(settings, defaultVelocityMult, 22F, defaultFlameTime, 1D, ARROW_TYPE_BASE);
    }

}
