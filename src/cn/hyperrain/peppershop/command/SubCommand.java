package cn.hyperrain.peppershop.command;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;

import java.util.ArrayList;
import java.util.LinkedList;


public interface SubCommand
{
	boolean onExecute(CommandSender sender, PluginCommand pluginCommand, String masterCommand, String subCommand, String[] subArgs);

	String getCommandName();

	String[] getAliases();

	CommandParameter[] getParameters();
}
