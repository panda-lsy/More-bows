package iDiamondhunter.morebows.compat;

import com.nyfaria.nyfsquiver.item.QuiverItem;
import com.nyfaria.nyfsquiver.ui.QuiverScreenHandler;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import net.minecraft.util.Pair;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

public class NyfsQuiversCompat {
    public static void drawFromQuiver(ItemStack itemStack, LivingEntity livingEntity ) {
        Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(livingEntity);
        if(component.isPresent() &&  !(((PlayerEntity)livingEntity).getAbilities().creativeMode || EnchantmentHelper.getLevel(Enchantments.INFINITY, itemStack) > 0)) {
            List<Pair<SlotReference, ItemStack>> allEquipped = component.get().getAllEquipped();
            for (Pair<SlotReference, ItemStack> entry : allEquipped) {
                ItemStack beep = entry.getRight();
                if (entry.getRight().getItem() instanceof QuiverItem) {
                    QuiverScreenHandler quiverContainer = new QuiverScreenHandler(0, ((PlayerEntity) livingEntity).getInventory(), beep);
                    if (!quiverContainer.getSlot(beep.getOrCreateNbt().getInt("current_slot")).getStack().isEmpty()) {
                        quiverContainer.getSlot(beep.getOrCreateNbt().getInt("current_slot")).getStack().decrement(1);
                        quiverContainer.getSlot(beep.getOrCreateNbt().getInt("current_slot")).markDirty();
                    }
                }
            }
        }
    }
}
