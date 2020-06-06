package cn.hyperrain.peppershop.command.subcommand;

import cn.hyperrain.peppershop.PepperShop;
import cn.hyperrain.peppershop.command.PluginCommand;
import cn.hyperrain.peppershop.command.SubCommand;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;

public class VersionCommand implements SubCommand
{
	@Override
	public boolean onExecute(CommandSender sender, PluginCommand pluginCommand, String masterCommand, String subCommand, String[] subArgs)
	{
		if (!(sender instanceof Player && !sender.isOp()))
		{
			sender.sendMessage(TextFormat.colorize(String.format(PepperShop.ins.getDescription().getDescription())));
		}

		return true;
	}

	@Override
	public String getCommandName()
	{
		return "version";
	}

	@Override
	public String[] getAliases()
	{
		return new String[]{"v"};
	}

	@Override
	public CommandParameter[] getParameters()
	{
		return new CommandParameter[]{new CommandParameter("version", false)};
	}
}
