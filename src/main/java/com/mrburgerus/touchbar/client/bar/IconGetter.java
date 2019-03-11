package com.mrburgerus.touchbar.client.bar;

import com.mrburgerus.touchbar.TouchBar;
import com.thizzer.jtouchbar.common.Image;
import com.thizzer.jtouchbar.common.ImageName;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL31;
import org.lwjgl.stb.STBImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import static org.lwjgl.opengl.GL11.*;

// Gets the sprite textures to render them.
// TODO: BLOCKS!
// How? render as in inventory.
public class IconGetter implements ISelectiveResourceReloadListener
{
	// Image Sheet
	private static BufferedImage textureSheet;
	// INSTANCE, created at runtime
	public static final IconGetter INSTANCE = new IconGetter();

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate)
	{
		TouchBar.LOGGER.info("Reloading... ");
		loadTextures();
	}

	/* IS THIS NEEDED? */
	public static void loadTextures()
	{
		// Bind the Texture for use
		Minecraft.getInstance().getTextureMap().bindTexture();
		int width = glGetTexLevelParameteri(GL_TEXTURE_2D, 0 , GL_TEXTURE_WIDTH);
		int height = glGetTexLevelParameteri(GL_TEXTURE_2D, 0 , GL_TEXTURE_HEIGHT);

		// Still experimenting with capacity... could be (width * height)
		TouchBar.LOGGER.debug("Creating Int Buffer");
		IntBuffer buffer = BufferUtils.createIntBuffer(width * height);
		GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, buffer);
		int[] texInt = new int[buffer.remaining()];

		// Create Buffered Image
		buffer.get(texInt);
		BufferedImage buffered = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		buffered.setRGB(0, 0, width, height, texInt, 0, width);
		// Try to write the sheet
		try
		{
			ImageIO.write(buffered, "png", new File("textureSheet.png"));
		}
		catch (IOException e)
		{
			TouchBar.LOGGER.error("COULD NOT SAVE TEXTURE SHEET");
		}

		textureSheet = buffered;
		TouchBar.LOGGER.info("Finished Touch Bar texture load");
	}

	public static Image getIconImage(int idx, EntityPlayer player)
	{
		ItemStack items = player.inventory.getStackInSlot(idx);
		// Which renderer to use
		if (items.getItem() instanceof ItemBlock) // If it is a block
		{
			TouchBar.LOGGER.debug("Found Block");
		}
		else if (items.getItem() instanceof ItemAir)
		{
			TouchBar.LOGGER.debug("Found Air");
			//Crashes, have to find a way to use no image.
			//return new Image(new byte[0]);
		}
		else // Found Item
		{
			TouchBar.LOGGER.debug("Found Item");
			return getItemImage(items);
		}

		//TouchBar.LOGGER.debug("Sprite: " + sprite.getName());
		// Get Textures (NOT WORKING)
		/*
		for (ResourceLocation location : sprite.getDependencies()) // Iterate through all the Textures
		{
			try
			{
				TouchBar.LOGGER.debug("Making Tex");
				IResource resource = Minecraft.getInstance().getResourceManager().getResource(location);
				InputStream stream = resource.getInputStream();
				return new Image(stream);
			}
			catch (IOException e)
			{
				TouchBar.LOGGER.error("Could NOT make texture!");
				e.printStackTrace();
			}
		}
		*/


		//sprite.loadSpriteFrames();
		/*
		if (items.getItem().getRegistryName() != null)
		{
			TextureAtlasSprite sprite1 = Minecraft.getInstance().getTextureMap().getSprite(items.getItem().getRegistryName());

			//Minecraft.getInstance().getTextureManager().getTexture(items.getItem().getRegistryName()).bindTexture();
			//int[] texInt = new int[0];
			//GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, texInt);
			//TouchBar.LOGGER.info("Len: " + texInt.length);
			//int[] spriteInt = new int[sprite.getIconWidth() * sprite.getIconHeight()];
			//return new Image(byteData);

		}
		*/
		// Fallback
		return new Image(ImageName.NSImageNameTouchBarAlarmTemplate, false);
	}

	// NASTY CODE!
	// Works ONLY For Items, not for Custom Models.
	private static Image getItemImage(ItemStack itemStack)
	{
		// byte[] of textures, combined.

		IBakedModel model = Minecraft.getInstance().getItemRenderer().getModelWithOverrides(itemStack);
		// Gets the Baked Quads and their textures
		List<BakedQuad> bakedQ = model.getQuads(null, null, new Random());
		byte[] outComposite = new byte[bakedQ.get(0).getSprite().getWidth() * bakedQ.get(0).getSprite().getHeight()];
		TouchBar.LOGGER.debug("Byte Length: " + outComposite.length);
		for (BakedQuad quad : bakedQ)
		{
			TextureAtlasSprite sprite = quad.getSprite();
			TouchBar.LOGGER.debug("Sprite: " + sprite.getFrameCount() + ", " + sprite.getName());
			// Get BUFFERED int[]
			int[] rgbInts = new int[sprite.getWidth() * sprite.getHeight()];
			// Gets correct positions of X, Y
			int spriteX = (int) (sprite.getMinU() * textureSheet.getWidth());
			int spriteZ = (int) (sprite.getMinV() * textureSheet.getHeight());
			TouchBar.LOGGER.debug("X, Z: " + spriteX + ", " + spriteZ);
			// Crashes somewhere in here
			textureSheet.getRGB(spriteX, spriteZ, sprite.getWidth(), sprite.getHeight(), rgbInts, 0, sprite.getWidth());
			BufferedImage img = new BufferedImage(sprite.getWidth(), sprite.getHeight(), BufferedImage.TYPE_INT_ARGB);
			img.setRGB(0, 0, sprite.getWidth(), sprite.getHeight(), rgbInts, 0, sprite.getWidth());

			TouchBar.LOGGER.debug("RGB Length: " + rgbInts.length);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			try
			{
				// Write the stream
				ImageIO.write(img, "png", stream);
				// Convert to bytes
				outComposite = stream.toByteArray();
			}
			catch (IOException e)
			{
				TouchBar.LOGGER.error("Could not get Item Image!");
				e.printStackTrace();
			}
		}


		return new Image(outComposite);
	}

	private static byte[] convertTo32(BufferedImage input)
	{
		int desiredSize = 32;
		int wScaled = input.getWidth() * (desiredSize / input.getWidth());
		int hScaled = input.getHeight() * (desiredSize / input.getHeight());
		BufferedImage output = new BufferedImage(wScaled, hScaled, BufferedImage.TYPE_INT_ARGB);
		
	}
}
