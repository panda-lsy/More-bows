package iDiamondhunter.morebows.compat;

import java.util.Optional;

import com.nyfaria.nyfsquiver.item.QuiverItem;
import com.nyfaria.nyfsquiver.ui.QuiverScreenHandler;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Pair;

/** This is required for Nyf's Quivers compatibility, as arrows are not removed from the quiver without this. */
public class NyfsQuiversCompat {
    public static void drawFromQuiver(PlayerEntity player, int amount) {
        final Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);

        if (component.isPresent()) {
            for (final Pair<SlotReference, ItemStack> equipped : component.get().getAllEquipped()) {
                final ItemStack equippedTrinketItemStack = equipped.getRight();

                if (equippedTrinketItemStack.getItem() instanceof QuiverItem) {
                    final QuiverScreenHandler quiverContainer = new QuiverScreenHandler(0, player.getInventory(), equippedTrinketItemStack);
                    final Slot slot = quiverContainer.getSlot(equippedTrinketItemStack.getOrCreateNbt().getInt("current_slot"));
                    final ItemStack arrowStack = slot.getStack();

                    if (!arrowStack.isEmpty()) {
                        arrowStack.decrement(amount);
                        slot.markDirty();
                    }
                }
            }
        }
    }
}
