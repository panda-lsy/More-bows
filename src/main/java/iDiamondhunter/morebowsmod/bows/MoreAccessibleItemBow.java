package iDiamondhunter.morebowsmod.bows;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;

/** This entire class is a huge hack. I'm ashamed of myself. And yes, this is important to document. 
 * TODO: Re-write all of this (or at least re-think most of it). Burn the original.
 **/
public abstract class MoreAccessibleItemBow extends ItemBow 
{
	/* TODO better names */
	@SideOnly(Side.CLIENT)
	protected IIcon[] iconArray;
	/** TODO: replace this */
	@Deprecated
	protected EntityArrow[] bowShots;
	/** TODO: replace this */
	@Deprecated
	protected float shotVelocity;
	/* TODO assign defaults in constructor, make final for values that shouldn't be changed */
	protected float arrowPowerDivisor = 20.0F;
	protected float defaultShotVelocityMultiplier = 2.0F;
	protected int flameBurnTime = 100;
	protected double damageMultiplier = 1;
	/** TODO why did I add this? */
	protected String defaultShotSound = "random.bow"; 

	/** TODO better parameter order, decide which variables should be set in the constructor (possibly provide a better default one) */
	public MoreAccessibleItemBow(int maxDamage)
    {
		this.maxStackSize = 1;
		this.setMaxDamage(maxDamage);
		this.bFull3D = true;
        this.setCreativeTab(CreativeTabs.tabCombat);
    }
	
	/** TODO find a cleaner way to implement this, change like all of this. also make better names. */
	@Override
	public void onPlayerStoppedUsing(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer, int par4)
    {
        int bowCharge = this.getMaxItemUseDuration(par1ItemStack) - par4;

        ArrowLooseEvent event = new ArrowLooseEvent(par3EntityPlayer, par1ItemStack, bowCharge);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled())
        {
            return;
        }
        bowCharge = event.charge;

        boolean flag = par3EntityPlayer.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, par1ItemStack) > 0;

        if (flag || par3EntityPlayer.inventory.hasItem(Items.arrow))
        {
        	shotVelocity = (float)bowCharge / arrowPowerDivisor;
        	shotVelocity = (shotVelocity * shotVelocity + shotVelocity * 2.0F) / 3.0F;

            if ((double)shotVelocity < 0.1D)
            {
                return;
            }

            if (shotVelocity > 1.0F)
            {
            	shotVelocity = 1.0F;
            }

            setArrows(par2World, par3EntityPlayer);
            addModifiersToArrows(par2World, par1ItemStack, flag, false);

            par1ItemStack.damageItem(1, par3EntityPlayer);
            playBowNoise(par2World, par3EntityPlayer);

            if (!flag)
            {
                par3EntityPlayer.inventory.consumeInventoryItem(Items.arrow);
            }

            if (!par2World.isRemote)
            {
            	spawnArrows(par2World);
            }
        }
    }
	
	/** TODO: Go through each bow and check if they have custom noises. Also make this better. */
	public void playBowNoise(World world, EntityPlayer player) {
		
		world.playSoundAtEntity(player, defaultShotSound, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + shotVelocity * 0.5F);
		
	}
	
	@Deprecated
	public void setArrows(World world, EntityPlayer player) { //TODO rename later
		
		//default behavior 
		bowShots = new EntityArrow[] { new EntityArrow(world, player, shotVelocity * defaultShotVelocityMultiplier) };
		
	}
	
	@Deprecated
	public void addModifiersToArrows(World world, ItemStack stack, Boolean noPickupFlag, Boolean alwaysCrit) { //TODO rename later
		
		//TODO THIS CODE IS AWFULL FIX IT
		//not just the readability, but also the fact that this is literal garbage to work with
		//when you need to insert an effect into this chain
		//see: ItemFlameBow
		
		boolean shotPowerFlag = (shotVelocity == 1.0F) || alwaysCrit;
		int k = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
		boolean powerEnchantmentFlag = (k > 0);
		int l = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);
		boolean punchEnchantmentFlag = (l > 0);
		boolean flameEnchantmentFlag = (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0);
		boolean damageMultiplierFlag = (damageMultiplier != 1);
		
		//default behavior 
		for (EntityArrow arr : bowShots) {
			
	        if (shotPowerFlag)
	        {
	    			arr.setIsCritical(true);
	        }

	        if (powerEnchantmentFlag)
	        {
	    			arr.setDamage(arr.getDamage() + (double)k * 0.5D + 0.5D);
	        }

	        if (punchEnchantmentFlag)
	        {
	    			arr.setKnockbackStrength(l);
	        }

	        if (flameEnchantmentFlag)
	        {
	    			arr.setFire(flameBurnTime);
	        }
	        
	        if (noPickupFlag)
	        {
	    			arr.canBePickedUp = 2;
	        }
	        
	        if (damageMultiplierFlag) {
	        		arr.setDamage(arr.getDamage() * damageMultiplier);
	        }
			
		}
		
	}
	
	@Deprecated
	public void spawnArrows(World world) { //TODO rename later
		
		//TODO add logic to spawn arrows over time
		for (EntityArrow arr : bowShots) {
			
			world.spawnEntityInWorld(arr);
			
		}
		
	}
    
    //Overrides are used as ItemBow's icon related variables are not visible :(
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegistry)
    {
        this.itemIcon = iconRegistry.registerIcon(this.getIconString() + "1"); //redo with off-by-one error fixed
        //this.iconArray = new IIcon[this.bowPullIconNameArray.length];
        this.iconArray = new IIcon[3];

        for (int i = 0; i < this.iconArray.length; ++i)
        {
            //this.iconArray[i] = iconRegistry.registerIcon(this.getIconString() + "_" + this.bowPullIconNameArray[i]);
        	this.iconArray[i] = iconRegistry.registerIcon(this.getIconString() + (i + 2)); //awful hack, icons start from 2 here
        	//this.iconArray[i] = iconRegistry.registerIcon(this.bowPullIconNameArray[i]);
        }
    }
    
    /** TODO I don't think anything will ever use this? Remove if possible */
    @Deprecated
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getItemIconForUseDuration(int index)
    {
        return this.iconArray[index];
    }
    
    /** TODO Replace this system! */
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
        if (usingItem == null) { return itemIcon; }
        int ticksInUse = stack.getMaxItemUseDuration() - useRemaining;

        if (ticksInUse >= 18) {
              return iconArray[2];
        } else if (ticksInUse > 13) {
            return iconArray[1];
        } else if (ticksInUse > 0) {
            return iconArray[0];
        } else {
            return itemIcon;
        }
    }
    

}
