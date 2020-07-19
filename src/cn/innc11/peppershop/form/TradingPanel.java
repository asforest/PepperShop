package cn.innc11.peppershop.form;

import cn.innc11.peppershop.localization.LangNodes;
import cn.innc11.peppershop.shop.BuyShop;
import cn.innc11.peppershop.shop.SellShop;
import cn.innc11.peppershop.shop.Shop;
import cn.innc11.peppershop.utils.InvItem;
import cn.innc11.peppershop.utils.Quick;
import cn.innc11.peppershop.PepperShop;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.element.ElementSlider;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.inventory.PlayerInventory;
import me.onebone.economyapi.EconomyAPI;

public class TradingPanel extends FormWindowCustom implements FormResponse
{
	String shopKey;
	String playerName;
	
	public TradingPanel(Shop shop, String playerName)
	{
		super(Quick.t(LangNodes.trading_title, "OWNER", (shop.shopData.serverShop? Quick.t(LangNodes.server_nickname):shop.shopData.owner)));
		
		this.shopKey = shop.convertShopLocationByPos();
		this.playerName = playerName;
		
		addElement(new ElementLabel(Quick.t(LangNodes.trading_shop_info,
				"GOODS", PepperShop.ins.itemNameTranslationConfig.getItemName(shop.getItem()),
				"PRICE", shop.getStringPrice(),
				"TYPE", shop.shopData.type.toString(),
				"STOCK", PepperShop.ins.itemNameTranslationConfig.getItemName(shop.getItem()),
				"ENCHANTMENTS", Quick.getEnchantments(shop.getItem())
				)));

		PlayerInventory playerInv = Server.getInstance().getPlayerExact(playerName).getInventory();
		float playerMoney = (float) EconomyAPI.getInstance().myMoney(playerName);

		int m = shop.getMaxTransactionVolume(playerMoney, InvItem.getItemInInventoryCount(playerInv, shop.getItem()));
		
		addElement(new ElementSlider(Quick.t(LangNodes.trading_trading_volume, "VOLUME", String.valueOf(m)), 0, m, 1, 0));
	}

	@Override
	public void onFormResponse(PlayerFormRespondedEvent e) 
	{
		int tv = (int) getResponse().getSliderResponse(1);
		
		if(tv!=0)
		{
			Shop shop = Shop.getShopByLocation(shopKey);
			Player player = PepperShop.ins.getServer().getPlayerExact(playerName);
			
			if(shop instanceof BuyShop)
			{
				((BuyShop) shop).buyItem(player, tv);
			}else if(shop instanceof SellShop)
			{
				((SellShop) shop).sellItme(player, tv);
			}
		}
	}

	@Override
	public void onFormClose(PlayerFormRespondedEvent e)
	{

	}

}
