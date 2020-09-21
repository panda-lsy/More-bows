package iDiamondhunter.morebowsmod.client;

import iDiamondhunter.morebowsmod.common.CommonProxyiDiamondhunter;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxyiDiamondhunter extends CommonProxyiDiamondhunter
{
	@Override
	public void registerRenderThings()
	{
		
		MinecraftForgeClient.preloadTexture("/MoreBows/Bows.png");
		
	}
}