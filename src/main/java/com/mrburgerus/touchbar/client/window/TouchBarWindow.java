package com.mrburgerus.touchbar.client.window;

import com.thizzer.jtouchbar.JTouchBar;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFWNativeCocoa;

public class TouchBarWindow
{
	// The touch bar object
	public static JTouchBar mcTouchBar = new JTouchBar();
	// Long corresponding to window (Simplified HEAVILY FROM 1.12.2)
	public static final long windowLong = Minecraft.getInstance().mainWindow.getHandle();
	// Customization Identifier String
	public static final String identifier = "mc_bar";

	// Initial Setup
	public static void setup()
	{
		mcTouchBar.setCustomizationIdentifier(identifier);
	}

	//Show window
	public static void show()
	{
		mcTouchBar.show(GLFWNativeCocoa.glfwGetCocoaWindow(windowLong));
	}
}
