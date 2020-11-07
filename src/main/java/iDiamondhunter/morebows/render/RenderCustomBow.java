package iDiamondhunter.morebows.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import iDiamondhunter.morebows.Client;
import iDiamondhunter.morebows.bows.CustomBow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

/** The "bow draw" animation is stuck in ItemRenderer, and it's really hard to fix by any other means than a custom renderer. TODO Finish documentation */
public class RenderCustomBow implements IItemRenderer {

    private final static ResourceLocation enchGlint = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    /** the same as Minecraft rendering TODO cleanup, document */
    public static void renderIcon(IIcon icon, int spriteIndex, boolean hasEffect) {
        if (icon == null) {
            return;
        }

        Minecraft.getMinecraft().renderEngine.bindTexture(Minecraft.getMinecraft().renderEngine.getResourceLocation(spriteIndex));
        TextureUtil.func_152777_a(false, false, 1.0F);
        final Tessellator tess = Tessellator.instance;
        final float f = icon.getMinU();
        final float f1 = icon.getMaxU();
        final float f2 = icon.getMinV();
        final float f3 = icon.getMaxV();
        final float f4 = 0.0F;
        final float f5 = 0.3F;
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslatef(-f4, -f5, 0.0F);
        final float f6 = 1.5F;
        GL11.glScalef(f6, f6, f6);
        GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef(-0.9375F, -0.0625F, 0.0F);
        ItemRenderer.renderItemIn2D(Tessellator.instance, f1, f2, f, f3, icon.getIconWidth(), icon.getIconHeight(), 0.0625F);

        if (hasEffect) {
            GL11.glDepthFunc(GL11.GL_EQUAL);
            GL11.glDisable(GL11.GL_LIGHTING);
            Minecraft.getMinecraft().renderEngine.bindTexture(enchGlint);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(768, 1, 1, 0);
            final float f7 = 0.76F;
            GL11.glColor4f(0.5F * f7, 0.25F * f7, 0.8F * f7, 1.0F);
            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GL11.glPushMatrix();
            final float f8 = 0.125F;
            GL11.glScalef(f8, f8, f8);
            float f9 = ((Minecraft.getSystemTime() % 3000L) / 3000.0F) * 8.0F;
            GL11.glTranslatef(f9, 0.0F, 0.0F);
            GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
            ItemRenderer.renderItemIn2D(tess, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(f8, f8, f8);
            f9 = ((Minecraft.getSystemTime() % 4873L) / 4873.0F) * 8.0F;
            GL11.glTranslatef(-f9, 0.0F, 0.0F);
            GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
            ItemRenderer.renderItemIn2D(tess, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        Minecraft.getMinecraft().renderEngine.bindTexture(Minecraft.getMinecraft().renderEngine.getResourceLocation(spriteIndex));
        TextureUtil.func_147945_b();
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return (type == ItemRenderType.EQUIPPED) || (type == ItemRenderType.EQUIPPED_FIRST_PERSON);
    }

    /* TODO cleanup */
    @Override
    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
        final EntityLivingBase entity = (EntityLivingBase) data[1];
        final EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        GL11.glPopMatrix();

        if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            /** Handles bow animations when in first person:
             * - Moving the bow backwards
             * - Applying some sort of rotation / transformation to match this to the FOV
             * - Making the bow shake when drawn back
             * This all has to first be reversed as well. See ItemRenderer.renderItemInFirstPerson, line 458
             * TODO finish documentation
             *  */
            if ((player.getHeldItem() != null) && (player.getHeldItem().getItem() instanceof CustomBow)) {
                final CustomBow bow = (CustomBow) player.getHeldItem().getItem();
                final ItemStack currItem = player.inventory.getCurrentItem();
                //Minecraft.getMinecraft().renderEngine.getResourceLocation(stack.getItemSpriteNumber());

                /* This code reverses, then re-applies the transformations given to an item when EnumAction.bow is used. TODO this doesn't reverse perfectly, try commenting out the re-applied bow shake to see what I mean, finish documentation */
                if (player.getItemInUseCount() > 0) {
                    final float ticks = Client.hackForTicks;
                    final float fake10 = currItem.getMaxItemUseDuration() - ((player.getItemInUseCount() - ticks) + 1.0F);
                    float fake11 = fake10 / 20.0F;
                    fake11 = ((fake11 * fake11) + (fake11 * 2.0F)) / 3.0F;

                    if (fake11 > 1.0F) {
                        fake11 = 1.0F;
                    }

                    final float fake12 = 1.0F + (fake11 * 0.2F);
                    // fov scale?
                    GL11.glScalef(1.0F, 1.0F, 1 / fake12);
                    // backwards motion?
                    GL11.glTranslatef(0.0F, 0.0F, -(fake11 * 0.1F));

                    if (fake11 > 0.1F) {
                        // bow shake
                        GL11.glTranslatef(0.0F, -(MathHelper.sin((fake10 - 0.1F) * 1.3F) * 0.01F * (fake11 - 0.1F)), 0.0F);
                    }

                    // re-apply transformations
                    final float f10 = currItem.getMaxItemUseDuration() - ((player.getItemInUseCount() - ticks) + 1.0F);
                    //float f11 = f10 / bow.powerDiv;
                    //float f11 = f10 / 40;
                    //float f11 = f10 / 20.0F;
                    // TODO probably use the same as used in FOV calculations? see iDiamondhunter.morebows.Client
                    float f11 = f10 / (bow.iconTimes[0] * (10 / 9));
                    f11 = ((f11 * f11) + (f11 * 2.0F)) / 3.0F;

                    if (f11 > 1.0F) {
                        f11 = 1.0F;
                    }

                    final float f12 = 1.0F + (f11 * 0.2F);

                    if (f11 > 0.1F) {
                        // bow shake
                        GL11.glTranslatef(0.0F, MathHelper.sin((f10 - 0.1F) * 1.3F) * 0.01F * (f11 - 0.1F), 0.0F);
                    }

                    // backwards motion?
                    GL11.glTranslatef(0.0F, 0.0F, f11 * 0.1F);
                    // fov scale?
                    GL11.glScalef(1.0F, 1.0F, f12);
                }
            }
        } else {
            /** Handles moving the bow into the right position when rendering an entity.
             *  Minecraft is hardcoded to only do this for items which are equal to the bow item, so we have to do it manually. */
            final boolean isWitch = (entity instanceof EntityWitch);
            final float rescale = 3.0F - (1.0F / 3.0F); // 1 / 0.375F
            final float scale = 0.625F;

            if (isWitch) {
                // undo final witch item transformations
                GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(15.0F, -1.0F, 0.0F, 0.0F);
            }

            // reverse standard item transformations & re-scale
            GL11.glRotatef(-20.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(-60.0F, 0.0F, 0.0F, 1.0F);
            GL11.glScalef(rescale, rescale, rescale);
            GL11.glTranslatef(-0.25F, -0.1875F, 0.1875F);
            // apply bow transformations
            GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
            GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
            GL11.glScalef(scale, -scale, scale);
            GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);

            if (isWitch) {
                // re-apply final witch item transformations
                GL11.glRotatef(-15.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(40.0F, 0.0F, 0.0F, 1.0F);
            }
        }

        if (entity instanceof EntityPlayer) {
            renderIcon(stack.getItem().getIcon(stack, 0, player, player.getItemInUse(), player.getItemInUseCount()), stack.getItemSpriteNumber(), stack.hasEffect(0));
        } else {
            renderIcon(stack.getItem().getIcon(stack, 0), stack.getItemSpriteNumber(), stack.hasEffect(0));
        }

        GL11.glPushMatrix();
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return false;
    }

}
