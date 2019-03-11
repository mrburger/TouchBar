package com.mrburgerus.touchbar.client.bar;

import com.google.common.collect.Lists;
import com.mrburgerus.touchbar.TouchBar;
import com.mrburgerus.touchbar.client.window.TouchBarWindow;
import com.thizzer.jtouchbar.common.Color;
import com.thizzer.jtouchbar.common.ImageAlignment;
import com.thizzer.jtouchbar.item.TouchBarItem;
import com.thizzer.jtouchbar.item.view.TouchBarButton;
import com.thizzer.jtouchbar.item.view.TouchBarScrubber;
import com.thizzer.jtouchbar.scrubber.ScrubberDataSource;
import com.thizzer.jtouchbar.scrubber.view.ScrubberImageItemView;
import com.thizzer.jtouchbar.scrubber.view.ScrubberView;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InventoryBar
{
	// Size of hot bar
	final static int hotBarSize = 9;
	// Old hotbar container, used for comparison
	private static List<ItemStack> hotbarItemsOld = Lists.newArrayList();
	// Old selected item index
	private static int selectedIdx = -1;
	// Old hotbar item
	private static List<ItemStack> offHandOld = Lists.newArrayList();

	//Names
	private static final String inventoryId = "INVENTORY_LIST";


	public static void doRedraw(EntityPlayer player)
	{
		if (hasHotBarChanged(hotbarItemsOld.toArray(new ItemStack[0]), getHotBar(player)))
		{
			updateTouchBar(player, hotbarItemsOld);
			//Set values to old
			hotbarItemsOld = Arrays.asList(getHotBar(player));
			offHandOld = player.inventory.offHandInventory;
		}
		if (player.inventory.currentItem != selectedIdx) // Player has selected a different item
		{
			selectedIdx = player.inventory.currentItem;
		}
	}

	// Called to initially create Touch Bar
	public static void createTouchBar(EntityPlayer player)
	{
		//TouchBarWindow.mcTouchBar.setItems(createItems(player));
		ArrayList<TouchBarItem> touchBarItems = new ArrayList<>();
		TouchBarButton offhandButton = new TouchBarButton();
		offhandButton.setBezelColor(Color.SYSTEM_GRAY);
		touchBarItems.add(new TouchBarItem("OFFHAND_BUTTON",offhandButton, true));
		touchBarItems.add(new TouchBarItem(inventoryId, createInventoryScrubber(player), true));

		TouchBarWindow.mcTouchBar.setItems(touchBarItems);

		TouchBarWindow.show();
	}

	public static void updateTouchBar(EntityPlayer player, List<ItemStack> hotbarItems)
	{
		if (TouchBarWindow.mcTouchBar.getItems().isEmpty())
		{
			createTouchBar(player);
		}
		else
		{
			// Gets each "Item" (A Button, Scrubber, or Other Main element)
			List<TouchBarItem> listItems = TouchBarWindow.mcTouchBar.getItems();
			// The "Scrubber" Item that holds all the inventory stuff
			TouchBarItem scrubberItem = null;
			for (TouchBarItem barItem : listItems)
			{
				if (barItem.getView() instanceof TouchBarScrubber && barItem.getIdentifier().equals(inventoryId))
				{
					scrubberItem = barItem;
					break;
				}
			}

			// If An issue is detected to prevent the method from proceeding.
			if (scrubberItem == null)
			{
				TouchBar.LOGGER.error("Inventory element in Touch Bar NOT found!");
				return;
			}
			if (!scrubberItem.isCustomizationAllowed()) // If the element cannot be customized
			{
				TouchBar.LOGGER.error("Cannot modify inventory element!");
				return;
			}

			// Create a new scrubber, and put it in place.
			scrubberItem.setView(createInventoryScrubber(player));
			// Get the list of hotbar items and compare
			/*
			for (int i = 0; i < hotbarItems.size(); i++)
			{
				TouchBarItem touchItem = listItems.get(i);
				//Check if equal at index
				if (hotbarItems.get(i).equals(hotbarItemsOld.get(i)))
				{
					// DO nothing, they are the same
				}
				else if (!player.inventory.offHandInventory.equals(offHandOld))
				{
					// Off Hand Changed

				}
				else
				{
					TouchBar.LOGGER.info("Index: " + i + " CHANGED.");
					listItems.set(0, new TouchBarItem("S_TEST"));
				}
			}
			*/
			TouchBarWindow.show();
		}
	}

	/* Get if Inventory has Changed */
	private static boolean hasHotBarChanged(ItemStack[] itemStackOld, ItemStack[] itemStackNew)
	{
		int sameCount = 0;
		//Check if same, Compare how many entries are equal
		if (itemStackOld.length == itemStackNew.length)
		{
			for (int i = 0; i < itemStackOld.length; i++)
			{
				if (itemStackOld[i].getItem().equals(itemStackNew[i].getItem()))
				{
					sameCount++;
				}
			}
		}
		// Same count equals 9 if all items same
		return (sameCount != 9);
	}

	/* Since the hotbar is indices 0-n, and we only care about that, We can store the items in a n+1 length array */
	private static ItemStack[] getHotBar(EntityPlayer playerIn)
	{
		ItemStack[] hotBarArr = new ItemStack[hotBarSize];
		for (int i = 0; i < hotBarArr.length; i++)
		{
			hotBarArr[i] = playerIn.inventory.mainInventory.get(i);
		}
		return hotBarArr;
	}

	/* Create a Scrubber with the Hotbar contents */
	static TouchBarScrubber createInventoryScrubber(EntityPlayer player)
	{
		TouchBarScrubber scrubber = new TouchBarScrubber();
		scrubber.setActionListener((scrubber1, index) -> {
			TouchBar.LOGGER.info("Index: " + index);
			// Set current Item
			player.inventory.currentItem = (int) index;
		});
		scrubber.setDataSource(new ScrubberDataSource()
		{
			@Override
			public int getNumberOfItems(TouchBarScrubber scrubber)
			{
				return hotBarSize;
			}

			@Override
			public ScrubberView getViewForIndex(TouchBarScrubber scrubber, long index)
			{
				// Return hot bar item icon eventually
				ScrubberImageItemView view = new ScrubberImageItemView();
				view.setImage(IconGetter.getIconImage((int) index, player));
				view.setAlignment(ImageAlignment.CENTER);
				return view;
			}
		});
		return scrubber;
	}
}
