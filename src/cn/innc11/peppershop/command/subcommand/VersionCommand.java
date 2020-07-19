package cn.innc11.peppershop.command.subcommand;

import cn.innc11.peppershop.PepperShop;
import cn.innc11.peppershop.command.PluginCommand;
import cn.innc11.peppershop.command.SubCommand;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.plugin.PluginDescription;
import cn.nukkit.utils.TextFormat;

import java.beans.ParameterDescriptor;

public class VersionCommand implements SubCommand
{
	@Override
	public boolean onExecute(CommandSender sender, PluginCommand pluginCommand, String masterCommand, String subCommand, String[] subArgs)
	{
		if (!(sender instanceof Player && !sender.isOp()))
		{
			PluginDescription descriptor = PepperShop.ins.getDescription();

			sender.sendMessage(TextFormat.colorize(String.format("%s_v%s %s",
					descriptor.getName(),
					descriptor.getVersion(),
					descriptor.getDescription()
			)));
		}

		return true;
	}

	@Override
	public String getSubCommandName()
	{
		return "version";
	}

	@Override
	public String[] getAliases()
	{
		return new String[]{"v"};
	}

	@Override
	public CommandParameter[] getSubParameters()
	{
		return null;
	}
}
