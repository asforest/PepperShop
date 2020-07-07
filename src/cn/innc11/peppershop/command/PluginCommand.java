package cn.innc11.peppershop.command;

import java.util.HashMap;
import java.util.Map;

import cn.innc11.peppershop.utils.Quick;
import cn.innc11.peppershop.command.subcommand.*;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

import static cn.innc11.peppershop.localization.LangNodes.*;

public class PluginCommand extends Command
{
	HashMap<String, SubCommand> subCommands = new HashMap<>();

	public PluginCommand()
	{
		super("peppershop");
		
		setDescription("PepperShop Command");
		setAliases(new String[]{"shop", "qs", "quickshop", "pes", "ps", "pps"});
        setUsage("/qs <SubCommand> [Args]");

		getCommandParameters().clear();

		registerCommand(new HelpCommand());

		registerCommand(new BuyCommand());

		registerCommand(new SellCommand());

		registerCommand(new PriceCommand());

		registerCommand(new ServerCommand());

		registerCommand(new ControlPanelCommand());

		registerCommand(new ReloadCommand());

		registerCommand(new VersionCommand());

	}

	public void registerCommand(SubCommand subCommand)
	{
		String commandName = subCommand.getCommandName();

		subCommands.put(commandName, subCommand);

		getCommandParameters().put(commandName, subCommand.getParameters());
	}



	@Override
	public boolean execute(CommandSender sender, String label, String[] args) 
	{
		if(args.length==0)
		{
			sendHelp(sender);
			return true;
		}

		String secondCommand = args[0];

		for(Map.Entry<String, SubCommand> s : subCommands.entrySet())
		{
			String Text = s.getKey();
			SubCommand Command = s.getValue();

			if(Text.equals(secondCommand) || contains(secondCommand, Command.getAliases()))
			{
				String[] subArgs = new String[args.length-1];

				for(int i=0;i<args.length-1;i++)
				{
					subArgs[i] = args[i+1];
				}

				return Command.onExecute(sender, this, label, secondCommand, subArgs);
			}
		}

		sendHelp(sender);

		return false;
	}
	
	public void sendHelp(CommandSender sender)
	{
		sender.sendMessage(Quick.t(pm_help_player));
		
        if(!(sender instanceof Player && !sender.isOp()))
        {
        	sender.sendMessage(Quick.t(pm_help_operator));
        }
	}

	private static boolean contains(String text, String[] texts)
	{
		for(String t : texts)
		{
			if(text.equals(t))
				return true;
		}

		return false;
	}

}
