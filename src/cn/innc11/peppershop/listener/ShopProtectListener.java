package cn.innc11.peppershop.listener;

import java.util.ArrayList;

import cn.innc11.peppershop.PepperShop;
import cn.innc11.peppershop.shop.Shop;
import cn.innc11.peppershop.virtualLand.VirtualAreaManage;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockChest;
import cn.nukkit.block.BlockWallSign;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBurnEvent;
import cn.nukkit.event.block.BlockPistonChangeEvent;
import cn.nukkit.event.entity.EntityExplodeEvent;
import cn.nukkit.event.inventory.InventoryMoveItemEvent;
import cn.nukkit.inventory.ChestInventory;
import cn.nukkit.inventory.Inventory;

public class ShopProtectListener implements Listener 
{
	 @EventHandler
	public void onEntityExplode(EntityExplodeEvent event) 
	{
		 ArrayList<Block> preserve = new ArrayList<Block>();
		 
		 for(Block block : event.getBlockList())
		 {
			 if(block instanceof BlockChest)
			 {
				 Shop shop = Shop.findShopByChest(block);
				 
				 if(shop!=null)
					 preserve.add(block);
			 }
			 
			 if(block instanceof BlockWallSign)
			 {
				 Shop shop = Shop.findShopBySign(block);
				 
				 if(shop!=null)
					 preserve.add(block);
			 }
		 }
		 
		 for(Block block : preserve)
		 {
			 event.getBlockList().remove(block);
		 }
		 
	}
	
	@EventHandler
	public void onBlockBurn(BlockBurnEvent event) 
	{
		Block block = event.getBlock();
		
		if(block instanceof BlockChest)
		{
			 if(Shop.findShopByChest(block)!=null)
			 {
				 event.setCancelled();
			 }
		}
		 
		if(block instanceof BlockWallSign)
		{
			 if(Shop.findShopBySign(block)!=null)
			 {
				 event.setCancelled();
			 }
		 }
	}
	
	@EventHandler
	public void onBlockPistonChange(BlockPistonChangeEvent e)
	{
		Block block = e.getBlock();
		
		if(block instanceof BlockChest)
		 {
			 Shop shop = Shop.findShopByChest(block);
			 
			 if(shop!=null)
				 e.setCancelled();
		 }
		 
		 if(block instanceof BlockWallSign)
		 {
			 Shop shop = Shop.findShopBySign(block);
			 
			 if(shop!=null)
				 e.setCancelled();
		 }
	}

	@EventHandler
	public void onInventoryMoveItem2(InventoryMoveItemEvent e)
	{
		if(VirtualAreaManage.existingAreaManagementPlugin() && PepperShop.ins.pluginConfig.limitHopper)
		{
			Inventory sourceInventory = e.getInventory();
			Inventory targetInventory = e.getTargetInventory();

			if(sourceInventory instanceof ChestInventory
					&& e.getAction() == InventoryMoveItemEvent.Action.SLOT_CHANGE)
			{
				ChestInventory chestInventory = (ChestInventory) sourceInventory;
				Shop shop = Shop.findShopByChestPos(chestInventory.getHolder());
				VirtualAreaManage vl = VirtualAreaManage.getByLoc(chestInventory.getHolder());

				if(shop!=null && vl==null)
				{
					e.setCancelled();
				}
			}

			if(targetInventory instanceof ChestInventory
					&& e.getAction() == InventoryMoveItemEvent.Action.SLOT_CHANGE)
			{
				ChestInventory chestInventory = (ChestInventory) targetInventory;
				Shop shop = Shop.findShopByChestPos(chestInventory.getHolder());
				VirtualAreaManage vl = VirtualAreaManage.getByLoc(chestInventory.getHolder());

				if(shop!=null && vl==null)
				{
					e.setCancelled();
				}
			}

		}

	}
	
}
