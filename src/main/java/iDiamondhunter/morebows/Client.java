package iDiamondhunter.morebows;

import com.google.errorprone.annotations.CompileTimeConstant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fmlclient.ConfigGuiHandler;

/** The client mod initializer. */
@Mod.EventBusSubscriber(modid = MoreBows.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class Client {
    /** The maximum amount of time (in ticks) that a bow can be used for. */
    @CompileTimeConstant
    static final int bowMaxUseDuration = 72000;

    @SubscribeEvent
    public static void registerRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(MoreBows.CUSTOM_ARROW.get(), CustomArrowRenderer::new);
        event.registerEntityRenderer(MoreBows.ARROW_SPAWNER.get(), NoopRenderer::new);
    }

    @SubscribeEvent
    public static void onInitializeClient(FMLClientSetupEvent event) {
        final ResourceLocation PULL = new ResourceLocation("pull");
        final ResourceLocation PULLING = new ResourceLocation("pulling");
        final ItemPropertyFunction PULL_PROVIDER = (stack, world, entity, unused) -> ((entity == null) || (entity.getUseItem() != stack) ? 0.0F : (stack.getUseDuration() - entity.getUseItemRemainingTicks()) / ((CustomBow) stack.getItem()).powerDiv);
        final ItemPropertyFunction PULLING_PROVIDER = (stack, world, entity, unused) -> ((entity != null) && entity.isUsingItem() && (entity.getUseItem() == stack) ? 1.0F : 0.0F);

        for (final Item bow : MoreBows.getAllItems()) {
            ItemProperties.register(bow, PULL, PULL_PROVIDER);
            ItemProperties.register(bow, PULLING, PULLING_PROVIDER);
        }

        final Client clientListener = new Client();
        MinecraftForge.EVENT_BUS.addListener(clientListener::FOV);
        MinecraftForge.EVENT_BUS.addListener(clientListener::renderBow);

        if (ModList.get().isLoaded("cloth_config")) {
            ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class, () -> new ConfigGuiHandler.ConfigGuiFactory((mc, screen) -> iDiamondhunter.morebows.compat.ConfigScreen.moreBowsConfigScreen(screen)));
        }
    }

    /**
     * Handles the FOV "zoom in" when drawing a custom bow.
     *
     * @param event the FOVUpdateEvent
     */
    @SubscribeEvent
    public void FOV(FOVUpdateEvent event) {
        final Player eventPlayer = event.getEntity();
        final Item eventItem = eventPlayer.getUseItem().getItem();

        if (eventItem instanceof final CustomBow bow) {
            float finalFov = event.getFov();
            float customBow = eventPlayer.getTicksUsingItem() / bow.powerDiv;

            if (customBow > 1.0F) {
                customBow = 1.0F;
            } else {
                customBow *= customBow;
            }

            finalFov *= 1.0F - (customBow * 0.15F);
            event.setNewfov(finalFov);
            /*float finalFov = event.getFov();
            final float itemUseCount = bowMaxUseDuration - eventPlayer.getUseItemRemainingTicks();
            /*
             * First, we have to reverse the standard bow zoom.
             * Minecraft helpfully applies the standard bow zoom
             * to any item that is an instance of a ItemBow.
             * However, our CustomBows draw back at different speeds,
             * so the standard zoom is not at the right speed.
             * To compensate for this, we just calculate the standard bow zoom,
             * and apply it in reverse.
             * /
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
             * /
            finalFov /= 1.0F - (realBow * 0.15F);
            /*
             * We now calculate and apply our CustomBow zoom.
             * The only difference between standard bow zoom and CustomBow zoom
             * is that we change the hardcoded value of 20.0F to
             * whatever powerDiv is.
             * /
            float customBow = itemUseCount / ((CustomBow) eventItem).powerDiv;

            if (customBow > 1.0F) {
                customBow = 1.0F;
            } else {
                customBow *= customBow;
            }

            finalFov *= 1.0F - (customBow * 0.15F);
            event.setNewfov(finalFov);*/
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
    public void renderBow(RenderHandEvent event) {
        final Minecraft mc = Minecraft.getInstance();

        // Only handle rendering if we're in first person and drawing back a CustomBow.
        if (mc.options.getCameraType().isFirstPerson() && mc.player.isUsingItem() && (mc.player.getUsedItemHand() == event.getHand()) && (mc.player.getTicksUsingItem() > 0) && (event.getItemStack().getItem() instanceof final CustomBow bow)) {
            // Cancel rendering so we can render instead
            event.setCanceled(true);
            final PoseStack stack = event.getMatrixStack();
            stack.pushPose();
            // TODO this is silly
            final boolean rightHanded = (event.getHand() == InteractionHand.MAIN_HAND ? mc.player.getMainArm() : mc.player.getMainArm().getOpposite()) == HumanoidArm.RIGHT;
            final int handedSide = rightHanded ? 1 : -1;
            /*
             * Translate to current hand
             * stack.translate(handedSide * 0.56F, -0.52F + (event.getEquipProgress() * -0.6F), -0.72F);
             * stack.translate(handedSide * -0.2785682F, 0.18344387F, 0.15731531F);
             * Merged translate calls
             * stack.translate(handedSide * (-0.2785682F + 0.56F), -0.52F + (event.getEquipProgress() * -0.6F) + 0.18344387F, -0.72F + 0.15731531F);
             */
            stack.translate(handedSide * 0.2814318F, -0.3365561F + (event.getEquipProgress() * -0.6F), -0.5626847F);
            // Rotate angles
            stack.mulPose(Vector3f.XP.rotationDegrees(-13.935F));
            stack.mulPose(Vector3f.YP.rotationDegrees(handedSide * 35.3F));
            stack.mulPose(Vector3f.ZP.rotationDegrees(handedSide * -9.785F));
            final float ticks = bowMaxUseDuration - ((mc.player.getUseItemRemainingTicks() - event.getPartialTicks()) + 1.0F);
            float divTicks = ticks / bow.powerDiv;
            divTicks = ((divTicks * divTicks) + (divTicks * 2.0F)) / 3.0F;

            if (divTicks > 1.0F) {
                divTicks = 1.0F;
            }

            // Bow animations and transformations
            if (divTicks > 0.1F) {
                // Bow shake
                stack.translate(0.0F, Mth.sin((ticks - 0.1F) * 1.3F) * (divTicks - 0.1F) * 0.004F, 0.0F);
            }

            // Backwards motion ("draw back" animation)
            stack.translate(0.0F, 0.0F, divTicks * 0.04F);
            // Relative scaling for FOV reasons
            stack.scale(1.0F, 1.0F, 1.0F + (divTicks * 0.2F));
            // Rotate bow based on handedness
            stack.mulPose(Vector3f.YN.rotationDegrees(handedSide * 45.0F));
            // Let Minecraft do the rest of the item rendering
            final ItemTransforms.TransformType type = rightHanded ? ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND;
            mc.getItemRenderer().renderStatic(mc.player, event.getItemStack(), type, !rightHanded, stack, event.getBuffers(), mc.player.level, event.getLight(), OverlayTexture.NO_OVERLAY, mc.player.getId() + type.ordinal());
            stack.popPose();
        }
    }

}
