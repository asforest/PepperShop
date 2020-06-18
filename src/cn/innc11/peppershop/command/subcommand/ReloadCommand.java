package cn.innc11.peppershop.command.subcommand;

import cn.innc11.peppershop.PepperShop;
import cn.innc11.peppershop.command.PluginCommand;
import cn.innc11.peppershop.command.SubCommand;
import cn.innc11.peppershop.utils.Quick;
import cn.innc11.peppershop.localization.LangNodes;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;

import java.io.FileNotFoundException;

public class ReloadCommand implements SubCommand
{
	@Override
	public boolean onExecute(CommandSender sender, PluginCommand pluginCommand, String masterCommand, String subCommand, String[] subArgs)
	{
		if (!(sender instanceof Player && !sender.isOp()))
		{
			try {
				PepperShop.ins.reloadConfigs();
			}catch(FileNotFoundException e){
				e.printStackTrace();
			}

			sender.sendMessage(Quick.t(LangNodes.pm_reload_done));
		}

		return true;
	}

	@Override
	public String getCommandName()
	{
		return "reload";
	}

	@Override
	public String[] getAliases()
	{
		return new String[]{"r"};
	}

	@Override
	public CommandParameter[] getParameters()
	{
		return new CommandParameter[]{new CommandParameter("reload", false)};
	}
}
