package iDiamondhunter.morebows.bows;

import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_NOT_CUSTOM;
import static iDiamondhunter.morebows.MoreBows.defaultFlameTime;
import static iDiamondhunter.morebows.MoreBows.defaultVelocityMult;

import net.minecraft.item.Item.Settings;
import net.minecraft.world.World;

/** TODO Merge into CustomBow? */
public final class MultiBow extends CustomBow {

    /**
     * The constructor for the MultiBow calls the super constructor.
     */
    public MultiBow(Settings settings) {
        super(settings, defaultVelocityMult, 13F, defaultFlameTime, 1D, ARROW_TYPE_NOT_CUSTOM);
    }

}
