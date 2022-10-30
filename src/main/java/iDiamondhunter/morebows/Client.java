package iDiamondhunter.morebows;

import org.jetbrains.annotations.Nullable;

import iDiamondhunter.morebows.entities.CustomArrow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Handles general client side only code. Client is also the client proxy.
 * - Client event handling
 * - Client rendering registration
 * - Even implements IRenderFactory!
 */
public final class Client extends MoreBows implements IRenderFactory<CustomArrow> {

    @SubscribeEvent
    public void confChange(OnConfigChangedEvent event) {
        if (MOD_ID.equals(event.getModID())) {
            ConfigManager.sync(MOD_ID, Config.Type.INSTANCE);
        }
    }

    @Override
    public Render<CustomArrow> createRenderFor(RenderManager manager) {
        return new ModRenderer(manager);
    }

    /**
     * Handles the FOV "zoom in" when drawing a custom bow.
     *
     * @param event the FOVUpdateEvent
     */
    @SubscribeEvent
    public void FOV(FOVUpdateEvent event) {
        final EntityPlayer eventPlayer = event.getEntity();
        final Item eventItem = eventPlayer.getActiveItemStack().getItem();

        if (eventItem instanceof CustomBow) {
            float finalFov = event.getFov();
            final float itemUseCount = bowMaxUseDuration - eventPlayer.getItemInUseCount();
            /*
             * First, we have to reverse the standard bow zoom.
             * Minecraft helpfully applies the standard bow zoom
             * to any item that is an instance of a ItemBow.
             * However, our CustomBows draw back at different speeds,
             * so the standard zoom is not at the right speed.
             * To compensate for this, we just calculate the standard bow zoom,
             * and apply it in reverse.
             */
            float realBow = itemUseCount / 20.0F;

            if (realBow > 1.0F) {
                realBow = 1.0F;
            } else {
                realBow *= realBow;
            }

            /*
             * Minecraft uses finalFov *= 1.0F - (realBow * 0.15F)
             * to calculate the standard bow zoom.
             * To reverse this, we just divide it instead.
             */
            finalFov /= 1.0F - (realBow * 0.15F);
            /*
             * We now calculate and apply our CustomBow zoom.
             * The only difference between standard bow zoom and CustomBow zoom
             * is that we change the hardcoded value of 20.0F to
             * whatever powerDiv is.
             */
            float customBow = itemUseCount / ((CustomBow) eventItem).powerDiv;

            if (customBow > 1.0F) {
                customBow = 1.0F;
            } else {
                customBow *= customBow;
            }

            finalFov *= 1.0F - (customBow * 0.15F);
            event.setNewfov(finalFov);
        }
    }

    @Override
    protected void register() {
        super.register();
        // Registration of custom renderers
        RenderingRegistry.registerEntityRenderingHandler(CustomArrow.class, new Client());
    }

    /** TODO review */
    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        for (final Item item : getAllItems()) {
            final @Nullable ResourceLocation itemLocation = item.getRegistryName();

            if (itemLocation != null) {
                ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(itemLocation.toString()));
            }
        }
    }

    /**
     * Handles rendering a CustomBow when drawing it back in first person.
     * This is necessary to make the bow draw back at the right speed.
     * This would not be an issue if there was a way to customize rendering for
     * EnumAction.BOW, but there is seemingly no way to do this.
     * TODO investigate a better way to do this.
     *
     * @param event the RenderSpecificHandEvent
     */
    @SubscribeEvent
    public void renderBow(RenderSpecificHandEvent event) {
        final Minecraft mc = Minecraft.getMinecraft();

        // Only handle rendering if we're in first person and drawing back a CustomBow.
        if ((mc.gameSettings.thirdPersonView == 0) && mc.player.isHandActive() && (mc.player.getActiveHand() == event.getHand()) && (mc.player.getItemInUseCount() > 0) && (event.getItemStack().getItem() instanceof CustomBow)) {
            // Cancel rendering so we can render instead
            event.setCanceled(true);
            GlStateManager.pushMatrix();
            // TODO this is silly
            final boolean rightHanded = (event.getHand() == EnumHand.MAIN_HAND ? mc.player.getPrimaryHand() : mc.player.getPrimaryHand().opposite()) == EnumHandSide.RIGHT;
            final int handedSide = rightHanded ? 1 : -1;
            /*
             * Translate to current hand
             * GlStateManager.translate(handedSide * 0.56F, -0.52F + (event.getEquipProgress() * -0.6F), -0.72F);
             * GlStateManager.translate(handedSide * -0.2785682F, 0.18344387F, 0.15731531F);
             * Merged translate calls
             * GlStateManager.translate(handedSide * (-0.2785682F + 0.56F), -0.52F + (event.getEquipProgress() * -0.6F) + 0.18344387F, -0.72F + 0.15731531F);
             */
            GlStateManager.translate(handedSide * 0.2814318F, -0.3365561F + (event.getEquipProgress() * -0.6F), -0.5626847F);
            // Rotate angles
            GlStateManager.rotate(-13.935F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(handedSide * 35.3F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(handedSide * -9.785F, 0.0F, 0.0F, 1.0F);
            final float ticks = bowMaxUseDuration - ((mc.player.getItemInUseCount() - event.getPartialTicks()) + 1.0F);
            float divTicks = ticks / ((CustomBow) event.getItemStack().getItem()).powerDiv;
            divTicks = ((divTicks * divTicks) + (divTicks * 2.0F)) / 3.0F;

            if (divTicks > 1.0F) {
                divTicks = 1.0F;
            }

            // Bow animations and transformations
            if (divTicks > 0.1F) {
                // Bow shake
                GlStateManager.translate(0.0F, MathHelper.sin((ticks - 0.1F) * 1.3F) * (divTicks - 0.1F) * 0.004F, 0.0F);
            }

            // Backwards motion ("draw back" animation)
            GlStateManager.translate(0.0F, 0.0F, divTicks * 0.04F);
            // Relative scaling for FOV reasons
            GlStateManager.scale(1.0F, 1.0F, 1.0F + (divTicks * 0.2F));
            // Rotate bow based on handedness
            GlStateManager.rotate(handedSide * 45.0F, 0.0F, -1.0F, 0.0F);
            // Let Minecraft do the rest of the item rendering
            mc.getItemRenderer().renderItemSide(mc.player, event.getItemStack(), rightHanded ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !rightHanded);
            GlStateManager.popMatrix();
        }
    }

}
