package cn.innc11.peppershop.shop;

import cn.innc11.peppershop.localization.LangNodes;
import cn.innc11.peppershop.utils.InvItem;
import cn.innc11.peppershop.utils.Quick;
import cn.innc11.peppershop.PepperShop;
import cn.innc11.peppershop.pluginEvent.PlayerBuyEvent;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.inventory.ChestInventory;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import me.onebone.economyapi.EconomyAPI;

public class BuyShop extends Shop {

	public BuyShop(String shop) 
	{
		super(shop);
	}
	
	public void buyItem(Player player, int count)
	{
		if(count <=0 )
		{
			player.sendMessage(Quick.t(LangNodes.im_trade_canceled));
			return;
		}

		EconomyAPI economyAPI = EconomyAPI.getInstance();
		double playerMoney = economyAPI.myMoney(player);
		double price = shopData.price * count;
		Player shopOwner = Server.getInstance().getPlayerExact(shopData.owner);
		Item item = getItem();
		item.setCount(count);
		PlayerInventory playerInv = player.getInventory();
		ChestInventory shopChestInventory = getShopEntityChest().getRealInventory();
		int itemCountInChest = InvItem.getItemInInventoryCount(shopChestInventory, item);

		if(getShopEntityChest()!=null)
		{
			if(itemCountInChest >= count || shopData.serverShop)
			{
				if(playerMoney >= price)
				{
					if(playerInv.canAddItem(item))
					{
						PlayerBuyEvent event = new PlayerBuyEvent(player, this, count);
						PepperShop.ins.getServer().getPluginManager().callEvent(event);

						if(!event.isCancelled())
						{
							economyAPI.reduceMoney(player, price);

							playerInv.addItem(item);

							if(!shopData.serverShop)
							{
								economyAPI.addMoney(shopData.owner, price);

								getShopEntityChest().getInventory().removeItem(item);

								if(shopOwner!=null) {
									shopOwner.sendMessage(Quick.t(LangNodes.im_buyshop_owner,
											"{ITEM}", PepperShop.ins.itemNameTranslationConfig.getItemName(item),
											"{COUNT}", String.valueOf(count),
											"{MONEY}", String.format("%.2f", price)));
								}
							}

							updateSignText();

							player.sendMessage(Quick.t(LangNodes.im_buyshop_customer,
									"{ITEM}", PepperShop.ins.itemNameTranslationConfig.getItemName(item),
									"{COUNT}", String.valueOf(count),
									"{MONEY}", String.format("%.2f", price)));
						}


					} else {
						player.sendMessage(Quick.t(LangNodes.im_buyshop_backpack_full, "{COUNT}", String.valueOf(count), "{ITEM}", PepperShop.ins.itemNameTranslationConfig.getItemName(item)));
					}
				}  else {
					player.sendMessage(Quick.t(LangNodes.im_buyshop_not_enough_money,
							"{MONEY}", String.format("%.2f", playerMoney),
							"{PRICE}", String.format("%.2f", shopData.price),
							"{COUNT}", String.valueOf(count),
							"{SUBTOTAL}", String.format("%.2f", shopData.price*count),
							"{LACK}", String.format("%.2f", price-playerMoney)));
				}
			}else {
				if(itemCountInChest==0)
					player.sendMessage(Quick.t(LangNodes.im_buyshop_sold_out));
				else
					player.sendMessage(Quick.t(LangNodes.im_buyshop_insufficient_stock, "{COUNT}", String.valueOf(count), "{REMAIN}", String.valueOf(itemCountInChest)));
			}
		}
	}

	@Override
	public int getMaxTransactionVolume(float playerMoney, int playerItemCount)
	{
		int itemCountInChest = InvItem.getItemInInventoryCount(getShopEntityChest().getInventory(), getItem());
		int a = (int) (playerMoney / shopData.price);
		
		if(shopData.serverShop)
			itemCountInChest = a;
		
		return Math.min(itemCountInChest, a);
	}

}
