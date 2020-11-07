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

/** This is a custom IItemRenderer, which exists as a result of not being able to modify the bow "draw back" animation speed with standard Minecraft renderers.
 *  It is also responsible for transforming the position of the bow when rendering an Entity that holds a bow.
 *  Minecraft normally only applies these transformations when the item matches the bow item. Items of type CustomBow do not match against this.
 *  */
public class RenderBow implements IItemRenderer {

    private final static ResourceLocation enchGlint = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    /** This effectively renders an icon in exactly the same way as normally rendering an icon in first person does. */
    public static void renderIcon(IIcon icon, int spriteIndex, boolean hasEffect) {
        Minecraft.getMinecraft().renderEngine.bindTexture(Minecraft.getMinecraft().renderEngine.getResourceLocation(spriteIndex));
        TextureUtil.func_152777_a(false, false, 1.0F);
        final Tessellator tess = Tessellator.instance;
        final float f = icon.getMinU();
        final float f1 = icon.getMaxU();
        final float f2 = icon.getMinV();
        final float f3 = icon.getMaxV();
        //final float f4 = 0.0F;
        //final float f5 = 0.3F;
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        //GL11.glTranslatef(-f4, -f5, 0.0F);
        GL11.glTranslatef(-0.0F, -0.3F, 0.0F);
        //final float f6 = 1.5F;
        //GL11.glScalef(f6, f6, f6);
        GL11.glScalef(1.5F, 1.5F, 1.5F);
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
            //GL11.glColor4f(0.5F * 0.76F, 0.25F * 0.76F, 0.8F * 0.76F, 1.0F);
            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GL11.glPushMatrix();
            //final float f8 = 0.125F;
            //GL11.glScalef(f8, f8, f8);
            GL11.glScalef(0.125F, 0.125F, 0.125F);
            float f9 = ((Minecraft.getSystemTime() % 3000L) / 3000.0F) * 8.0F;
            GL11.glTranslatef(f9, 0.0F, 0.0F);
            GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
            ItemRenderer.renderItemIn2D(tess, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            //GL11.glScalef(f8, f8, f8);
            GL11.glScalef(0.125F, 0.125F, 0.125F);
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

    /** This method is mainly responsible for transforming the CustomBow before rendering occurs.
     *  This handles two cases:
     *  - First person player rendering ("draw back" animation, bow shake, perspective transformation etc)
     *  - Any entity that holds a bow (moving the bow to the right position in the hands of the Entity)
     *  TODO cleanup
     */
    @Override
    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
        final boolean isPlayer;
        final EntityPlayer ePlayer;
        final EntityLivingBase entity = (EntityLivingBase) data[1];

        // match against any variation of the player class: mp, sp etc.
        if (isPlayer = (entity instanceof EntityPlayer)) {
            ePlayer = (EntityPlayer) entity;
        } else {
            ePlayer = null;
        }

        GL11.glPopMatrix();

        if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            /** Handles bow animations when in first person:
             * - Moving the bow backwards
             * - Applying some sort of rotation / transformation to match this to the FOV
             * - Making the bow shake when drawn back
             * See ItemRenderer.renderItemInFirstPerson, line 458
             * TODO finish documentation, clean up
             *  */
            final CustomBow bow = (CustomBow) stack.getItem(); // this will always be a CustomBow, as this renderer is only registered for CustomBows
            final int useTicks = ePlayer.getItemInUseCount();

            /* This code reverses, then re-applies the transformations given to an item when EnumAction.bow is used. TODO this doesn't reverse perfectly, try commenting out the re-applied bow shake to see what I mean, finish documentation */
            if (useTicks > 0) {
                // reveres the normal item transformations, to move the bow back to where it started so we can apply our own tranformations.
                GL11.glRotatef(-18.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(-12.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-8.0F, 1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(-0.9F, 0.2F, 0.0F);
                // sets up some values
                final float ticks = stack.getMaxItemUseDuration() - ((useTicks - Client.partialTicks) + 1.0F);
                // in the normal first person renderer, this is hardcoded to be "float divTicks = ticks / 20.0F". I've used the same code as I did in the custom FOV zoom (see iDiamondhunter.morebows.Client.fov).
                float divTicks = ticks / (bow.iconTimes[0] * (10 / 9));
                divTicks = ((divTicks * divTicks) + (divTicks * 2.0F)) / 3.0F;

                if (divTicks > 1.0F) {
                    divTicks = 1.0F;
                }

                final float tickFOV = 1.0F + (divTicks * 0.2F);

                // Bow animations and transformations
                
                if (divTicks > 0.1F) {
                    // bow shake
                    GL11.glTranslatef(0.0F, MathHelper.sin((ticks - 0.1F) * 1.3F) * 0.01F * (divTicks - 0.1F), 0.0F);
                }

                // backwards motion ("draw back" animaton)
                GL11.glTranslatef(0.0F, 0.0F, divTicks * 0.1F);
                // does... something.
                GL11.glRotatef(-335.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(0.0F, 0.5F, 0.0F);
                // FOV scaling (scales the bow to stay in the same perceptual position, "de-distorts" the bow when FOV changes)
                GL11.glScalef(1.0F, 1.0F, tickFOV);
                // does it look like I know what I'm doing to you?
                GL11.glTranslatef(0.0F, -0.5F, 0.0F);
                GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
            }
        } else {
            /** Handles moving the bow into the right position when rendering an entity.
             *  Minecraft is hardcoded to only do this for items which are equal to the bow item, so we have to do it manually. */
            final boolean isWitch = /* (!isPlayer && */ (entity.getClass() == EntityWitch.class) /* ) */;
            final float scale = 3.0F - (1.0F / 3.0F); // a more precise representation of 1 / 0.375F? 2.625F isn't accurate?

            if (isWitch) {
                // Witches are a special case, as they have additional transformations applied to items at the end of rendering.
                // This undoes these transformations.
                GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(15.0F, -1.0F, 0.0F, 0.0F);
            }

            // reveres the normal item transformations, to move the bow back to where it started so we can apply our own tranformations.
            GL11.glRotatef(-20.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(-60.0F, 0.0F, 0.0F, 1.0F);
            //GL11.glScalef(2.625F, 2.625F, 2.625F);
            GL11.glScalef(scale, scale, scale); // reverse scale
            GL11.glTranslatef(-0.25F, -0.1875F, 0.1875F);
            // apply bow specific transformations
            GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
            GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
            GL11.glScalef(0.625F, -0.625F, 0.625F);
            GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);

            if (isWitch) {
                // Re-apply final witch item transformations
                GL11.glRotatef(-15.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(40.0F, 0.0F, 0.0F, 1.0F);
            }
        }

        if (isPlayer) {
            // Render the icon for the bow use duration
            renderIcon(stack.getItem().getIcon(stack, 0, ePlayer, ePlayer.getItemInUse(), ePlayer.getItemInUseCount()), stack.getItemSpriteNumber(), stack.hasEffect(0));
        } else {
            // I don't think non-player Entities animate the bow icons when drawing bows?
            renderIcon(stack.getItem().getIcon(stack, 0), stack.getItemSpriteNumber(), stack.hasEffect(0));
        }

        GL11.glPushMatrix();
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return false;
    }

}
