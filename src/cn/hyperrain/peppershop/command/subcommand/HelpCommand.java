package cn.hyperrain.peppershop.command.subcommand;

import cn.hyperrain.peppershop.command.PluginCommand;
import cn.hyperrain.peppershop.command.SubCommand;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;

public class HelpCommand implements SubCommand
{
	@Override
	public boolean onExecute(CommandSender sender, PluginCommand pluginCommand, String masterCommand, String subCommand, String[] subArgs)
	{
		pluginCommand.sendHelp(sender);

		return true;
	}

	@Override
	public String getCommandName()
	{
		return "help";
	}

	@Override
	public String[] getAliases()
	{
		return new String[]{"h"};
	}

	@Override
	public CommandParameter[] getParameters()
	{
		return new CommandParameter[]{new CommandParameter("help", false)};
	}

}
