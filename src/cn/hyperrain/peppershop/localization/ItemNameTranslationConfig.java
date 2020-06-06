package cn.hyperrain.peppershop.localization;

import java.io.File;
import java.util.HashMap;

import cn.hyperrain.peppershop.PepperShop;
import cn.hyperrain.peppershop.stroage.BaseConfig;
import cn.hyperrain.peppershop.utils.Quick;
import cn.nukkit.item.Item;

public class ItemNameTranslationConfig extends BaseConfig
{
	HashMap<String, String> itemNameMap = new HashMap<>();
	
	public ItemNameTranslationConfig(File file)
	{
		super(file, true);

		reload();
	}

	@Override
	public void Reload()
	{
		itemNameMap.clear();

		if(!tryToLoad())
		{
			if(exists())
			{
				config.reload();
			}else{
				return;
			}
		}

		Quick.info(LangNodes.pm_custom_name_file_found);

		config.reload();

		Object configMap = config.getRootSection().getAllMap();

		if(configMap!=null)
		{
			HashMap<String, String> cMap = (HashMap<String, String>) configMap;
			for(String key : cMap.keySet())
			{
				Item item = Item.fromString(key);

				if(item.getId()!=0)
				{
					itemNameMap.put(item.getId()+":"+item.getDamage(), cMap.get(key));
				}
			}

		}

		Quick.info(LangNodes.pm_loaded_custom_item_names,
				"{COUNT}", itemNameMap.size());
	}

	
	public String getItemName(Item item) 
	{
		return getPureItemName(item)+"&r";
	}

	public String getPureItemName(Item item)
	{
		if ((item.hasCustomName()) && (item.getCustomName()!=null) && (!item.getCustomName().isEmpty()))
		{
			return item.getCustomName();
		}

		if(PepperShop.ins.pluginConfig.useItemNameTranslations)
		{
			String key = item.getId()+":"+item.getDamage();

			if(itemNameMap.containsKey(key))
			{
				return itemNameMap.get(key);
			}
		}

		return item.getName();
	}

}
