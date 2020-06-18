package cn.innc11.peppershop.command.subcommand;

import cn.innc11.peppershop.PepperShop;
import cn.innc11.peppershop.command.PluginCommand;
import cn.innc11.peppershop.command.SubCommand;
import cn.innc11.peppershop.shop.Shop;
import cn.innc11.peppershop.utils.Pair;
import cn.innc11.peppershop.utils.Quick;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;

import static cn.innc11.peppershop.localization.LangNodes.*;
import static cn.innc11.peppershop.localization.LangNodes.im_intercept_console;

public class ServerCommand implements SubCommand
{
	@Override
	public boolean onExecute(CommandSender sender, PluginCommand pluginCommand, String masterCommand, String subCommand, String[] subArgs)
	{
		if (sender instanceof Player)
		{
			if(sender.isOp())
			{
				Pair<Boolean, Shop> vi = PepperShop.ins.ShopInteractionListener.isValidInteraction(sender.getName());

				if(vi!=null)
				{
					Shop shop = vi.value;

					shop.shopData.serverShop = !shop.shopData.serverShop;

					PepperShop.ins.shopsConfig.getShopsConfig(shop, false).save();

					shop.updateSignText();

					sender.sendMessage(shop.shopData.serverShop ? Quick.t(im_shop_updated_server) : Quick.t(im_shop_updated_ordinary));
				} else {
					sender.sendMessage(Quick.t(im_not_selected_shop));
				}
			}
		} else {
			sender.sendMessage(Quick.t(im_intercept_console));
		}

		return true;
	}

	@Override
	public String getCommandName()
	{
		return "server";
	}

	@Override
	public String[] getAliases()
	{
		return new String[]{"se"};
	}

	@Override
	public CommandParameter[] getParameters()
	{
		return new CommandParameter[]{new CommandParameter("server", false)};
	}
}
