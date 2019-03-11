package com.mrburgerus.touchbar;

import com.mrburgerus.touchbar.client.ClientProxy;
import com.mrburgerus.touchbar.client.bar.InventoryBar;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("touchbar")
public class TouchBar
{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
	public static ServerProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);


	public TouchBar()
	{
		proxy.initialize();
		MinecraftForge.EVENT_BUS.register(this);
    }

	/* RUNS EVERY N TICKS TO ENSURE ACCURACY */
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void touchBarUpdate(TickEvent.PlayerTickEvent event)
	{
		//Run once
		if (event.phase == TickEvent.Phase.END && event.player.world.isRemote)
		{
			InventoryBar.doRedraw(event.player);
		}
	}
}
