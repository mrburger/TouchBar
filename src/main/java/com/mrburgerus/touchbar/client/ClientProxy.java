package com.mrburgerus.touchbar.client;

import com.mrburgerus.touchbar.ServerProxy;
import com.mrburgerus.touchbar.client.bar.IconGetter;
import com.mrburgerus.touchbar.client.window.TouchBarWindow;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

public class ClientProxy extends ServerProxy
{
	@Override
	public void initialize()
	{
		super.initialize();
		TouchBarWindow.setup();
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void reloadTextures(final TextureStitchEvent.Post event)
	{
		// Call the texture loader
		IconGetter.loadTextures();
	}
}
