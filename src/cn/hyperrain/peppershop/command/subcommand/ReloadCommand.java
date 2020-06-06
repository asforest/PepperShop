package cn.hyperrain.peppershop.command.subcommand;

import cn.hyperrain.peppershop.PepperShop;
import cn.hyperrain.peppershop.command.PluginCommand;
import cn.hyperrain.peppershop.command.SubCommand;
import cn.hyperrain.peppershop.utils.Quick;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;

import java.io.FileNotFoundException;

import static cn.hyperrain.peppershop.localization.LangNodes.pm_reload_done;

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

			sender.sendMessage(Quick.t(pm_reload_done));
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
