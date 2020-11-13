package iDiamondhunter.morebows.render;

import static iDiamondhunter.morebows.Client.partTicks;

import org.lwjgl.opengl.GL11;

import iDiamondhunter.morebows.bows.CustomBow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.IItemRenderer;

/** This is a custom IItemRenderer, which exists as a result of not being able to modify the bow "draw back" animation speed with standard Minecraft renderers.
 *  It is also responsible for transforming the position of the bow when rendering an Entity that holds a bow.
 *  Minecraft normally only applies these transformations when the item matches the bow item. Items of type CustomBow do not match against this.
 *  */
public final class RenderBow implements IItemRenderer {

    @Override
    public boolean handleRenderType(ItemStack stack, ItemRenderType type) {
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
        final EntityLivingBase entity = (EntityLivingBase) data[1];
        GL11.glPopMatrix();

        if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            /* Handles bow animations when in first person:
             * - Moving the bow backwards
             * - Applying some sort of rotation / transformation to match this to the FOV
             * - Making the bow shake when drawn back
             * See ItemRenderer.renderItemInFirstPerson, line 458
             * TODO finish documentation, clean up */
            final int useTicks = ((EntityPlayer) entity).getItemInUseCount(); // the entity will always be a EntityPlayer, as we're rendering in first person

            /* This code reverses, then re-applies the transformations given to an item when EnumAction.bow is used. TODO this doesn't reverse perfectly, try commenting out the re-applied bow shake to see what I mean, finish documentation */
            if (useTicks > 0) {
                // reveres the normal item transformations, to move the bow back to where it started so we can apply our own tranformations.
                GL11.glRotatef(-18.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(-12.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-8.0F, 1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(-0.9F, 0.2F, 0.0F);
                final float ticks = stack.getMaxItemUseDuration() - ((useTicks - partTicks) + 1.0F);
                // in the normal first person renderer, this is hardcoded to be "float divTicks = partTicks / 20.0F". I've used the same code as I did in the custom FOV zoom (see iDiamondhunter.morebows.Client.fov).
                float divTicks = ticks / ((((CustomBow) stack.getItem()).iconTimes[0] * 10) / 9);
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
                // probably relative scaling for FOV reasons
                GL11.glScalef(1.0F, 1.0F, tickFOV);
                // does it look like I know what I'm doing to you?
                GL11.glTranslatef(0.0F, -0.5F, 0.0F);
                GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
            }
        } else {
            /* Handles moving the bow into the right position when rendering an entity.
             * Minecraft is hardcoded to only do this for items which are equal to the bow item, so we have to do it manually. */
            final boolean isWitch = (entity.getClass() == EntityWitch.class); // Checks if the entity we're rendering our bow with is a witch
            //final float scale = 3.0F - (1.0F / 3.0F); // a more precise representation of 1 / 0.375F, or 2.6666667F

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
            //GL11.glScalef(scale, scale, scale); // reverse scale
            GL11.glScalef(2.6666667F, 2.6666667F, 2.6666667F); // reverse scale
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

        /** This is a hack to avoid re-implementing all of vanilla Minecraft's rendering.
         *  As we've now finished our spatial transformations, all that we need to do now is render the bow.
         *  Quite luckily, Minecraft already does this for us! We're not interested in re-inventing the wheel in this particular case.
         *  So, as we've only declared that we want to render items that use ItemRenderType.EQUIPPED or ItemRenderType.EQUIPPED_FIRST_PERSON,
         *  all we have to do is ask Minecraft to render our item with something we don't handle ourselves!
         *  renderItem only deals with rendering the actual item icon, as it assumes that any transformations that need to have been done
         *  will have been done already by the time it actually renders anything.
         */
        RenderManager.instance.itemRenderer.renderItem(entity, stack, 0, ItemRenderType.ENTITY);
        GL11.glPushMatrix();
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack stack, ItemRendererHelper data) {
        return false;
    }

}
