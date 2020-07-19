package cn.innc11.peppershop.localization;

import cn.innc11.peppershop.PepperShop;
import cn.innc11.peppershop.stroage.BaseConfig;
import cn.innc11.peppershop.shop.Shop;
import cn.innc11.peppershop.shop.ShopType;
import cn.innc11.peppershop.utils.Quick;
import cn.nukkit.utils.TextFormat;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

public class Localization extends BaseConfig
{
	HashMap<LangNodes, String> lang = new HashMap<>();

	public SignText signText;
	public EnchantmentNames enchantNames;

	public Localization(File file) throws FileNotFoundException
	{
		super(file, false);

		try {
			checkFileExistence();
		}catch(FileNotFoundException e) {
			throw new FileNotFoundException(String.format("The language file: %s not found!", file.getAbsolutePath()));
		}

		loadFile();

		reload();

		signText = new SignText(this);
		enchantNames = new EnchantmentNames(this);
	}

	@Override
	public void Reload()
	{
		config.reload();
		lang.clear();

		boolean supplement = false;
		int ct = 0;

		for(LangNodes key : LangNodes.values())
		{
			String field = key.getDefaultLangText();
			Object v = config.get(field);

			if(v==null)
			{
				config.set(field, field);
				lang.put(key, field);
				supplement = true;
				PepperShop.ins.getLogger().info(TextFormat.colorize("&cSet default language text for "+ getCompleteFileName() +"("+key.name()+")"));
			}

			if(v instanceof String)
			{
				lang.put(key, (String) v);
				ct++;
			} else if(v instanceof ArrayList)
			{
				ArrayList<String> values = (ArrayList<String>) v;

				StringBuffer sb = new StringBuffer();

				for(int i=0;i<values.size();i++)
				{
					sb.append(values.get(i));

					if(i!=values.size()-1)
						sb.append("\n");
				}

				lang.put(key, sb.toString());
				ct++;
			}


		}

		if(supplement)
			save();

		PepperShop.logger.info(TextFormat.colorize("Loaded &6"+ct+"&r languages from &b"+ getCompleteFileName()));
	}

	public String get(LangNodes key, Object... argsPair)
	{
		String rawStr = lang.get(key);
		String cookedStr = rawStr;

		int argCount = argsPair.length / 2;

		for(int i=0;i<argCount;i++)
		{
			String reg = argsPair[i*2].toString();
			reg = "\\\\{" + reg;
			reg = reg + "\\\\}";
			String replacement = argsPair[i*2+1].toString();
			cookedStr = rawStr.replaceAll(reg, replacement);
		}

		return TextFormat.colorize(cookedStr);
	}

	public static class SignText
	{
		Localization localization;

		public SignText(Localization loc)
		{
			localization = loc;
		}

		public String getStockText(Shop shop)
		{
			String buyStockText = Quick.t(LangNodes.buyshop_stock);
			String sellStockText = Quick.t(LangNodes.sellshop_stock);

			String st = (shop.shopData.type== ShopType.BUY ? buyStockText : sellStockText);

			st = st.replaceAll("\\{STOCK_SPACE\\}", String.valueOf(shop.getStock()));

			return st;
		}

		private String fillPlaceholder(String str, Shop shop)
		{
			ItemNameTranslationConfig inc = PepperShop.ins.itemNameTranslationConfig;

			str = str.replaceAll("\\{ITEM\\}", inc.getItemName(shop.getItem()));
			str = str.replaceAll("\\{PRICE\\}", shop.getStringPrice());
			str = str.replaceAll("\\{STOCK\\}", shop.shopData.serverShop ? "" : getStockText(shop));
			str = str.replaceAll("\\{OWNER\\}", shop.shopData.serverShop? Quick.t(LangNodes.server_nickname):shop.shopData.owner);
			str = str.replaceAll("\\{DAMAGE\\}", String.valueOf(shop.shopData.item.getDamage()));

			return str;
		}

		public String[] get(Shop shop)
		{
			String buyText1 = Quick.t(LangNodes.buyshop_text1);
			String buyText2 = Quick.t(LangNodes.buyshop_text2);
			String buyText3 = Quick.t(LangNodes.buyshop_text3);
			String buyText4 = Quick.t(LangNodes.buyshop_text4);

			String sellText1 = Quick.t(LangNodes.sellshop_text1);
			String sellText2 = Quick.t(LangNodes.sellshop_text2);
			String sellText3 = Quick.t(LangNodes.sellshop_text3);
			String sellText4 = Quick.t(LangNodes.sellshop_text4);


			String[] text = new String[4];
			boolean isBuy = shop.shopData.type==ShopType.BUY;

			text[0] = TextFormat.colorize(fillPlaceholder((isBuy ? buyText1 : sellText1), shop));
			text[1] = TextFormat.colorize(fillPlaceholder((isBuy ? buyText2 : sellText2), shop));
			text[2] = TextFormat.colorize(fillPlaceholder((isBuy ? buyText3 : sellText3), shop));
			text[3] = TextFormat.colorize(fillPlaceholder((isBuy ? buyText4 : sellText4), shop));

			return text;
		}
	}

	public static class EnchantmentNames
	{
		Localization localization;

		public EnchantmentNames(Localization loc)
		{
			localization = loc;
		}

		public String getEnchantmentName(int enchantmentId)
		{
			String langKey = "E_"+enchantmentId;

			if(LangNodes.contains(langKey))
				return Quick.t(LangNodes.valueOf(langKey));

			return "UnknownEnchantment "+enchantmentId;
		}
	}
}
