package cn.hyperrain.peppershop.command.subcommand;

import cn.hyperrain.peppershop.PepperShop;
import cn.hyperrain.peppershop.command.PluginCommand;
import cn.hyperrain.peppershop.command.SubCommand;
import cn.hyperrain.peppershop.shop.Shop;
import cn.hyperrain.peppershop.shop.ShopType;
import cn.hyperrain.peppershop.utils.Pair;
import cn.hyperrain.peppershop.utils.Quick;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;

import static cn.hyperrain.peppershop.localization.LangNodes.*;
import static cn.hyperrain.peppershop.localization.LangNodes.im_intercept_console;

public class SellCommand implements SubCommand
{
	@Override
	public boolean onExecute(CommandSender sender, PluginCommand pluginCommand, String masterCommand, String subCommand, String[] subArgs)
	{
		if (sender instanceof Player)
		{
			Pair<Boolean, Shop> vi = PepperShop.ins.ShopInteractionListener.isValidInteraction(sender.getName());
			if(vi!=null)
			{
				if(vi.key.booleanValue())
				{
					Shop shop = vi.value;

					if(shop.shopData.owner.equals(sender.getName()))
					{

						if(shop.shopData.type!=ShopType.SELL)
						{
							shop.shopData.type = ShopType.SELL;

							PepperShop.ins.shopsConfig.getShopsConfig(shop, false).save();

							shop.updateSignText();

							sender.sendMessage(Quick.t(im_shop_type_updated, "{TYPE}", ShopType.SELL.toString()));
						} else {
							sender.sendMessage(Quick.t(im_shop_type_donot_need_update));
						}

					} else {
						sender.sendMessage(Quick.t(im_not_allow_modify_price_not_owner));
					}
				} else {
					//sender.sendMessage(Quick.t(im_interaction_timeout));
				}

			}else{
				sender.sendMessage(Quick.t(im_not_selected_shop));
			}
		} else {
			sender.sendMessage(Quick.t(im_intercept_console));
		}

		return true;
	}

	@Override
	public String getCommandName()
	{
		return "sell";
	}

	@Override
	public String[] getAliases()
	{
		return new String[]{"s"};
	}

	@Override
	public CommandParameter[] getParameters()
	{
		return new CommandParameter[]{new CommandParameter("sell", false)};
	}
}
