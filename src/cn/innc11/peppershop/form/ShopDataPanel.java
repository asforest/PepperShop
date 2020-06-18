package cn.innc11.peppershop.form;

import cn.innc11.peppershop.PepperShop;
import cn.innc11.peppershop.localization.LangNodes;
import cn.innc11.peppershop.shop.Shop;
import cn.innc11.peppershop.shop.ShopType;
import cn.innc11.peppershop.utils.Quick;
import cn.nukkit.Player;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementToggle;
import cn.nukkit.form.window.FormWindowCustom;

import java.util.Arrays;

public class ShopDataPanel extends FormWindowCustom implements FormResponse
{
	String shopKey;
	String playerName;
	
	public ShopDataPanel(Shop shop, String playerName)
	{
		super(Quick.t(LangNodes.shopdata_title));
		
		this.shopKey = shop.convertShopLocationByPos();
		this.playerName = playerName;
		
		Player player = PepperShop.server.getPlayerExact(playerName);
		
		addElement(new ElementInput(Quick.t(LangNodes.shopdata_price), "", shop.getStringPrice()));

		addElement(new ElementDropdown(Quick.t(LangNodes.shopdata_type), Arrays.asList(Quick.t(LangNodes.buy), Quick.t(LangNodes.sell)), shop.shopData.type== ShopType.BUY ? 0 :1));
	
		if(player.isOp())
		{
			addElement(new ElementInput(Quick.t(LangNodes.shopdata_owner), "", shop.shopData.owner));

			addElement(new ElementToggle(Quick.t(LangNodes.shopdata_server_shop), shop.shopData.serverShop));
		}
	}

	@Override
	public void onFormResponse(PlayerFormRespondedEvent e) 
	{
		Shop shop = Shop.getShopByLocation(shopKey);
		Player player = PepperShop.server.getPlayerExact(playerName);

		if(!player.isOp() && !shop.shopData.owner.equals(player.getName()))
			return;

		String price = getResponse().getInputResponse(0);
		int shopType = getResponse().getDropdownResponse(1).getElementID();

		if(!PepperShop.isPrice(price))
		{
			e.getPlayer().sendMessage(Quick.t(LangNodes.im_price_wrong_format));
			return;
		}
		
		shop.shopData.price = Float.parseFloat(price);
		shop.shopData.type = shopType==0 ? ShopType.BUY : ShopType.SELL;

		if(player.isOp())
		{
			shop.shopData.owner = getResponse().getInputResponse(2);
			shop.shopData.serverShop = getResponse().getToggleResponse(3);
		}else{
			shop.shopData.serverShop = false;
		}

		shop.updateSignText();

		PepperShop.ins.shopsConfig.getShopsConfig(shop, false).save();

		player.sendMessage(Quick.t(LangNodes.im_shop_data_updated));
	}

	@Override
	public void onFormClose(PlayerFormRespondedEvent e)
	{

	}

}
