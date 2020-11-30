package iDiamondhunter.morebows;

import static iDiamondhunter.morebows.Client.partialTicks;
import static iDiamondhunter.morebows.MoreBows.ARROW_TYPE_FROST;
import static iDiamondhunter.morebows.MoreBows.bowMaxUseDuration;

import org.lwjgl.opengl.GL11;

import iDiamondhunter.morebows.entities.CustomArrow;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.IItemRenderer;

/**
 * This class is comprised of two parts: an entity renderer that handles both ArrowSpawners and CustomArrows, and an IItemRenderer for the CustomBow item.
 * Entity rendering:
 * Handles rendering the entities added by this mod.
 * If the entity is a CustomArrow, and the CustomArrow is of type FROST, it renders as a snowball or a default cube depending on the value of MoreBows.oldFrostArrowRendering.
 * If it's not of type FROST, it renders as an arrow.
 * If it's not a CustomArrow, it doesn't render anything! This is deliberately used to not render any ArrowSpawners.
 * Bow rendering:
 * A custom IItemRenderer, which exists as a result of not being able to modify the bow "draw back" animation speed with standard Minecraft renderers.
 * It is also responsible for transforming the position of the bow when rendering an Entity that holds a bow.
 * Minecraft normally only applies these transformations when the item matches the bow item. Items of type CustomBow do not match against this.
 */
public final class ModRenderer extends RenderEntity implements IItemRenderer {

    /** Not sure if this is a super cused hack, of if it's actually the best way to do this... */
    private final static Render arrow = RenderManager.instance.getEntityClassRenderObject(EntityArrow.class);
    private final static Render snow = RenderManager.instance.getEntityClassRenderObject(EntitySnowball.class);

    @Override
    public void doRender(Entity e, double a, double b, double c, float d, float f) {
        if (e instanceof CustomArrow) {
            if (((CustomArrow) e).type == ARROW_TYPE_FROST) {
                if (!MoreBows.oldFrostArrowRendering) {
                    snow.doRender(e, a, b, c, d, f);
                } else {
                    super.doRender(e, a, b, c, d, f);
                }
            } else {
                arrow.doRender(e, a, b, c, d, f);
            }
        } /** else do nothing */
    }

    public boolean handleRenderType(ItemStack stack, ItemRenderType type) {
        return (type == ItemRenderType.EQUIPPED) || (type == ItemRenderType.EQUIPPED_FIRST_PERSON);
    }

    /**
     * This method is mainly responsible for transforming the CustomBow before rendering occurs.
     * This handles two cases:
     * - First person player rendering ("draw back" animation, bow shake, perspective transformation etc)
     * - Any entity that holds a bow (moving the bow to the right position in the hands of the Entity)
     */
    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
        final EntityLivingBase entity = (EntityLivingBase) data[1];
        GL11.glPopMatrix();

        if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            /**
             * Handles bow animations when in first person:
             * - Moving the bow backwards
             * - Applying some sort of rotation / transformation to match this to the FOV
             * - Making the bow shake when drawn back
             * See ItemRenderer.renderItemInFirstPerson, line 458
             */
            /* The entity will always be a EntityPlayer, as we're rendering in first person */
            final int useTicks = ((EntityPlayer) entity).getItemInUseCount();

            /** This code reverses the effects of the normal item transformations, then applies the transformations given to an item when EnumAction.bow is used. */
            if (useTicks > 0) {
                /** Reveres the normal item transformations, to move the bow back to where it started so we can apply our own tranformations. */
                GL11.glRotatef(-18.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(-12.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-8.0F, 1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(-0.9F, 0.2F, 0.0F);
                final float ticks = bowMaxUseDuration - ((useTicks - partialTicks) + 1.0F);
                /**
                 * In the normal first person renderer, this is hardcoded to be "float divTicks = ticks / 20.0F".
                 * See iDiamondhunter.morebows.Client.fov for why this is used instead.
                 */
                float divTicks = ticks / (((CustomBow) stack.getItem()).iconTimes[0] * 1.1F);
                divTicks = ((divTicks * divTicks) + (divTicks * 2.0F)) / 3.0F;

                if (divTicks > 1.0F) {
                    divTicks = 1.0F;
                }

                // final float tickFOV = 1.0F + (divTicks * 0.2F);

                /** Bow animations and transformations */
                if (divTicks > 0.1F) {
                    /** Bow shake */
                    GL11.glTranslatef(0.0F, MathHelper.sin((ticks - 0.1F) * 1.3F) * 0.01F * (divTicks - 0.1F), 0.0F);
                }

                /** Backwards motion ("draw back" animaton) */
                GL11.glTranslatef(0.0F, 0.0F, divTicks * 0.1F);
                /** Does... something. */
                GL11.glRotatef(-335.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(0.0F, 0.5F, 0.0F);
                /** Probably relative scaling for FOV reasons */
                // GL11.glScalef(1.0F, 1.0F, tickFOV);
                GL11.glScalef(1.0F, 1.0F, (1.0F + (divTicks * 0.2F)));
                /** Does it look like I know what I'm doing to you? */
                GL11.glTranslatef(0.0F, -0.5F, 0.0F);
                GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
            }
        } else {
            /**
             * Handles moving the bow into the right position when rendering an entity.
             * Minecraft is hardcoded to only do this for items which are equal to the bow item, so we have to do it manually.
             */
            /* Checks if the entity we're rendering our bow with is a witch */
            final boolean isWitch = (entity instanceof EntityWitch);

            // final float scale = 3.0F - (1.0F / 3.0F); // a more precise representation of 1 / 0.375F, or 2.6666667F
            if (isWitch) {
                /**
                 * Witches are a special case, as they have additional transformations applied to items at the end of rendering.
                 * This undoes these transformations.
                 */
                GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(15.0F, -1.0F, 0.0F, 0.0F);
            }

            /** Reveres the normal item transformations, to move the bow back to where it started so we can apply our own tranformations. */
            GL11.glRotatef(-20.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(-60.0F, 0.0F, 0.0F, 1.0F);
            // GL11.glScalef(scale, scale, scale);
            GL11.glScalef(2.6666667F, 2.6666667F, 2.6666667F); // reverse scale
            GL11.glTranslatef(-0.25F, -0.1875F, 0.1875F);
            /** Apply bow specific transformations */
            GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
            GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
            GL11.glScalef(0.625F, -0.625F, 0.625F);
            GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);

            if (isWitch) {
                /** Re-apply final witch item transformations */
                GL11.glRotatef(-15.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(40.0F, 0.0F, 0.0F, 1.0F);
            }
        }

        /**
         * This is a hack to avoid re-implementing all of vanilla Minecraft's rendering.
         * As we've now finished our spatial transformations, all that we need to do now is render the bow.
         * Quite luckily, Minecraft already does this for us! We're not interested in re-inventing the wheel in this particular case.
         * So, as we've only declared that we want to render items that use ItemRenderType.EQUIPPED or ItemRenderType.EQUIPPED_FIRST_PERSON,
         * all we have to do is ask Minecraft to render our item with something we don't handle ourselves!
         * renderItem only deals with rendering the actual item icon, as it assumes that any transformations that need to have been done
         * will have been done already by the time it actually renders anything.
         */
        RenderManager.instance.itemRenderer.renderItem(entity, stack, 0, ItemRenderType.ENTITY);
        GL11.glPushMatrix();
    }

    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack stack, ItemRendererHelper data) {
        return false;
    }

}
