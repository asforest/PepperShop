package cn.innc11.peppershop.listener;

import cn.innc11.peppershop.PepperShop;
import cn.innc11.peppershop.localization.LangNodes;
import cn.innc11.peppershop.shop.Shop;
import cn.innc11.peppershop.utils.Quick;
import cn.innc11.peppershop.variousland.VariousLand;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.inventory.InventoryMoveItemEvent;
import cn.nukkit.event.inventory.InventoryOpenEvent;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.inventory.ChestInventory;
import cn.nukkit.inventory.Inventory;

public class ItemAndInventoryListener implements Listener
{
	@EventHandler
	public void onInventoryMoveItem(InventoryMoveItemEvent e)
	{
		Inventory sourceInventory = e.getInventory();
		Inventory targetInventory = e.getTargetInventory();

		if(sourceInventory instanceof ChestInventory && e.getAction() == InventoryMoveItemEvent.Action.SLOT_CHANGE)
		{
			ChestInventory chestInventory = (ChestInventory) sourceInventory;
			Shop shop = Shop.findShopByChestPos(chestInventory.getHolder());

			if(shop!=null)
			{
				shop.updateSignText(5);
				PepperShop.ins.hologramListener.addShopItemEntity(Server.getInstance().getOnlinePlayers().values(), shop.shopData);/////////////////
			}
		}

		if(targetInventory instanceof ChestInventory && e.getAction() == InventoryMoveItemEvent.Action.SLOT_CHANGE)
		{
			ChestInventory chestInventory = (ChestInventory) targetInventory;
			Shop shop = Shop.findShopByChestPos(chestInventory.getHolder());

			if(shop!=null)
			{
				shop.updateSignText(5);
				PepperShop.ins.hologramListener.addShopItemEntity(Server.getInstance().getOnlinePlayers().values(), shop.shopData);//////////////
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onInventoryOpen(InventoryOpenEvent e)
	{
		if(e.getInventory() instanceof ChestInventory)
		{
			Player player = e.getPlayer();
			ChestInventory chestInventory = (ChestInventory) e.getInventory();
			Shop shop = Shop.findShopByChestPos(chestInventory.getHolder());

			if(shop==null)
				return;

			// check permission
			if(VariousLand.existingAreaManagementPlugin())
			{
				VariousLand vl = VariousLand.getByLoc(chestInventory.getHolder());

				if(vl!=null)
				{
					boolean allow = false;
					allow |= vl.hasPermission(player.getName(), VariousLand.Permissions.build);
					allow |= vl.getOwner().equals(player.getName());
					allow |= player.isOp() && PepperShop.ins.pluginConfig.operatorIgnoreBuildPermission;

					if(!allow)
					{
						e.setCancelled();
						player.sendMessage(Quick.t(LangNodes.im_no_residence_permission, "{PERMISSION}", "build"));
					}
				}else {
					if(!player.getName().equals(shop.shopData.owner) && !player.isOp())
					{
						e.setCancelled();
						player.sendMessage(Quick.t(LangNodes.im_not_allow_others_open_chest));
					}
				}
			}

		}
	}

	@EventHandler
	public void onInventoryTransactionEvent(InventoryTransactionEvent e)
	{
		for(Inventory inv : e.getTransaction().getInventories().toArray(new Inventory[0]))
		{
			if(inv instanceof ChestInventory)
			{
				ChestInventory ci = (ChestInventory) inv;
				Shop shop = Shop.findShopByChestPos(ci.getHolder());
				if(shop!=null)
				{
					shop.updateSignText(5);
					PepperShop.ins.hologramListener.addShopItemEntity(Server.getInstance().getOnlinePlayers().values(), shop.shopData);/////////////////
				}
			}
		}
	}
}
