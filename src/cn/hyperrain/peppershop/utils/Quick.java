package cn.hyperrain.peppershop.utils;

import cn.hyperrain.peppershop.PepperShop;
import cn.hyperrain.peppershop.localization.LangNodes;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.TextFormat;

public class Quick
{
	public static boolean debug = false;

	public static String t(LangNodes l, Object... args)
	{
		return PepperShop.ins.localization.get(l, args);
	}

	public static String getEnchantments(Item item)
	{
		if(!item.hasEnchantments())
		{
			return "";
		}

		Enchantment[] enchantments = item.getEnchantments();
		StringBuffer sb = new StringBuffer();

		for (int i =0; i<enchantments.length; i++)
		{
			Enchantment enchantment = enchantments[i];

			String prefix = (i==0?
					t(LangNodes.enchant_prefix_first, "{PREFIX}", t(LangNodes.enchant_prefix))
					:
					t(LangNodes.enchant_prefix)
				);

			String suffix = (i==enchantments.length-1?
					t(LangNodes.enchant_suffix_last, "{SUFFIX}", t(LangNodes.enchant_suffix))
					:
					t(LangNodes.enchant_suffix)
				);

			sb.append(t(LangNodes.enchant_each_line,
					"{PREFIX}", prefix,
					"{ENCHANT}", PepperShop.ins.localization.enchantNames.getEnchantmentName(enchantment.getId()),
					"{LEVEL}", String.valueOf(enchantment.getLevel()),
					"{SUFFIX}", suffix
				));

		}

		return t(LangNodes.enchant_text, "{LINES}", sb.toString());
	}



	// logger


	public static void critical(String s)
	{
		PepperShop.logger.critical(TextFormat.colorize(s));
	}

	public static void error(String s)
	{
		PepperShop.logger.error(TextFormat.colorize(s));
	}

	public static void warning(String s)
	{
		PepperShop.logger.warning(TextFormat.colorize(s));
	}

	public static void info(String s)
	{
		PepperShop.logger.info(s);
	}

	public static void debug(String s)
	{
		if(debug)
			PepperShop.logger.warning(s);
	}


	public static void critical(LangNodes l, Object... args)
	{
		Quick.critical(Quick.t(l, args));
	}

	public static void error(LangNodes l, Object... args)
	{
		Quick.error(Quick.t(l, args));
	}

	public static void warning(LangNodes l, Object... args)
	{
		Quick.warning(Quick.t(l, args));
	}

	public static void info(LangNodes l, Object... args)
	{
		Quick.info(Quick.t(l, args));
	}

	public static void debug(LangNodes l, Object... args)
	{
		Quick.debug(Quick.t(l, args));
	}



}
