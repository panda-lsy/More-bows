package iDiamondhunter.morebows.compat;

import net.minecraft.entity.player.PlayerEntity;

/** This is required for Nyf's Quivers compatibility, as arrows are not removed from the quiver without this. */
public final class NyfsQuiversCompat {
    private NyfsQuiversCompat () {
        // Empty private constructor to hide default constructor
    }

    /**
     * Remove the given amount of arrows from a quiver
     *
     * @param player the player to remove the arrows from
     * @param amount the amount of arrows to remove
     */
    public static void drawFromQuiver(PlayerEntity player, int amount) {
        /*
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
        }*/
    }
}
