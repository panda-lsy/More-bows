package iDiamondhunter.morebows.bows;

import iDiamondhunter.morebows.entities.CustomArrow;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class FlameBow extends CustomBow {
    public FlameBow() {
        super(576, (byte) 0, new byte[] {14, 9}, 15F, 2.0D);
    }

    @Override
    public void addModifiers(World world, ItemStack stack, Boolean noPickup) {
        super.addModifiers(world, stack, noPickup);
        /* TODO this is a bit sus, I think it's the right original behavior though */
        final boolean flameEnch = (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0);

        for (final EntityArrow arr : arrows) {
            if (flameEnch) {
                arr.setDamage(arr.getDamage() * 1.25D);
            }

            arr.setFire(50);
        }
    }

    /* TODO Remove somehow */
    @Override
    public void setArrows(World world, EntityPlayer player) {
        arrows = new EntityArrow[] { new CustomArrow(world, player, shotVelocity * 2.0F, true) };
    }

}
