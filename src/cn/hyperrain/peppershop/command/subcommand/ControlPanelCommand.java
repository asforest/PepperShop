package cn.hyperrain.peppershop.command.subcommand;

import cn.hyperrain.peppershop.command.PluginCommand;
import cn.hyperrain.peppershop.command.SubCommand;
import cn.hyperrain.peppershop.form.PluginControlPanel;
import cn.hyperrain.peppershop.utils.Quick;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;

import static cn.hyperrain.peppershop.localization.LangNodes.im_intercept_console;

public class ControlPanelCommand implements SubCommand
{
	@Override
	public boolean onExecute(CommandSender sender, PluginCommand pluginCommand, String masterCommand, String subCommand, String[] subArgs)
	{
		if (sender instanceof Player)
		{
			if(sender.isOp())
			{
				((Player)sender).showFormWindow(new PluginControlPanel());
			}
		} else {
			sender.sendMessage(Quick.t(im_intercept_console));
		}

		return true;
	}

	@Override
	public String getCommandName()
	{
		return "controlpanel";
	}

	@Override
	public String[] getAliases()
	{
		return new String[]{"c", "cp"};
	}

	@Override
	public CommandParameter[] getParameters()
	{
		return new CommandParameter[]{new CommandParameter("controlpanel", false)};
	}
}
