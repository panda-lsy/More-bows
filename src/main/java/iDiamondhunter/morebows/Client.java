package iDiamondhunter.morebows;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import iDiamondhunter.morebows.bows.CustomBow;
import iDiamondhunter.morebows.client.NoRender;
import iDiamondhunter.morebows.entities.ArrowSpawner;
import net.minecraftforge.client.event.FOVUpdateEvent;

/* import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent.Specials.Pre; */

/* import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Items; */

public class Client extends MoreBows {

    /** Handles the FOV "zoom in" when drawing a custom bow */
    @SubscribeEvent
    @SideOnly(value = Side.CLIENT)
    public void FOVUpdate(FOVUpdateEvent event) {
        if ((event.entity.getItemInUse() != null) && (event.entity.getItemInUse().getItem() instanceof CustomBow)) {
            /** See net.minecraft.client.entity.EntityPlayerSP.getFOVMultiplier() */
            final CustomBow bow = (CustomBow) event.entity.getItemInUse().getItem();
            float f1 = (float) event.entity.getItemInUseDuration() / (float) (bow.iconTimes[0] * (10 / 9));

            if (f1 > 1.0F) {
                f1 = 1.0F;
            } else {
                f1 *= f1;
            }

            event.newfov *= 1.0F - (f1 * 0.15F);
        }
    }

    /* What's this? An unfinished future feature, not enabled in this commit due to potential bugs? Sneaky... */
    /*@SubscribeEvent
    @SideOnly(value = Side.CLIENT)
    public void playerRenderEvent(Pre event) {
        final EntityPlayer player = event.entityPlayer;

        if ((player.getItemInUse() != null) && (player.getItemInUse().getItem() instanceof CustomBow)) {
            // We'll handle it.
            event.renderItem = false;
            // Get the item to render
            final ItemStack currItem = player.inventory.getCurrentItem();
            // Start the OpenGL stuff
            GL11.glPushMatrix();
            event.renderer.modelBipedMain.bipedRightArm.postRender(0.0625F);
            GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);
            final float scale = 0.625F;
            GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
            GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
            GL11.glScalef(scale, -scale, scale);
            GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            // Get the RBG value
            final int itemColor = currItem.getItem().getColorFromItemStack(currItem, 0);
            // Split the RGB values
            final float red = ((itemColor >> 16) & 255) / 255.0F;
            final float green = ((itemColor >> 8) & 255) / 255.0F;
            final float blue = (itemColor & 255) / 255.0F;
            final float alpha = 1.0F;
            GL11.glColor4f(red, green, blue, alpha);
            // I think this is actually the best way to do this, which is just super cursed.
            Minecraft.getMinecraft().entityRenderer.itemRenderer.renderItem(event.entityLiving, currItem, 0);
            // GL is gone. Who needs it anyway?
            GL11.glPopMatrix();
        }
    }*/

    @Override
    protected void registerEntities() {
        super.registerEntities();
        // Handles not rendering the arrow spawner
        RenderingRegistry.registerEntityRenderingHandler(ArrowSpawner.class, new NoRender());
        /* TODO Re-implement */
        //RenderingRegistry.registerEntityRenderingHandler(FrostArrow.class, new RenderSnowball(Items.snowball));
    }

}
