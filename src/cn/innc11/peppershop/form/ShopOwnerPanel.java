package cn.innc11.peppershop.form;

import cn.innc11.peppershop.localization.LangNodes;
import cn.innc11.peppershop.shop.Shop;
import cn.innc11.peppershop.shop.ShopType;
import cn.innc11.peppershop.utils.Quick;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowSimple;

public class ShopOwnerPanel extends FormWindowSimple implements FormResponse
{
	String shopKey;
	String playerName;
	
	public ShopOwnerPanel(Shop shop, String playerName)
	{
		super(Quick.t(LangNodes.owner_title,
				"{OWNER}", (shop.shopData.serverShop? Quick.t(LangNodes.server_nickname):shop.shopData.owner)), "");
		
		this.shopKey = shop.convertShopLocationByPos();
		this.playerName = playerName;

		setContent(Quick.t(LangNodes.owner_content,
				"{PRICE}", shop.getStringPrice(),
				"{TYPE}",  Quick.t(shop.shopData.type== ShopType.BUY ? LangNodes.buy : LangNodes.sell),
				"{STOCK}", String.valueOf(shop.getStock()),
				"{ENCHANTMENTS}", Quick.getEnchantments(shop.getItem())
		));

		addButton(new ElementButton(Quick.t(LangNodes.owner_button_shop_trading_panel)));

		addButton(new ElementButton(Quick.t(LangNodes.owner_button_shop_data_panel)));
	}

	@Override
	public void onFormResponse(PlayerFormRespondedEvent e) 
	{
		Shop shop = Shop.getShopByLocation(shopKey);

		if(!e.getPlayer().isOp() && !e.getPlayer().getName().equals(shop.shopData.owner))
		{
			return;
		}

		int clickedButtonIndex = getResponse().getClickedButtonId();

		switch (clickedButtonIndex)
		{
			case 0:
				e.getPlayer().showFormWindow(new TradingPanel(shop, playerName));
				break;

			case 1:
				e.getPlayer().showFormWindow(new ShopDataPanel(shop, playerName));
				break;

		}
		
	 }

	@Override
	public void onFormClose(PlayerFormRespondedEvent e)
	{

	}
}
