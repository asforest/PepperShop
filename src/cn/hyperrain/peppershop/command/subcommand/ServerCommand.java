package cn.hyperrain.peppershop.command.subcommand;

import cn.hyperrain.peppershop.PepperShop;
import cn.hyperrain.peppershop.command.PluginCommand;
import cn.hyperrain.peppershop.command.SubCommand;
import cn.hyperrain.peppershop.shop.Shop;
import cn.hyperrain.peppershop.utils.Pair;
import cn.hyperrain.peppershop.utils.Quick;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;

import static cn.hyperrain.peppershop.localization.LangNodes.*;
import static cn.hyperrain.peppershop.localization.LangNodes.im_intercept_console;

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
